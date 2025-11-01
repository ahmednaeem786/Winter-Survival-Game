package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.Earth;
import game.terrain.Snow.SpawnHelper;

import java.util.List;

/**
 * A class representing cave terrain that can spawn animals every 5 turns.
 * @author Reynard Andyti Putra Kaban
 */
public class Cave extends Ground {
    private static final int SPAWN_INTERVAL = 5; // Every 5 turns
    private Snow.SpawnRule spawnRule;

    public Cave() {
        super('C', "Cave");
        this.spawnRule = new CaveRule();
    }

    /**
     * Cave spawn rule implementation.
     * Spawns animals every 5 turns regardless of probability gates.
     */
    private static class CaveRule implements Snow.SpawnRule {
        @Override
        public boolean shouldAttemptSpawn(int globalTurn) {
            return globalTurn % SPAWN_INTERVAL == 0;
        }

        @Override
        public List<Class<? extends Actor>> allowedSpecies(GameMap map) {
            // Use the map-specific spawn profile from Earth class
            return Earth.getAllowedSpecies(map.toString(), Cave.class);
        }

        @Override
        public void applySpawnEffects(Actor spawned, GameMap map) {
            // Cave-specific spawn effects (if any) go here
            // Universal post-spawn effects (deer->apple, bear->berries, wolf->tree, croc->poison)
            // are now handled by Snow.SpawnHelper after actor is added to map
        }
    }

    /**
     * Called each turn to potentially spawn animals.
     * @param location the location of this cave
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        
        // Get current turn from the global turn counter
        int currentTurn = SpawnHelper.getGlobalTurn();
        
        // Attempt to spawn using the spawn helper
        SpawnHelper.attemptSpawn(location, spawnRule, currentTurn);
    }
}
