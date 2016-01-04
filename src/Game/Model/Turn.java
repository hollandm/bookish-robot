package Game.Model;

import Game.Game.DataKey;

import java.util.ArrayList;

/**
 * Created by mbhol on 12/30/2015.
 */
public class Turn {

    public Turn(DataKey key, Player actingPlayer, Card drawnCard, int turnCount) {
        this.actingPlayer = actingPlayer;
        this.drawnCard = drawnCard;
        this.turnCount = turnCount;

        ArrayList<Card> hand = this.actingPlayer.getHand(key);
        hand.add(drawnCard);

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
     *  Readable by MasterKey, actingPlayer. If the played card was a king or baron then it is also visible to the
     *      target player. For everyone else it will return the "Unknown" card.
     */
    private Card actingPlayerRemainingCard;

    public Card getActingPlayerRemainingCard(DataKey key) {

        if (key.isMasterKey())
            return this.actingPlayerRemainingCard;

        if (key.getPlayer() == this.actingPlayer)
            return this.actingPlayerRemainingCard;

        if (key.getPlayer() == this.targetPlayer && !this.targetPlayerWasHandmaidProtected
                && (isTurnFinalized && (this.playedCard == Card.Baron || this.playedCard == Card.King)))
            return this.actingPlayerRemainingCard;

        return Card.Unknown;
    }

    /**
     * The player targeted. Will be set to null when isTurnFinalized if not relevent to the played card
     *  Writable with the MasterKey. Also writable by actingPlayer if isTurnFinalized is false.
     *      The setter will also update targetPlayerWasHandmaidProtected
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

        if (targetPlayer != null) {
            this.targetPlayerWasHandmaidProtected = targetPlayer.isHandmaidenProtected();
        } else {
            this.targetPlayerWasHandmaidProtected = false;
        }

    }

    /**
     * Weather or not the target player was protected by the handmaiden during this turn
     */
    private boolean targetPlayerWasHandmaidProtected;

    public boolean wasTargetPlayerHandmaidProtected() {
        return targetPlayerWasHandmaidProtected;
    }


    /**
     * The card possessed by the other player, null if not relevant
     *  Writable with the MasterKey only
     *  Readable with MasterKey and by the target player. Also by the acting player if they used a Baron or a King.
     *      Readable by everyone if this card was correctly guessed by a guard
     */
    private Card targetPlayersCard;

    public Card getTargetPlayersCard(DataKey key) {

        if (key.isMasterKey())
            return this.targetPlayersCard;

        if (key.getPlayer() == this.targetPlayer)
            return this.targetPlayersCard;

        // Visible to acting player if they played a Baron, King, or Prince, or Priest and the target is not protected
        if (key.getPlayer() == this.actingPlayer && !this.targetPlayerWasHandmaidProtected
                && (isTurnFinalized && (this.playedCard == Card.Baron || this.playedCard == Card.King || this.playedCard == Card.Prince || this.playedCard == Card.Priest)))
            return this.targetPlayersCard;

        // Visible to everyone if this card was correctly guessed by a guard
        if (this.playedCard == Card.Guard && !this.targetPlayerWasHandmaidProtected
                && this.guessedCard == this.targetPlayersCard)
            return this.targetPlayersCard;

        return Card.Unknown;
    }

    public void setTargetPlayersCard(DataKey key, Card card) {
        if (key.isMasterKey())
            this.targetPlayersCard = card;
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

    public void setTargetPlayerDrawnCard(DataKey key, Card drawnCard) {

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

        if (!key.isMasterKey())
            return;

        // You can only guess if the playing a guard
        if (this.playedCard != Card.Guard)
            this.guessedCard = null;

        // There are certain cards that you can't target a player with
        if (!Card.getTargetingCards().contains(this.playedCard))
            this.setTargetPlayer(key, null);

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
    public Player getEliminatedPlayer() {

        if (!isTurnFinalized)
            return null;

        // Guard
        if (this.playedCard == Card.Guard && !targetPlayerWasHandmaidProtected && this.targetPlayersCard == this.guessedCard)
            return this.targetPlayer;

        // Priest, Not possible to eliminate

        // Baron
        if (this.playedCard == Card.Baron && !targetPlayerWasHandmaidProtected) {
            if (this.actingPlayerRemainingCard.value > this.targetPlayersCard.value)
                return this.targetPlayer;
            else
                return this.actingPlayer;
        }

        // Handmaiden, Not possible to eliminate

        // Prince
        if (this.playedCard == Card.Prince && !targetPlayerWasHandmaidProtected)
            if (this.targetPlayersCard == Card.Princess || this.targetPlayerDrawnCard == null)
                return this.targetPlayer;

        // King, Not possible to eliminate

        // Countess, Not possible to eliminate

        // Princess
        if (this.playedCard == Card.Princess)
            return this.actingPlayer;

        return null;
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
