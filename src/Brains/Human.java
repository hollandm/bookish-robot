package Brains;
import Game.Game.DataKey;
import Game.Model.GameState;
import Game.Model.Player;
import Game.Model.Turn;

/**
 * Created by mbhol on 12/30/2015.
 */
public class Human extends Base_Brain {

    String name = "Human Player";


    public Turn takeTurn(GameState state) {
        return new Turn();
    }


}
