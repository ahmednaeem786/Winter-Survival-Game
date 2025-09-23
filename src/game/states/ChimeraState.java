package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.actors.Chimera;

/**
 * Interface defining the State Pattern contract for Chimera elemental transformations.
 *
 * This interface enables chimeras to dynamically change their behavior, appearance,
 * and combat capabilities based on their current elemental state.
 *
 * Each state manages its own internal counters, transition conditions, and behavioral logic
 * while providing a consistent interface for the Chimera class to interact with.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 2.1
 */
public interface ChimeraState {
    /**
     * Determines the behavior action for this state on the current turn.
     *
     * Each state implements unique behavioral patterns.
     *
     * @param chimera the chimera actor executing the behavior
     * @param actions the list of available actions for this turn
     * @param lastAction the action performed in the previous turn
     * @param map the current game map for spatial context
     * @param display the display object for outputting messages
     * @return the action to be performed this turn
     */
    Action getBehaviorAction(Actor chimera, ActionList actions, Action lastAction,
                             GameMap map, Display display);

    /**
     * Gets the intrinsic weapon associated with this elemental state.
     *
     * Each state provides a unique weapon that reflects its elemental nature:
     * - Default: Basic physical attacks (BearClaw)
     * - Fire: Flame-based attacks with area effects
     * - Ice: Precise crystalline projectiles (IceShard)
     *
     * @return the IntrinsicWeapon used in this state
     */
    IntrinsicWeapon getStateWeapon();

    /**
     * Attempts to transition to a different state based on predetermined conditions.
     *
     * State transitions are governed by specific rules and probability checks:
     * - Time-based transitions (turns spent in current state)
     * - Probability-based decisions using random chance
     *
     * @param chimera the chimera attempting the transition
     * @param map the current game map for environmental context
     * @param display the display for transition announcement messages
     * @return the new state to transition to, or current state if no transition occurs
     */
    ChimeraState attemptStateTransition(Actor chimera, GameMap map, Display display);

    /**
     * Gets the display name of this state for user interface purposes.
     *
     * @return a human-readable string representing the state name
     */
    String getStateName();

    /**
     * Gets the character symbol used to display this state on the game map.
     *
     * Each state has a unique character for visual identification:
     * - Default: 'C' for Chimera
     * - Fire: 'F' for Fire Chimera
     * - Ice: 'I' for Ice Chimera
     *
     * @return the character symbol for this state
     */
    char getStateDisplayChar();

    /**
     * Called when the chimera enters this state.
     *
     * Handles state initialization, counter resets, and any special effects
     * that occur during state transitions. May include:
     * - Resetting internal counters and tracking variables
     * - Applying stat modifications or buffs
     * - Displaying transition messages to the player
     * - Affecting the tamer if the chimera is tamed
     *
     * @param chimera the chimera entering this state
     * @param map the current game map for context
     * @param display the display for entry messages and effects
     */
    void onEnterState(Actor chimera, GameMap map, Display display);

    /**
     * Default implementation for applying state-specific buffs to chimera allies.
     *
     * @param chimera the chimera entering this state
     * @param map the current game map
     * @param display the display for buff notifications
     */
    default void applyBuffsToAllies(Chimera chimera, GameMap map, Display display) {
        // Default: no buffs to allies
    }
}