package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Class representing a flame breath attack as an intrinsic weapon.
 * This intrinsic weapon deals 80 damage points with a 65% chance
 * to hit the target. Used by chimeras in their fire state.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class FlameBreath extends IntrinsicWeapon {
    public FlameBreath() {
        super(80, "breathes fire on", 65, "flame breath");
    }
}