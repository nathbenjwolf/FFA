package Battle;

import sun.rmi.runtime.Log;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Battle extends JFrame implements MouseListener{
    static int topBorderLen = 30;
    static int borderLen = 8;
    Board board;
    BattlePanel battlePanel;

    public Battle() {
        board = new Board();
        board.addMouseListener(this);

        battlePanel = new BattlePanel();
        battlePanel.addMouseListener(this);

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        setSize(Board.cellSize*Board.numXCells+(borderLen*2), Board.cellSize*Board.numYCells+borderLen+topBorderLen+BattlePanel.panelHeight);

        // Board
        board.setPreferredSize(new Dimension(Board.cellSize*Board.numXCells, Board.cellSize*Board.numYCells));
        add(board, BorderLayout.NORTH);

        // BattlePanel
        battlePanel.setPreferredSize(new Dimension(Board.cellSize*Board.numXCells, BattlePanel.panelHeight));
        add(battlePanel, BorderLayout.SOUTH);

        setTitle("FFA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object sourcePanel = e.getSource();
        if(sourcePanel instanceof Board) {
            System.err.println("Board was clicked: (" + e.getX() + "," + e.getY() + ")");
            Cell cell = board.pixelToCell(e.getX(), e.getY());
            System.err.println("Board was clicked: (" + cell.x + "," + cell.y + ")");
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
        Battle ex = new Battle();

//        EventQueue.invokeLater(new Runnable() {
//            @Override
//            public void run() {
//                Battle ex = new Battle();
//                ex.setVisible(true);
//            }
//        });
    }

}
