package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;
import utils.BattlePanelButton;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 11/25/2015.
 */
public class MenuState extends GameState {
    static final String actionStringName = "Action";
    static final String moveStringName = "Move";
    static final String endTurnStringName = "End Turn";

    public MenuState(Battle battle, Board board, BattlePanel battlePanel) {
        super(battle, board, battlePanel);
    }
    @Override
    protected void onInit() {
        List<JButton> buttons = new ArrayList<>();

        JButton actionButton = new BattlePanelButton(actionStringName);
        actionButton.addActionListener(this);
        actionButton.setEnabled(!battle.hasActioned);
        buttons.add(actionButton);

        JButton moveButton = new BattlePanelButton(moveStringName);
        moveButton.addActionListener(this);
        moveButton.setEnabled(!battle.hasMoved);
        buttons.add(moveButton);

        JButton endTurnButton = new BattlePanelButton(endTurnStringName);
        endTurnButton.addActionListener(this);
        buttons.add(endTurnButton);

        battlePanel.updateBattlePanel(buttons, null);
    }

    @Override
    public void onBoardClicked(Cell cell) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        System.err.println( command + " button pressed");

        switch(command) {
            case actionStringName:
                battle.currentGameState = new ActionState(battle, board, battlePanel);
                break;
            case moveStringName:
                battle.currentGameState = new MoveTargetState(battle, board, battlePanel);
                break;
            case endTurnStringName:
                battle.currentGameState = new OrientationTargetState(battle, board, battlePanel);
                break;
        }
    }
}
