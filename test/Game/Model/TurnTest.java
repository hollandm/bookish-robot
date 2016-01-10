package Game.Model;

import Game.Exceptions.NoTargetException;
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
        Player p = new Player("Test Player", 123);
        p.addCardToHand(masterKey, TEST_PLAYER_CARD_0);
        if (isActingPlayer)
            p.addCardToHand(masterKey, TEST_PLAYER_CARD_1);
        return p;
    }

    @Test
    public void testGetTurnCount() throws Exception {


        Player p = makePlayer(true);

        Turn t = new Turn(masterKey, p, 0);
        assert t.getTurnCount() == 0;

        t = new Turn(masterKey, p, 1);
        assert t.getTurnCount() == 1;

    }

    @Test
    public void testGetActingPlayer() throws Exception {

        Player p = makePlayer(true);

        Turn t = new Turn(masterKey, p, 0);
        assert t.getActingPlayer() == p;

    }

    @Test
    public void testGetDrawnCard() throws Exception {

        Player p1 = makePlayer(true);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, 0);
        assert t.getDrawnCard(masterKey) == TEST_PLAYER_CARD_1;
        assert t.getDrawnCard(p1Key) == TEST_PLAYER_CARD_1;
        assert t.getDrawnCard(p2Key) == Card.Unknown;

    }

    @Test
    public void testGetPlayedCard() throws Exception {

        Player p1 = makePlayer(true);

        Turn t = new Turn(masterKey, p1, 0);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
    }

    @Test
    public void testGetActingPlayerRemainingCard() throws Exception {

        Player p1 = new Player("Test Player", 1);
        p1.addCardToHand(masterKey, Card.Countess);
        p1.addCardToHand(masterKey, Card.Priest);
        DataKey p1Key = keyFactory.getTestKey(p1);

        Player p2 = new Player("Test Player", 2);
        Card p2Card = Card.Priest;
        p2.addCardToHand(masterKey, p2Card);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Player p3 = new Player("Test Player", 3);
        Card p3Card = Card.Princess;
        p3.addCardToHand(masterKey, p3Card);
        DataKey p3Key = keyFactory.getTestKey(p3);

        Turn t = new Turn(masterKey, p1, 0);
        assert t.getActingPlayerRemainingCard(masterKey) == Card.Priest;
        assert t.getActingPlayerRemainingCard(p1Key) == Card.Priest;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Unknown;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

        t.finalize(masterKey);
        assert t.getActingPlayerRemainingCard(masterKey) == Card.Priest;
        assert t.getActingPlayerRemainingCard(p1Key) == Card.Priest;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Unknown;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

        p1.getHand(masterKey).clear();
        p1.addCardToHand(masterKey, Card.Baron);
        p1.addCardToHand(masterKey, Card.King);

        t = new Turn(masterKey, p1, 1);
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

        t = new Turn(masterKey, p1, 1);
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

        p2.setProtected(masterKey, true);

        t = new Turn(masterKey, p1, 1);
        t.setTargetPlayer(masterKey, p2);
        t.swapPlayedAndRemainingCards(p1Key);

        assert t.getActingPlayerRemainingCard(masterKey) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p1Key) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Unknown;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

        t.finalize(masterKey);
        assert t.getActingPlayerRemainingCard(masterKey) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p1Key) == Card.Baron;
        assert t.getActingPlayerRemainingCard(p2Key) == Card.Unknown;
        assert t.getActingPlayerRemainingCard(p3Key) == Card.Unknown;

    }

    @Test
    public void testTargetPlayer() throws Exception {

        Player p1 = makePlayer(true);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(p2Key, p2);
        assert t.getTargetPlayer() == null;

        t.setTargetPlayer(p1Key, p2);
        assert t.getTargetPlayer() == p2;

        t.setTargetPlayer(masterKey, p1);
        assert t.getTargetPlayer() == p1;

        t.finalize(masterKey);

        t.setTargetPlayer(p2Key, p2);
        assert t.getTargetPlayer() == null;

        t.setTargetPlayer(p1Key, p2);
        assert t.getTargetPlayer() == null;

        t.setTargetPlayer(masterKey, p2);
        assert t.getTargetPlayer() == p2;

        t = new Turn(masterKey, p1, 0);
        t.swapPlayedAndRemainingCards(p1Key);

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

        Turn t = new Turn(masterKey, p1, 0);
        assert !t.wasTargetPlayerProtected();

        t.setTargetPlayer(p1Key, p2);
        assert !t.wasTargetPlayerProtected();

        p2.setProtected(masterKey, true);
        t.setTargetPlayer(p1Key, p2);
        assert t.wasTargetPlayerProtected();

    }

    @Test
    public void testGetTargetPlayersCard() throws Exception {

        Player p1 = new Player("Test Player", 1);
        p1.addCardToHand(masterKey, Card.Countess);
        p1.addCardToHand(masterKey, Card.Priest);
        DataKey p1Key = keyFactory.getTestKey(p1);

        Player p2 = new Player("Test Player", 2);
        Card p2Card = Card.Priest;
        p2.addCardToHand(masterKey, p2Card);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Player p3 = new Player("Test Player", 3);
        Card p3Card = Card.Princess;
        p3.addCardToHand(masterKey, p3Card);
        DataKey p3Key = keyFactory.getTestKey(p3);


        // Without being finalized
        Turn t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);

        assert t.getTargetPlayersCard(masterKey) == null;
        assert t.getTargetPlayersCard(p1Key) == null;
        assert t.getTargetPlayersCard(p2Key) == null;
        assert t.getTargetPlayersCard(p3Key) == null;

        // With no target set (finalize should strip the target since playing a non targeting card)
        t.finalize(masterKey);
        assert t.getTargetPlayer() == null;

        assert t.getTargetPlayersCard(masterKey) == null;
        assert t.getTargetPlayersCard(p1Key) == null;
        assert t.getTargetPlayersCard(p2Key) == null;
        assert t.getTargetPlayersCard(p3Key) == null;

        // reset
        p1.discardCard(masterKey, Card.Countess);
        p1.addCardToHand(masterKey, Card.Guard);

        //With a Priest
        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == p2Card;
        assert t.getTargetPlayersCard(p1Key) == p2Card;
        assert t.getTargetPlayersCard(p2Key) == p2Card;
        assert t.getTargetPlayersCard(p3Key) == Card.Unknown;

        // With a Protected Target
        p2.setProtected(masterKey, true);

        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == p2Card;
        assert t.getTargetPlayersCard(p1Key) == Card.Unknown;
        assert t.getTargetPlayersCard(p2Key) == p2Card;
        assert t.getTargetPlayersCard(p3Key) == Card.Unknown;

        // reset
        p2.setProtected(masterKey, false);
        p1.discardCard(masterKey, Card.Priest);
        p1.addCardToHand(masterKey, Card.Baron);

        //With a Guard, no guess
        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == p2Card;
        assert t.getTargetPlayersCard(p1Key) == Card.Unknown;
        assert t.getTargetPlayersCard(p2Key) == p2Card;
        assert t.getTargetPlayersCard(p3Key) == Card.Unknown;

        //With a Guard, wrong guess
        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);
        t.setGuessedCard(masterKey, Card.Princess);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == p2Card;
        assert t.getTargetPlayersCard(p1Key) == Card.Unknown;
        assert t.getTargetPlayersCard(p2Key) == p2Card;
        assert t.getTargetPlayersCard(p3Key) == Card.Unknown;

        //With a Guard, correct guess
        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);
        t.setGuessedCard(masterKey, p2Card);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == p2Card;
        assert t.getTargetPlayersCard(p1Key) == p2Card;
        assert t.getTargetPlayersCard(p2Key) == p2Card;
        assert t.getTargetPlayersCard(p3Key) == p2Card;

        // reset
        p1.discardCard(masterKey, Card.Guard);
        p1.addCardToHand(masterKey, Card.Prince);


        //With a Baron, target losing
        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == p2Card;
        assert t.getTargetPlayersCard(p1Key) == p2Card;
        assert t.getTargetPlayersCard(p2Key) == p2Card;
        assert t.getTargetPlayersCard(p3Key) == p2Card;

        //With a Baron, target winning
        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p3);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == p3Card;
        assert t.getTargetPlayersCard(p1Key) == p3Card;
        assert t.getTargetPlayersCard(p2Key) == Card.Unknown;
        assert t.getTargetPlayersCard(p3Key) == p3Card;

        //With a Baron, tied
        p3.discardCard(masterKey, p3Card);
        p3Card = Card.Prince;
        p3.addCardToHand(masterKey, p3Card);
        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p3);
        t.finalize(masterKey);

        assert t.getTargetPlayersCard(masterKey) == p3Card;
        assert t.getTargetPlayersCard(p1Key) == p3Card;
        assert t.getTargetPlayersCard(p2Key) == Card.Unknown;
        assert t.getTargetPlayersCard(p3Key) == p3Card;

    }

    @Test
    public void testGuessedCard() throws Exception {
        Player p1 = new Player("Test Player", 1);
        p1.addCardToHand(masterKey, Card.Priest);
        p1.addCardToHand(masterKey, Card.Priest);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, 0);
        assert t.getGuessedCard() == null;

        t.setGuessedCard(p2Key, Card.Baron);
        assert t.getGuessedCard() == null;

        t.setGuessedCard(p1Key, Card.Baron);
        assert t.getGuessedCard() == Card.Baron;

        t.setGuessedCard(masterKey, Card.Priest);
        assert t.getGuessedCard() == Card.Priest;

        t.finalize(masterKey);
        assert t.getGuessedCard() == null;

        p1.discardCard(masterKey, Card.Priest);
        p1.discardCard(masterKey, Card.Priest);
        p1.addCardToHand(masterKey, Card.Guard);
        p1.addCardToHand(masterKey, Card.Guard);

        t = new Turn(masterKey, p1, 0);
        assert t.getGuessedCard() == null;

        t.setGuessedCard(p2Key, Card.Baron);
        assert t.getGuessedCard() == null;

        t.setGuessedCard(p1Key, Card.Baron);
        assert t.getGuessedCard() == Card.Baron;

        t.setGuessedCard(masterKey, Card.Priest);
        assert t.getGuessedCard() == Card.Priest;

        t.finalize(masterKey);
        assert t.getGuessedCard() == Card.Priest;

    }

    @Test
    public void testTargetDrawnCard() throws Exception {
        Player p1 = new Player("Test Player", 1);
        p1.addCardToHand(masterKey, Card.Prince);
        p1.addCardToHand(masterKey, Card.Priest);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, 0);
        assert t.getTargetDrawnCard(masterKey) == null;
        assert t.getTargetDrawnCard(p1Key) == null;
        assert t.getTargetDrawnCard(p2Key) == null;

        try {
            t.setTargetPlayerDrawnCard(masterKey, Card.Countess);
            assert false;
        } catch (NoTargetException e) {}

        t.setTargetPlayer(masterKey, p2);

        t.setTargetPlayerDrawnCard(p1Key, Card.Countess);
        assert t.getTargetDrawnCard(masterKey) == null;
        assert t.getTargetDrawnCard(p1Key) == null;
        assert t.getTargetDrawnCard(p2Key) == null;

        t.setTargetPlayerDrawnCard(p2Key, Card.Countess);
        assert t.getTargetDrawnCard(masterKey) == null;
        assert t.getTargetDrawnCard(p1Key) == null;
        assert t.getTargetDrawnCard(p2Key) == null;

        t.setTargetPlayerDrawnCard(masterKey, Card.Countess);
        assert t.getTargetDrawnCard(masterKey) == Card.Countess;
        assert t.getTargetDrawnCard(p1Key) == Card.Unknown;
        assert t.getTargetDrawnCard(p2Key) == Card.Countess;

    }

    @Test
    public void testFinalize() throws Exception {

        Player p1 = new Player("Test Player", 1);
        p1.addCardToHand(masterKey, Card.Countess);
        p1.addCardToHand(masterKey, Card.Handmaid);
        DataKey p1Key = keyFactory.getTestKey(p1);

        Player p2 = new Player("Test Player", 2);
        Card p2Card = Card.Priest;
        p2.addCardToHand(masterKey, p2Card);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);
        t.setGuessedCard(masterKey, Card.Baron);

        assert t.getTargetPlayer() == p2;
        assert t.getGuessedCard() == Card.Baron;

        t.finalize(p1Key);
        assert t.getTargetPlayer() == p2;
        assert t.getGuessedCard() == Card.Baron;

        t.finalize(masterKey);
        assert t.getTargetPlayer() == null;
        assert t.getGuessedCard() == null;
    }

    @Test
    public void testSwapPlayedAndRemainingCards() throws Exception {
        Player p1 = makePlayer(true);
        DataKey p1Key = keyFactory.getTestKey(p1);
        Player p2 = makePlayer(false);
        DataKey p2Key = keyFactory.getTestKey(p2);

        Turn t = new Turn(masterKey, p1, 0);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;

        t.swapPlayedAndRemainingCards(p2Key);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;

        t.swapPlayedAndRemainingCards(p1Key);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_1;
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_0;

        t.swapPlayedAndRemainingCards(masterKey);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;

        t.finalize(masterKey);

        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;

        t.swapPlayedAndRemainingCards(p2Key);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;

        t.swapPlayedAndRemainingCards(p1Key);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;

        t.swapPlayedAndRemainingCards(masterKey);
        assert t.getPlayedCard() == TEST_PLAYER_CARD_0;
        assert t.getActingPlayerRemainingCard(masterKey) == TEST_PLAYER_CARD_1;

    }

    @Test
    public void testIsValidTurn() throws Exception {

        GameState state = new GameState();
        state.addPlayer("Test Player", 1);
        state.addPlayer("Test Player", 2);
        state.addPlayer("Test Player", 3);

        state.startGame(masterKey);

        Player p1 = state.getCurrentPlayer();
        Player p2 = state.getNnthNextPlayer(1);
        Player p3 = state.getNnthNextPlayer(2);
        assert p1 != p3 && p2 != p3;
        state.eliminatePlayer(masterKey, p3);

        p1.discardCard(masterKey, p1.getHand(masterKey).get(0));
        p1.addCardToHand(masterKey, Card.Handmaid);
        p1.addCardToHand(masterKey, Card.Handmaid);

        Turn t = new Turn(masterKey, p1, 0);
        assert t.isValidTurn(state);

        p1.discardCard(masterKey, Card.Handmaid);
        p1.discardCard(masterKey, Card.Handmaid);
        p1.addCardToHand(masterKey, Card.Prince);
        p1.addCardToHand(masterKey, Card.Prince);

        t = new Turn(masterKey, p1, 0);
        // No Target Set
        assert !t.isValidTurn(state);

        // Valid Target set
        t.setTargetPlayer(masterKey, p2);
        assert t.isValidTurn(state);

        // Already eliminated target set (invalid)
        t.setTargetPlayer(masterKey, p3);
        assert !t.isValidTurn(state);

        p1.discardCard(masterKey, Card.Prince);
        p1.discardCard(masterKey, Card.Prince);
        p1.addCardToHand(masterKey, Card.Guard);
        p1.addCardToHand(masterKey, Card.Guard);

        t = new Turn(masterKey, p1, 0);
        t.setTargetPlayer(masterKey, p2);

        // No guess for guard
        assert !t.isValidTurn(state);

        // Guessed guard (invalid)
        t.setGuessedCard(masterKey, Card.Guard);
        assert !t.isValidTurn(state);

        // Guessed Priest (valid)
        t.setGuessedCard(masterKey, Card.Priest);
        assert t.isValidTurn(state);

    }

    @Test
    public void testGetEliminatedPlayer() throws Exception {
        // TODO
    }

}