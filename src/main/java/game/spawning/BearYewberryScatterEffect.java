package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;
import game.tuning.Tuning;
import java.util.Random;

/**
 * Post-spawn effect for Bear: scatters yew berries in surrounding exits with 50% chance per exit.
 * 
 * <p>When a bear spawns from any spawner type (Swamp, Tundra, Meadow, Cave), this effect
 * iterates through all exits of the spawner location. For each exit, there is a 50% chance
 * (configurable via {@link Tuning#BEAR_YEW_BERRY_SPAWN_CHANCE_PER_EXIT}) that a yew berry
 * will be placed at that exit's destination.
 * 
 * <p>This creates a scattered pattern of yew berries around the spawn location, providing
 * valuable resources for players who encounter bear spawns.
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class BearYewberryScatterEffect implements PostSpawnEffect {
    /** Random number generator for determining which exits receive yew berries. */
    private final Random rng;

    /**
     * Constructs a new BearYewberryScatterEffect with the specified random number generator.
     * 
     * @param rng the random number generator to use for probability checks
     */
    public BearYewberryScatterEffect(Random rng) {
        this.rng = rng;
    }

    /**
     * Applies the bear yew berry scatter effect.
     * 
     * <p>For each exit of the spawner location, randomly determines (based on the configured
     * probability) whether to place a yew berry at that exit's destination.
     * 
     * @param spawnerLocation the location of the spawner terrain
     * @param spawned the bear that was just spawned (unused in this implementation)
     * @param map the game map (unused in this implementation)
     */
    @Override
    public void apply(Location spawnerLocation, Actor spawned, GameMap map) {
        for (Exit exit : spawnerLocation.getExits()) {
            if (rng.nextDouble() < Tuning.BEAR_YEW_BERRY_SPAWN_CHANCE_PER_EXIT) {
                exit.getDestination().addItem(new YewBerry());
            }
        }
    }
}
