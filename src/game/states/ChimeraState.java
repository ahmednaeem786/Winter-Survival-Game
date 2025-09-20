package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.weapons.Weapon;

/**
 * Interface representing different forms a chimera can take.
 * This is the foundation for the State Pattern implementation.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.0
 */
public interface ChimeraState {

    /**
     * Determines the behavior action for this state.
     */
    Action getBehaviorAction(Actor chimera, ActionList actions, Action lastAction,
                             GameMap map, Display display);

    /**
     * Gets the intrinsic weapon for this state.
     */
    Weapon getStateWeapon();

    /**
     * Gets the display name of this state.
     */
    String getStateName();

    /**
     * Gets the display character for the chimera in this state.
     */
    char getStateDisplayChar();
}