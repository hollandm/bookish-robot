package Game.Model;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

import Game.Game.DataKey;
import Game.Exceptions.*;


public class GameState {

    //gameStarted: tracks if the game has started, this can help prevent the game state from being invalidated
    private GameStage stage = GameStage.PREGAME;

    // turn: The number of individual players turns that have occurred this game including this one
    private int turn = 0;

    // players: The list of players in the game
    private ArrayList<Player> players;

    // playerTurnOrder: The turn queue. The current player is the first person in the queue.
    //      Players who are eliminated are removed from the queue
    private LinkedList<Player> playerTurnOrder;

    // deck: The cards in the deck
    private Stack<Card> deck;

    // discard: the cards that were set aside at the beginning of the game
    private ArrayList<Card> discard;

    Stack<Turn> turnHistory;

    /*************
     ** Getters **
     *************/

    /**
     * getCurrentTurn
     * @return The number of individual players turns that have occurred this game, including this one
     */
    public int getCurrentTurn() {
        return turn;
    }

    /**
     * getPlayerByID
     * @param id
     * @return the player with the given id or null if it is an invalid ID
     */
    public Player getPlayerByID(int id) {

        for (Player p : this.players)
            if (p.getPlayerID() == id)
                return p;

        return null;
    }

    /**
     * getCurrentPlayer
     * @return The player object of the player whose turn it is
     */
    public Player getCurrentPlayer() {

        if (playerTurnOrder.isEmpty())
            return null;

        return playerTurnOrder.peek();
    }

    /**
     * getNnthNextPlayer
     *  Get the nth next player were 0 is the current player, 1 is the next player, ect. This will wrap so you can see
     *      more than one round into the future assuming no more players get eliminated.
     *
     * @param n, the index to look up
     * @return the nth next player
     */
    public Player getNnthNextPlayer(int n) {

        if (playerTurnOrder.isEmpty())
            return null;

        return playerTurnOrder.get(n % playerTurnOrder.size());
    }


    public boolean isActivePlayer(Player p) {

        if (p == null)
            return false;

        return this.playerTurnOrder.contains(p);
    }

    public LinkedList<Player> getActivePlayers() {
        return new LinkedList<>(playerTurnOrder);
    }

    /**
     * countPlayers
     * @return The number of players in the game
     */
    public int countPlayers() {
        return players.size();
    }

    /**
     * countRemainingPlayers
     * @return The number of players still in the game
     */
    public int countRemainingPlayers() {
        return playerTurnOrder.size();
    }

    /**
     * countCardsLeftInDeck
     * @return The number of cards left in the deck
     */
    public int countCardsLeftInDeck() {
        return deck.size();
    }


    /**
     * getStage
     * @return The stage of the game (setup, mid-game, or post-game)
     */
    public GameStage getStage() {
        return stage;
    }

    /**
     * getDeck
     * @param key
     * @return If master key is provided then the actual deck, otherwise a copy of unknown cards
     */
    public Stack<Card> getDeck(DataKey key) {

        if (key.isMasterKey())
            return deck;

        Stack<Card> deckCopy = new Stack<>();
        for (Card c : deck)
            deckCopy.add(Card.Unknown);

        return deckCopy;
    }


    /***********************************
     ** Constructors & Initialization **
     ***********************************/

    public GameState() {
        players = new ArrayList<>();
        deck = new Stack<>();
        discard = new ArrayList<>();
        turnHistory = new Stack<>();
        playerTurnOrder = new LinkedList<>();
    }

    /**
     * addPlayer
     *  Adds a player to the game if the game has not already started or if there is room for new players
     *
     * @return player id assigned to the new player
     */
    public Player addPlayer(String name, int id) throws InvalidStageException, PlayerCountExeption {

        if (stage != GameStage.PREGAME)
            throw new InvalidStageException();

        if (players.size() >= Rules.MAX_PLAYERS)
            throw new PlayerCountExeption();

        Player newPlayer = new Player(name, id);
        players.add(newPlayer);

        return newPlayer;
    }

    /**
     * setupDeck
     *  Sets up the deck for a new game. Adds cards, shuffles, and discards
     * @throws InvalidStageException
     */
    public void setupDeck() throws InvalidStageException {

        if (stage != GameStage.PREGAME)
            throw new InvalidStageException();

        // Create the deck, do not shuffle or discard yet
        addToDeck(Rules.NUMBER_OF_GUARDS, Card.Guard);
        addToDeck(Rules.NUMBER_OF_PRIESTS, Card.Priest);
        addToDeck(Rules.NUMBER_OF_BARONS, Card.Baron);
        addToDeck(Rules.NUMBER_OF_HANDMAIDS, Card.Handmaid);
        addToDeck(Rules.NUMBER_OF_PRINCES, Card.Prince);
        addToDeck(Rules.NUMBER_OF_KINGS, Card.King);
        addToDeck(Rules.NUMBER_OF_COUNTESSES, Card.Countess);
        addToDeck(Rules.NUMBER_OF_PRINCESSES, Card.Princess);

        //Shuffle the deck
        Collections.shuffle(deck);

        // Pull cards out to the discard pile
        discard.add(deck.pop());

        // If there are only two players then pull out three more cards
        if (players.size() == 2)
            for (int i = 0; i < 3; ++i)
                discard.add(deck.pop());
    }

    /**
     * addToDeck
     *  Helper function to add a given number of cards of a given type to the deck
     * @param quantity
     * @param type
     */
    private void addToDeck(int quantity, Card type) {
        for (int i = 0; i < quantity; ++i)
            deck.add(type);
    }

    /*************
     ** Actions **
     *************/

    /**
     * startGame
     *  Starts the game if there are enough players and it is not already started
     *
     * @throws InvalidStageException
     * @throws PlayerCountExeption
     */
    public void startGame(DataKey key) throws InvalidStageException, PlayerCountExeption {

        if (stage != GameStage.PREGAME)
            throw new InvalidStageException();

        if (players.size() < Rules.MIN_PLAYERS)
            throw new PlayerCountExeption();

        // No need to check if master key because brains can't do anything yet


        // Add players to the turn order. #TODO: Support winner of last game going first
        playerTurnOrder = new LinkedList<>();
        for (Player p : this.players) {
            playerTurnOrder.add(p);
        }

        setupDeck();

        for (Player p: this.players) {
            Card drawnCard = deck.pop();
            ArrayList<Card> hand = p.getHand(key);
            hand.add(drawnCard);
        }

        stage = GameStage.GAME_STARTED;
    }

    public void eliminatePlayer(DataKey key, Player p) {

        if (!key.isMasterKey())
            return;

        this.playerTurnOrder.remove(p);
    }

    /**
     * nextTurn
     *  Increments the turn counter and changes the current player, but only if the master key is provided
     * @param key
     */
    public void nextTurn(DataKey key) {

        if (!key.isMasterKey())
            return;

        ++turn;

        Player tmp = playerTurnOrder.pop();
        playerTurnOrder.add(tmp);
    }



}


