package Game.Model;

import org.junit.Before;
import org.junit.Test;
import Game.Game.DataKey;
import Game.Game;


public class TurnTest {

    Card TEST_PLAYER_CARD_0 = Card.Handmaid;
    Card TEST_PLAYER_CARD_1 = Card.Priest;

    Game keyFactory;
    DataKey masterKey;

    @Before
    public void setUp() throws Exception {

        Game.setAllowTesting(true);
        keyFactory = new Game();
        masterKey = keyFactory.getTestKey(null);

    }

    private Player makePlayer(boolean isActingPlayer) throws Exception {
        Player p = new Player("Test Player 1", 123);
        p.addCardToHand(masterKey, TEST_PLAYER_CARD_0);
        if (isActingPlayer)
            p.addCardToHand(masterKey, TEST_PLAYER_CARD_1);
        return p;
    }

    @Test
    public void testGetTurnCount() throws Exception {


        Player p = makePlayer(true);

        Turn t = new Turn(masterKey, p, TEST_PLAYER_CARD_1, 0);
        assert t.getTurnCount() == 0;

        t = new Turn(masterKey, p, TEST_PLAYER_CARD_1, 1);
        assert t.getTurnCount() == 1;

    }

    @Test
    public void testGetActingPlayer() throws Exception {

        Player p = makePlayer(true);

        Turn t = new Turn(masterKey, p, TEST_PLAYER_CARD_1, 0);
        assert t.getActingPlayer() == p;

    }

    @Test
    public void testGetDrawnCard() throws Exception {

        Player p1 = makePlayer(true);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, TEST_PLAYER_CARD_1, 0);
        assert t.getDrawnCard(masterKey) == TEST_PLAYER_CARD_1;
        assert t.getDrawnCard(p1Key) == TEST_PLAYER_CARD_1;
        assert t.getDrawnCard(p2Key) == Card.Unknown;

    }

    @Test
    public void testGetPlayedCard() throws Exception {

        Player p1 = makePlayer(true);

        Turn t = new Turn(masterKey, p1, TEST_PLAYER_CARD_1, 0);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
    }

    @Test
    public void testGetActingPlayerRemainingCard() throws Exception {

        Player p1 = makePlayer(true);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);
        Player p3 = makePlayer(false);
        DataKey p3Key = keyFactory.getTestKey(p3);

        Turn t = new Turn(masterKey, p1, TEST_PLAYER_CARD_1, 0);
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;
        assert t.getActingPlayerRemainingCard(p1Key) == TEST_PLAYER_CARD_1;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Unknown;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

        t.finalize(masterKey);
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;
        assert t.getActingPlayerRemainingCard(p1Key) == TEST_PLAYER_CARD_1;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Unknown;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

        p1.discardCard(masterKey, TEST_PLAYER_CARD_0);
        p1.discardCard(masterKey, TEST_PLAYER_CARD_1);
        p1.addCardToHand(masterKey, Card.Baron);
        p1.addCardToHand(masterKey, Card.King);

        t = new Turn(masterKey, p1, Card.Baron, 1);
        t.setTargetPlayer(masterKey, p2);

        assert t.getActingPlayerRemainingCard(masterKey) == Card.King;
        assert t.getActingPlayerRemainingCard(p1Key) == Card.King;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Unknown;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

        t.finalize(masterKey);
        assert t.getActingPlayerRemainingCard(masterKey) == Card.King;
        assert t.getActingPlayerRemainingCard(p1Key) == Card.King;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.King;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

        t = new Turn(masterKey, p1, Card.King, 1);
        t.setTargetPlayer(masterKey, p2);
        t.swapPlayedAndRemainingCards(p1Key);

        assert t.getActingPlayerRemainingCard(masterKey) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p1Key) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Unknown;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

        t.finalize(masterKey);
        assert t.getActingPlayerRemainingCard(masterKey) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p1Key) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

    }

    @Test
    public void testTargetPlayer() throws Exception {

        Player p1 = makePlayer(true);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, TEST_PLAYER_CARD_1, 0);
        t.setTargetPlayer(p2Key, p2);
        assert t.getTargetPlayer() == null;

        t.setTargetPlayer(p1Key, p2);
        assert t.getTargetPlayer() == p2;

        t.setTargetPlayer(masterKey, p1);
        assert t.getTargetPlayer() == p1;

        t.finalize(masterKey);

        t.setTargetPlayer(p2Key, p2);
        assert t.getTargetPlayer() == p1;

        t.setTargetPlayer(p1Key, p2);
        assert t.getTargetPlayer() == p1;

        t.setTargetPlayer(masterKey, p2);
        assert t.getTargetPlayer() == p2;
    }

    @Test
    public void testWasTargetPlayerProtected() throws Exception {

        Player p1 = makePlayer(true);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);

        Turn t = new Turn(masterKey, p1, TEST_PLAYER_CARD_1, 0);
        assert !t.wasTargetPlayerProtected();

        t.setTargetPlayer(p1Key, p2);
        assert !t.wasTargetPlayerProtected();

        p2.setProtected(masterKey, true);
        t.setTargetPlayer(p1Key, p2);
        assert t.wasTargetPlayerProtected();

    }

    @Test
    public void testGetTargetPlayersCard() throws Exception {

        Player p1 = makePlayer(true);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, TEST_PLAYER_CARD_1, 0);
        t.setTargetPlayer(masterKey, p2);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == TEST_PLAYER_CARD_0;
        assert t.getTargetPlayersCard(p1Key) == Card.Unknown;
        assert t.getTargetPlayersCard(p2Key) == TEST_PLAYER_CARD_0;

        // TODO
    }

    @Test
    public void testGetGuessedCard() throws Exception {
        // TODO
    }

    @Test
    public void testSetGuessedCard() throws Exception {
        // TODO
    }

    @Test
    public void testGetTargetDrawnCard() throws Exception {
        // TODO
    }

    @Test
    public void testSetTargetPlayerDrawnCard() throws Exception {
        // TODO
    }

    @Test
    public void testFinalize() throws Exception {
        // TODO
    }

    @Test
    public void testGetEliminatedPlayer() throws Exception {
        // TODO
    }

    @Test
    public void testSwapPlayedAndRemainingCards() throws Exception {
        // TODO
    }

    @Test
    public void testIsValidTurn() throws Exception {
        // TODO
    }
}