package Game.Model;

import java.util.ArrayList;

/**
 * Created by mbhol on 12/30/2015.
 */
public enum Card {
    Princess(8, "Princess"), Countess(7, "Countess"), King(6, "King"), Prince(5, "Prince"), Handmaid(4, "Handmaid"),
    Baron(3, "Baron"), Priest(2, "Priest"), Guard(1, "Guard"), Unknown(0, "Unknown Card");

    public final int value;
    public final String name;

    Card(int value, String name) {
        this.value = value;
        this.name = name;
    }

    @Override
    public String toString() {
        return this.name;
    }


    /**
     * getGuessableCards
     * @return a list of cards that can be guessed with a guard card
     */
    public static ArrayList<Card> getGuessableCards() {
        ArrayList<Card> guessable = new ArrayList<>();
        guessable.add(Princess);
        guessable.add(Countess);
        guessable.add(King);
        guessable.add(Prince);
        guessable.add(Handmaid);
        guessable.add(Baron);
        guessable.add(Priest);
        return guessable;
    }

    /**
     * getTargetingCards
     * @return a list of cards that require a target
     */
    public static ArrayList<Card> getTargetingCards() {
        ArrayList<Card> targeting = new ArrayList<>();
        targeting.add(King);
        targeting.add(Prince);
        targeting.add(Baron);
        targeting.add(Priest);
        targeting.add(Guard);
        return targeting;
    }
}

