package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Class representing a wolf's bite as an intrinsic weapon.
 * This intrinsic weapon deals 50 damage points with a 50% chance
 * to hit the target.
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class WolfBite extends IntrinsicWeapon {
    public WolfBite() {
        super(50, "bites", 50, "bite");
    }
}
