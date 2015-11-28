package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 11/28/2015.
 */
public class AttackTargetState extends GameState {
    public AttackTargetState(Battle battle, Board board, BattlePanel battlePanel) {
        super(battle, board, battlePanel);
    }

    @Override
    protected void onInit() {
        board.showAbilityCells(battle.activeCharacter, battle.activeCharacter.attack);
        List<String> buttonNames = new ArrayList<>();
        addButtons(buttonNames, true);
    }

    @Override
    public void onBoardClicked(Cell cell) {
        if(board.useAbility(battle.activeCharacter, battle.activeCharacter.attack, cell)) {
            battle.hasActioned = true;
            battle.currentGameState = new MenuState(battle, board, battlePanel);
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.err.println( command + " button pressed");

        switch(command) {
            case backStringName:
                board.clearAbilityCells();
                battle.currentGameState = new ActionState(battle, board, battlePanel);
                break;
        }
    }
}
