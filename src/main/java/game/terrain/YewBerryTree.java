package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;
import game.terrain.PlantConstants;
import game.terrain.Snow.SpawnHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class representing a YewBerry Tree that can drop yew berries.
 *
 * <p>YewBerry trees support two modes of berry dropping:
 * <ul>
 *   <li>Standard mode: Drops a yew berry every 5 turns automatically</li>
 *   <li>Proximity mode: Drops a yew berry immediately whenever any actor is nearby (used for wolf spawn effect in REQ2)</li>
 * </ul>
 *
 * <p>When proximity drop is enabled (via {@link #enableProximityDrop()}), the tree
 * checks each turn if any actor is in an adjacent location. If so, it drops a yew berry
 * in an available exit location. This provides instant rewards when players approach
 * trees grown by wolf spawns.
 *
 * <p>Trees can be mature (can produce fruit) or immature (cannot produce).
 * Only mature trees drop berries.
 *
 * @author Muhamad Shafy Dimas Rafarrel (original implementation)
 * @author Reynard Andyti Putra Kaban (REQ2: proximity drop enhancement)
 * @version 2.0
 */
public class YewBerryTree extends Ground {
    /** Turn counter for standard timer-based berry drops. */
    private int turnCounter = 0;

    /** Whether this tree can produce fruit (mature trees can produce). */
    private final boolean canProduce;

    /** Whether proximity-based berry drops are enabled (REQ2 feature). */
    private boolean proximityDropEnabled = false;

    /**
     * Constructs a new YewBerryTree with the specified production capability.
     *
     * @param canProduce true if the tree is mature and can produce fruit, false otherwise
     */
    public YewBerryTree(boolean canProduce) {
        super('Y', "YewBerry Tree");
        this.canProduce = canProduce;
    }

    /**
     * Constructs a new mature YewBerryTree that can produce fruit.
     * This is the default constructor for normal yewberry trees.
     */
    public YewBerryTree() {
        this(true);
    }

    /**
     * Called each turn to potentially drop a yew berry.
     *
     * <p>Drop behavior depends on the tree's configuration:
     * <ul>
     *   <li>If proximity drop is enabled: Drops immediately when any actor is nearby</li>
     *   <li>If proximity drop is disabled: Drops every 5 turns (standard behavior)</li>
     * </ul>
     *
     * <p>If the tree cannot produce (immature), no berries are dropped.
     *
     * @param location the location of this tree
     */
    @Override
    public void tick(Location location) {
        super.tick(location);
        if (!canProduce) return;
        
        if (proximityDropEnabled) {
            // Proximity mode: drop when an actor is nearby (replaces 5-turn timer)
            // This is the unique behavior for wolf-spawned trees
            if (actorNearby(location)) {
                dropItem(location, new YewBerry());
            }
        } else {
            // Standard mode: drop every 5 turns (see REQ1)
            turnCounter++;
            if (turnCounter >= PlantConstants.SAPLING_TO_TREE_TURNS) {
                turnCounter = 0;
                dropItem(location, new YewBerry());
            }
        }
    }
    /**
     * Drops an item in a random adjacent location if possible.
     *
     * <p>Searches for the first available exit location that is not occupied by an actor
     * and does not already contain items. Places the item there. Uses shuffled exits for randomness.
     *
     * @param here the location of the tree
     * @param item the item to drop
     */
    private void dropItem(Location here, edu.monash.fit2099.engine.items.Item item) {
        List<Exit> exits = new ArrayList<>(here.getExits());
        Collections.shuffle(exits, SpawnHelper.getRandom());

        for (Exit exit : exits) {
            Location dest = exit.getDestination();
            if (!dest.containsAnActor() && dest.getItems().isEmpty()) {
                dest.addItem(item);
                return;
            }
        }
    }
    /**
     * Checks if any actor is in an adjacent location to this tree.
     *
     * @param location the location of the tree
     * @return true if any actor is in an adjacent location, false otherwise
     */
    private boolean actorNearby(Location location) {
        return location.getExits().stream().anyMatch(e -> e.getDestination().containsAnActor());
    }
    /**
     * Enables proximity-based berry drops (REQ2 feature).
     *
     * <p>When enabled, the tree will drop berries immediately when any actor is nearby,
     * rather than waiting for the 5-turn timer. This is used for trees grown by wolf spawns.
     */
    public void enableProximityDrop() {
        this.proximityDropEnabled = true;
    }

    /**
     * Checks if proximity-based berry drops are enabled.
     *
     * @return true if proximity drops are enabled, false otherwise
     */
    public boolean proximityDropEnabled() {
        return proximityDropEnabled;
    }

    /**
     * Checks if this tree is mature (can produce fruit).
     *
     * @return true if the tree is mature, false if it cannot produce
     */
    public boolean isMature() {
        return canProduce;
    }
    /**
     * Trees block movement - actors cannot enter locations with yewberry trees.
     *
     * @param actor the actor attempting to enter
     * @return always false - trees block movement
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Returns the name of this ground type.
     *
     * @return "Yew Berry Tree"
     */
    @Override
    public String toString() {
        return "Yew Berry Tree";
    }
}