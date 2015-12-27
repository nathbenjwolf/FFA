package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;
import utils.BattlePanelButton;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;

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
        List<JButton> buttons = new ArrayList<>();

        JButton attackButton = new BattlePanelButton(attackStringName, battle.activeCharacter.attack.getIcon());
        attackButton.addActionListener(this);
        buttons.add(attackButton);

        JButton abilityButton = new BattlePanelButton(abilityStringName);
        abilityButton.addActionListener(this);
        buttons.add(abilityButton);

        battlePanel.updateBattlePanel(buttons, getBackButton());
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
