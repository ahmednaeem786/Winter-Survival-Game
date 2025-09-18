package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.abilities.Abilities;
import game.actions.AttackAction;
import game.items.Apple;
import game.items.YewBerry;
import game.taming.CombatAssistant;
import game.taming.Follower;
import game.taming.TameableAnimal;
import game.weapons.BearClaw;

import java.util.*;

/**
 * Class representing a Bear in the winter survival game.
 *
 * Bears are aggressive predators that pose a significant threat to the
 * Explorer, but can be tamed to become powerful allies.
 *
 * Combat characteristics:
 * - 200 hit points
 * - Claw attack: 75 damage with 80% hit rate
 * - Wanders when not engaged in combat
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 3.1
 */
public class Bear extends TameableAnimal implements Follower, CombatAssistant {

    /**
     * Random number generator for behavior decisions.
     */
    private Random random = new Random();

    /**
     * Constructs a new Bear with combat capabilities.
     */
    public Bear() {
        super("Bear", 'B', 200, Set.of(Apple.class, YewBerry.class));
        this.setIntrinsicWeapon(new BearClaw());
    }

    @Override
    protected void onTamed() {
        this.enableAbility(Abilities.TAMED);
    }

    /**
     * Defines behavior for wild (untamed) bears.
     * Wild bears attack any adjacent actors and wander randomly.
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

        // Attack any adjacent actors
        for (Exit exit : currentLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();
            if (adjacentLocation.containsAnActor()) {
                Actor target = adjacentLocation.getActor();
                // Attack any actor that doesn't have TAMED ability
                if (!target.hasAbility(Abilities.TAMED)) {
                    return new AttackAction(target, exit.getName(), this.getIntrinsicWeapon());
                }
            }
        }

        return wanderRandomly(map);
    }

    /**
     * Defines behavior for tamed bears.
     * Tamed bears assist in combat and follow their owner around the map.
     *
     * @param actions available actions for this turn
     * @param lastAction the previous action performed
     * @param map the current game map
     * @param display the display for output
     * @return a combat or follow action based on current situation
     */
    @Override
    protected Action tamedBehavior(ActionList actions, Action lastAction, GameMap map, Display display) {
        // Priority 1: Combat assistance
        Action combatAction = findCombatTarget(map);
        if (combatAction != null) {
            return combatAction;
        }

        // Priority 2: Follow owner
        return followOwner(map);
    }

    /**
     * Makes the bear follow its owner by moving towards them.
     * If already adjacent to the owner, the bear stays in place.
     *
     * @param map the current game map
     * @return a movement action towards the owner, or do nothing if already adjacent
     */
    @Override
    public Action followOwner(GameMap map) {
        if (!shouldFollow()) {
            return new DoNothingAction();
        }

        Location tamerLocation = map.locationOf(tamer);
        Location myLocation = map.locationOf(this);

        if (isAdjacentTo(myLocation, tamerLocation)) {
            return new DoNothingAction();
        }

        return moveTowards(tamerLocation, map);
    }

    /**
     * Determines whether the bear should follow its owner.
     *
     * @return true if the bear is tamed and has an owner, false otherwise
     */
    @Override
    public boolean shouldFollow() {
        return tamed && tamer != null;
    }

    /**
     * Finds hostile actors threatening the owner and engages them.
     *
     * @param map the current game map
     * @return an action to attack or move toward threats, null if no threats found
     */
    @Override
    public Action findCombatTarget(GameMap map) {
        if (!canAssistInCombat()) {
            return null;
        }

        Location tamerLocation = map.locationOf(tamer);

        // Check if tamer is being threatened
        for (Exit exit : tamerLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();
            if (adjacentLocation.containsAnActor()) {
                Actor potentialThreat = adjacentLocation.getActor();
                if (potentialThreat != this && !potentialThreat.hasAbility(Abilities.TAMED)) {
                    return moveTowardsOrAttack(potentialThreat, map);
                }
            }
        }
        return null;
    }

    /**
     * Checks if the bear can assist in combat.
     *
     * @return true if tamed and has an owner, false otherwise
     */
    @Override
    public boolean canAssistInCombat() {
        return tamed && tamer != null;
    }

    /**
     * Makes the bear wander randomly when not engaged in specific tasks.
     *
     * @param map the current game map
     * @return a random movement action or do nothing if no valid moves
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
     * Moves the bear towards a target location using pathfinding.
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
     * Calculates the distance between two locations.
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
     * Moves toward or attacks a target actor based on proximity.
     *
     * @param target the actor to engage
     * @param map the current game map
     * @return an attack action if adjacent, otherwise a movement action
     */
    private Action moveTowardsOrAttack(Actor target, GameMap map) {
        Location targetLocation = map.locationOf(target);
        Location myLocation = map.locationOf(this);

        // Check if adjacent - if so, attack
        for (Exit exit : myLocation.getExits()) {
            if (exit.getDestination().equals(targetLocation)) {
                return new AttackAction(target, exit.getName(), this.getIntrinsicWeapon());
            }
        }

        // Otherwise move towards target
        return moveTowards(targetLocation, map);
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
}