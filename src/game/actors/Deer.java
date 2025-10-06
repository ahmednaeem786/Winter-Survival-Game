package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.attributes.BaseActorAttribute;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.abilities.Abilities;
import game.items.Apple;
import game.items.YewBerry;
import game.taming.Follower;
import game.taming.ItemCollector;
import game.taming.TameableAnimal;

import java.util.*;

/**
 * Class representing a Deer in the winter survival game.
 *
 * Deer are peaceful animals that wander around the game world. Once tamed,
 * they can collect dropped fruits and give them to their owner.
 *
 * Characteristics:
 * - 50 hit points
 * - Non-aggressive behavior
 * - Can be tamed with apples or yew berries
 * - Collects items for the player when tamed
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 3.5
 */
public class Deer extends TameableAnimal implements ItemCollector, Follower {

    /**
     * Random number generator for movement decisions.
     */
    private Random random = new Random();

    /**
     * List of items collected by this deer.
     */
    private List<Item> collectedItems = new ArrayList<>();

    /**
     * Constructs a new Deer with default characteristics.
     */
    public Deer() {
        super("Deer", 'd', 50, Set.of(Apple.class, YewBerry.class));
        // Initialize warmth attribute for animal spawning system
        this.addNewStatistic(BaseAttributes.WARMTH, new BaseActorAttribute(10));
    }

    @Override
    protected void onTamed() {
        this.enableAbility(Abilities.TAMED);
    }

    /**
     * Defines behavior for wild (untamed) deer.
     * Wild deer simply wander around the map randomly.
     *
     * @param actions available actions for this turn
     * @param lastAction the previous action performed
     * @param map the current game map
     * @param display the display for output
     * @return a random movement action or do nothing if no movement possible
     */
    @Override
    protected Action wildBehavior(ActionList actions, Action lastAction, GameMap map, Display display) {
        return wanderRandomly(map);
    }

    /**
     * Defines behavior for tamed deer.
     * Tamed deer collect nearby items, follow their owner, and give items to them.
     *
     * @param actions available actions for this turn
     * @param lastAction the previous action performed
     * @param map the current game map
     * @param display the display for output
     * @return an action based on current priorities
     */
    @Override
    protected Action tamedBehavior(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Priority 1: Give items to owner if adjacent
        if (isAdjacentToOwner(map)) {
            giveItemsToOwner(display);
        }

        // Priority 2: Collect nearby items
        collectNearbyItems(map, display);

        // Priority 3: Follow owner
        return followOwner(map);
    }

    /**
     * Collects all items from a given location, moves them into the deer's
     * collection, and optionally prints a message to the display.
     *
     * @param location the location from which items are collected
     * @param display  the display for output messages (can be null)
     */
    private void collectItemsFromLocation(Location location, Display display) {
        List<Item> items = new ArrayList<>(location.getItems());
        for (Item item : items) {
            location.removeItem(item);
            collectedItems.add(item);
            if (display != null) {
                display.println("Deer collects an " + item.toString());
            }
        }
    }

    /**
     * Collects items from the deer's current location
     * and all adjacent locations.
     *
     * @param map     the current game map
     * @param display the display for output messages (can be null)
     */
    @Override
    public void collectNearbyItems(GameMap map, Display display) {
        Location myLocation = map.locationOf(this);

        // Collect from current location
        collectItemsFromLocation(myLocation, display);

        // Collect from adjacent locations
        for (Exit exit : myLocation.getExits()) {
            collectItemsFromLocation(exit.getDestination(), display);
        }
    }

    /**
     * Gives collected items to the owner
     * if they are adjacent with display messages.
     *
     * @param display the display for output messages (can be null)
     */
    public void giveItemsToOwner(Display display) {
        if (tamer != null && !collectedItems.isEmpty()) {
            for (Item item : collectedItems) {
                tamer.addItemToInventory(item);
                if (display != null) {
                    display.println("Deer gave explorer an " + item.toString());
                }
            }
            collectedItems.clear();
        }
    }

    /**
     * Returns the list of items currently collected by this deer.
     *
     * @return a copy of the collected items list
     */
    @Override
    public List<Item> getCollectedItems() {
        return new ArrayList<>(collectedItems);
    }

    /**
     * Makes the deer follow its owner by moving towards them.
     * If already adjacent to the owner, the deer stays in place.
     *
     * @param map the current game map
     * @return a movement action towards the owner, or do nothing if already adjacent
     */
    @Override
    public Action followOwner(GameMap map) {
        if (!shouldFollow()) {
            return wanderRandomly(map);
        }

        Location tamerLocation = map.locationOf(tamer);
        Location myLocation = map.locationOf(this);

        // If already adjacent, stay put
        if (isAdjacentTo(myLocation, tamerLocation)) {
            return new DoNothingAction();
        }

        // Move towards tamer
        return moveTowards(tamerLocation, map);
    }

    /**
     * Determines whether the deer should follow its owner.
     *
     * @return true if the deer is tamed and has an owner, false otherwise
     */
    @Override
    public boolean shouldFollow() {
        return tamed && tamer != null;
    }

    /**
     * Checks if the deer is adjacent to its owner.
     *
     * @param map the current game map
     * @return true if adjacent to owner, false otherwise
     */
    private boolean isAdjacentToOwner(GameMap map) {
        if (tamer == null) {
            return false;
        }
        Location tamerLocation = map.locationOf(tamer);
        Location myLocation = map.locationOf(this);
        return isAdjacentTo(myLocation, tamerLocation);
    }

    /**
     * Makes the deer wander randomly when not following specific tasks.
     *
     * @param map the current game map
     * @return a random movement action or do nothing if no movement possible
     */
    private Action wanderRandomly(GameMap map) {
        Location currentLocation = map.locationOf(this);
        List<Exit> exits = new ArrayList<>(currentLocation.getExits());

        if (!exits.isEmpty()) {
            Collections.shuffle(exits, random);
            for (Exit exit : exits) {
                Location destination = exit.getDestination();
                if (!destination.containsAnActor()) {
                    return new MoveActorAction(destination, exit.getName());
                }
            }
        }
        return new DoNothingAction();
    }

    /**
     * Moves the deer towards a target location using pathfinding.
     * Selects the adjacent location that minimizes distance to the target.
     *
     * @param target the location to move towards
     * @param map the current game map
     * @return a movement action towards the target, or do nothing if no valid path
     */
    private Action moveTowards(Location target, GameMap map) {
        Location myLocation = map.locationOf(this);
        List<Exit> exits = new ArrayList<>(myLocation.getExits());

        Exit bestExit = null;
        double shortestDistance = Double.MAX_VALUE;

        // Find the exit that gets us closest to the target
        for (Exit exit : exits) {
            Location destination = exit.getDestination();
            if (!destination.containsAnActor()) {
                double distance = calculateDistance(destination, target);
                if (distance < shortestDistance) {
                    shortestDistance = distance;
                    bestExit = exit;
                }
            }
        }

        if (bestExit != null) {
            return new MoveActorAction(bestExit.getDestination(), bestExit.getName());
        }

        return wanderRandomly(map);
    }

    /**
     * Calculates the Euclidean distance between two locations.
     *
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the distance between the two locations
     */
    private double calculateDistance(Location loc1, Location loc2) {
        int deltaX = loc1.x() - loc2.x();
        int deltaY = loc1.y() - loc2.y();
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY);
    }

    /**
     * Checks if two locations are adjacent to each other.
     *
     * @param loc1 the first location
     * @param loc2 the second location
     * @return true if locations are adjacent, false otherwise
     */
    private boolean isAdjacentTo(Location loc1, Location loc2) {
        int deltaX = Math.abs(loc1.x() - loc2.x());
        int deltaY = Math.abs(loc1.y() - loc2.y());
        return deltaX <= 1 && deltaY <= 1 && !(deltaX == 0 && deltaY == 0);
    }

    /**
     * Factory method to create a default Deer instance for spawning.
     * Used by the animal spawning system.
     *
     * @return a new Deer instance with default characteristics
     */
    public static Deer createDefault() {
        return new Deer();
    }

    /**
     * Applies cold resistance effects to a Deer.
     * Used when spawning from tundra terrain.
     *
     * @param deer the Deer to apply cold resistance to
     */
    public static void applyColdResistant(Deer deer) {
        // Apply cold resistance capability/status
        // For now, this is a placeholder - in a real implementation,
        // this would add a cold resistance capability or status
        // TODO: Implement cold resistance when capability system is available
    }

    /**
     * Applies meadow foraging effects to a Deer.
     * Used when spawning from meadow terrain.
     *
     * @param deer the Deer to apply foraging abilities to
     */
    public static void applyMeadowForaging(Deer deer) {
        // Apply ground consumption capability
        // This would allow the deer to consume items on the ground
        // For now, this is a placeholder - in a real implementation,
        // this would add a ground consumption capability
        // TODO: Implement ground consumption when capability system is available
    }
}