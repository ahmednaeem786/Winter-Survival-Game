package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Post-spawn effect for Deer: drops an apple in one random exit of the spawner location.
 * 
 * <p>When a deer spawns from any spawner type (Swamp, Tundra, Meadow, Cave), this effect
 * selects one random exit from the spawner location and places an apple there. This provides
 * a reward/benefit for players who encounter deer spawns.
 * 
 * <p>The effect works independently of which spawner type triggered the deer spawn,
 * ensuring consistent behavior across all spawner types.
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class DeerAppleDropEffect implements PostSpawnEffect {
    /** Random number generator for selecting which exit receives the apple. */
    private final Random rng;

    /**
     * Constructs a new DeerAppleDropEffect with the specified random number generator.
     * 
     * @param rng the random number generator to use for exit selection
     */
    public DeerAppleDropEffect(Random rng) {
        this.rng = rng;
    }

    /**
     * Applies the deer apple drop effect by placing an apple in one random exit.
     * 
     * <p>If the spawner has no exits, no apple is dropped. Otherwise, one exit
     * is randomly selected and an apple is placed at that exit's destination.
     * 
     * @param spawnerLocation the location of the spawner terrain
     * @param spawned the deer that was just spawned (unused in this implementation)
     * @param map the game map (unused in this implementation)
     */
    @Override
    public void apply(Location spawnerLocation, Actor spawned, GameMap map) {
        List<Exit> exits = new ArrayList<>(spawnerLocation.getExits());
        if (!exits.isEmpty()) {
            Exit exit = exits.get(rng.nextInt(exits.size()));
            exit.getDestination().addItem(new Apple());
        }
    }
}
