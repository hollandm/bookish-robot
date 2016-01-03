package Game;


import Brains.Base_Brain;
import Brains.*;
import Game.Exceptions.GameException;
import Game.Model.*;

import java.util.ArrayList;


public class Game {

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

        state.startGame(masterKey);

        while (state.getStage() == GameStage.GAME_STARTED) {
            int currentPlayerId = state.getCurrentPlayer().getPlayerID();
            Base_Brain currentPlayerBrain = brains.get(currentPlayerId);

            Turn thisTurn = startTurn(masterKey);

            do {
                currentPlayerBrain.takeTurn(state, thisTurn);
            } while (thisTurn.isValidTurn(state) == false);

            applyAction(thisTurn, masterKey);

            thisTurn.finalize(masterKey);

            for (Base_Brain brain : brains)
                brain.showTurn(thisTurn);

            state.nextTurn(masterKey);

        }

    }

    private Turn startTurn(DataKey key) {

        // Handmaid protection wears off
        state.getCurrentPlayer().setHandmaidProtected(key, false);

        Card drawnCard = state.getDeck(key).pop();
        Turn thisTurn = new Turn(key, state.getCurrentPlayer(), drawnCard, state.getCurrentTurn());

        return thisTurn;
    }

    private void applyAction(Turn action, DataKey key) {

        if (!key.isMasterKey())
            return;

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
                if (action.wasTargetPlayerHandmaidProtected())
                    break;
                Card activePlayerCard = action.getActingPlayerRemainingCard(key);
                Card targetPlayerCard = action.getTargetPlayersCard(key);

                action.getActingPlayer().getHand(key).remove(activePlayerCard);
                action.getActingPlayer().getHand(key).add(targetPlayerCard);

                action.getTargetPlayer().getHand(key).remove(targetPlayerCard);
                action.getTargetPlayer().getHand(key).add(activePlayerCard);

                break;

            case Prince:
                if (action.wasTargetPlayerHandmaidProtected())
                    break;

                action.getTargetPlayer().discardCard(key, action.getTargetPlayersCard(key));
                if (state.countCardsLeftInDeck() == 0 || action.getTargetPlayersCard(key) == Card.Princess) {
                    state.eliminatePlayer(key, action.getTargetPlayer());
                    break;
                }

                action.getTargetPlayer().addCardToHand(key, state.getDeck(key).pop());
                break;

            case Handmaid:
                action.getActingPlayer().setHandmaidProtected(key, true);
                break;

            case Baron:
                if (action.wasTargetPlayerHandmaidProtected())
                    break;

                if (action.getActingPlayerRemainingCard(key).value > action.getTargetPlayersCard(key).value)
                    state.eliminatePlayer(key, action.getTargetPlayer());
                else if (action.getActingPlayerRemainingCard(key).value < action.getTargetPlayersCard(key).value)
                    state.eliminatePlayer(key, action.getActingPlayer());

                break;

            case Priest:
                // The hand is visible to the player in the turn object
                break;

            case Guard:
                if (action.wasTargetPlayerHandmaidProtected())
                    break;

                if (action.getTargetPlayersCard(key) == action.getGuessedCard())
                    state.eliminatePlayer(key, action.getTargetPlayer());

                break;

        }


        // Check if the game has ended
        if (state.countCardsLeftInDeck() == 0 || state.countRemainingPlayers() == 1) {
            //TODO: Do stuff if the game has ended

            return;
        }

        state.nextTurn(key);
    }

    public static void main(String[] args) throws GameException {

        ArrayList<Base_Brain> brains = new ArrayList<>();
        brains.add(new RandomAI());
        brains.add(new RandomAI());

        Game game = new Game();

        game.runGame(brains);

    }


}
