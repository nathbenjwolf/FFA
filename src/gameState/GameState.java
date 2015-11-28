package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;

import javax.swing.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nathan on 11/25/2015.
 */
public abstract class GameState implements ActionListener {
    static final String backStringName = "Back";

    protected Battle battle;
    protected Board board;
    protected BattlePanel battlePanel;


    public GameState(Battle battle, Board board, BattlePanel battlePanel) {
        this.battle = battle;
        this.board = board;
        this.battlePanel = battlePanel;
        onInit();
    }

    protected abstract void onInit();

    public abstract void onBoardClicked(Cell cell);

    protected void addButtons(List<String> buttonNames, boolean hasBackButton) {
        List<JButton> buttons = new ArrayList<JButton>();
        for(String buttonName : buttonNames) {
            JButton button = new JButton(buttonName);
            button.addActionListener(this);
            buttons.add(button);
        }

        // Back button
        JButton backButton = null;
        if(hasBackButton) {
            backButton = new JButton(backStringName);
            backButton.addActionListener(this);
        }

        battlePanel.updateBattlePanel(buttons, backButton);
    }
}
