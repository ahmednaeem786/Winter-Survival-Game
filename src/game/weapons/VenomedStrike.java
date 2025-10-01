package game.weapons;

import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;

/**
 * An intrinsic weapon representing venomous strikes that apply damage over time.
 *
 * This weapon is used by chimeras in their Poison State, dealing moderate initial damage
 * with a lingering toxic effect. The venomed strike applies a damage-over-time effect
 * that continues to harm the target for multiple turns after the initial attack.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class VenomedStrike extends IntrinsicWeapon {

    /**
     * Creates a new VenomedStrike weapon with predetermined combat statistics.
     *
     * Weapon characteristics:
     * - Damage: 45 points initial (moderate with DOT supplement)
     * - Hit Chance: 80% (reliable for poison application)
     *
     * The DOT effect is handled by the PoisonState class when attacks are made.
     */
    public VenomedStrike() {
        super(45, "strikes with venomed claws at", 80, "venomed strike");
    }
}