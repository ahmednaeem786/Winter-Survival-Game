package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.BaseActorAttribute;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.abilities.Abilities;
import game.actions.AttackAction;
import game.items.Apple;
import game.items.YewBerry;
import game.taming.TameableAnimal;
import game.weapons.CrocodileBite;
import game.tuning.Tuning;

import java.util.*;

/**
 * A Crocodile is a new animal added in REQ2 that can spawn in designated biomes and spawners.
 * 
 * <p>Crocodiles are aggressive predators with the following characteristics:
 * <ul>
 *   <li>300 hit points</li>
 *   <li>Bite attack: 80 damage with 75% hit rate</li>
 *   <li>Warmth level: Starts at 55, decreases each turn. At 0, crocodile becomes unconscious and is removed</li>
 *   <li>Wandering behavior: Moves randomly when no targets are nearby</li>
 *   <li>Tamable: Can be tamed with Apple or YewBerry</li>
 * </ul>
 * 
 * <p>When spawned, crocodiles trigger a post-spawn effect that poisons all actors
 * in the surrounding area for 3 turns (10 damage per turn).
 * 
 * <p>All stats are defined in {@link game.tuning.Tuning}.
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class Crocodile extends TameableAnimal {
    /** Random number generator for movement decisions. */
    private final Random random = new Random();

    /**
     * Constructs a new Crocodile with default characteristics.
     * Initializes with stats from Tuning constants and sets up the bite weapon.
     */
    public Crocodile() {
        super("Crocodile", '<', Tuning.CROCODILE_HP, Set.of(Apple.class, YewBerry.class));
        this.setIntrinsicWeapon(new CrocodileBite());
        this.addNewStatistic(BaseAttributes.WARMTH, new BaseActorAttribute(Tuning.CROCODILE_START_WARMTH));
    }

    /**
     * Called when the crocodile is successfully tamed.
     * Enables the TAMED ability to mark the crocodile as tamed.
     */
    @Override
    protected void onTamed() {
        this.enableAbility(Abilities.TAMED);
    }

    /**
     * Defines behavior for wild (untamed) crocodiles.
     * Wild crocodiles attack adjacent non-tamed actors and wander randomly.
     * 
     * @param actions available actions for this turn
     * @param lastAction the previous action performed
     * @param map the current game map
     * @param display the display for output
     * @return an attack action if target available, otherwise random movement
     */
    @Override
    protected Action wildBehavior(ActionList actions, Action lastAction, GameMap map, Display display) {
        Location currentLocation = map.locationOf(this);
        // If the crocodile is no longer on the map (e.g., died from consuming poison), return do nothing
        if (currentLocation == null || !map.contains(this)) {
            return new DoNothingAction();
        }
        for (Exit exit : currentLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();
            if (adjacentLocation.containsAnActor()) {
                Actor target = adjacentLocation.getActor();
                if (!target.hasAbility(Abilities.TAMED)) {
                    return new AttackAction(target, exit.getName(), this.getIntrinsicWeapon());
                }
            }
        }
        return wanderRandomly(map);
    }

    /**
     * Defines behavior for tamed crocodiles.
     * Tamed crocodiles follow their owner and stay adjacent to them.
     * 
     * @param actions available actions for this turn
     * @param lastAction the previous action performed
     * @param map the current game map
     * @param display the display for output
     * @return a movement action towards the owner, or do nothing if already adjacent
     */
    @Override
    protected Action tamedBehavior(ActionList actions, Action lastAction, GameMap map, Display display) {
        if (tamer == null) {
            return new DoNothingAction();
        }
        Location myLocation = map.locationOf(this);
        Location tamerLocation = map.locationOf(tamer);
        // If the crocodile or tamer is no longer on the map, return do nothing
        if (myLocation == null || tamerLocation == null || !map.contains(this) || !map.contains(tamer)) {
            return new DoNothingAction();
        }
        if (isAdjacentTo(myLocation, tamerLocation)) {
            return new DoNothingAction();
        }
        return moveTowards(tamerLocation, map);
    }

    /**
     * Makes the crocodile wander randomly to an unoccupied adjacent location.
     * 
     * @param map the current game map
     * @return a random movement action, or do nothing if no valid move
     */
    private Action wanderRandomly(GameMap map) {
        Location currentLocation = map.locationOf(this);
        // If the crocodile is no longer on the map (e.g., died from consuming poison), return do nothing
        if (currentLocation == null || !map.contains(this)) {
            return new DoNothingAction();
        }
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
     * Moves the crocodile towards a target location using pathfinding.
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
        return new DoNothingAction();
    }

    /**
     * Checks if two locations are adjacent (including diagonals).
     * 
     * @param loc1 the first location
     * @param loc2 the second location
     * @return true if the locations are adjacent, false otherwise
     */
    private boolean isAdjacentTo(Location loc1, Location loc2) {
        int deltaX = Math.abs(loc1.x() - loc2.x());
        int deltaY = Math.abs(loc1.y() - loc2.y());
        return deltaX <= 1 && deltaY <= 1 && !(deltaX == 0 && deltaY == 0);
    }

    /**
     * Calculates the Euclidean distance between two locations.
     * 
     * @param loc1 the first location
     * @param loc2 the second location
     * @return the distance between the two locations
     */
    private double calculateDistance(Location loc1, Location loc2) {
        int dx = loc1.x() - loc2.x();
        int dy = loc1.y() - loc2.y();
        return Math.sqrt(dx * dx + dy * dy);
    }
}
