package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.tuning.Tuning;

/**
 * Intrinsic weapon for Crocodile representing its powerful bite attack.
 * 
 * <p>This weapon deals high damage with a moderate hit rate:
 * <ul>
 *   <li>Damage: 80 hit points</li>
 *   <li>Hit rate: 75%</li>
 *   <li>Verb: "bites" (e.g., "Crocodile bites Player")</li>
 * </ul>
 * 
 * <p>All stats are defined in {@link game.tuning.Tuning}.
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class CrocodileBite extends IntrinsicWeapon {
    /**
     * Constructs a new CrocodileBite weapon with default stats.
     * Uses constants from Tuning class for damage and hit rate.
     */
    public CrocodileBite() {
        super(Tuning.CROCODILE_BITE_DAMAGE, "bites", (int)(Tuning.CROCODILE_BITE_HIT * 100), "bite");
    }
}
