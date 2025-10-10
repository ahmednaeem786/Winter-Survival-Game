package game.states;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actions.MoveActorAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.weapons.IntrinsicWeapon;
import game.abilities.Abilities;
import game.actions.AttackAction;
import game.actors.Chimera;
import game.status.PoisonEffect;
import game.status.StatusRecipient;
import game.status.StatusRecipientRegistry;
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
 * @version 3.3
 */
public class PoisonState implements ChimeraState {

    private static final Random random = new Random();

    private final IntrinsicWeapon venomedStrike = new VenomedStrike();

    private final Map<Actor, PoisonTracker> poisonedActors = new HashMap<>();

    private int turnsSinceAttack = 0;

    private int turnsInState = 0;

    // Poison effect constants
    private static final int POISON_DAMAGE_PER_TURN = 2;
    private static final int POISON_DURATION = 3;

    /**
     * Tracks poison information for display purposes
     */
    private static class PoisonTracker {
        int turnsRemaining;

        PoisonTracker(int turns) {
            this.turnsRemaining = turns;
        }
    }


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

        // Display poison status for all poisoned actors
        updatePoisonTracking(map, display);

        Location currentLocation = map.locationOf(chimera);

        Chimera chimeraActor = (Chimera) chimera;

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
                    turnsSinceAttack = 0;
                    return new PoisonAttackAction(target, exit.getName(), venomedStrike, display);
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

    @Override
    public Action createAttackAction(Actor target, String direction, Location targetLocation,
                                     GameMap map, Display display) {
        return new PoisonAttackAction(target, direction, getStateWeapon(), display);
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
     * Updates poison tracking and displays status messages for poisoned actors.
     * Removes dead actors from tracking.
     */
    private void updatePoisonTracking(GameMap map, Display display) {
        Iterator<Map.Entry<Actor, PoisonTracker>> iterator = poisonedActors.entrySet().iterator();

        while (iterator.hasNext()) {
            Map.Entry<Actor, PoisonTracker> entry = iterator.next();
            Actor actor = entry.getKey();
            PoisonTracker tracker = entry.getValue();

            // Check if actor is dead or unconscious
            if (!actor.isConscious() || actor.getAttribute(edu.monash.fit2099.engine.actors.attributes.BaseAttributes.HEALTH) <= 0) {
                iterator.remove();
                if (map != null && map.contains(actor)) {
                    map.removeActor(actor);
                    display.println(actor + " succumbs to poison and dies!\n");
                }
                continue;
            }

            // Display poison status
            if (tracker.turnsRemaining > 0) {
                display.println(actor + " is poisoned! (" + tracker.turnsRemaining + " turns remaining)");
                tracker.turnsRemaining--;
            } else {
                // Poison expired
                iterator.remove();
                display.println(actor + " recovers from poison.\n");
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

    /**
     * Processes damage-over-time effects for all poisoned actors.
     *
     * DOT Processing:
     * 1. Iterate through all poisoned actors
     * 2. Apply -2 health damage per turn
     * 3. Decrement remaining poison turns
     * 4. Remove actors when poison expires or they die
     *
     */
    private class PoisonAttackAction extends AttackAction {
        private Actor target;
        private Display display;

        public PoisonAttackAction(Actor target, String direction, IntrinsicWeapon weapon, Display disp) {
            super(target, direction, weapon);
            this.target = target;
            this.display = disp;
        }

        @Override
        public String execute(Actor actor, GameMap map) {
            // Check health before attack
            int healthBefore = target.getAttribute(BaseAttributes.HEALTH);

            // Execute attack
            String result = super.execute(actor, map);

            // Check health after attack
            int healthAfter = target.getAttribute(BaseAttributes.HEALTH);

            // Only poison if attack actually hit
            if (healthAfter < healthBefore && target.isConscious()) {
                applyPoisonEffect(target);
                result += "\n" + target + " has been poisoned! (-2 HP per turn for 3 turns)";
            }

            return result;
        }

        /**
         * Applies poison status effect to the target actor.
         *
         * @param target the actor to apply poison to
         */
        private void applyPoisonEffect(Actor target) {
            StatusRecipient recipient = StatusRecipientRegistry.getRecipient(target);
            if (recipient != null) {
                recipient.addStatusEffect(new PoisonEffect(POISON_DURATION, POISON_DAMAGE_PER_TURN));
                poisonedActors.put(target, new PoisonTracker(POISON_DURATION));
            }
        }
    }
}