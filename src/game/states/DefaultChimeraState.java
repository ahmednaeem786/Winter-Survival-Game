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
import edu.monash.fit2099.engine.weapons.Weapon;
import game.abilities.Abilities;
import game.actions.AttackAction;
import game.weapons.BearClaw;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Default neutral state for the chimera.
 * Basic aggressive behavior similar to other wild creatures.
 * This serves as the foundation before implementing the main states.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.2
 */
public class DefaultChimeraState implements ChimeraState {
    private static final Random random = new Random();
    private final Weapon defaultAttack = new BearClaw();

    @Override
    public Action getBehaviorAction(Actor chimera, ActionList actions, Action lastAction,
                                    GameMap map, Display display) {
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

        // Wander randomly if no targets
        return wanderRandomly(currentLocation);
    }

    @Override
    public Weapon getStateWeapon() {
        return defaultAttack;
    }

    @Override
    public String getStateName() {
        return "Chimera";
    }

    @Override
    public char getStateDisplayChar() {
        return 'C';
    }

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
}