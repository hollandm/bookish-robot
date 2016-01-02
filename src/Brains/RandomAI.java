package Brains;

import Game.Model.Card;
import Game.Model.GameState;
import Game.Model.Player;
import Game.Model.Turn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by mbhol on 12/31/2015.
 */
public class RandomAI extends Base_Brain {

    String name = "Random AI";


    public void takeTurn(GameState state, Turn thisTurn) {

        Player myPlayer = state.getCurrentPlayer();
        ArrayList<Card> myHand = myPlayer.getHand(super.key);

        Collections.shuffle(myHand);
        Card selectedCard = myHand.get(0);

        Player target = selectRandomEnemy(state);

    }

    private Player selectRandomEnemy(GameState state) {

        int targetId;
        Random generator = new Random();

        do {
            targetId = generator.nextInt(state.countPlayers());
        } while (targetId == super.id);

        return state.getPlayerByID(targetId);
    }

}
