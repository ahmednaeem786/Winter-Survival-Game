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
import game.weapons.WolfBite;

import java.util.*;

/**
 * Class representing a Wolf in the winter survival game.
 *
 * Wolves are aggressive predators that pose a significant threat to the
 * Explorer.
 *
 * Combat characteristics:
 * - 100 hit points
 * - Bite attack: 50 damage with 50% hit rate
 * - Wanders when not engaged in combat
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 3.0
 */
public class Wolf extends TameableAnimal implements Follower, CombatAssistant  {

    /**
     * Random number generator for behavior decisions.
     */
    private Random random = new Random();

    /**
     * Constructs a new Wolf with combat capabilities.
     */
    public Wolf() {
        super("Wolf", 'e', 100, Set.of(Apple.class, YewBerry.class));
        this.setIntrinsicWeapon(new WolfBite());
    }

    @Override
    protected void onTamed() {
        this.enableAbility(Abilities.TAMED);
    }

    /**
     * Defines behavior for wild (untamed) wolves.
     * Wild wolves attack adjacent non-tamed actors and wander randomly.
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

        // Attack any adjacent non-tamed actors
        for (Exit exit : currentLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();
            if (adjacentLocation.containsAnActor()) {
                Actor target = adjacentLocation.getActor();
                // Only attack if target doesn't have TAMED ability (avoiding player)
                if (!target.hasAbility(Abilities.TAMED)) {
                    return new AttackAction(target, exit.getName(), this.getIntrinsicWeapon());
                }
            }
        }

        return wanderRandomly(map);
    }

    /**
     * Defines behavior for tamed wolves.
     * Tamed wolves follow their owner around the map.
     *
     * @param actions available actions for this turn
     * @param lastAction the previous action performed
     * @param map the current game map
     * @param display the display for output
     * @return a follow action to stay near the owner
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
     * Makes the wolf follow its owner by moving towards them.
     * If already adjacent to the owner, the wolf stays in place.
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

        // If already adjacent, stay put
        if (isAdjacentTo(myLocation, tamerLocation)) {
            return new DoNothingAction();
        }

        // Move towards tamer
        return moveTowards(tamerLocation, map);
    }

    /**
     * Determines whether the wolf should follow its owner.
     *
     * @return true if the wolf is tamed and has an owner, false otherwise
     */
    @Override
    public boolean shouldFollow() {
        return tamed && tamer != null;
    }

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

    @Override
    public boolean canAssistInCombat() {
        return tamed && tamer != null;
    }

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
     * Moves the wolf towards a target location using pathfinding.
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

        return new DoNothingAction();
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

    private boolean isAdjacentTo(Location loc1, Location loc2) {
        int deltaX = Math.abs(loc1.x() - loc2.x());
        int deltaY = Math.abs(loc1.y() - loc2.y());
        return deltaX <= 1 && deltaY <= 1 && !(deltaX == 0 && deltaY == 0);
    }
}