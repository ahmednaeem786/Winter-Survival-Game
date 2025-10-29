package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import game.items.YewBerry;
import game.terrain.Snow.SpawnHelper;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Class representing a Yew Berry Tree.
 * Drops deadly yew berries every 5 turns in surrounding locations.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public class YewBerryTree extends Ground {
    private int turnCounter = 0;
    private final boolean canProduce;

    /**
     * Constructor for YewBerryTree.
     */
    public YewBerryTree(boolean canProduce) {
        super('Y', "YewBerry Tree");
        this.canProduce = canProduce;
    }

    /**
     * Default constructor - tree produces fruit
     */
    public YewBerryTree() {
        this(true);
    }

    /**
     * Called every turn to potentially drop a yew berry.
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
        if (turnCounter >= PlantConstants.SAPLING_TO_TREE_TURNS) {
            turnCounter = 0;
            dropItem(location, new YewBerry());
        }
    }

    /**
     * Drops an item in a random adjacent location if possible.
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
        return "Yew Berry Tree";
    }
}