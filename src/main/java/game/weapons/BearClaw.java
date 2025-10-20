package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Class representing a bear's claw as an intrinsic weapon.
 * This intrinsic weapon deals 75 damage points with an 80% chance
 * to hit the target.
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class BearClaw extends IntrinsicWeapon {
    public BearClaw() {
        super(75, "claws", 80, "claw");
    }
}
