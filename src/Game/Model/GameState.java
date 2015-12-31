package Game.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Stack;

import Game.Game.DataKey;
import Game.Exceptions.*;


public class GameState {

    //gameStarted: tracks if the game has started, this can help prevent the game state from being invalidated
    private GameStage stage = GameStage.PREGAME;

    // turn: The number of individual players turns that have occurred this game including this one
    private int turn = 0;

    // players: The list of players in the game
    private ArrayList<Player> players;

    // deck: The cards in the deck
    private Stack<Card> deck;

    // discard: the cards that were set aside at the begining of the game
    private ArrayList<Card> discard;

    Stack<Turn> turnHistory;

    /*************
     ** Getters **
     *************/

    /**
     * getCurrentTurn
     * @return The number of individual players turns that have occurred this game including this one
     */
    public int getCurrentTurn() {
        return turn;
    }

    /**
     * getCurrentPlayersID
     * @return The id of the current player
     */
    public int getCurrentPlayersID() {
        return turn % players.size();
    }

    /**
     * getPlayerByID
     * @param id
     * @return the player with the given id
     */
    public Player getPlayerByID(int id) {
        return players.get(id);
    }

    /**
     * countPlayers
     * @return The number of players in the game
     */
    public int countPlayers() {
        return players.size();
    }

    /**
     * countCardsLeftInDeck
     * @return The number of cards left in the deck
     */
    public int countCardsLeftInDeck() {
        return deck.size();
    }


    public GameStage getStage() {
        return stage;
    }


    /***********************************
     ** Constructors & Initialization **
     ***********************************/

    public GameState() {
        players = new ArrayList<>();
        deck = new Stack<>();
        discard = new ArrayList<>();
        turnHistory = new Stack<>();
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

        setupDeck();

        for (Player p: this.players) {
            Card drawnCard = deck.pop();
            ArrayList<Card> hand = p.getHand(key);
            hand.add(drawnCard);
        }

        stage = GameStage.GAME_STARTED;
    }

}


