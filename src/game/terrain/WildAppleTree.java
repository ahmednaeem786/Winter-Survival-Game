package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.Apple;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

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
    private static final Random RNG = new Random();

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
        if (turnCounter >= 3) {
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
        List<Exit> exits = new ArrayList<>(treeLocation.getExits());
        Collections.shuffle(exits, RNG); // randomize order so apples don't always drop in same direction

        for (Exit exit : exits) {
            Location dest = exit.getDestination();
            // prefer locations with no actor and no items
            if (!dest.containsAnActor() && dest.getItems().isEmpty()) {
                dest.addItem(item);
                return;
            }
        }

        // fallback: if no adjacent free tile available, place on the tree tile
        treeLocation.addItem(item);
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