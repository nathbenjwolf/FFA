package utils;

/**
 * Created by Nathan on 11/21/2015.
 */
public abstract class Globals {
    private static int characterId = 0;

    public static int getCharacterId() {
        return characterId++;
    }

}
