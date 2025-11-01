package game.terrain;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Ground;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.Exit;
import game.tuning.Tuning;
import game.status.PoisonEffect;
import game.status.StatusRecipient;
import game.status.StatusRecipientRegistry;
import game.spawning.AnimalRegistry;
import game.spawning.PostSpawnEffect;
import game.spawning.PostSpawnEffectRegistry;
import game.Earth;

import java.util.List;
import java.util.Random;

/**
 * Swamp terrain tile that spawns animals when actors are nearby.
 * 
 * <p>Swamps are a new spawner type added in REQ2 with unique spawning mechanics:
 * <ul>
 *   <li>Spawn condition: Only attempts to spawn if at least one adjacent tile contains an actor</li>
 *   <li>Spawn probability: 50% chance when the condition is met (configurable via {@link Tuning#SWAMP_SPAWN_CHANCE_WITH_NEARBY_ACTOR})</li>
 *   <li>Swamp poison: All animals spawned from swamps receive poison for 10 turns (5 damage/turn)</li>
 *   <li>Allowed species: Determined by map-specific spawn profiles in {@link Earth}</li>
 * </ul>
 * 
 * <p>Spawn configuration:
 * <ul>
 *   <li>Forest map: Swamps can spawn Crocodiles and Deer</li>
 *   <li>Plains map: Swamps can only spawn Crocodiles</li>
 * </ul>
 * 
 * <p>After spawning, animals also receive any registered post-spawn effects (e.g., crocodile poison pulse,
 * deer apple drop) via the {@link PostSpawnEffectRegistry}.
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
public class Swamp extends Ground {
    /** Display character for swamp terrain. */
    private static final char SWAMP_CHAR = '~';
    
    /** Random number generator for spawn probability checks. */
    private final Random rng = new Random();

    /**
     * Constructs a new Swamp terrain tile.
     */
    public Swamp() {
        super(SWAMP_CHAR, "Swamp");
    }

    /**
     * Called each turn to potentially spawn animals if conditions are met.
     * 
     * <p>Spawn conditions (all must be true):
     * <ol>
     *   <li>At least one adjacent tile contains an actor</li>
     *   <li>Random probability check passes (50% chance)</li>
     *   <li>The spawner location is not already occupied</li>
     *   <li>The map has allowed species for this spawner type</li>
     * </ol>
     * 
     * <p>When an animal spawns, it receives:
     * <ul>
     *   <li>Swamp-specific poison (10 turns, 5 damage/turn)</li>
     *   <li>Any registered post-spawn effects for that animal type</li>
     * </ul>
     * 
     * @param location the location of this swamp tile
     */
    @Override
    public void tick(Location location) {
        GameMap map = location.map();
        String mapName = map.toString();
        List<Class<? extends Actor>> allowed = Earth.getAllowedSpecies(mapName, Swamp.class);
        if (allowed.isEmpty()) return;
        boolean actorNearby = location.getExits().stream()
                .anyMatch(exit -> exit.getDestination().containsAnActor());
        if (!actorNearby) return;
        if (rng.nextDouble() >= Tuning.SWAMP_SPAWN_CHANCE_WITH_NEARBY_ACTOR) return;
        if (location.containsAnActor()) return;
        Class<? extends Actor> toSpawn = allowed.get(rng.nextInt(allowed.size()));
        try {
            Actor spawned = AnimalRegistry.create(toSpawn);
            StatusRecipient recip = StatusRecipientRegistry.getRecipient(spawned);
            if (recip != null) {
                recip.addStatusEffect(new PoisonEffect(Tuning.SWAMP_POISON_DURATION, Tuning.SWAMP_POISON_DPT));
            }
            map.addActor(spawned, location);
            PostSpawnEffect effect = PostSpawnEffectRegistry.getFor(toSpawn);
            if (effect != null) {
                effect.apply(location, spawned, map);
            }
        } catch (Exception e) {
            // swallow; don't disrupt world!
        }
    }
}
