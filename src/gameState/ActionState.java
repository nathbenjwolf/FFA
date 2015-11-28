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
public class ActionState extends GameState {
    static final String attackStringName = "Attack";
    static final String abilityStringName = "Ability";

    public ActionState(Battle battle, Board board, BattlePanel battlePanel) {
        super(battle, board, battlePanel);
    }

    @Override
    protected void onInit() {
        List<String> buttonNames = new ArrayList<>();
        buttonNames.add(attackStringName);
        buttonNames.add(abilityStringName);

        addButtons(buttonNames, true);
    }

    @Override
    public void onBoardClicked(Cell cell) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.err.println( command + " button pressed");

        switch(command) {
            case attackStringName:
                battle.currentGameState = new AttackTargetState(battle, board, battlePanel);
                break;
            case abilityStringName:
                // TODO: Change to abilityState once implemented
                break;
            case backStringName:
                battle.currentGameState = new MenuState(battle, board, battlePanel);
                break;
        }
    }
}
