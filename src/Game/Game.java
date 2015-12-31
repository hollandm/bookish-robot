package Game;


import Brains.Base_Brain;
import Brains.Human;
import Game.Exceptions.GameException;
import Game.Model.GameStage;
import Game.Model.GameState;
import Game.Model.Player;
import Game.Model.Turn;

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

        while (state.getStage() == GameStage.GAME_STARTED) {
            int currentPlayerId = state.getCurrentPlayersID();
            Base_Brain currentPlayerBrain = brains.get(currentPlayerId);

            Turn currentPlayerAction = currentPlayerBrain.takeTurn(state);

            applyAction(currentPlayerAction, masterKey);

        }

    }

    private void applyAction(Turn action, DataKey key) {

        if (!key.isMasterKey())
            return;

        //TODO: Validate given action
        //TODO: Apply given action

        //TODO: Check if this causes an end game
    }

    public static void main(String[] args) throws GameException {

        ArrayList<Base_Brain> brains = new ArrayList<>();
        brains.add(new Human());
        brains.add(new Human());

        Game game = new Game();

        game.runGame(brains);

    }


}
