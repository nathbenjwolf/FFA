package utils;

import battle.Cell;

/**
 * Created by Nathan on 12/12/2015.
 */
public abstract class Orientation {
    public enum Direction {
        UP,
        RIGHT,
        DOWN,
        LEFT
    }

    public static Direction getDirection(Cell source, Cell destination) {
        if(source.x > destination.x) {
            // Left, Up, or Below
            float slope = ((float)(source.y-destination.y))/((float)(source.x-destination.x));
            if(slope > 1) {
                // Up
                return Direction.UP;
            } else if(slope < -1) {
                // Down
                return Direction.DOWN;
            } else {
                // Left
                return Direction.LEFT;
            }
        } else if(source.x < destination.x) {
            // Right, Up, or Below
            float slope = ((float)(source.y-destination.y))/((float)(source.x-destination.x));
            if(slope > 1) {
                // Down
                return Direction.DOWN;
            } else if(slope < -1) {
                // Up
                return Direction.UP;
            } else {
                // Left
                return Direction.RIGHT;
            }
        } else {
            // Above or Below
            if(source.y > destination.y) {
                // Above
                return Direction.UP;
            } else {
                // Below
                return Direction.DOWN;
            }
        }
    }
}
