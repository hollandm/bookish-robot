import Game.Exceptions.HandTooBigException;
import Game.Model.Card;
import Game.Model.Player;
import org.junit.Before;
import org.junit.Test;

import Game.Game.DataKey;
import Game.Game;

import java.util.ArrayList;


public class PlayerTest {



    static final int TEST_PLAYER_1_ID = 123;
    static final String TEST_PLAYER_1_NAME = "Test Player 1";


    static final int TEST_PLAYER_2_ID = 789;
    static final String TEST_PLAYER_2_NAME = "Test Player 2";


    Game keyFactory;
    DataKey masterKey;

    @Before
    public void setUp() throws Exception {

        Game.setAllowTesting(true);
        keyFactory = new Game();
        masterKey = keyFactory.getTestKey(null);

    }

    @Test
    public void testGetPlayerID() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);

        int id = testPlayer1.getPlayerID();
        assert id == TEST_PLAYER_1_ID;

        id = testPlayer2.getPlayerID();
        assert id == TEST_PLAYER_2_ID;

    }

    @Test
    public void testGetPlayerName() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);

        String name = testPlayer1.getPlayerName();
        assert name.equals(TEST_PLAYER_1_NAME);

        name = testPlayer2.getPlayerName();
        assert name.equals(TEST_PLAYER_2_NAME);
    }

    @Test
    public void testToString() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);

        assert testPlayer1.toString().equals(TEST_PLAYER_1_NAME + " " + TEST_PLAYER_1_ID);
        assert testPlayer2.toString().equals(TEST_PLAYER_2_NAME + " " + TEST_PLAYER_2_ID);

    }

    @Test
    public void testGetHand() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        DataKey p1Key = keyFactory.getTestKey(testPlayer1);

        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);
        DataKey p2Key = keyFactory.getTestKey(testPlayer2);

        ArrayList<Card> actualHand;
        ArrayList<Card> handCopy;
        ArrayList<Card> unknownHand;

        actualHand = testPlayer1.getHand(masterKey);
        handCopy = testPlayer1.getHand(p1Key);
        unknownHand = testPlayer1.getHand(p2Key);

        assert actualHand.size() == 0;
        assert handCopy.size() == 0;
        assert unknownHand.size() == 0;

        assert handCopy != actualHand;
        assert handCopy != unknownHand;
        assert actualHand != unknownHand;

        assert actualHand == testPlayer1.getHand(masterKey);
        assert handCopy != testPlayer1.getHand(p1Key);
        assert unknownHand != testPlayer1.getHand(p2Key);

        actualHand.add(Card.Guard);

        assert actualHand.size() == 1;
        assert handCopy.size() == 0;
        assert unknownHand.size() == 0;

        handCopy = testPlayer1.getHand(p1Key);
        unknownHand = testPlayer1.getHand(p2Key);

        assert actualHand.size() == 1;
        assert handCopy.size() == 1;
        assert unknownHand.size() == 1;

        assert actualHand.get(0) == Card.Guard;
        assert handCopy.get(0) == Card.Guard;
        assert unknownHand.get(0) == Card.Unknown;

        assert testPlayer2.getHand(masterKey).size() == 0;
        assert testPlayer2.getHand(p2Key).size() == 0;

    }

    @Test
    public void testAddCardToHand() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        DataKey p1Key = keyFactory.getTestKey(testPlayer1);

        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);
        DataKey p2Key = keyFactory.getTestKey(testPlayer2);

        ArrayList<Card> actualHand = testPlayer1.getHand(masterKey);
        ArrayList<Card> handCopy;
        ArrayList<Card> unknownHand;

        testPlayer1.addCardToHand(p1Key, Card.Baron);

        handCopy = testPlayer1.getHand(p1Key);
        unknownHand = testPlayer1.getHand(p2Key);

        assert actualHand.size() == 0;
        assert handCopy.size() == 0;
        assert unknownHand.size() == 0;

        testPlayer1.addCardToHand(p2Key, Card.Baron);

        handCopy = testPlayer1.getHand(p1Key);
        unknownHand = testPlayer1.getHand(p2Key);

        assert actualHand.size() == 0;
        assert handCopy.size() == 0;
        assert unknownHand.size() == 0;

        testPlayer1.addCardToHand(masterKey, Card.Baron);

        handCopy = testPlayer1.getHand(p1Key);
        unknownHand = testPlayer1.getHand(p2Key);

        assert actualHand.size() == 1;
        assert handCopy.size() == 1;
        assert unknownHand.size() == 1;

        assert actualHand.get(0) == Card.Baron;
        assert handCopy.get(0) == Card.Baron;
        assert unknownHand.get(0) == Card.Unknown;


        testPlayer1.addCardToHand(masterKey, Card.Guard);
        assert actualHand.size() == 2;

        try {
            testPlayer1.addCardToHand(masterKey, Card.Guard);
            assert false;
        } catch (HandTooBigException e) {
            assert actualHand.size() == 2;
        }
    }

    @Test
    public void testHasCard() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        DataKey p1Key = keyFactory.getTestKey(testPlayer1);

        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);
        DataKey p2Key = keyFactory.getTestKey(testPlayer2);

        assert !testPlayer1.hasCard(masterKey, Card.Baron);
        assert !testPlayer1.hasCard(p1Key, Card.Baron);
        assert !testPlayer1.hasCard(p2Key, Card.Baron);

        testPlayer1.addCardToHand(masterKey, Card.Baron);

        assert testPlayer1.hasCard(masterKey, Card.Baron);
        assert testPlayer1.hasCard(p1Key, Card.Baron);
        assert !testPlayer1.hasCard(p2Key, Card.Baron);

        testPlayer1.addCardToHand(masterKey, Card.Priest);

        assert testPlayer1.hasCard(masterKey, Card.Baron);
        assert testPlayer1.hasCard(masterKey, Card.Priest);
        assert !testPlayer1.hasCard(masterKey, Card.Princess);

    }

    @Test
    public void testWins() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        DataKey p1Key = keyFactory.getTestKey(testPlayer1);

        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);
        DataKey p2Key = keyFactory.getTestKey(testPlayer2);

        assert testPlayer1.getWins() == 0;

        testPlayer1.addWin(masterKey);
        assert testPlayer1.getWins() == 1;

        testPlayer1.addWin(masterKey);
        assert testPlayer1.getWins() == 2;

        testPlayer1.addWin(p1Key);
        assert testPlayer1.getWins() == 2;

        testPlayer1.addWin(p2Key);
        assert testPlayer1.getWins() == 2;

    }

    @Test
    public void testHandmaidenProtection() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        DataKey p1Key = keyFactory.getTestKey(testPlayer1);

        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);
        DataKey p2Key = keyFactory.getTestKey(testPlayer2);

        assert !testPlayer1.isHandmaidenProtected();

        testPlayer1.setHandmaidProtected(p1Key, true);
        assert !testPlayer1.isHandmaidenProtected();

        testPlayer1.setHandmaidProtected(p2Key, true);
        assert !testPlayer1.isHandmaidenProtected();

        testPlayer1.setHandmaidProtected(masterKey, true);
        assert testPlayer1.isHandmaidenProtected();

        testPlayer1.setHandmaidProtected(p1Key, false);
        assert testPlayer1.isHandmaidenProtected();

        testPlayer1.setHandmaidProtected(p2Key, false);
        assert testPlayer1.isHandmaidenProtected();

        testPlayer1.setHandmaidProtected(masterKey, false);
        assert !testPlayer1.isHandmaidenProtected();

    }



    @Test
    public void testDiscardCard() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        DataKey p1Key = keyFactory.getTestKey(testPlayer1);

        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);
        DataKey p2Key = keyFactory.getTestKey(testPlayer2);

        ArrayList<Card> actualHand = testPlayer1.getHand(masterKey);
        assert actualHand.size() == 0;
        assert !testPlayer1.hasCard(masterKey, Card.Guard);

        testPlayer1.addCardToHand(masterKey, Card.Guard);

        assert actualHand.size() == 1;
        assert testPlayer1.hasCard(masterKey, Card.Guard);

        testPlayer1.discardCard(p1Key, Card.Guard);
        assert actualHand.size() == 1;
        assert testPlayer1.hasCard(masterKey, Card.Guard);

        testPlayer1.discardCard(p2Key, Card.Guard);
        assert actualHand.size() == 1;
        assert testPlayer1.hasCard(masterKey, Card.Guard);

        testPlayer1.discardCard(masterKey, Card.Guard);
        assert actualHand.size() == 0;
        assert !testPlayer1.hasCard(masterKey, Card.Guard);

        testPlayer1.addCardToHand(masterKey, Card.Guard);
        testPlayer1.addCardToHand(masterKey, Card.Guard);
        assert actualHand.size() == 2;
        assert testPlayer1.hasCard(masterKey, Card.Guard);

        testPlayer1.discardCard(masterKey, Card.Guard);
        assert actualHand.size() == 1;
        assert testPlayer1.hasCard(masterKey, Card.Guard);

        testPlayer1.discardCard(masterKey, Card.Guard);
        assert actualHand.size() == 0;
        assert !testPlayer1.hasCard(masterKey, Card.Guard);

        testPlayer1.discardCard(masterKey, Card.Guard);
        assert actualHand.size() == 0;
        assert !testPlayer1.hasCard(masterKey, Card.Guard);

    }



    @Test
    public void testGetPlayedCards() throws Exception {

        Player testPlayer1 = new Player(TEST_PLAYER_1_NAME, TEST_PLAYER_1_ID);
        DataKey p1Key = keyFactory.getTestKey(testPlayer1);

        Player testPlayer2 = new Player(TEST_PLAYER_2_NAME, TEST_PLAYER_2_ID);
        DataKey p2Key = keyFactory.getTestKey(testPlayer2);

        ArrayList<Card> discard = testPlayer1.getPlayedCards(masterKey);
        assert discard == testPlayer1.getPlayedCards(masterKey);

        testPlayer1.addCardToHand(masterKey, Card.Baron);
        testPlayer1.discardCard(masterKey, Card.Baron);

        assert discard.size() == 1;
        assert discard.get(0) == Card.Baron;


        testPlayer1.addCardToHand(masterKey, Card.Priest);
        testPlayer1.discardCard(masterKey, Card.Priest);

        assert discard.size() == 2;
        assert discard.get(0) == Card.Baron;
        assert discard.get(1) == Card.Priest;


        testPlayer1.discardCard(masterKey, Card.Princess);
        assert discard.size() == 3;
        assert discard.get(0) == Card.Baron;
        assert discard.get(1) == Card.Priest;
        assert discard.get(2) == Card.Princess;


    }
}