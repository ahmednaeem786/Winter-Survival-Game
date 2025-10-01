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
import game.weapons.VenomedStrike;

import java.util.*;

/**
 * The poison elemental state of the Chimera, representing toxic damage-over-time combat.
 *
 * In this state, the chimera becomes a persistent threat through venomous attacks that
 * continue to damage enemies over multiple turns. The poison state emphasizes area denial
 * and sustained damage rather than burst attacks.
 *
 * Combat Characteristics:
 * - Uses VenomedStrike as intrinsic weapon (moderate damage + DOT)
 * - Applies poison DOT effect: -2 health per turn for 3 turns
 * - Tracks poisoned enemies and DOT timers
 * - Maintains distance to let poison effects work
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.9
 */
public class PoisonState implements ChimeraState {

    private static final Random random = new Random();

    private final IntrinsicWeapon venomedStrike = new VenomedStrike();

    private final Map<Actor, Integer> poisonedActors = new HashMap<>();

    private int turnsSinceAttack = 0;

    private int turnsInState = 0;

    /**
     * Executes the poison state behavior for the chimera.
     *
     * Behavior pattern:
     * 1. Increment turn counter and process DOT effects on poisoned enemies
     * 2. Look for new targets to poison (prioritize unpoisoned enemies)
     * 3. If enemies present, attack with venomed strike and apply DOT
     *
     * @param chimera the chimera actor performing the behavior
     * @param actions the list of available actions (unused in this state)
     * @param lastAction the previous action performed (unused in this state)
     * @param map the current game map for spatial context
     * @param display the display object for DOT messages and combat feedback
     * @return AttackAction with venomed strike if enemy found, otherwise stalking movement
     */
    @Override
    public Action getBehaviorAction(Actor chimera, ActionList actions, Action lastAction,
                                    GameMap map, Display display) {
        turnsInState++;

        // Process poison DOT effects first
        processPoisonEffects(map, display);

        Location currentLocation = map.locationOf(chimera);

        game.actors.Chimera chimeraActor = (game.actors.Chimera) chimera;

        // Look for adjacent enemies to poison
        for (Exit exit : currentLocation.getExits()) {
            Location adjacentLocation = exit.getDestination();
            if (adjacentLocation.containsAnActor()) {
                Actor target = adjacentLocation.getActor();

                if (chimeraActor.isTamed()) {
                    if (target.equals(chimeraActor.getTamer()) || target.hasAbility(Abilities.TAMED)) {
                        continue;
                    }
                }

                // Attack non-tamed actors (or all actors if chimera is wild)
                if (!target.hasAbility(Abilities.TAMED)) {

                    applyPoisonEffect(target, display);
                    turnsSinceAttack = 0;
                    return new AttackAction(target, exit.getName(), venomedStrike);
                }
            }
        }

        turnsSinceAttack++;
        return stalkingMovement(currentLocation);
    }

    /**
     * Gets the intrinsic weapon for the poison state.
     *
     * @return VenomedStrike weapon representing toxic attacks with DOT effects
     */
    @Override
    public IntrinsicWeapon getStateWeapon() {
        return venomedStrike;
    }

    /**
     * Attempts state transition from Poison to Fire or Default states.
     *
     * @param chimera the chimera attempting transition
     * @param map the current game map (unused in this transition)
     * @param display the display for transition messages
     * @return FireState if low health, DefaultChimeraState if passive too long, otherwise this state
     */
    @Override
    public ChimeraState attemptStateTransition(Actor chimera, GameMap map, Display display) {
        int chance = random.nextInt(100);

        if (turnsInState >= 4) {
            // Poison -> Fire after 3 turns: 50% chance, 50% stay Poison
            if (chance < 50) {
                display.println("The poison boils away as the chimera erupts in flames!");
                return new FireState();
            }
        } else if (turnsSinceAttack >= 5) {
            // Poison -> Default when passive too long: 30% chance, 70% stay Poison
            if (chance < 30) {
                display.println("The toxins fade as the chimera returns to its natural form!");
                return new DefaultChimeraState();
            }
        }
        return this; // Stay in Poison state
    }

    /**
     * Gets the display name for this state.
     *
     * @return "Poison Chimera" as the poison state name
     */
    @Override
    public String getStateName() {
        return "Poison Chimera";
    }

    /**
     * Gets the map display character for this state.
     *
     * @return 'P' representing the Poison Chimera state
     */
    @Override
    public char getStateDisplayChar() {
        return 'P';
    }

    /**
     * Handles entry into the poison state.
     *
     * @param chimera the chimera entering this state
     * @param map the current game map (unused in this method)
     * @param display the display for entry effect messages
     */
    @Override
    public void onEnterState(Actor chimera, GameMap map, Display display) {
        display.println("Toxic vapors swirl around the chimera as it takes its poisonous form!");
        poisonedActors.clear();
        turnsSinceAttack = 0;
        turnsInState = 0;
    }

    /**
     * Applies poison DOT effect to a target actor.
     * Only poisons non-tamed actors to avoid harming allies.
     */
    private void applyPoisonEffect(Actor target, Display display) {
        poisonedActors.put(target, 3); // 3 turns of poison
        display.println(target + " has been poisoned! (-2 health per turn for 3 turns)");
    }

    /**
     * Processes damage-over-time effects for all poisoned actors.
     *
     * DOT Processing:
     * 1. Iterate through all poisoned actors
     * 2. Apply -2 health damage per turn
     * 3. Decrement remaining poison turns
     * 4. Remove actors when poison expires or they die
     *
     * @param display the display for DOT damage messages
     */
    private void processPoisonEffects(GameMap map, Display display) {
        Iterator<Map.Entry<Actor, Integer>> iterator = poisonedActors.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Actor, Integer> entry = iterator.next();
            Actor poisonedActor = entry.getKey();
            int turnsRemaining = entry.getValue();

            if (poisonedActor == null) {
                iterator.remove();
                continue;
            }

            // Apply poison damage
            poisonedActor.modifyAttribute(BaseAttributes.HEALTH,
                    ActorAttributeOperation.DECREASE, 2);

            int hpAfter = poisonedActor.getAttribute(BaseAttributes.HEALTH);
            int nextTurns = Math.max(0, turnsRemaining - 1);

            display.println(poisonedActor + " (" + hpAfter + "/" +
                    poisonedActor.getMaximumAttribute(BaseAttributes.HEALTH)
                    + ") takes 2 poison damage! (" +
                    nextTurns + " turns remaining)");

            // Death check: remove immediately
            if (hpAfter <= 0 || !poisonedActor.isConscious()) {
                iterator.remove();
                if (map != null && map.contains(poisonedActor)) {
                    map.removeActor(poisonedActor);
                }
                display.println(poisonedActor + " succumbs to poison.\n");
                continue;
            }

            if (nextTurns == 0) {
                iterator.remove();
                display.println(poisonedActor + " recovers from poison.\n");
            } else {
                entry.setValue(nextTurns);
            }
        }
    }

    /**
     * Executes methodical stalking movement seeking new targets to poison.
     *
     * This movement reflects the poison chimera's patient, predatory nature,
     * seeking to spread toxins efficiently rather than rushing into combat.
     *
     * @param currentLocation the chimera's current position
     * @return MoveActorAction to tactically advantageous position, or DoNothingAction if blocked
     */
    private Action stalkingMovement(Location currentLocation) {
        List<Exit> exits = new ArrayList<>(currentLocation.getExits());
        if (!exits.isEmpty()) {
            Collections.shuffle(exits, random);

            // Prefer locations that might have enemies to poison nearby
            for (Exit exit : exits) {
                Location destination = exit.getDestination();
                if (!destination.containsAnActor()) {
                    int nearbyEnemies = 0;
                    for (Exit destExit : destination.getExits()) {
                        Location nearbyLoc = destExit.getDestination();
                        if (nearbyLoc.containsAnActor()) {
                            Actor nearby = nearbyLoc.getActor();
                            if (!nearby.hasAbility(Abilities.TAMED) && !poisonedActors.containsKey(nearby)) {
                                nearbyEnemies++;
                            }
                        }
                    }

                    // Prefer positions with unpoisoned enemies nearby
                    if (nearbyEnemies > 0) {
                        return new MoveActorAction(destination, exit.getName());
                    }
                }
            }

            // If no tactical advantage, move randomly
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