package Game.Model;

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
}

