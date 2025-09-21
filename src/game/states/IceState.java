package game.states;

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
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.abilities.Abilities;
import game.actions.AttackAction;
import game.weapons.IceShard;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The ice elemental state of the Chimera, representing defensive combat.
 *
 * In this state, the chimera becomes more defensive and strategic. The ice state emphasizes survival and tactical
 * advantages, including health bonuses for both the chimera and its tamer.
 *
 * Combat Characteristics:
 * - Uses IceShard as intrinsic weapon (precise, high-accuracy attacks)
 * - Grants ice armor buff (+5 max health) to chimera and tamer on entry
 * - Seeks defensive positions with fewer exits when possible
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 2.4
 */
public class IceState implements ChimeraState {
    private static final Random random = new Random();
    private final IntrinsicWeapon iceShard = new IceShard();
    private int turnsAlone = 0;
    private int turnsInState = 0;

    /**
     * Executes the ice state behavior for the chimera.
     *
     * Behavior pattern:
     *
     * The ice state prioritizes defensive positioning and precise strikes over
     * aggressive advancement, reflecting the calculated nature of ice tactics.
     *
     * @param chimera the chimera actor performing the behavior
     * @param actions the list of available actions (unused in this state)
     * @param lastAction the previous action performed (unused in this state)
     * @param map the current game map for spatial context
     * @param display the display object for messages (unused in this method)
     * @return AttackAction with ice shard if enemy found, otherwise defensive movement
     */
    @Override
    public Action getBehaviorAction(Actor chimera, ActionList actions, Action lastAction,
                                    GameMap map, Display display) {
        turnsInState++;
        Location currentLocation = map.locationOf(chimera);
        List<Actor> adjacentEnemies = findAdjacentEnemies(currentLocation);

        if (adjacentEnemies.isEmpty()) {
            turnsAlone++;
        } else {
            turnsAlone = 0;
            for (Exit exit : currentLocation.getExits()) {
                Location adjacentLocation = exit.getDestination();
                if (adjacentLocation.containsAnActor()) {
                    Actor target = adjacentLocation.getActor();
                    if (!target.hasAbility(Abilities.TAMED)) {
                        return new AttackAction(target, exit.getName(), iceShard);
                    }
                }
            }
        }
        return defensiveMovement(currentLocation);
    }

    /**
     * Gets the intrinsic weapon for the ice state.
     *
     * @return IceShard weapon representing precise crystalline attacks
     */
    @Override
    public IntrinsicWeapon getStateWeapon() {
        return iceShard;
    }


    @Override
    public ChimeraState attemptStateTransition(Actor chimera, GameMap map, Display display) {
        int chance = random.nextInt(100);

        if (turnsInState >= 3) {
            // Ice -> Poison after 3 turns: 40% chance, 60% stay Ice
            if (chance < 40) {
                display.println("The ice transforms into toxic vapors as the chimera seeks a different approach!\n");
                return new PoisonState();
            }
        } else if (turnsAlone >= 4) {
            // Ice -> Default when alone too long: 30% chance, 70% stay Ice
            if (chance < 30) {
                display.println("The ice melts away as the chimera returns to its natural form!\n");
                return new DefaultChimeraState();
            }
        }
        return this; // Stay in Ice state
    }

    /**
     * Gets the display name for this state.
     *
     * @return "Ice Chimera" as the ice state name
     */
    @Override
    public String getStateName() {
        return "Ice Chimera";
    }

    /**
     * Gets the map display character for this state.
     *
     * @return 'I' representing the Ice Chimera state
     */
    @Override
    public char getStateDisplayChar() {
        return 'I';
    }

    /**
     * Handles entry into the ice state with comprehensive buff effects.
     *
     * The ice armor represents the protective shell that forms
     * around ice chimeras and their allies, providing increased resilience and protection.
     *
     * @param chimera the chimera entering this state
     * @param map the current game map (unused in this method)
     * @param display the display for entry effects and buff notifications
     */
    @Override
    public void onEnterState(Actor chimera, GameMap map, Display display) {
        display.println("Frost spreads as the chimera takes its ice form!\n");
        turnsAlone = 0;
        turnsInState = 0;

        // Give ice armor buff - increase max health by 5 (only happens once per ice transition)
        int beforeMax = chimera.getMaximumAttribute(BaseAttributes.HEALTH);
        chimera.modifyStatsMaximum(BaseAttributes.HEALTH,
               ActorAttributeOperation.INCREASE, 5);
        int afterMax = chimera.getMaximumAttribute(BaseAttributes.HEALTH);

        display.println("Ice armor forms around the chimera, increasing its resilience! (Max Health: "
                + beforeMax + " → " + afterMax + ")");

        // If the chimera is tamed, also give the buff to the tamer
        if (chimera instanceof game.taming.Tameable) {
            game.taming.Tameable tameableChimera = (game.taming.Tameable) chimera;

            if (tameableChimera.isTamed() && tameableChimera.getTamer() != null) {
                Actor tamer = tameableChimera.getTamer();
                int tamerBeforeMax = tamer.getMaximumAttribute(BaseAttributes.HEALTH);
                tamer.modifyStatsMaximum(BaseAttributes.HEALTH,
                        ActorAttributeOperation.INCREASE, 5);
                int tamerAfterMax = tamer.getMaximumAttribute(BaseAttributes.HEALTH);

                display.println("Ice armor extends to " + tamer + ", increasing their resilience! (Max Health: "
                        + tamerBeforeMax + " → " + tamerAfterMax + ")\n");
            }
        }
    }

    /**
     * Finds all adjacent enemy actors for threat assessment.
     *
     * Scans all exits from the given location and identifies non-tamed actors
     * as potential threats. Used for both combat targeting and state transition logic.
     *
     * @param center the location to scan around for enemies
     * @return list of adjacent enemy actors (may be empty)
     */
    private List<Actor> findAdjacentEnemies(Location center) {
        List<Actor> enemies = new ArrayList<>();
        for (Exit exit : center.getExits()) {
            Location adjacentLoc = exit.getDestination();
            if (adjacentLoc.containsAnActor()) {
                Actor actor = adjacentLoc.getActor();
                if (!actor.hasAbility(Abilities.TAMED)) {
                    enemies.add(actor);
                }
            }
        }
        return enemies;
    }

    /**
     * Executes defensive movement seeking tactical positioning.
     *
     * This movement pattern reflects the ice chimera's preference for
     * defensible positions and strategic retreats over aggressive advancement.
     *
     * @param currentLocation the chimera's current position
     * @return MoveActorAction to tactically advantageous position, or DoNothingAction if blocked
     */
    private Action defensiveMovement(Location currentLocation) {
        List<Exit> exits = new ArrayList<>(currentLocation.getExits());
        if (!exits.isEmpty()) {
            Exit bestExit = null;
            int minExits = Integer.MAX_VALUE;

            for (Exit exit : exits) {
                Location destination = exit.getDestination();
                if (!destination.containsAnActor()) {
                    int exitCount = destination.getExits().size();
                    if (exitCount < minExits) {
                        minExits = exitCount;
                        bestExit = exit;
                    }
                }
            }

            if (bestExit != null) {
                return new MoveActorAction(bestExit.getDestination(), bestExit.getName());
            }

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
