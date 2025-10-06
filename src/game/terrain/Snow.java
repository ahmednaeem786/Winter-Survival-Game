package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
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
     * Each terrain type can have its own spawning behavior.
     */
    public interface SpawnRule {
        /**
         * Determines if spawning should be attempted this turn.
         * @param globalTurn the current global turn number
         * @return true if spawning should be attempted
         */
        boolean shouldAttemptSpawn(int globalTurn);

        /**
         * Gets the list of allowed species for this spawner on the given map.
         * @param map the game map
         * @return list of allowed actor classes
         */
        List<Class<? extends Actor>> allowedSpecies(GameMap map);

        /**
         * Applies spawn effects to the newly spawned actor.
         * @param spawned the newly spawned actor
         * @param map the game map
         */
        void applySpawnEffects(Actor spawned, GameMap map);
    }

    /**
     * Shared spawn helper that handles the common spawning logic.
     * This class provides a centralized way to handle spawning across different terrain types.
     */
    public static class SpawnHelper {
        private static Random random = new Random();
        private static int globalTurn = 0;

        /**
         * Attempts to spawn an animal at the given location using the provided spawn rule.
         * @param location the location to spawn at
         * @param spawnRule the rule governing this spawn attempt
         * @param globalTurn the current global turn
         * @return true if an animal was successfully spawned
         */
        public static boolean attemptSpawn(Location location, SpawnRule spawnRule, int globalTurn) {
            GameMap map = location.map();
            
            // Check if spawning should be attempted
            if (!spawnRule.shouldAttemptSpawn(globalTurn)) {
                return false;
            }

            // Check if location is empty and can be entered
            if (location.containsAnActor()) {
                return false;
            }

            // Get allowed species for this map
            List<Class<? extends Actor>> allowedSpecies = spawnRule.allowedSpecies(map);
            if (allowedSpecies.isEmpty()) {
                return false;
            }

            // Randomly select a species
            Class<? extends Actor> selectedSpecies = allowedSpecies.get(random.nextInt(allowedSpecies.size()));

            try {
                // Create the actor using reflection (will be replaced with factory methods in Part 7)
                Actor spawned = selectedSpecies.getDeclaredConstructor().newInstance();
                
                // Apply spawn effects
                spawnRule.applySpawnEffects(spawned, map);
                
                // Place the actor on the map
                map.addActor(spawned, location);
                return true;
            } catch (Exception e) {
                // If spawning fails, return false
                return false;
            }
        }

        /**
         * Sets the random number generator for deterministic testing.
         * @param rng the random number generator to use
         */
        public static void setRandom(Random rng) {
            random = rng;
        }

        /**
         * Gets the current global turn number.
         * @return the current turn number
         */
        public static int getGlobalTurn() {
            return globalTurn;
        }

        /**
         * Increments the global turn counter.
         * This should be called once per game turn.
         */
        public static void incrementGlobalTurn() {
            globalTurn++;
        }

        /**
         * Sets the global turn counter for testing purposes.
         * @param turn the turn number to set
         */
        public static void setGlobalTurn(int turn) {
            globalTurn = turn;
        }
    }
}
