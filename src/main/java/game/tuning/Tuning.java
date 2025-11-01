package game.tuning;

/**
 * Centralized game balancing and tuning constants for animals, effects, and spawners (REQ2).
 * 
 * <p>This class provides a single source of truth for all gameplay constants, ensuring
 * easy balancing and consistency across the game. All tuning values are defined here
 * as public static final fields.
 * 
 * <p>Constants are organized by feature:
 * <ul>
 *   <li>Crocodile statistics (HP, attack damage, hit rate, warmth)</li>
 *   <li>Swamp spawner mechanics (poison duration/damage, spawn probability)</li>
 *   <li>Post-spawn effect parameters (crocodile pulse, bear berry scatter)</li>
 * </ul>
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public final class Tuning {
    /** Crocodile starting hit points. */
    public static final int CROCODILE_HP = 300;
    
    /** Crocodile bite attack damage. */
    public static final int CROCODILE_BITE_DAMAGE = 80;
    
    /** Crocodile bite attack hit rate (0.0 to 1.0). */
    public static final double CROCODILE_BITE_HIT = 0.75;
    
    /** Crocodile starting warmth level. When this reaches 0, crocodile becomes unconscious. */
    public static final int CROCODILE_START_WARMTH = 55;

    /** Duration (in turns) for poison applied to animals spawned from swamps. */
    public static final int SWAMP_POISON_DURATION = 10;
    
    /** Damage per turn for swamp-spawned animal poison. */
    public static final int SWAMP_POISON_DPT = 5;
    
    /** Probability (0.0 to 1.0) that a swamp will spawn an animal when an actor is nearby. */
    public static final double SWAMP_SPAWN_CHANCE_WITH_NEARBY_ACTOR = 0.5;

    /** Duration (in turns) for poison applied to surrounding actors when crocodile spawns. */
    public static final int CROCODILE_PULSE_DURATION = 3;
    
    /** Damage per turn for crocodile spawn poison pulse effect. */
    public static final int CROCODILE_PULSE_DPT = 10;

    /** Probability (0.0 to 1.0) that a yew berry will spawn in each exit when a bear spawns. */
    public static final double BEAR_YEW_BERRY_SPAWN_CHANCE_PER_EXIT = 0.5;

    /**
     * Private constructor to prevent instantiation.
     * This is a utility class containing only static constants.
     */
    private Tuning() {
        // Utility class: do not instantiate
    }
}
