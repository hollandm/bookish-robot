package Game;


import Brains.Base_Brain;
import Brains.*;
import Game.Exceptions.GameException;
import Game.Model.*;

import java.util.ArrayList;


public class Game {

    public class DataKey {
        private final Player player;

        private DataKey(Player player) {
            this.player = player;
        }

        public Player getPlayer() {
            return this.player;
        }

        public boolean isMasterKey() {
            return this.player == null;
        }
    }

    private int nextPlayerID = 0;

    GameState state = new GameState();

    public Game() {

    }

    public void runGame(ArrayList<Base_Brain> brains) throws GameException{

        DataKey masterKey = new DataKey(null);

        for (Base_Brain brain : brains) {
            Player p = state.addPlayer(brain.getName(), brain.id);
            DataKey key = new DataKey(p);

            brain.setKey(key);
        }

        state.startGame(masterKey);

        while (state.getStage() == GameStage.GAME_STARTED) {
            int currentPlayerId = state.getCurrentPlayersID();
            Base_Brain currentPlayerBrain = brains.get(currentPlayerId);

            Turn thisTurn = startTurn(masterKey);

            currentPlayerBrain.takeTurn(state, thisTurn);

            applyAction(thisTurn, masterKey);

        }

    }

    private Turn startTurn(DataKey key) {

        // Handmaid protection wears off
        state.getCurrentPlayer().setHandmaidProtected(key, false);

        Card drawnCard = state.getDeck(key).pop();
        Turn thisTurn = new Turn(key, state.getCurrentPlayer(), drawnCard, state.getCurrentTurn());

        return thisTurn;
    }

    private void applyAction(Turn action, DataKey key) {

        if (!key.isMasterKey())
            return;

        //TODO: Validate given action
        //TODO: Apply given action

        //TODO: Check if this causes an end game

        state.nextTurn(key);
    }

    public static void main(String[] args) throws GameException {

        ArrayList<Base_Brain> brains = new ArrayList<>();
        brains.add(new RandomAI());
        brains.add(new RandomAI());

        Game game = new Game();

        game.runGame(brains);

    }


}
