package game.actors;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.abilities.Abilities;
import game.actions.AttackAction;
import game.items.Apple;
import game.items.YewBerry;
import game.states.ChimeraState;
import game.states.DefaultChimeraState;
import game.taming.TameableAnimal;
import game.taming.Follower;
import game.taming.CombatAssistant;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

/**
 * A mystical chimera creature that transforms between elemental forms using the State Pattern.
 *
 * The Chimera follows predetermined state transitions:
 * - Default State: Can only transition to Fire State (60% chance after 3 turns)
 * - Fire State: Can transition to Ice State (60% chance after 3 turns)
 * - Ice State: Can transition to Fire (70% when surrounded) or Default (50% when isolated 4+ turns)
 *
 * This implementation extends TameableAnimal and implements Follower and CombatAssistant interfaces,
 * allowing it to be tamed by players and assist in combat while following its owner.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 3.1
 */
public class Chimera extends TameableAnimal implements Follower, CombatAssistant {

    /**
     * Current state of the chimera, determining behavior and appearance.
     */
    private ChimeraState currentState;

    /**
     * Random number generator for behavior decisions.
     */
    private Random random = new Random();

    /**
     * Creates a new Chimera starting in default state.
     */
    public Chimera() {
        super("Chimera", 'C', 350, Set.of(Apple.class, YewBerry.class));
        this.currentState = new DefaultChimeraState();
        this.setIntrinsicWeapon(currentState.getStateWeapon());
    }

    /**
     * Called when the chimera is successfully tamed.
     * Enables the TAMED ability which affects combat targeting and behavior.
     */
    @Override
    protected void onTamed() {
        this.enableAbility(Abilities.TAMED);
    }

    /**
     * Handles state transitions for both wild and tamed chimeras.
     */
    private void handleStateTransition(GameMap map, Display display) {
        ChimeraState newState = currentState.attemptStateTransition(this, map, display);

        if (newState != currentState) {
            this.currentState = newState;
            this.setIntrinsicWeapon(newState.getStateWeapon());
            newState.onEnterState(this, map, display);

            newState.applyBuffsToAllies(this, map, display);
            String status = tamed ? "Tamed" : "Wild";
            display.println(status + " Chimera is now in state: " + newState.getStateName() + " (" + newState.getStateDisplayChar() + ")\n");
        }
    }

    /**
     * Defines behavior for wild (untamed) chimeras.
     * Wild chimeras delegate to their current state for behavior.
     *
     * @param actions available actions for this turn
     * @param lastAction the previous action performed
     * @param map the current game map
     * @param display the display for output
     * @return action determined by current state
     */
    @Override
    protected Action wildBehavior(ActionList actions, Action lastAction, GameMap map, Display display) {
        handleStateTransition(map, display);
        return currentState.getBehaviorAction(this, actions, lastAction, map, display);
    }

    /**
     * Defines behavior for tamed chimeras.
     * Tamed chimeras assist in combat and follow their owner.
     *
     * @param actions available actions for this turn
     * @param lastAction the previous action performed
     * @param map the current game map
     * @param display the display for output
     * @return a combat or follow action based on current situation
     */
    @Override
    protected Action tamedBehavior(ActionList actions, Action lastAction, GameMap map, Display display) {
        handleStateTransition(map, display);

        // Run state behavior to update internal counters (but don't use the returned action)
        currentState.getBehaviorAction(this, actions, lastAction, map, display);

        // Priority 1: Combat assistance
        Action combatAction = findCombatTarget(map);
        if (combatAction != null) {
            return combatAction;
        }

        // Priority 2: Follow owner
        return followOwner(map);
    }

    /**
     * Changes the chimera's current state and updates all related properties.
     */
    private void setState(ChimeraState newState, GameMap map, Display display) {
        this.currentState = newState;
        this.setIntrinsicWeapon(newState.getStateWeapon());
        newState.onEnterState(this, map, display);

        newState.applyBuffsToAllies(this, map, display);
    }

    /**
     * Makes the chimera follow its owner by moving towards them.
     * If already adjacent to the owner, the chimera stays in place.
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
     * Determines whether the chimera should follow its owner.
     *
     * @return true if the chimera is tamed and has an owner, false otherwise
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
     * Checks if the chimera can assist in combat.
     *
     * @return true if tamed and has an owner, false otherwise
     */
    @Override
    public boolean canAssistInCombat() {
        return tamed && tamer != null;
    }

    /**
     * Gets the current state for external inspection.
     *
     * @return the current state of the chimera
     */
    public ChimeraState getCurrentState() {
        return currentState;
    }

    /**
     * Moves the chimera towards a target location using pathfinding.
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

        // Check if adjacent - if so, attack using state-specific attack
        for (Exit exit : myLocation.getExits()) {
            if (exit.getDestination().equals(targetLocation)) {
                Display display = new Display();
                return currentState.createAttackAction(target, exit.getName(), targetLocation, map, display);
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

    /**
     * Applies ice armor buff (+5 max health) to the chimera's tamer if tamed.
     * Only affects tamed chimeras with valid tamers.
     *
     * @param display the display for buff notifications
     */
    public void applyIceArmorToTamer(Display display) {
        if (tamed && tamer != null) {
            int tBefore = tamer.getMaximumAttribute(BaseAttributes.HEALTH);
            tamer.modifyStatsMaximum(BaseAttributes.HEALTH, ActorAttributeOperation.INCREASE, 5);
            int tAfter = tamer.getMaximumAttribute(BaseAttributes.HEALTH);
            display.println("Ice armor extends to " + tamer
                    + ", increasing their resilience! (Max Health: "
                    + tBefore + " → " + tAfter + ")\n");
        }
    }


    /**
     * Returns a string representation of the chimera showing current state information.
     * Displays state name, display character, and current/maximum health values.
     *
     * @return formatted string with chimera status information
     */
    @Override
    public String toString() {
        return String.format("%s [%c] (%d/%d)",
                currentState.getStateName(),
                currentState.getStateDisplayChar(),
                this.getAttribute(BaseAttributes.HEALTH),
                this.getMaximumAttribute(BaseAttributes.HEALTH));
    }
}
