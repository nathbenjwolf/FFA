package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

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
        battlePanel.updateBattlePanel(null, getBackButton());
    }

    @Override
    public void onBoardClicked(Cell cell) {
        if(board.moveCharacter(battle.activeCharacter, cell)) {
            battle.hasMoved = true;
            battle.currentGameState = new MenuState(battle, board, battlePanel);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.err.println( command + " button pressed");

        switch(command) {
            case backStringName:
                board.clearMoveCells();
                battle.currentGameState = new MenuState(battle, board, battlePanel);
                break;
        }
    }
}
