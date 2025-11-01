package game.behaviors;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Behavior that decrements an Actor's warmth each turn and removes them when warmth reaches zero.
 * 
 * <p>This behavior is used by animals that are affected by cold weather (e.g., Crocodiles).
 * Each turn, the warmth level decreases by 1. When warmth reaches 0, the actor becomes
 * unconscious and is removed from the game map.
 * 
 * <p>The initial warmth value is configurable via the constructor, allowing different
 * animals to have different cold resistance levels.
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class DecayWarmthBehavior {
    /** Current warmth level, decreases each turn. */
    private int warmth;
    
    /** Initial warmth level, used for reset functionality. */
    private final int initialWarmth;

    /**
     * Constructs a new DecayWarmthBehavior with the specified starting warmth.
     * 
     * @param warmthStart the initial warmth level for the actor
     */
    public DecayWarmthBehavior(int warmthStart) {
        this.warmth = warmthStart;
        this.initialWarmth = warmthStart;
    }

    /**
     * Decrements the warmth level by 1. If warmth reaches 0 or below, removes the actor from the map.
     * This method should be called once per turn.
     * 
     * @param actor the actor whose warmth is being managed
     * @param map the game map the actor is on
     */
    public void tick(Actor actor, GameMap map) {
        warmth--;
        if (warmth <= 0) {
            map.removeActor(actor);
        }
    }
    
    /**
     * Resets the warmth level back to the initial value.
     * Useful for testing or restoring warmth after certain events.
     */
    public void reset() {
        this.warmth = initialWarmth;
    }
    
    /**
     * Gets the current warmth level.
     * 
     * @return the current warmth value
     */
    public int getWarmth() {
        return warmth;
    }
}
