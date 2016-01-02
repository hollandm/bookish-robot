package Brains;
import Game.Game.DataKey;
import Game.Model.Card;
import Game.Model.GameState;
import Game.Model.Player;
import Game.Model.Turn;

import java.util.ArrayList;

/**
 * Created by mbhol on 12/30/2015.
 */
public class Human extends Base_Brain {

    String name = "Human Player";


    public void takeTurn(GameState state, Turn thisTurn) {

        Player myPlayer = state.getCurrentPlayer();
        ArrayList<Card> myHand = myPlayer.getHand(super.key);

        System.out.println("It is " + name + "'s turn.");
        System.out.println("0) " + myHand.get(0));
        System.out.println("1) " + myHand.get(1));

        // TODO: Check which card to play

        int humanChoice = 0;

        Card selectedCard = myHand.get(humanChoice);


    }


}
