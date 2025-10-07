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
 * A class representing tundra terrain that can spawn cold-resistant animals.
 * @author REQ2 Implementation
 */
public class Tundra extends Ground {
    private static final double SPAWN_CHANCE = 0.05; // 5% chance
    private static final int HEALTH_BOOST = 10;
    private game.terrain.Snow.SpawnRule spawnRule;
    private static Random random = new Random();

    public Tundra() {
        super('_', "Tundra");
        this.spawnRule = new TundraRule();
    }

    /**
     * Tundra spawn rule implementation.
     * Spawns animals with 5% chance each turn, gives them +10 HP and cold resistance.
     */
    private static class TundraRule implements game.terrain.Snow.SpawnRule {
        @Override
        public boolean shouldAttemptSpawn(int globalTurn) {
            return random.nextDouble() < SPAWN_CHANCE;
        }

        @Override
        public List<Class<? extends Actor>> allowedSpecies(GameMap map) {
            // Use the map-specific spawn profile from Earth class
            return game.Earth.getAllowedSpecies(map.toString(), Tundra.class);
        }

        @Override
        public void applySpawnEffects(Actor spawned, GameMap map) {
            // Apply +10 HP boost
            if (spawned.hasStatistic(edu.monash.fit2099.engine.actors.attributes.BaseAttributes.HEALTH)) {
                spawned.modifyAttribute(
                    edu.monash.fit2099.engine.actors.attributes.BaseAttributes.HEALTH,
                    edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation.INCREASE,
                    HEALTH_BOOST
                );
            }
            
            // Apply cold resistance capability
            if (spawned instanceof game.taming.TameableAnimal) {
                game.taming.TameableAnimal animal = (game.taming.TameableAnimal) spawned;
                
                // Enable cold resistance ability
                animal.enableAbility(game.abilities.Abilities.COLD_RESISTANCE);
            }
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
