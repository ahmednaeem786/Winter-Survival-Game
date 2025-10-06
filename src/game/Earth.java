package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.Bear;
import game.actors.Deer;
import game.actors.Player;
import game.actors.Wolf;
import game.terrain.HazelnutTree;
import game.terrain.Snow;
import game.terrain.WildAppleTree;
import game.terrain.YewBerryTree;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing the game world (Earth) for the winter survival game.
 *
 * This class constructs and initializes the entire game world, including
 * the terrain, player character, and all wildlife.
 * It serves as the main world builder that
 * creates the survival scenario.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 1.8
 */
public class Earth extends World {

    /**
     * Map-specific spawn profiles defining which species can spawn from which terrain types.
     * Key: Map name, Value: Map of Ground class to List of allowed Actor classes
     */
    private static final Map<String, Map<Class<?>, List<Class<? extends Actor>>>> SPAWN_PROFILES;

    static {
        SPAWN_PROFILES = new HashMap<>();
        
        // Forest map spawn profile
        Map<Class<?>, List<Class<? extends Actor>>> forestProfile = new HashMap<>();
        forestProfile.put(game.terrain.Tundra.class, Arrays.asList(Bear.class));
        forestProfile.put(game.terrain.Cave.class, Arrays.asList(Bear.class, Wolf.class, Deer.class));
        forestProfile.put(game.terrain.Meadow.class, Arrays.asList(Deer.class));
        SPAWN_PROFILES.put("Forest", forestProfile);
        
        // Plains map spawn profile
        Map<Class<?>, List<Class<? extends Actor>>> plainsProfile = new HashMap<>();
        plainsProfile.put(game.terrain.Tundra.class, Arrays.asList(Wolf.class));
        plainsProfile.put(game.terrain.Cave.class, Arrays.asList(Bear.class, Wolf.class));
        plainsProfile.put(game.terrain.Meadow.class, Arrays.asList(Deer.class, Bear.class));
        SPAWN_PROFILES.put("Plains", plainsProfile);
    }

    /**
     * Constructs a new Earth world with the specified display.
     *
     * @param display the Display object for rendering the game world
     */
    public Earth(Display display) {
        super(display);
    }

    /**
     * Constructs the complete game world including terrain, player, and wildlife.
     *
     * This method builds the entire winter survival environment:
     * - Creates a snow-covered forest map
     * - Distributes various animals throughout the world for encounters
     *
     *
     * @throws Exception if there are issues with world construction
     */
    public void constructWorld() throws Exception {
        DefaultGroundCreator groundCreator = new DefaultGroundCreator();
        groundCreator.registerGround('.', Snow::new);

        List<String> map = Arrays.asList(
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................",
                "........................................"
        );

        GameMap gameMap = new GameMap("Forest", groundCreator, map);
        this.addGameMap(gameMap);

        Player player = new Player("Explorer", 'ඞ', 100);
        this.addPlayer(player, gameMap.at(1, 1));

        populateWithAnimals(gameMap);
    }

    /**
     * Populates the game map with various animals for survival encounters.
     *
     * This method strategically places different types of wildlife across
     * the map. The placement ensures players will encounter
     * various threats and opportunities while exploring
     * the winter forest.
     *
     * @param gameMap the GameMap to populate with animals
     * @throws GameEngineException if there are issues placing actors on the map
     */
    private void populateWithAnimals(GameMap gameMap) throws GameEngineException  {
        gameMap.addActor(new Bear(), gameMap.at(12, 7));

        gameMap.addActor(new Wolf(), gameMap.at(15,3));
        gameMap.addActor(new Wolf(), gameMap.at(21,0));

        gameMap.addActor(new Deer(), gameMap.at(9, 1));
        gameMap.addActor(new Deer(), gameMap.at(27, 2));

        gameMap.at(4, 1).setGround(new WildAppleTree());
        gameMap.at(30, 4).setGround(new WildAppleTree());

        gameMap.at(12, 4).setGround(new HazelnutTree());
        gameMap.at(30, 7).setGround(new HazelnutTree());

        gameMap.at(3, 4).setGround(new YewBerryTree());
        gameMap.at(22, 5).setGround(new YewBerryTree());

        //Trees that cannot produce fruit
        gameMap.at(2, 8).setGround(new WildAppleTree(false));
        gameMap.at(10, 9).setGround(new HazelnutTree(false));
        gameMap.at(23, 9).setGround(new YewBerryTree(false));

        // Ensure required spawners exist for the map
        ensureRequiredSpawners(gameMap);
    }

    /**
     * Gets the spawn profile for a specific map.
     * 
     * @param mapName the name of the map
     * @return the spawn profile for the map, or empty map if not found
     */
    public static Map<Class<?>, List<Class<? extends Actor>>> getSpawnProfile(String mapName) {
        return SPAWN_PROFILES.getOrDefault(mapName, new HashMap<>());
    }

    /**
     * Gets the allowed species for a specific terrain type on a specific map.
     * 
     * @param mapName the name of the map
     * @param terrainClass the class of the terrain type
     * @return list of allowed actor classes for this terrain on this map
     */
    public static List<Class<? extends Actor>> getAllowedSpecies(String mapName, Class<?> terrainClass) {
        Map<Class<?>, List<Class<? extends Actor>>> profile = getSpawnProfile(mapName);
        return profile.getOrDefault(terrainClass, new ArrayList<>());
    }

    /**
     * Ensures that at least one of each required spawner type exists on the map.
     * If a required spawner is missing, converts an appropriate tile to that spawner type.
     * 
     * @param gameMap the map to ensure spawners on
     */
    private void ensureRequiredSpawners(GameMap gameMap) {
        String mapName = gameMap.toString();
        Map<Class<?>, List<Class<? extends Actor>>> profile = getSpawnProfile(mapName);
        
        // Check for each required terrain type
        for (Class<?> terrainClass : profile.keySet()) {
            if (!hasTerrainType(gameMap, terrainClass)) {
                // Convert a suitable tile to the required terrain type
                convertTileToTerrain(gameMap, terrainClass);
            }
        }
    }

    /**
     * Checks if the map has at least one tile of the specified terrain type.
     * 
     * @param gameMap the map to check
     * @param terrainClass the terrain class to look for
     * @return true if the terrain type exists on the map
     */
    private boolean hasTerrainType(GameMap gameMap, Class<?> terrainClass) {
        // This is a simplified check - in a real implementation,
        // this would iterate through all tiles on the map
        // For now, we'll assume the required terrain types are manually placed
        return true; // Placeholder implementation
    }

    /**
     * Converts a suitable tile to the specified terrain type.
     * 
     * @param gameMap the map to modify
     * @param terrainClass the terrain class to convert to
     */
    private void convertTileToTerrain(GameMap gameMap, Class<?> terrainClass) {
        // This is a placeholder implementation
        // In a real implementation, this would:
        // 1. Find a suitable tile (e.g., snow) to convert
        // 2. Replace it with the required terrain type
        // 3. Ensure the conversion doesn't break the map layout
        
        // For now, we'll just add a comment indicating where this would happen
        // TODO: Implement actual terrain conversion logic
    }
}
