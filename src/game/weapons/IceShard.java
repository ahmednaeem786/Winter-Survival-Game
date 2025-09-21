
package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * Class representing an ice shard attack as an intrinsic weapon.
 * This intrinsic weapon deals 50 damage points with an 85% chance
 * to hit the target. Used by chimeras in their ice state.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class IceShard extends IntrinsicWeapon {
    public IceShard() {
        super(50, "launches ice shards at", 85, "ice shard");
    }
}
