package battle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import character.*;
import character.Character;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Battle extends JFrame implements MouseListener{
    static int topBorderLen = 30;
    static int borderLen = 8;

    private Board board;
    private BattlePanel battlePanel;
    public List<Character> team1;
    public List<Character> team2;
    public List<Character> characterOrder;
    public Character activeCharacter;

    public Battle(List<Character> team1, List<Character> team2, String map) {
        this.team1 = team1;
        this.team2 = team2;
        generateCharacterOrder();

        board = new Board(team1, team2, map);
        board.addMouseListener(this);
        board.showMoveCells(activeCharacter);

        battlePanel = new BattlePanel();
        battlePanel.addMouseListener(this);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setSize(Board.cellSize*board.numXCells+(borderLen*2), Board.cellSize*board.numYCells+borderLen+topBorderLen+BattlePanel.panelHeight);

        // Board
        board.setPreferredSize(new Dimension(Board.cellSize*board.numXCells, Board.cellSize*board.numYCells));
        add(board, BorderLayout.NORTH);

        // BattlePanel
        battlePanel.setPreferredSize(new Dimension(Board.cellSize*board.numXCells, BattlePanel.panelHeight));
        add(battlePanel, BorderLayout.SOUTH);

        setTitle("FFA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void generateCharacterOrder() {
        characterOrder = new ArrayList<Character>();
        characterOrder.addAll(team1);
        characterOrder.addAll(team2);
        Collections.shuffle(characterOrder);
        activeCharacter = characterOrder.get(0);
    }

    private void nextCharacterTurn() {
        int charIndex = characterOrder.indexOf(activeCharacter);
        charIndex++;
        if(charIndex == characterOrder.size()) {
            charIndex = 0;
        }

        activeCharacter = characterOrder.get(charIndex);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object sourcePanel = e.getSource();
        if(sourcePanel instanceof Board) {
            System.err.println("Board was clicked: (" + e.getX() + "," + e.getY() + ")");
            Cell cell = board.pixelToCell(e.getX(), e.getY());
            System.err.println("Board was clicked: (" + cell.x + "," + cell.y + ")");
            if(board.moveCharacter(activeCharacter, cell)) {
                nextCharacterTurn();
                board.showMoveCells(activeCharacter);
            }
        } else if(sourcePanel instanceof BattlePanel) {
            System.err.println("BattlePanel was clicked: (" + e.getX() + "," + e.getY() + ")");
        } else {
            System.err.println("Nothing was clicked?");
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {}
    @Override
    public void mouseReleased(MouseEvent e) {}
    @Override
    public void mouseEntered(MouseEvent e) {}
    @Override
    public void mouseExited(MouseEvent e) {}

    public static void main(String[] args) {
        List<Character> team1 = new ArrayList<Character>();
        team1.add(new Warrior(10,3,1,2));
        team1.add(new Ranger(6,2,4,3));

        List<Character> team2 = new ArrayList<Character>();
        team2.add(new Warrior(10,3,1,2));
        team2.add(new Ranger(6,2,4,3));

        Battle ex = new Battle(team1, team2, "Assets/Maps/battle2.png");
    }

}
