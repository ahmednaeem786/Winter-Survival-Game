package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.actors.Bear;
import game.actors.Wolf;
import game.actors.Deer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A class representing meadow terrain that can spawn animals with foraging abilities.
 * @author REQ2 Implementation
 */
public class Meadow extends Ground {
    private static final int SPAWN_CADENCE = 7; // Every 7 turns
    private static final double SPAWN_CHANCE = 0.5; // 50% chance
    private game.terrain.Snow.SpawnRule spawnRule;
    private static Random random = new Random();

    public Meadow() {
        super('w', "Meadow");
        this.spawnRule = new MeadowRule();
    }

    /**
     * Meadow spawn rule implementation.
     * Spawns animals every 7 turns with 50% chance, gives them ground consumption ability.
     */
    private static class MeadowRule implements game.terrain.Snow.SpawnRule {
        @Override
        public boolean shouldAttemptSpawn(int globalTurn) {
            // Check if it's a spawn turn (every 7 turns)
            if (globalTurn % SPAWN_CADENCE != 0) {
                return false;
            }
            
            // 50% chance gate
            return random.nextDouble() < SPAWN_CHANCE;
        }

        @Override
        public List<Class<? extends Actor>> allowedSpecies(GameMap map) {
            // Use the map-specific spawn profile from Earth class
            return game.Earth.getAllowedSpecies(map.toString(), Meadow.class);
        }

        @Override
        public void applySpawnEffects(Actor spawned, GameMap map) {
            // Apply ground consumption capability
            // This would allow the animal to consume items on the ground
            // For now, we'll add a comment indicating this capability would be applied
            // TODO: Add ground consumption capability when capability system is available
            
            // The ground consumption effect should work exactly like the Explorer's
            // consumption of apples, yew berries, and hazelnuts
            // Eating a Yew berry will make them unconscious and remove them from the map
        }
    }

    /**
     * Called each turn to potentially spawn animals.
     * @param location the location of this meadow
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        
        // Get current turn from the global turn counter
        int currentTurn = game.terrain.Snow.SpawnHelper.getGlobalTurn();
        
        // Attempt to spawn using the spawn helper
        game.terrain.Snow.SpawnHelper.attemptSpawn(location, spawnRule, currentTurn);
    }

    /**
     * Sets the random number generator for deterministic testing.
     * @param rng the random number generator to use
     */
    public static void setRandom(Random rng) {
        random = rng;
    }
}
