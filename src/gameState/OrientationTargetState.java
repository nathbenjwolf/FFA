package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 12/12/2015.
 */
public class OrientationTargetState extends GameState {


    public OrientationTargetState(Battle battle, Board board, BattlePanel battlePanel) {
        super(battle, board, battlePanel);
    }

    @Override
    protected void onInit() {
        board.showOrientationCells(battle.activeCharacter);
        battlePanel.updateBattlePanel(null, getBackButton());
    }

    @Override
    public void onBoardClicked(Cell cell) {
        if(board.orientCharacter(battle.activeCharacter, cell)) {
            battle.nextCharacterTurn();
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.err.println( command + " button pressed");

        switch(command) {
            case backStringName:
                board.clearOrientationCells();
                battle.currentGameState = new MenuState(battle, board, battlePanel);
                break;
        }
    }
}
