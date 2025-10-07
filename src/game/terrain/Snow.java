package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;

import java.util.List;
import java.util.Random;

/**
 * A class representing snow on the ground.
 * @author Adrian Kristanto
 */
public class Snow extends Ground {
    public Snow() {
        super('.', "Snow");
    }

    /**
     * Interface for defining spawn rules for different terrain types.
     */
    public interface SpawnRule {
        /**
         * Determines if a spawn attempt should be made this turn.
         * @param globalTurn the current global turn number
         * @return true if spawn should be attempted
         */
        boolean shouldAttemptSpawn(int globalTurn);

        /**
         * Gets the list of allowed species for this terrain type on the given map.
         * @param map the game map
         * @return list of allowed actor classes
         */
        List<Class<? extends Actor>> allowedSpecies(GameMap map);

        /**
         * Applies any special effects to a spawned actor.
         * @param spawned the actor that was spawned
         * @param map the game map
         */
        void applySpawnEffects(Actor spawned, GameMap map);
    }

    /**
     * Helper class for managing animal spawning across different terrain types.
     */
    public static class SpawnHelper {
        private static int globalTurn = 0;
        private static Random random = new Random();

        /**
         * Gets the current global turn number.
         * @return the current turn
         */
        public static int getGlobalTurn() {
            return globalTurn;
        }

        /**
         * Increments the global turn counter.
         */
        public static void incrementTurn() {
            globalTurn++;
        }

        /**
         * Attempts to spawn an animal at the given location using the provided spawn rule.
         * @param location the location to spawn at
         * @param spawnRule the rule to use for spawning
         * @param currentTurn the current turn number
         */
        public static void attemptSpawn(Location location, SpawnRule spawnRule, int currentTurn) {
            if (!spawnRule.shouldAttemptSpawn(currentTurn)) {
                return;
            }

            GameMap map = location.map();
            List<Class<? extends Actor>> allowedSpecies = spawnRule.allowedSpecies(map);
            
            if (allowedSpecies.isEmpty()) {
                return;
            }

            // Randomly select a species to spawn
            Class<? extends Actor> speciesClass = allowedSpecies.get(random.nextInt(allowedSpecies.size()));
            
            try {
                Actor spawned = speciesClass.getDeclaredConstructor().newInstance();
                spawnRule.applySpawnEffects(spawned, map);
                map.addActor(spawned, location);
            } catch (Exception e) {
                // If spawning fails, just continue without error
                // This prevents the game from crashing due to spawning issues
            }
        }

        /**
         * Sets the random number generator for deterministic testing.
         * @param rng the random number generator to use
         */
        public static void setRandom(Random rng) {
            random = rng;
        }
    }
}
