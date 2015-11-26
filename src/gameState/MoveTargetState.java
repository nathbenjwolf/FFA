package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;

/**
 * Created by Nathan on 11/25/2015.
 */
public class MoveTargetState extends GameState {
    public MoveTargetState(Battle battle, Board board, BattlePanel battlePanel) {
        super(battle, board, battlePanel);
    }

    @Override
    protected void onInit() {
        board.showMoveCells(battle.activeCharacter);
    }

    @Override
    public void onBoardClicked(Cell cell) {
        if(board.moveCharacter(battle.activeCharacter, cell)) {
            battle.nextCharacterTurn();
        }
    }

    @Override
    public void onBattlePanelClicked(int pixelX, int pixelY) {

    }
}
