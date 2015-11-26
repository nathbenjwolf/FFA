package gameState;

import battle.Battle;
import battle.BattlePanel;
import battle.Board;
import battle.Cell;

/**
 * Created by Nathan on 11/25/2015.
 */
public abstract class GameState {
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

    public abstract void onBattlePanelClicked(int pixelX, int pixelY);
}
