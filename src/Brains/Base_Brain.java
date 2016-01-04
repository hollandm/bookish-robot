package Brains;

import Game.Game.DataKey;
import Game.Model.GameState;
import Game.Model.Turn;

public abstract class Base_Brain {

    private static int nextBrainID = 0;
    public final int id = ++nextBrainID;
    public int getId() {
        return id;
    }


    protected DataKey key;
    public void setKey(DataKey key) {
        this.key = key;
    }

    protected String name;
    public String getName() {
        return name;
    }

    public void takeTurn(GameState state, Turn thisTurn) {

    }

    /**
     * showTurn
     *  Shows the Brain the results of a turn.
     *
     * @param turn
     */
    public void showTurn(Turn turn) {

    }

}
