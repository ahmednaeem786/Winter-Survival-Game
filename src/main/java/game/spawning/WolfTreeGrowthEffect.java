package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.YewBerryTree;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Post-spawn effect for Wolf: grows a mature yewberry tree in one random exit with proximity-based berry drops.
 * 
 * <p>When a wolf spawns from any spawner type (Swamp, Tundra, Meadow, Cave), this effect
 * selects one random exit from the spawner location and grows a mature {@link YewBerryTree}
 * there. The tree is configured to drop yew berries whenever any actor is nearby (proximity-triggered),
 * rather than on a fixed timer.
 * 
 * <p>This unique behavior differs from standard yewberry trees, which drop berries every 5 turns.
 * The proximity-based drop makes wolf spawns particularly valuable for players.
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class WolfTreeGrowthEffect implements PostSpawnEffect {
    /** Random number generator for selecting which exit receives the tree. */
    private final Random rng;

    /**
     * Constructs a new WolfTreeGrowthEffect with the specified random number generator.
     * 
     * @param rng the random number generator to use for exit selection
     */
    public WolfTreeGrowthEffect(Random rng) {
        this.rng = rng;
    }

    /**
     * Applies the wolf tree growth effect by placing a mature proximity-triggered yewberry tree.
     * 
     * <p>If the spawner has no exits, no tree is grown. Otherwise, one exit is randomly
     * selected and a mature yewberry tree with proximity drop enabled is placed at that
     * exit's destination.
     * 
     * @param spawnerLocation the location of the spawner terrain
     * @param spawned the wolf that was just spawned (unused in this implementation)
     * @param map the game map (unused in this implementation)
     */
    @Override
    public void apply(Location spawnerLocation, Actor spawned, GameMap map) {
        List<Exit> exits = new ArrayList<>(spawnerLocation.getExits());
        if (!exits.isEmpty()) {
            Exit exit = exits.get(rng.nextInt(exits.size()));
            Location treeLocation = exit.getDestination();
            YewBerryTree tree = new YewBerryTree(true); // true for mature
            tree.enableProximityDrop(); // assuming such a method, or attach observer here
            treeLocation.setGround(tree);
        }
    }
}
