package game.spawning;

import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.GameMap;

/**
 * Strategy interface for post-spawn effects applied immediately after an animal spawns.
 * 
 * <p>This interface allows different post-spawn behaviors to be defined for each animal type,
 * such as dropping items, applying status effects, or modifying the world state. Effects
 * are registered via {@link PostSpawnEffectRegistry} and applied automatically after
 * spawning, regardless of which spawner type (Swamp, Tundra, Meadow, Cave) is used.
 * 
 * <p>Implementations of this interface should be registered in {@link PostSpawnEffectRegistry}
 * during world initialization. The effects are applied by the spawning system after
 * the actor has been added to the map.
 * 
 * <p>Examples of post-spawn effects:
 * <ul>
 *   <li>Deer: Drops an apple in one random exit</li>
 *   <li>Bear: Scatters yew berries with 50% chance per exit</li>
 *   <li>Wolf: Grows a mature yewberry tree in one random exit</li>
 *   <li>Crocodile: Poisons all surrounding actors</li>
 * </ul>
 * 
 * @author Reynard Andyti Putra Kaban (REQ2 implementation)
 * @version 1.0
 */
@FunctionalInterface
public interface PostSpawnEffect {
    /**
     * Applies the post-spawn effect for the spawned actor.
     * 
     * <p>This method is called immediately after an animal is spawned and added to the map.
     * The spawnerLocation parameter refers to the location where the spawner terrain is,
     * which may be the same as where the actor was placed, or adjacent to it.
     * 
     * @param spawnerLocation the location of the spawner terrain (where the effect should be applied)
     * @param spawned the actor that was just spawned
     * @param map the game map the actor was spawned on
     */
    void apply(Location spawnerLocation, Actor spawned, GameMap map);
}
