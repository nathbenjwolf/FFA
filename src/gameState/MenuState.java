package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;

/**
 * Created by Nathan on 11/25/2015.
 */
public class MenuState extends GameState {
    public MenuState(Battle battle, Board board, BattlePanel battlePanel) {
        super(battle, board, battlePanel);
    }

    @Override
    protected void onInit() {

    }

    @Override
    public void onBoardClicked(Cell cell) {

    }

    @Override
    public void onBattlePanelClicked(int pixelX, int pixelY) {
        // Change state to moveState
        battle.currentGameState = new MoveTargetState(battle, board, battlePanel);
    }
}
