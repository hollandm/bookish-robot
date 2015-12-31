package Game.Model;

import Game.Model.Card;
import Game.Model.Player;

/**
 * Created by mbhol on 12/30/2015.
 */
public class Turn {

    // The Player whose took this action
    int currentPlayerID;

    // The card the player drew
    Card drawnCard;

    // The card the player played
    Card playedCard;

    // The player targeted
    int targetPlayerID;



}
