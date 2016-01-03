package Game.Model;

import java.util.ArrayList;

import Game.Game.DataKey;


public class Player  {

    private int playerID;
    public int getPlayerID() {
        return playerID;
    }

    private String playerName;
    public String getPlayerName() {
        return playerName;
    }

    private ArrayList<Card> hand = new ArrayList<>(2);

    public ArrayList<Card> getHand(DataKey key) {

        if (key.isMasterKey())
            return hand;

        if (key.getPlayer() == this)
            return new ArrayList<>(hand);

        ArrayList<Card> mysteryHand = new ArrayList<>();
        for (Card c : hand)
            mysteryHand.add(Card.Unknown);

        return mysteryHand;
    }

    public void addCardToHand(DataKey key, Card drawnCard) {

        if (!key.isMasterKey())
            return;

        hand.add(drawnCard);

    }


    private int wins = 0;
    public int getWins() {
        return wins;
    }
    public void addWin(DataKey key) {
        if (key.isMasterKey())
            ++wins;
    }

    public Player(String name, int id) {
        playerID = id;
        playerName = name;
    }

    private boolean isHandmaidenProtected = false;
    public boolean isHandmaidenProtected() {
        return this.isHandmaidenProtected;
    }
    public void setHandmaidProtected(DataKey key, boolean isProtected) {
        if (!key.isMasterKey())
            return;

       this.isHandmaidenProtected = isProtected;
    }

    private ArrayList<Card> discardedCards = new ArrayList<>();

    public void addPlayedCard(DataKey key, Card playedCard) {

        if (!key.isMasterKey())
            return;

        this.discardedCards.add(playedCard);
    }

    public ArrayList<Card> getPlayedCards(DataKey key) {

        if (key.isMasterKey())
            return this.discardedCards;

        return new ArrayList<>(this.discardedCards);
    }

    public void discardCard(DataKey key, Card card) {
        if (!key.isMasterKey())
            return;

        hand.remove(card);
        discardedCards.add(card);
    }

}
