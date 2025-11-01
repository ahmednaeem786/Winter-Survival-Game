package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;
import game.terrain.Snow.SpawnHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

/**
 * Class representing a Wild Apple Tree.
 * Drops apples every 3 turns in surrounding locations.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class WildAppleTree extends Ground {
    private int turnCounter = 0;
    private final boolean canProduce;

    /**
     * Constructor for WildAppleTree.
     */
    public WildAppleTree(boolean canProduce) {
        super('T', "Wild Apple Tree");
        this.canProduce = canProduce;
    }

    /**
     * Default constructor - tree produces fruit
     */
    public WildAppleTree() {
        this(true);
    }

    /**
     * Called every turn to potentially drop an apple.
     *
     * @param location the location of this tree
     */
    @Override
    public void tick(Location location) {
        super.tick(location);

        if (!canProduce) {
            return; // Non-producing trees do nothing
        }

        turnCounter++;
        if (turnCounter >= PlantConstants.TREE_APPLE_DROP_INTERVAL) {
            turnCounter = 0;
            dropItem(location, new Apple());
        }
    }

    /**
     * Drops an item in a random adjacent location if possible.
     *
     * @param treeLocation the location of the tree
     * @param item the item to drop
     */
    private void dropItem(Location treeLocation, edu.monash.fit2099.engine.items.Item item) {
        // Collect adjacent locations that are free (no actor, no items) and not the tree tile itself.
        List<Location> candidates = treeLocation.getExits().stream()
            .map(e -> e.getDestination())
            .filter(dest -> dest != treeLocation) // defensive - shouldn't be equal, but keep for clarity
            .filter(dest -> !dest.containsAnActor())
            .filter(dest -> dest.getItems().isEmpty())
            .collect(Collectors.toList());

        if (candidates.isEmpty()) {
            // No place to drop the apple — do nothing.
            return;
        }

        // Randomly choose one of the free adjacent tiles
        Location chosen = candidates.get(SpawnHelper.getRandom().nextInt(candidates.size()));
        chosen.addItem(item);
    }

    /**
     * Returns true if an actor can enter this location.
     * Trees block movement.
     */
    @Override
    public boolean canActorEnter(Actor actor) {
        return false;
    }

    /**
     * Returns the name of this ground type.
     */
    @Override
    public String toString() {
        return "Wild Apple Tree";
    }
}