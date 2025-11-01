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
import game.weapons.BearClaw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * The default elemental state of the Chimera, representing its natural form.
 *
 * This state serves as the baseline chimera behavior with moderate aggression
 * and basic physical attacks. The chimera exhibits opportunistic combat behavior,
 * attacking adjacent enemies when encountered and wandering randomly otherwise.
 *
 * State Transition Rules:
 * - Can only transition to Fire State
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 3.1
 */
public class DefaultChimeraState implements ChimeraState {
    private static final Random random = new Random();
    private final IntrinsicWeapon defaultAttack = new BearClaw();
    private int turnsInState = 0;

    /**
     * Executes the default state behavior for the chimera.
     *
     * Behavior pattern:
     * 1. Increment turn counter for state transition tracking
     * 2. Check all adjacent locations for enemy actors
     * 3. Attack the first non-tamed actor found
     * 4. If no enemies present, wander randomly
     *
     * @param chimera the chimera actor performing the behavior
     * @param actions the list of available actions (unused in this state)
     * @param lastAction the previous action performed (unused in this state)
     * @param map the current game map for spatial context
     * @param display the display object for messages (unused in this method)
     * @return AttackAction if enemy found, otherwise random movement or DoNothing
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
                    return new AttackAction(target, exit.getName(), defaultAttack);
                }
            }
        }

        return wanderRandomly(currentLocation);
    }

    /**
     * Gets the intrinsic weapon for the default state.
     *
     * @return BearClaw weapon representing basic physical attacks
     */
    @Override
    public IntrinsicWeapon getStateWeapon() {
        return defaultAttack;
    }

    /**
     * Attempts state transition from Default to Fire state.
     *
     * Transition Logic:
     * - 60% probability of transitioning to Fire state
     * - 40% probability of remaining in Default state
     *
     * @param chimera the chimera attempting transition
     * @param map the current game map (unused in this transition)
     * @param display the display for transition messages
     * @return FireState if transition successful, otherwise this state
     */
    @Override
    public ChimeraState attemptStateTransition(Actor chimera, GameMap map, Display display) {
        // Predetermined: Default can only transition to Fire state
        if (turnsInState >= 3) {
            int chance = random.nextInt(100);
            if (chance < 60) {
                display.println("\nThe chimera's body begins to glow with inner fire!");
                return new FireState();
            }
        }
        return this;
    }

    /**
     * Gets the display name for this state.
     *
     * @return "Chimera" as the default state name
     */
    @Override
    public String getStateName() {
        return "Chimera";
    }

    /**
     * Gets the map display character for this state.
     *
     * @return 'C' representing the default Chimera state
     */
    @Override
    public char getStateDisplayChar() {
        return 'c';
    }

    /**
     * Handles entry into the default state.
     * Resets the turn counter to begin tracking time for next transition.
     *
     * @param chimera the chimera entering this state
     * @param map the current game map (unused in this method)
     * @param display the display object (unused in this method)
     */
    @Override
    public void onEnterState(Actor chimera, GameMap map, Display display) {
        turnsInState = 0;
    }

    /**
     * Executes random wandering behavior when no enemies are present.
     *
     * @param currentLocation the chimera's current position
     * @return MoveActorAction to random valid destination, or DoNothingAction if stuck
     */
    private Action wanderRandomly(Location currentLocation) {
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

    @Override
    public Action createAttackAction(Actor target, String direction, Location targetLocation,
                                     GameMap map, Display display) {
        return new AttackAction(target, direction, getStateWeapon());
    }
}