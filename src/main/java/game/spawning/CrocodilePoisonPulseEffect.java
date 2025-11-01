package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.status.PoisonEffect;
import game.status.StatusRecipient;
import game.status.StatusRecipientRegistry;
import game.tuning.Tuning;
import game.capabilities.StatusAbilities;

/**
 * Post-spawn effect for Crocodile: poisons all actors in surrounding locations when a crocodile spawns.
 * 
 * <p>When a crocodile spawns from any spawner type (Swamp, Tundra, Meadow, Cave), this effect
 * applies a poison status effect to all actors in the exits (adjacent locations) of the spawner.
 * The poison lasts for 3 turns and deals 10 damage per turn (configurable via
 * {@link Tuning#CROCODILE_PULSE_DURATION} and {@link Tuning#CROCODILE_PULSE_DPT}).
 * 
 * <p>This effect uses the capability pattern to determine which actors can receive status effects,
 * avoiding instanceof checks and following SOLID principles. Only actors with the
 * {@link StatusAbilities#CAN_RECIEVE_STATUS} ability will be poisoned.
 * 
 * <p>This creates a dangerous area-of-effect when crocodiles spawn, making them a significant
 * threat that can damage nearby creatures immediately upon spawning.
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class CrocodilePoisonPulseEffect implements PostSpawnEffect {
    /**
     * Applies the crocodile poison pulse effect to all adjacent actors.
     * 
     * <p>Iterates through all exits of the spawner location and checks if there is an actor
     * at each exit's destination. If an actor is found and they can receive status effects,
     * applies a poison effect with the configured duration and damage per turn.
     * 
     * @param spawnerLocation the location of the spawner terrain
     * @param spawned the crocodile that was just spawned (unused in this implementation)
     * @param map the game map (unused in this implementation)
     */
    @Override
    public void apply(Location spawnerLocation, Actor spawned, GameMap map) {
        for (Exit exit : spawnerLocation.getExits()) {
            Location adj = exit.getDestination();
            Actor a = adj.getActor();
            // Only poison if the actor is capable of receiving effects
            if (a != null && a.hasAbility(StatusAbilities.CAN_RECIEVE_STATUS)) {
                StatusRecipient recip = StatusRecipientRegistry.getRecipient(a);
                if (recip != null) {
                    recip.addStatusEffect(
                        new PoisonEffect(Tuning.CROCODILE_PULSE_DURATION, Tuning.CROCODILE_PULSE_DPT)
                    );
                }
            }
        }
    }
}
