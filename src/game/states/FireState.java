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
import game.weapons.FlameBreath;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * Fire state - the aggressive flame-breathing form.
 *
 * Characteristics:
 * - Highly aggressive behavior seeking multiple targets
 * - Flame breath attack (80 damage, 65% hit rate)
 * - More aggressive than default state
 *
 * This is the first of four elemental states for the chimera.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.4
 */
public class FireState implements ChimeraState {
    private static final Random random = new Random();
    private final Weapon flameBreath = new FlameBreath();

    @Override
    public Action getBehaviorAction(Actor chimera, ActionList actions, Action lastAction,
                                    GameMap map, Display display) {
        Location currentLocation = map.locationOf(chimera);

        for (Exit exit : currentLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();
            if (adjacentLocation.containsAnActor()) {
                Actor target = adjacentLocation.getActor();
                if (!target.hasAbility(Abilities.TAMED)) {
                    return new AttackAction(target, exit.getName(), flameBreath);
                }
            }
        }

        return aggressiveWander(currentLocation);
    }

    @Override
    public Weapon getStateWeapon() {
        return flameBreath;
    }

    @Override
    public String getStateName() {
        return "Fire Chimera";
    }

    @Override
    public char getStateDisplayChar() {
        return 'F';
    }

    /**
     * Aggressive wandering behavior - moves randomly but prefers open areas.
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
}