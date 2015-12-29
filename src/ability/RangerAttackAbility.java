package ability;

import character.Character;

import java.util.List;

/**
 * Created by Nathan on 11/28/2015.
 */
public class RangerAttackAbility extends EnemyTargetAbility {
    public RangerAttackAbility() {
        super(4, 5);
        iconFilename = "Assets/ButtonIcons/ArcherAttackIcon.png";
    }
}
