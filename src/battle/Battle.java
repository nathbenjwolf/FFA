package battle;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import character.*;
import character.Character;
import gameState.GameState;
import gameState.MenuState;

/**
 * Created by Nathan on 11/14/2015.
 */
public class Battle extends JFrame implements MouseListener{
    static int topBorderLen = 0; // 30 if setResizable isn't false
    static int borderLen = 0; // 8 if setResizable isn't false

    private Board board;
    private BattlePanel battlePanel;
    private PortraitPanel leftPortrait;
    private PortraitPanel rightPortrait;
    public List<Character> team1;
    public List<Character> team2;
    public List<Character> characterOrder;
    public GameState currentGameState;

    public Character activeCharacter;
    public boolean hasActioned = false;
    public boolean hasMoved = false;

    public Battle(List<Character> team1, List<Character> team2, String groundMap, String objectsMap) {
        this.team1 = team1;
        this.team2 = team2;

        leftPortrait = new PortraitPanel();
        rightPortrait = new PortraitPanel();

        board = new Board(this, groundMap, objectsMap);
        board.addMouseListener(this);

        battlePanel = new BattlePanel();
        battlePanel.addMouseListener(this);

        generateCharacterOrder();

        initUI();
    }

    private void initUI() {
        setLayout(new BorderLayout());
        //setSize(Board.cellSize*board.numXCells+(borderLen*2), Board.cellSize*board.numYCells+borderLen+topBorderLen+BattlePanel.panelHeight);

        // Board
        board.setPreferredSize(new Dimension(board.getBoardDesiredWidth(), board.getBoardDesiredHeight()));
        add(board, BorderLayout.NORTH);

        // Left Portrait
        leftPortrait.setPreferredSize(new Dimension(PortraitPanel.panelWidth, PortraitPanel.panelHeight));
        add(leftPortrait, BorderLayout.WEST);

        // Right Portrait
        rightPortrait.setPreferredSize(new Dimension(PortraitPanel.panelWidth, PortraitPanel.panelHeight));
        add(rightPortrait, BorderLayout.EAST);

        // BattlePanel
        battlePanel.setPreferredSize(new Dimension(board.getBoardDesiredWidth() - PortraitPanel.panelWidth*2, BattlePanel.panelHeight));
        add(battlePanel, BorderLayout.CENTER);

        setResizable(false);
        pack();

        setTitle("FFA");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        setVisible(true);
    }

    private void generateCharacterOrder() {
        characterOrder = new ArrayList<>();
        characterOrder.addAll(team1);
        characterOrder.addAll(team2);
        Collections.shuffle(characterOrder);
        activeCharacter = characterOrder.get(characterOrder.size()-1);
        nextCharacterTurn();
    }

    public void updateCharacterOrder() {
        List<Character> newCharacterOrder = new ArrayList<>();
        for(Character character : characterOrder) {
            if(team1.contains(character) || team2.contains(character)) {
                newCharacterOrder.add(character);
            }
        }

        characterOrder = newCharacterOrder;
    }

    public void nextCharacterTurn() {
        updateCharacterOrder();
        int charIndex = characterOrder.indexOf(activeCharacter);
        charIndex++;
        if(charIndex == characterOrder.size()) {
            charIndex = 0;
        }

        hasActioned = false;
        hasMoved = false;
        activeCharacter = characterOrder.get(charIndex);

        currentGameState = new MenuState(this, board, battlePanel);
    }

    public void updateCharacterStatus() {
        // Check for dead characters on both teams
        List<Character> deadCharacters = new ArrayList<>();
        for(Character character : characterOrder) {
            if(character.isDead()) {
                deadCharacters.add(character);
            }
        }

        for(Character deadCharacter : deadCharacters) {
            team1.remove(deadCharacter);
            team2.remove(deadCharacter);
            characterOrder.remove(deadCharacter);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        Object sourcePanel = e.getSource();
        if(sourcePanel instanceof Board) {
            System.err.println("Board was clicked: (" + e.getX() + "," + e.getY() + ")");
            Cell cell = board.pixelToCell(e.getX(), e.getY());
            if(cell != null) {
                System.err.println("Board was clicked: (" + cell.x + "," + cell.y + ")");
                currentGameState.onBoardClicked(cell);
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
        team1.add(new Warrior(10,3));
        team1.add(new Ranger(7,2));
        team1.add(new Wizard(5,2));
        team1.add(new Priest(6,3));

        List<Character> team2 = new ArrayList<Character>();
        team2.add(new Warrior(10,3));
        team2.add(new Ranger(7,2));
        team2.add(new Wizard(5,2));
        team2.add(new Priest(6,3));

        Battle ex = new Battle(team1, team2, "Assets/Maps/battle1ground.png", "Assets/Maps/battle1objects.png");
    }

}
