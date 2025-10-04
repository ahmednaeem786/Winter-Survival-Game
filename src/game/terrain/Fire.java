package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.abilities.Abilities;

/**
 * A class representing fire on the ground.
 * Fire lasts for 3 turns and deals burning damage to actors who pass through it.
 * After 3 turns, fire turns the ground into Dirt permanently.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class Fire extends Ground {
    private int turnsRemaining;
    private static final int FIRE_DURATION = 3;
    private static final int BURN_DAMAGE = 5;
    private static final int BURN_DURATION = 5;

    /**
     * Constructor for Fire.
     */
    public Fire() {
        super('^', "Fire");
        this.turnsRemaining = FIRE_DURATION;
    }

    /**
     * Called every turn to manage fire duration and apply burning to actors.
     *
     * @param location the location of this fire
     */
    @Override
    public void tick(Location location) {
        super.tick(location);

        // Apply burning to any actor standing on fire
        if (location.containsAnActor()) {
            Actor actor = location.getActor();
            applyBurning(actor);
        }

        turnsRemaining--;
        if (turnsRemaining <= 0) {
            // Fire burns out, turn ground to Dirt
            location.setGround(new Dirt());
        }
    }

    /**
     * Applies burning effect to an actor.
     * The burning effect deals 5 damage per turn for 5 turns.
     * Multiple burning effects stack.
     *
     * @param actor the actor to apply burning to
     */
    private void applyBurning(Actor actor) {
        // Add or stack burning effect
        if (!actor.hasAbility(Abilities.BURNING)) {
            actor.enableAbility(Abilities.BURNING);
        }

        if (actor.hasAbility(Abilities.CAN_BE_BURNED)) {
            actor.enableAbility(Abilities.NEWLY_BURNED);
        }
    }

    /**
     * Returns the burning damage amount.
     *
     * @return the amount of damage dealt per turn by burning
     */
    public static int getBurnDamage() {
        return BURN_DAMAGE;
    }

    /**
     * Returns the burning duration.
     *
     * @return the number of turns burning lasts
     */
    public static int getBurnDuration() {
        return BURN_DURATION;
    }
}
