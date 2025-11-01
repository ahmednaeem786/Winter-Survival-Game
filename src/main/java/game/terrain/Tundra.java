package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.Earth;
import game.abilities.Abilities;
import game.terrain.Snow.SpawnHelper;

import java.util.List;
import java.util.Random;

/**
 * A class representing tundra terrain that can spawn cold-resistant animals.
 * @author Reynard Andyti Putra Kaban
 */
public class Tundra extends Ground {
    private static final double SPAWN_CHANCE = 0.05; // 5% chance
    private static final int HEALTH_BOOST = 10;
    private Snow.SpawnRule spawnRule;
    private static Random random = new Random();

    public Tundra() {
        super('_', "Tundra");
        this.spawnRule = new TundraRule();
    }

    /**
     * Tundra spawn rule implementation.
     * Spawns animals with 5% chance each turn, gives them +10 HP and cold resistance.
     */
    private static class TundraRule implements Snow.SpawnRule {
        @Override
        public boolean shouldAttemptSpawn(int globalTurn) {
            return random.nextDouble() < SPAWN_CHANCE;
        }

        @Override
        public List<Class<? extends Actor>> allowedSpecies(GameMap map) {
            // Use the map-specific spawn profile from Earth class
            return Earth.getAllowedSpecies(map.toString(), Tundra.class);
        }

        @Override
        public void applySpawnEffects(Actor spawned, GameMap map) {
            // Apply +10 max HP boost
            if (spawned.hasStatistic(BaseAttributes.HEALTH)) {
                int beforeMaxHP = spawned.getMaximumAttribute(BaseAttributes.HEALTH);
                
                // Increase maximum health
                spawned.modifyStatsMaximum(
                    BaseAttributes.HEALTH,
                    ActorAttributeOperation.INCREASE,
                    HEALTH_BOOST
                );
                spawned.modifyAttribute(
                    BaseAttributes.HEALTH,
                    ActorAttributeOperation.INCREASE,
                    HEALTH_BOOST
                );
            }
            // Apply cold resistance capability to all spawned actors
            // All actors spawned from Tundra get this ability
            spawned.enableAbility(Abilities.COLD_RESISTANCE);
        }
    }

    /**
     * Called each turn to potentially spawn animals.
     * @param location the location of this tundra
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        
        // Get current turn from the global turn counter
        int currentTurn = SpawnHelper.getGlobalTurn();
        
        // Attempt to spawn using the spawn helper
        SpawnHelper.attemptSpawn(location, spawnRule, currentTurn);
    }

    /**
     * Sets the random number generator for deterministic testing.
     * @param rng the random number generator to use
     */
    public static void setRandom(Random rng) {
        random = rng;
    }
}
