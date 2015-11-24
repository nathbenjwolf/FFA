package utils;

import battle.Cell;
import mapElement.MapElement;
import character.Character;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Nathan on 11/23/2015.
 */
public abstract class PathFinding {

    public static Set<Cell> findPathableCells(MapElement[][] map, Set<Cell> blockingCells, Character character, Cell cell, int range) {
        return findPathableCellsRec(map, blockingCells, character, new Cell(cell), range);
    }

    public static Set<Cell> findProjectileCells(MapElement[][] map, Set<Cell> blockingCells, Cell cell, int range) {
        return new HashSet<Cell>(); // Implement
    }

    public static Set<Cell> findRadialCells(MapElement[][] map, Cell cell, int radius) {
        return findRadialCellsRec(map, new Cell(cell), radius);
    }

    public static Set<Cell> findPathableRadialCells(MapElement[][] map, Character character, Cell cell, int radius) {
        Set<Cell> radialCells = findRadialCellsRec(map, new Cell(cell), radius);
        Set<Cell> blockedCells = new HashSet<>();
        for(Cell radialCell: radialCells) {
            if(map[radialCell.x][radialCell.y].isElementMovementBlocking(character)) {
                blockedCells.add(radialCell);
            }
        }
        radialCells.removeAll(blockedCells);
        return radialCells;
    }

    private static Set<Cell> findPathableCellsRec(MapElement[][] map, Set<Cell> blockingCells, Character character, Cell cell, int range) {
        Set<Cell> cells = new HashSet<Cell>();
        cells.add(new Cell(cell));

        if(range == 0) {
            return cells;
        }

        // Cannot pass movement blocking map elements or cells

        // UP
        cell.y--;
        if(cell.y >= 0 && !map[cell.x][cell.y].isElementMovementBlocking(character) && !blockingCells.contains(cell)) {
            cells.addAll(findPathableCellsRec(map, blockingCells, character, cell, range-1));
        }
        cell.y++;

        // DOWN
        cell.y++;
        if(cell.y < map[0].length && !map[cell.x][cell.y].isElementMovementBlocking(character) && !blockingCells.contains(cell)) {
            cells.addAll(findPathableCellsRec(map, blockingCells, character, cell, range-1));
        }
        cell.y--;

        // LEFT
        cell.x--;
        if(cell.x >= 0 && !map[cell.x][cell.y].isElementMovementBlocking(character) && !blockingCells.contains(cell)) {
            cells.addAll(findPathableCellsRec(map, blockingCells, character, cell, range-1));
        }
        cell.x++;

        // RIGHT
        cell.x++;
        if(cell.x < map.length && !map[cell.x][cell.y].isElementMovementBlocking(character) && !blockingCells.contains(cell)) {
            cells.addAll(findPathableCellsRec(map, blockingCells, character, cell, range-1));
        }
        cell.x--;

        return cells;
    }

    private static Set<Cell> findRadialCellsRec(MapElement[][] map, Cell cell, int radius) {
        Set<Cell> cells = new HashSet<Cell>();
        cells.add(new Cell(cell));

        if(radius == 0) {
            return cells;
        }

        // UP
        cell.y--;
        if(cell.y >= 0) {
            cells.addAll(findRadialCellsRec(map, cell, radius-1));
        }
        cell.y++;

        // DOWN
        cell.y++;
        if(cell.y < map[0].length) {
            cells.addAll(findRadialCellsRec(map, cell, radius-1));
        }
        cell.y--;

        // LEFT
        cell.x--;
        if(cell.x >= 0) {
            cells.addAll(findRadialCellsRec(map, cell, radius-1));
        }
        cell.x++;

        // RIGHT
        cell.x++;
        if(cell.x < map.length) {
            cells.addAll(findRadialCellsRec(map, cell, radius-1));
        }
        cell.x--;

        return cells;
    }
}
