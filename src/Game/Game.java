package Game;


import Brains.Base_Brain;
import Brains.*;
import Game.Exceptions.GameException;
import Game.Exceptions.HandTooBigException;
import Game.Model.*;

import java.util.ArrayList;
import java.util.Collections;


public class Game {

    public static final boolean PRINT_GAME_EVENTS = true;
    public static final boolean PRINT_SENSITIVE_DATA = true;
    private static boolean isNewLine = true;
    public static int indentation = 0;

    @SuppressWarnings("PointlessBooleanExpression")
    public static void print(String text, boolean isSensitive) {
        if (!PRINT_GAME_EVENTS)
            return;

        if (isSensitive && !PRINT_SENSITIVE_DATA)
            return;

        if (isNewLine)
            System.out.print(String.join("", Collections.nCopies(indentation, " ")));

        System.out.print(text);

        Game.isNewLine = false;
    }

    @SuppressWarnings("PointlessBooleanExpression")
    public static void println(String text, boolean isSensitive) {
        if (!PRINT_GAME_EVENTS)
            return;

        if (isSensitive && !PRINT_SENSITIVE_DATA)
            return;

        if (isNewLine)
            System.out.print(String.join("", Collections.nCopies(indentation, " ")));

        System.out.println(text);
        Game.isNewLine = true;
    }


    public class DataKey {
        private final Player player;

        private DataKey(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return this.player;
        }

        public boolean isMasterKey() {
            return this.player == null;
        }
    }

    private static boolean allowTesting = false;
    private static boolean allowTesting_isSet = false;
    public static void setAllowTesting(boolean allow) {

        if (allowTesting_isSet)
            return;

        allowTesting_isSet = true;
        allowTesting = allow;
    }

    public DataKey getTestKey(Player p) {
        if (!allowTesting)
            return null;

        return new DataKey(p);
    }

    private int nextPlayerID = 0;

    GameState state = new GameState();

    public Game() {

    }

    public void runGame(ArrayList<Base_Brain> brains) throws GameException{

        DataKey masterKey = new DataKey(null);

        for (Base_Brain brain : brains) {
            Player p = state.addPlayer(brain.getName(), brain.id);
            DataKey key = new DataKey(p);

            brain.setKey(key);
        }

        Game.println("Starting a game with " + state.countPlayers() + " players.", false);

        state.startGame(masterKey);

        while (state.getStage() == GameStage.GAME_STARTED) {
            Turn thisTurn = startTurn(masterKey);

            Player currentPlayer = state.getCurrentPlayer();
            int currentPlayerId = currentPlayer.getPlayerID();
            Base_Brain currentPlayerBrain = null;

            for (Base_Brain brain : brains)
                if (brain.getId() == currentPlayerId)
                    currentPlayerBrain = brain;


            Game.println("Turn " + thisTurn.getTurnCount() + ": " + thisTurn.getActingPlayer() + "'s Turn.", false);
            Game.indentation += 4;
            Game.println("Drew: " + thisTurn.getDrawnCard(masterKey), true);

            if (currentPlayer.hasCard(masterKey, Card.Countess) && (currentPlayer.hasCard(masterKey, Card.King) || currentPlayer.hasCard(masterKey, Card.Prince))) {

                if (thisTurn.getPlayedCard() != Card.Countess)
                    thisTurn.swapPlayedAndRemainingCards(masterKey);

            } else {

                do {
                    //noinspection ConstantConditions
                    currentPlayerBrain.takeTurn(state, thisTurn);
                } while (thisTurn.isValidTurn(state) == false);

            }

            thisTurn.finalize(masterKey);

            applyAction(thisTurn, masterKey);

            Game.indentation -= 4;

            checkWinner(masterKey);

            state.saveTurn(masterKey, thisTurn);

            for (Base_Brain brain : brains)
                brain.showTurn(thisTurn);

            state.nextTurn(masterKey);

        }

    }

    private Turn startTurn(DataKey key) {

        // Handmaid protection wears off
        state.getCurrentPlayer().setProtected(key, false);

        Card drawnCard = state.getDeck(key).pop();
        try {
            state.getCurrentPlayer().addCardToHand(key, drawnCard);
        } catch (HandTooBigException e) {
            System.err.println("Hand to big error");
        }

        return new Turn(key, state.getCurrentPlayer(), drawnCard, state.getCurrentTurn());
    }

    private void applyAction(Turn action, DataKey key) {

        if (!key.isMasterKey())
            return;

        if (Game.PRINT_GAME_EVENTS) {

            Game.println("Played: " + action.getPlayedCard(), false);

            if (action.getTargetPlayer() != null) {
                Game.println("Targeted: " + action.getTargetPlayer(), false);

                if (action.wasTargetPlayerProtected())
                    Game.println(action.getTargetPlayer() + " is protected by the handmaiden", false);
            }
        }


        //Remove the card they played from their hand
        action.getActingPlayer().discardCard(key, action.getPlayedCard());

        switch (action.getPlayedCard()) {
            case Princess:
                state.eliminatePlayer(key, action.getActingPlayer());
                break;

            case Countess:
                // This card doesn't do anything on it's own
                break;

            case King:
                if (action.wasTargetPlayerProtected())
                    break;
                Card activePlayerCard = action.getActingPlayerRemainingCard(key);
                Card targetPlayerCard = action.getTargetPlayersCard(key);

                action.getActingPlayer().getHand(key).remove(activePlayerCard);
                action.getActingPlayer().getHand(key).add(targetPlayerCard);

                action.getTargetPlayer().getHand(key).remove(targetPlayerCard);
                action.getTargetPlayer().getHand(key).add(activePlayerCard);



                if (Game.PRINT_GAME_EVENTS) {
                    Game.println(action.getActingPlayer() + " has traded hands with " + action.getTargetPlayer(), false);

                    Game.println(action.getActingPlayer() + " now has " + targetPlayerCard, true);
                    Game.println(action.getTargetPlayer() + " now has " + activePlayerCard, true);
                }

                break;

            case Prince:
                if (action.wasTargetPlayerProtected())
                    break;

                if (Game.PRINT_GAME_EVENTS)
                    Game.println(action.getTargetPlayer() + " was forced to discard " + action.getTargetPlayersCard(key), false);

                action.getTargetPlayer().discardCard(key, action.getTargetPlayersCard(key));
                if (state.countCardsLeftInDeck() == 0 || action.getTargetPlayersCard(key) == Card.Princess) {
                    state.eliminatePlayer(key, action.getTargetPlayer());
                    break;
                }

                if (Game.PRINT_GAME_EVENTS) {
                    if (Game.PRINT_SENSITIVE_DATA)
                        Game.println(action.getTargetPlayer() + " drew " + action.getDrawnCard(key), true);
                    else
                        Game.println(action.getTargetPlayer() + " drew a new card", false);
                }

                try {
                    action.getTargetPlayer().addCardToHand(key, state.getDeck(key).pop());
                } catch (HandTooBigException e) {
                    System.err.println("Could not add card to hand, hand to big");
                }

                break;

            case Handmaid:
                action.getActingPlayer().setProtected(key, true);
                break;

            case Baron:
                if (action.wasTargetPlayerProtected())
                    break;

                if (Game.PRINT_GAME_EVENTS) {
                    Game.println(action.getTargetPlayer() + " has to compare cards with " + action.getActingPlayer(), false);
                    Game.println( action.getActingPlayer() + " has " + action.getActingPlayerRemainingCard(key), true);
                    Game.println(action.getTargetPlayer() + " has " + action.getTargetPlayersCard(key), true);
                }


                if (action.getActingPlayerRemainingCard(key).value > action.getTargetPlayersCard(key).value) {
                    action.getTargetPlayer().discardCard(key, action.getTargetPlayersCard(key));
                    state.eliminatePlayer(key, action.getTargetPlayer());
                }
                else if (action.getActingPlayerRemainingCard(key).value < action.getTargetPlayersCard(key).value) {
                    action.getActingPlayer().discardCard(key, action.getActingPlayerRemainingCard(key));
                    state.eliminatePlayer(key, action.getActingPlayer());
                }


                break;

            case Priest:
                // The hand is visible to the player in the turn object
                if (action.wasTargetPlayerProtected())
                    break;

                if (Game.PRINT_GAME_EVENTS)
                    Game.println(action.getTargetPlayer() + " has " + action.getTargetPlayersCard(key), true);

                break;

            case Guard:
                if (action.wasTargetPlayerProtected())
                    break;

                if (Game.PRINT_GAME_EVENTS)
                    Game.println(action.getActingPlayer() + " guessed " + action.getGuessedCard(), false);

                boolean wasCorrect = action.getTargetPlayersCard(key) == action.getGuessedCard();

                //noinspection PointlessBooleanExpression
                if (Game.PRINT_GAME_EVENTS && (Game.PRINT_SENSITIVE_DATA || wasCorrect))
                    Game.println(action.getTargetPlayer() + " had " + action.getTargetPlayersCard(key), !wasCorrect);

                if (wasCorrect)
                    state.eliminatePlayer(key, action.getTargetPlayer());


                break;

        }
    }

    public Player checkWinner(DataKey key) {
        if (state.countCardsLeftInDeck() == 0 || state.countRemainingPlayers() == 1) {
            Player winner = null;

            // Last player standing is automatically the winner
            if (state.countRemainingPlayers() == 1) {
                winner = state.getActivePlayers(key).get(0);

                Game.println("There is only one player remaining", false);

            } else {

                // Otherwise the player with the highest card value in their hand wins
                int maxScore = 0;
                ArrayList<Player> tiedPlayers = new ArrayList<>();

                for (Player p : state.getActivePlayers(key)) {
                    int playerScore = p.getHand(key).get(0).value;
                    if (playerScore > maxScore) {
                        winner = p;
                        maxScore = playerScore;

                        tiedPlayers.clear();
                        tiedPlayers.add(p);

                    } else if (playerScore == maxScore) {
                        tiedPlayers.add(p);
                    }
                }

                // Ties are broken by comparing the total value of all played cards
                if (tiedPlayers.size() > 1) {

                    maxScore = 0;

                    for (Player p : tiedPlayers) {
                        int playerScore = 0;
                        for (Card c : p.getPlayedCards(key))
                            playerScore += c.value;

                        if (playerScore > maxScore) {
                            maxScore = playerScore;
                            winner = p;
                        }
                    }

                }

                Game.println("There are no more cards in the deck", false);
            }

            winner.addWin(key);
            state.setWinner(key, winner);
            return winner;
        }

        return null;
    }

    public static void main(String[] args) throws GameException {

        Game.setAllowTesting(false);

        ArrayList<Base_Brain> brains = new ArrayList<>();
        brains.add(new RandomAI());
        brains.add(new RandomAI());
        brains.add(new RandomAI());
        brains.add(new RandomAI());
        brains.add(new RandomAI());
        brains.add(new RandomAI());



        Game game = new Game();

        game.runGame(brains);

    }


}
