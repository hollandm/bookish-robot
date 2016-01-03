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

    String name = "Random AI";
    Random random = new Random();

    public void takeTurn(GameState state, Turn thisTurn) {

        Player myPlayer = state.getCurrentPlayer();
        ArrayList<Card> myHand = myPlayer.getHand(super.key);

        if (random.nextBoolean())
            thisTurn.swapPlayedAndRemainingCards(key);

        thisTurn.setTargetPlayer(key, selectRandomEnemy(state));

        ArrayList<Card> guessable = Card.getGuessableCards();
        Collections.shuffle(guessable);
        thisTurn.setGuessedCard(key, guessable.get(0));

    }

    private Player selectRandomEnemy(GameState state) {

        LinkedList<Player> players = state.getActivePlayers();
        players.remove(state.getPlayerByID(super.id));

        Collections.shuffle(players);

        return players.get(0);
    }

}
