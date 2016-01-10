package Game.Model;

import Game.Exceptions.NoTargetException;
import Game.Game.DataKey;
import java.util.ArrayList;

public class Turn {

    public Turn(DataKey key, Player actingPlayer, int turnCount) {
        this.actingPlayer = actingPlayer;
        this.drawnCard = actingPlayer.getHand(key).get(1);
        this.turnCount = turnCount;

        ArrayList<Card> hand = this.actingPlayer.getHand(key);
        this.playedCard = hand.get(0);
        this.actingPlayerRemainingCard = hand.get(1);

    }

    /**
     * The number of turns that have occurred up to and including this one
     *  Only writable from constructor
     *  Readable to all players
     */
    private int turnCount;

    public int getTurnCount() {
        return this.turnCount;
    }

    /**
     * The Player whose turn it was
     *  Only writable from constructor
     *  Readable to all players
     */
    private Player actingPlayer;

    public Player getActingPlayer() {
        return this.actingPlayer;
    }

    /**
     * The card the player drew
     *  Only writable from constructor
     *  Readable to actingPlayer and MasterKey
     */
    private Card drawnCard;

    public Card getDrawnCard(DataKey key) {

        if (!key.isMasterKey() && key.getPlayer() != this.actingPlayer)
            return Card.Unknown;

        return this.drawnCard;
    }

    /**
     * The card the player played
     *  Player and MasterKey can swap this with the actingPlayerRemainingCard before this turn is finalized
     *  Readable by all players
     */
    private Card playedCard;

    public Card getPlayedCard() {
        return this.playedCard;
    }

    /**
     * The other card in the acting players hand (relevant for the King and Baron)
     *  Player and MasterKey can swap this with the actingPlayerRemainingCard before this turn is finalized
     *  Readable:
     *      - Always by MasterKey and actingPlayer.
     *      - By target if they were not protected, and they were targeted by either a baron or a king
     *      - By everyone, if the played card was a baron and the target lost
     */
    private Card actingPlayerRemainingCard;

    public Card getActingPlayerRemainingCard(DataKey key) {

        if (key.isMasterKey())
            return this.actingPlayerRemainingCard;

        if (key.getPlayer() == this.actingPlayer)
            return this.actingPlayerRemainingCard;

        if (this.targetPlayerWasProtected)
            return Card.Unknown;

        if (!this.isTurnFinalized)
            return Card.Unknown;

        if (key.getPlayer() == this.targetPlayer && (this.playedCard == Card.Baron || this.playedCard == Card.King))
            return this.actingPlayerRemainingCard;

        // Visible to everyone if this card was eliminated by the Baron
        if (this.playedCard == Card.Baron && this.actingPlayerRemainingCard.value < this.targetPlayersCard.value)
            return this.targetPlayersCard;

        return Card.Unknown;
    }

    /**
     * The player targeted. Will be set to null when isTurnFinalized if not relevent to the played card
     *  Writable with the MasterKey. Also writable by actingPlayer if isTurnFinalized is false.
     *      The setter will also update targetPlayerWasProtected
     *  Readable by everyone
     */
    private Player targetPlayer;

    public Player getTargetPlayer() {
        return this.targetPlayer;
    }

    public void setTargetPlayer(DataKey key, Player targetPlayer) {

        if (!key.isMasterKey() && (key.getPlayer() != actingPlayer || isTurnFinalized))
            return;

        this.targetPlayer = targetPlayer;

        this.targetPlayerWasProtected = targetPlayer != null && targetPlayer.isProtected();

        // Can't set target player card here because the key provided is probably the acting player key and so it won't
        //      be able to read it

    }

    /**
     * Weather or not the target player was protected by the handmaiden during this turn
     */
    private boolean targetPlayerWasProtected;

    public boolean wasTargetPlayerProtected() {
        return targetPlayerWasProtected;
    }


    /**
     * The card possessed by the other player, null if not relevant
     *  Writable with the MasterKey only
     *  Readable with MasterKey and by the target player. Also by the acting player if they used a Baron or a King.
     *      Readable by everyone if this card was correctly guessed by a guard
     */
    private Card targetPlayersCard;

    public Card getTargetPlayersCard(DataKey key) {

        if (this.targetPlayersCard == null)
            return null;

        if (key.isMasterKey())
            return this.targetPlayersCard;

        if (key.getPlayer() == this.targetPlayer)
            return this.targetPlayersCard;

        if (this.targetPlayerWasProtected)
            return Card.Unknown;

        if (!this.isTurnFinalized)
            return Card.Unknown;

        // Visible to acting player if
        if (key.getPlayer() == this.actingPlayer) {

            // They played a king
            if (this.playedCard == Card.King)
                return this.targetPlayersCard;

            // They played a prince
            if (this.playedCard == Card.Prince)
                return this.targetPlayersCard;

            // They played a priest
            if (this.playedCard == Card.Priest)
                return this.targetPlayersCard;

            // They played a Baron
            if (this.playedCard == Card.Baron)
                return this.targetPlayersCard;
        }


        // Visible to everyone if this card was correctly guessed by a guard
        if (this.playedCard == Card.Guard && this.guessedCard == this.targetPlayersCard)
            return this.targetPlayersCard;

        // Visible to everyone if this card was eliminated by the Baron
        if (this.playedCard == Card.Baron && this.actingPlayerRemainingCard.value > this.targetPlayersCard.value)
            return this.targetPlayersCard;

        return Card.Unknown;
    }

    /**
     * The card guessed by the acting player, null if not relevant. When this turn is isTurnFinalized, if the playedCard is
     *      not the guard then this will be set to null
     *  Writable with the MasterKey or by actingPlayer
     *  Readable by everyone
     */
    private Card guessedCard;

    public Card getGuessedCard() {
        return guessedCard;
    }

    public void setGuessedCard(DataKey key, Card guessedCard) {

        if (!key.isMasterKey() && key.getPlayer() != this.actingPlayer)
            return;

        this.guessedCard = guessedCard;
    }

    /**
     * The card the target drew after being targeted with a prince
     *  Writable only with the MasterKey
     *  Readable with MasterKey or to target player. If this has a null value then visible to all
     */
    private Card targetPlayerDrawnCard;

    public Card getTargetDrawnCard(DataKey key) {

        if (targetPlayerDrawnCard == null)
            return null;

        if (key.isMasterKey() || key.getPlayer() == this.targetPlayer)
            return targetPlayerDrawnCard;

        return Card.Unknown;
    }

    public void setTargetPlayerDrawnCard(DataKey key, Card drawnCard) throws NoTargetException {

        if (this.targetPlayer == null)
            throw new NoTargetException();

        if (!key.isMasterKey())
            return;

        this.targetPlayerDrawnCard = drawnCard;
    }

    /**
     * If this turn object has been finalized then it can no longer be changed by players
     *  Only writable with MasterKey, not readable to anyone
     */
    private boolean isTurnFinalized = false;
    public void finalize(DataKey key) {

        if (isTurnFinalized)
            return;

        if (!key.isMasterKey())
            return;

        // You can only guess if the playing a guard
        if (this.playedCard != Card.Guard)
            this.guessedCard = null;

        // There are certain cards that you can't target a player with
        if (!Card.getTargetingCards().contains(this.playedCard))
            this.setTargetPlayer(key, null);

        // This must be set here, because setTargetPlayer will only have the acting players read permissions
        if (this.targetPlayer != null)
            this.targetPlayersCard = this.targetPlayer.getHand(key).get(0);

        isTurnFinalized = true;
    }

    /**
     * getEliminatedPlayer
     *
     * @return If a player was eliminated this turn and the turn has been finalized then return that player.
     *      Otherwise return null
     */
    private Player eliminatedPlayer = null;

    public void setEliminatedPlayer(DataKey key, Player eliminated) {
        if (key.isMasterKey())
            this.eliminatedPlayer = eliminated;
    }

    public Player getEliminatedPlayer() {

        if (!isTurnFinalized)
            return null;

        return eliminatedPlayer;
    }

    /**
     * swapPlayedAndRemainingCards
     *  Lets MasterKey or activePlayers key swap which card in the active players hand will be played
     *
     * @param key
     */
    public void swapPlayedAndRemainingCards(DataKey key) {

        if (isTurnFinalized)
            return;

        if (!key.isMasterKey() && key.getPlayer() != actingPlayer)
            return;

        Card tmp = this.actingPlayerRemainingCard;
        this.actingPlayerRemainingCard = this.playedCard;
        this.playedCard = tmp;

    }

    public boolean isValidTurn(GameState state) {

        if (Card.getTargetingCards().contains(this.playedCard) && !state.isActivePlayer(this.targetPlayer))
            return false;

        if (this.playedCard == Card.Guard && !Card.getGuessableCards().contains(this.guessedCard))
            return false;

        return true;
    }

}
