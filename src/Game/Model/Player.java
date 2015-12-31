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

        ArrayList<Card> handCopy = new ArrayList<>();

        if (key.getPlayer() == this)
            for (Card c : hand)
                handCopy.add(c);

        else
            for (Card c : hand)
                handCopy.add(Card.Unknown);

        return handCopy;
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

}
