package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;

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

        JButton actionButton = new JButton(actionStringName);
        actionButton.addActionListener(this);
        actionButton.setEnabled(!battle.hasActioned);
        buttons.add(actionButton);

        JButton moveButton = new JButton(moveStringName);
        moveButton.addActionListener(this);
        moveButton.setEnabled(!battle.hasMoved);
        buttons.add(moveButton);

        JButton endTurnButton = new JButton(endTurnStringName);
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

        switch(command) {
            case actionStringName:
                System.err.println("Action btn pressed");
                battle.currentGameState = new MoveTargetState(battle, board, battlePanel);
                break;
            case moveStringName:
                System.err.println("Move btn pressed");
                battle.currentGameState = new MoveTargetState(battle, board, battlePanel);
                break;
            case endTurnStringName:
                System.err.println("End turn btn pressed");
                battle.nextCharacterTurn(); // TODO: Change to facingTargetState once implemented
                break;
        }
    }
}
