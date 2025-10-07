package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.abilities.Abilities;
import game.actions.AttackAction;
import game.status.BurnEffect;
import game.status.StatusRecipient;
import game.status.StatusRecipientRegistry;
import game.terrain.Fire;
import game.weapons.FlameBreath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The fire elemental state of the Chimera, representing aggressive flame-based combat.
 *
 * In this state, the chimera becomes more aggressive and destructive, using flame breath
 * attacks against enemies.
 *
 * State Transition Rules:
 * - Can only transition to Ice State
 * - Requires minimum 3 turns in fire state
 * - 60% chance to transition to Ice State after conditions are met
 * - 40% chance to remain in Fire State each turn
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 3.3
 */
public class FireState implements ChimeraState {
    private static final Random random = new Random();
    private final IntrinsicWeapon flameBreath = new FlameBreath();
    private int enemiesAttacked = 0;
    private int turnsInState = 0;

    // Burn effect constants
    private static final int BURN_DAMAGE_PER_TURN = 5;
    private static final int BURN_DURATION = 5;

    /**
     * Executes the fire state behavior for the chimera.
     *
     * @param chimera the chimera actor performing the behavior
     * @param actions the list of available actions (unused in this state)
     * @param lastAction the previous action performed (unused in this state)
     * @param map the current game map for spatial context
     * @param display the display object for messages (unused in this method)
     * @return AttackAction with flame breath if enemy found, otherwise aggressive movement
     */
    @Override
    public Action getBehaviorAction(Actor chimera, ActionList actions, Action lastAction,
                                    GameMap map, Display display) {
        turnsInState++;
        Location currentLocation = map.locationOf(chimera);

        for (Exit exit : currentLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();
            if (adjacentLocation.containsAnActor()) {
                Actor target = adjacentLocation.getActor();
                if (!target.hasAbility(Abilities.TAMED)) {
                    enemiesAttacked++;
                    return new FireAttackAction(target, exit.getName(), flameBreath, adjacentLocation, display);
                }
            }
        }
        return aggressiveWander(currentLocation);
    }

    /**
     * Creates a fire attack action that spreads flames to surrounding locations.
     * When executed, this attack burns up to two random adjacent tiles around the target.
     *
     * @param target the actor being attacked
     * @param direction the direction of the attack
     * @param targetLocation the location of the target
     * @param map the current game map
     * @param display the display for attack and fire spread messages
     * @return a FireAttackAction with flame spreading effects
     */
    @Override
    public Action createAttackAction(Actor target, String direction, Location targetLocation,
                                     GameMap map, Display display) {
        return new FireAttackAction(target, direction, flameBreath, targetLocation, display);
    }

    /**
     * Gets the intrinsic weapon for the fire state.
     *
     * @return FlameBreath weapon representing fire-based attacks
     */
    @Override
    public IntrinsicWeapon getStateWeapon() {
        return flameBreath;
    }

    /**
     * Attempts state transition from Fire to Ice state.
     *
     * Transition Logic:
     * - Must spend at least 3 turns in fire state
     * - 60% probability of transitioning to Ice state
     * - 40% probability of remaining in Fire state
     *
     * @param chimera the chimera attempting transition
     * @param map the current game map (unused in this transition)
     * @param display the display for transition messages
     * @return IceState if transition successful, otherwise this state
     */
    @Override
    public ChimeraState attemptStateTransition(Actor chimera, GameMap map, Display display) {
        // Predetermined: Fire can only transition to Ice or Default state
        int chance = random.nextInt(100);

        if (turnsInState >= 3) {
            // Fire -> Ice after 3 turns: 60% chance, 40% stay Fire
            if (chance < 60) {
                display.println("\nThe flames die down as ice crystals form around the chimera!");
                return new IceState();
            }
        }

        return this; // Stay in Fire state
    }

    /**
     * Gets the display name for this state.
     *
     * @return "Fire Chimera" as the fire state name
     */
    @Override
    public String getStateName() {
        return "Fire Chimera";
    }

    /**
     * Gets the map display character for this state.
     *
     * @return 'F' representing the Fire Chimera state
     */
    @Override
    public char getStateDisplayChar() {
        return 'F';
    }

    /**
     * Handles entry into the fire state.
     *
     * @param chimera the chimera entering this state
     * @param map the current game map (unused in this method)
     * @param display the display for entry effect messages
     */
    @Override
    public void onEnterState(Actor chimera, GameMap map, Display display) {
        display.println("Flames engulf the chimera as it enters its fire form!\n");
        enemiesAttacked = 0;
        turnsInState = 0;
    }

    /**
     * Executes aggressive wandering behavior when no enemies are present.
     *
     * Similar to random wandering but represents the more active and aggressive
     * nature of the fire state. The chimera moves unpredictably while seeking combat.
     *
     * Movement Algorithm:
     * 1. Get all available exits from current location
     * 2. Shuffle exits randomly for unpredictable aggressive movement
     * 3. Try each exit in random order until unoccupied destination found
     *
     * @param currentLocation the chimera's current position
     * @return MoveActorAction to random valid destination, or DoNothingAction if blocked
     */
    private Action aggressiveWander(Location currentLocation) {
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
     * Custom attack action that burns two surrounding locations of the target.
     */
    private class FireAttackAction extends AttackAction {
        private Actor target;
        private Location targetLocation;
        private Display display;

        public FireAttackAction(Actor target, String direction, IntrinsicWeapon weapon,
                                Location targetLoc, Display disp) {
            super(target, direction, weapon);
            this.targetLocation = targetLoc;
            this.target = target;
            this.display = disp;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            String result = super.execute(actor, map);

            // Apply burning status effect to the target
            if (target != null && target.isConscious()) {
                applyBurningEffect(target);
            }

            // Burn two random surrounding locations of the target
            burnSurroundings(targetLocation);

            return result + " (Fire spreads around the target!)";
        }

        /**
         * Applies burning status effect to the target actor.
         *
         * @param target the actor to apply burning to
         */
        private void applyBurningEffect(Actor target) {
            StatusRecipient recipient = StatusRecipientRegistry.getRecipient(target);
            if (recipient != null) {
                recipient.addStatusEffect(new BurnEffect(BURN_DURATION, BURN_DAMAGE_PER_TURN));
                display.println(target + " is set ablaze! (5 damage per turn for 5 turns)");
            }
        }

        /**
         * Burns two random surrounding locations of the target.
         *
         * @param center the location of the attacked target
         */
        private void burnSurroundings(Location center) {
            List<Location> validLocations = new ArrayList<>();

            // Find all valid locations to burn
            for (Exit exit : center.getExits()) {
                Location adjacentLocation = exit.getDestination();
                if (!adjacentLocation.containsAnActor() &&
                        adjacentLocation.getGround().getDisplayChar() != '^') {
                    validLocations.add(adjacentLocation);
                }
            }

            // Burn up to 2 random locations
            Collections.shuffle(validLocations, random);
            int burned = 0;
            for (Location loc : validLocations) {
                if (burned >= 2) break;
                loc.setGround(new Fire());
                burned++;
            }

            if (burned > 0) {
                display.println("The fire spreads to "
                        + burned
                        + " surrounding location(s)!");
            }
        }
    }
}