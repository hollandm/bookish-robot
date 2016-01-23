package Game.Model;

import Game.Exceptions.InvalidStageException;
import Game.Exceptions.PlayerCountException;
import Game.Game;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Queue;
import java.util.Stack;

/**
 * Created by mbhol on 1/13/2016.
 */
public class GameStateTest {

    Game keyFactory;
    Game.DataKey masterKey;

    @Before
    public void setUp() throws Exception {

        Game.setAllowTesting(true);
        keyFactory = new Game();
        masterKey = keyFactory.getTestKey(null);

    }

    @Test
    public void testGetCurrentTurn() throws Exception {



    }

    @Test
    public void testGetPlayerByID() throws Exception {

    }

    @Test
    public void testGetCurrentPlayer() throws Exception {

    }

    @Test
    public void testGetNthNextPlayer() throws Exception {

    }

    @Test
    public void testIsActivePlayer() throws Exception {

    }

    @Test
    public void testGetActivePlayers() throws Exception {

    }

    @Test
    public void testCountPlayers() throws Exception {

    }

    @Test
    public void testCountRemainingPlayers() throws Exception {

    }

    @Test
    public void testCountCardsLeftInDeck() throws Exception {

    }

    @Test
    public void testGetStage() throws Exception {

    }

    @Test
    public void testGetDeck() throws Exception {

    }

    @Test
    public void testGetTurnHistory() throws Exception {

    }

    @Test
    public void testPlayers() throws Exception {

        Game.DataKey playerKey = keyFactory.getTestKey(new Player("", 0));

        GameState gs = new GameState();

        assert gs.getPlayers(masterKey) == gs.getPlayers(masterKey);
        assert gs.getPlayers(masterKey) != gs.getPlayers(playerKey);
        assert gs.getPlayers(playerKey) != gs.getPlayers(playerKey);

        ArrayList<Player> players = gs.getPlayers(masterKey);
        assert players.size() == 0;

        try {
            gs.addPlayer("Test Player", 1);
            assert players.size() == 1;

            gs.addPlayer("Test Player", 2);
            assert players.size() == 2;

            gs.addPlayer("Test Plasdsayer", 972);
            assert players.size() == 3;

            gs.addPlayer("Test Player", 4);
            assert players.size() == 4;

            gs.addPlayer("Test Player", 5);
            assert players.size() == 5;

            gs.addPlayer("Test Player", 6);
            assert players.size() == 6;
        } catch (Exception e) {
            assert false;
        }

        try {
            gs.addPlayer("Test Player", 7);
            assert false;
        } catch (PlayerCountException e) {
            assert true;
        }

        gs = new GameState();
        players = gs.getPlayers(masterKey);
        try {
            gs.addPlayer("Test Player", 1);
            assert players.size() == 1;

            gs.addPlayer("Test Player", 2);
            assert players.size() == 2;

        } catch (Exception e) {
            assert false;
        }

        gs.startGame(masterKey);
        try {
            gs.addPlayer("Test Player", 1);
            assert false;
        } catch (InvalidStageException e) {
            assert true;
        }
    }

    @Test
    public void testStartGame() throws Exception {


        // Start the game with not enough players
        GameState gs = new GameState();

        assert gs.getStage() == GameStage.PREGAME;

        gs.addPlayer("Test Player", 1);
        Player tp1 = gs.getPlayerByID(1);
        Game.DataKey p1k = keyFactory.getTestKey(tp1);

        try {
            gs.startGame(p1k);
            assert false;
        } catch (PlayerCountException e) {}
        assert gs.getStage() == GameStage.PREGAME;

        try {
            gs.startGame(masterKey);
            assert false;
        } catch (PlayerCountException e) {}
        assert gs.getStage() == GameStage.PREGAME;

        gs.addPlayer("Test Player", 2);
        try {
            gs.startGame(p1k);
        } catch (PlayerCountException e) {
            assert false;
        }
        assert gs.getStage() == GameStage.PREGAME;

        try {
            gs.startGame(masterKey);
        } catch (PlayerCountException e) {
            assert false;
        }
        assert gs.getStage() == GameStage.GAME_STARTED;

        gs = new GameState();
        testStartGameHelper_addPlayers(gs, 6);
        assert gs.getStage() == GameStage.PREGAME;

        try {
            gs.startGame(masterKey);
        } catch (PlayerCountException e) {
            assert false;
        }
        assert gs.getStage() == GameStage.GAME_STARTED;


        gs = new GameState();
        assert gs.getStage() == GameStage.PREGAME;
        testStartGameHelper_addPlayers(gs, 6);

        try {
            gs.addPlayer("Test Player", 7);
            assert false;
            gs.startGame(masterKey);
            assert false;
        } catch (PlayerCountException e) {}
        assert gs.getStage() == GameStage.PREGAME;

        gs = new GameState();
        testStartGameHelper_addPlayers(gs, 3);
        gs.startGame(masterKey);

        // Check that all players have been added to the activePlayers array
        Queue<Player> turnOrder = gs.getActivePlayers(masterKey);
        ArrayList<Player> players = gs.getPlayers(masterKey);
        for (Player p : turnOrder)
            assert players.contains(p);
        assert players.size() == turnOrder.size();

        // Deck is randomized
        //This could pass sometimes, but It is very rare so I'm gonna say it is okay
        int remainingTries = 10;
        isTheSameLoop:
        for (; remainingTries > 0; --remainingTries) {
            gs = new GameState();
            GameState gs2 = new GameState();
            testStartGameHelper_addPlayers(gs, 3);
            testStartGameHelper_addPlayers(gs2, 3);
            gs.startGame(masterKey);
            gs2.startGame(masterKey);

            Stack<Card> gsDeck = gs.getDeck(masterKey);
            Stack<Card> gs2Deck = gs2.getDeck(masterKey);

            while (!gsDeck.empty())
                if (gsDeck.pop() != gs2Deck.pop())
                    break isTheSameLoop;
        }
        
        assert remainingTries > 0;

        //Each player is given a card
        for (Player p : players)
            assert p.getHand(masterKey).size() == 1;

    }

    private void testStartGameHelper_addPlayers(GameState gs, int numPlayers) throws Exception {
        for (int i = 0; i < numPlayers; ++i) {
            gs.addPlayer("Test Player", i);
        }

        assert gs.countPlayers() == numPlayers;
    }

    @Test
    public void testEliminatePlayer() throws Exception {

    }

    @Test
    public void testSaveTurn() throws Exception {

    }

    @Test
    public void testNextTurn() throws Exception {

    }

    @Test
    public void testWinner() throws Exception {

        GameState gs = new GameState();
        gs.addPlayer("Test Player", 1);

        Player tp = gs.getPlayerByID(1);
        Game.DataKey pk = keyFactory.getTestKey(tp);

        gs.setWinner(pk, tp);
        assert gs.getWinner() == null;

        gs.setWinner(masterKey, tp);
        assert gs.getWinner() == tp;

    }
}