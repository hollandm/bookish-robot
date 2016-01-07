package Game.Model;

import org.junit.Before;
import org.junit.Test;
import Game.Game.DataKey;
import Game.Game;


public class TurnTest {

    Game keyFactory;
    DataKey masterKey;

    @Before
    public void setUp() throws Exception {

        Game.setAllowTesting(true);
        keyFactory = new Game();
        masterKey = keyFactory.getTestKey(null);

    }

    private Player makePlayer() throws Exception {
        Player p = new Player("Test Player 1", 123);
        p.addCardToHand(masterKey, Card.Guard);
        p.addCardToHand(masterKey, Card.Baron);
        return p;
    }

    @Test
    public void testGetTurnCount() throws Exception {


        Player p = makePlayer();

        Turn t = new Turn(masterKey, p, Card.Guard, 0);
        assert t.getTurnCount() == 0;

        t = new Turn(masterKey, p, Card.Guard, 1);
        assert t.getTurnCount() == 1;

    }

    @Test
    public void testGetActingPlayer() throws Exception {

        Player p = makePlayer();

        Turn t = new Turn(masterKey, p, Card.Guard, 0);
        assert t.getActingPlayer() == p;

    }

    @Test
    public void testGetDrawnCard() throws Exception {

        Player p1 = makePlayer();
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer();
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, Card.Guard, 0);
        assert t.getDrawnCard(masterKey) == Card.Guard;
        assert t.getDrawnCard(p1Key) == Card.Guard;
        assert t.getDrawnCard(p2Key) == Card.Unknown;

    }

    @Test
    public void testGetPlayedCard() throws Exception {
        // TODO
    }

    @Test
    public void testGetActingPlayerRemainingCard() throws Exception {
        // TODO
    }

    @Test
    public void testGetTargetPlayer() throws Exception {
        // TODO
    }

    @Test
    public void testSetTargetPlayer() throws Exception {
        // TODO
    }

    @Test
    public void testWasTargetPlayerHandmaidProtected() throws Exception {
        // TODO
    }

    @Test
    public void testGetTargetPlayersCard() throws Exception {
        // TODO
    }

    @Test
    public void testSetTargetPlayersCard() throws Exception {
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