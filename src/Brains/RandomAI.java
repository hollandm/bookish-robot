package Brains;

import Game.Model.Card;
import Game.Model.GameState;
import Game.Model.Player;
import Game.Model.Turn;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;

/**
 * Created by mbhol on 12/31/2015.
 */
public class RandomAI extends Base_Brain {

    Random random = new Random();

    public RandomAI() {
        super.name = "Random AI";
    }


    public void takeTurn(GameState state, Turn thisTurn) {

        if (random.nextBoolean())
            thisTurn.swapPlayedAndRemainingCards(key);

//        System.out.println(name + " " + super.id + " is playing " + thisTurn.getPlayedCard());

        if (Card.getTargetingCards().contains(thisTurn.getPlayedCard())) {
            Player target = selectRandomEnemy(state);
            thisTurn.setTargetPlayer(key, target);
//            System.out.println("    Targeting " + thisTurn.getTargetPlayer());
        }

        if (thisTurn.getPlayedCard() == Card.Guard) {
            ArrayList<Card> guessable = Card.getGuessableCards();
            Collections.shuffle(guessable);
            thisTurn.setGuessedCard(key, guessable.get(0));
//            System.out.println("    Guessing " + thisTurn.getGuessedCard());
        }


    }

    private Player selectRandomEnemy(GameState state) {

        LinkedList<Player> players = state.getActivePlayers(key);
        players.remove(state.getPlayerByID(super.id));

        Collections.shuffle(players);

        Player selected = players.get(0);

        return selected;
    }

}
