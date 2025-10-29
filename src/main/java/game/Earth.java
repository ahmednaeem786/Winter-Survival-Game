package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actions.DoNothingAction;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.actors.attributes.ActorAttributeOperation;
import edu.monash.fit2099.engine.actors.attributes.BaseAttributes;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import edu.monash.fit2099.engine.positions.World;
import game.abilities.Abilities;
import game.actors.*;
import game.items.TeleportCube;
import game.teleportation.TeleportDestination;
import game.terrain.*;
import game.terrain.Cave;
import game.terrain.Meadow;
import game.terrain.Snow.SpawnHelper;
import game.terrain.Tundra;
import game.terrain.Snow;

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
 * @version 3.6
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
     * Override the game loop to increment the global turn counter for spawning
     * and handle animal warmth decrease every turn.
     * Also ensures that only actions from actors on the player's current map are displayed.
     */
    @Override
    protected void gameLoop() throws GameEngineException {
        // Increment the global turn counter for spawning
        SpawnHelper.incrementTurn();
        
        // Handle animal warmth decrease every turn
        handleAnimalWarmthDecrease();
        
        // Get the player's current map
        GameMap playersMap = actorLocations.locationOf(player).map();
        
        // Tick over all the maps (for terrain ticks, etc.)
        for (GameMap gameMap : gameMaps) {
            gameMap.tick();
        }
        
        // Draw the player's map
        playersMap.draw(display);
        
        // Process all the actors, but only display actions for actors on the player's map
        for (Actor actor : actorLocations) {
            if (stillRunning()) {
                processActorTurnWithMapFilter(actor, playersMap);
            }
        }
    }
    
    /**
     * Process an actor's turn, but only display the result if the actor is on the specified map.
     * This is a custom version of the parent class's processActorTurn method.
     * 
     * @param actor the actor whose turn is being processed
     * @param playersMap the map the player is currently on (used to filter display output)
     */
    private void processActorTurnWithMapFilter(Actor actor, GameMap playersMap) {
        // Get actor's location and map
        Location here;
        here = actorLocations.locationOf(actor);
        GameMap map = here.map();
        
        // Prepare all allowable actions for this actor
        ActionList actions = prepareActorActions(actor, here);
        
        // Use a dummy display for actors not on the player's map to suppress their messages
        Display actorDisplay = (map == playersMap) ? display : new Display();
        
        // Get the action from the actor
        Action action = actor.playTurn(actions, lastActionMap.get(actor), map, actorDisplay);
        
        // Record the action
        lastActionMap.put(actor, action);
        
        // Execute the action
        String result = action.execute(actor, map);
        
        // Only display the result if the actor is on the player's current map
        if (map == playersMap) {
            display.println(result);
        }
    }
    
    /**
     * Prepare all allowable actions for an actor at a specific location.
     * This replicates the logic from World.prepareAllowableActions.
     * 
     * @param actor the actor
     * @param here the actor's current location
     * @return list of all allowable actions
     */
    private ActionList prepareActorActions(
            Actor actor,
            Location here) {
        
        ActionList actions = new ActionList();
        
        // Actions from items in inventory
        for (Item item : actor.getItemInventory()) {
            actions.add(item.allowableActions(actor, here.map()));
            actions.add(item.getDropAction(actor));
        }
        
        // Actions from current ground
        actions.add(here.getGround().allowableActions(actor, here, ""));
        
        // Actions from surrounding locations
        for (Exit exit : here.getExits()) {
            Location destination = exit.getDestination();
            
            if (actorLocations.isAnActorAt(destination)) {
                Actor otherActor = actorLocations.getActorAt(destination);
                actions.add(otherActor.allowableActions(actor, exit.getName(), here.map()));
                for (Item item : actor.getItemInventory()) {
                    actions.add(item.allowableActions(otherActor, destination));
                }
            } else {
                actions.add(destination.getGround().allowableActions(actor, destination, exit.getName()));
            }
            actions.add(destination.getMoveAction(actor, exit.getName(), exit.getHotKey()));
        }
        
        // Actions from items on the ground
        for (Item item : here.getItems()) {
            actions.add(item.allowableActions(here));
            actions.add(item.getPickUpAction(actor));
        }
        
        // Add do-nothing option
        actions.add(new DoNothingAction());
        
        return actions;
    }
    
    /**
     * Handles warmth decrease for all animals every turn.
     * Animals lose 1 warmth each turn, and become unconscious when warmth reaches 0.
     * Animals with cold resistance don't lose warmth.
     * Only displays status messages for animals on the player's current map.
     */
    private void handleAnimalWarmthDecrease() {
        // Get the player's current map
        GameMap playersMap = actorLocations.locationOf(player).map();
        
        // Process all actors on all maps
        for (GameMap gameMap : gameMaps) {
            // Check if this is the player's current map
            boolean isPlayersMap = (gameMap == playersMap);
            
            // Get all actors on this map
            for (int x = 0; x < gameMap.getXRange().max(); x++) {
                for (int y = 0; y < gameMap.getYRange().max(); y++) {
                    try {
                        if (gameMap.at(x, y).containsAnActor()) {
                            Actor actor = gameMap.at(x, y).getActor();
                            
                            // Check if actor has warmth attribute (only animals with warmth need processing)
                            if (actor.hasStatistic(BaseAttributes.WARMTH)) {
                                // Check if actor has cold resistance
                                if (actor.hasAbility(Abilities.COLD_RESISTANCE)) {
                                    // Animal is immune to cold - display status message
                                    if (isPlayersMap) {
                                        display.println(actor + " is immune to cold and feels comfortable in the frozen tundra.");
                                    }
                                } else {
                                    // Decrease warmth by 1 each turn for non-resistant animals
                                    actor.modifyAttribute(
                                        BaseAttributes.WARMTH,
                                        ActorAttributeOperation.DECREASE,
                                        1
                                    );
                                    
                                    // Check current warmth level
                                    int currentWarmth = actor.getAttribute(BaseAttributes.WARMTH);
                                    
                                    // Only show status messages for animals on the player's map
                                    if (isPlayersMap) {
                                        // Show status messages based on warmth level
                                        if (currentWarmth <= 0) {
                                            // Animal becomes unconscious due to cold
                                            display.println(actor + " becomes unconscious due to extreme cold and collapses!");
                                        } else if (currentWarmth <= 3) {
                                            // Animal is very cold
                                            display.println(actor + " shivers violently from the cold!");
                                        } else if (currentWarmth <= 5) {
                                            // Animal is cold
                                            display.println(actor + " feels very cold and is struggling to stay warm.");
                                        } else if (currentWarmth <= 8) {
                                            // Animal is getting cold
                                            display.println(actor + " feels cold and is looking for warmth.");
                                        }
                                    }
                                    
                                    // Remove actor from map if warmth reaches 0 (regardless of which map)
                                    if (currentWarmth <= 0) {
                                        gameMap.removeActor(actor);
                                    }
                                }
                            }
                        }
                    } catch (Exception e) {
                        // Skip invalid coordinates
                        continue;
                    }
                }
            }
        }
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
        DefaultGroundCreator forestGroundCreator  = new DefaultGroundCreator();
        forestGroundCreator.registerGround('.', Snow::new);

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

        GameMap gameMap = new GameMap("Forest", forestGroundCreator, map);
        this.addGameMap(gameMap);

        // Create Plains map
        DefaultGroundCreator plainsGroundCreator = new DefaultGroundCreator();
        plainsGroundCreator.registerGround('.', Snow::new);

        List<String> plainsMap = Arrays.asList(
                ".........................",
                ".........................",
                ".........................",
                ".........................",
                ".........................",
                ".........................",
                ".........................",
                "........................."
        );

        GameMap plainsGameMap = new GameMap("Plains", plainsGroundCreator, plainsMap);
        this.addGameMap(plainsGameMap);

        gameMap.at(16,8).setGround(PlantFactory.createAppleSproutForMap(false));
        gameMap.at(6,3).setGround(PlantFactory.createAppleSaplingForMap(false));
        gameMap.at(3,6).setGround(PlantFactory.createYewSaplingForMap(false));

        plainsGameMap.at(2,1).setGround(PlantFactory.createAppleSproutForMap(true));
        plainsGameMap.at(4,2).setGround(PlantFactory.createAppleSproutForMap(true));
        plainsGameMap.at(6,2).setGround(PlantFactory.createYewSaplingForMap(true));

        Player player = new Player("Explorer", 'ඞ', 100);
        this.addPlayer(player, gameMap.at(15, 8));

        populateWithAnimals(gameMap);
        ensureRequiredSpawners(plainsGameMap);
        setupTeleportation(gameMap, plainsGameMap, player);
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

        gameMap.addActor(new Chimera(), gameMap.at(2, 2));


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
        // Iterate through all tiles on the map to check for the terrain type
        for (int x = 0; x < gameMap.getXRange().max(); x++) {
            for (int y = 0; y < gameMap.getYRange().max(); y++) {
                try {
                    if (gameMap.at(x, y).getGround().getClass() == terrainClass) {
                        return true;
                    }
                } catch (Exception e) {
                    // Skip invalid coordinates
                    continue;
                }
            }
        }
        return false;
    }

    /**
     * Converts a suitable tile to the specified terrain type.
     * Randomly selects a Snow tile to ensure spawners are distributed across the map.
     * 
     * @param gameMap the map to modify
     * @param terrainClass the terrain class to convert to
     */
    private void convertTileToTerrain(GameMap gameMap, Class<?> terrainClass) {
        // Collect all available Snow tiles
        java.util.List<Location> snowTiles = new java.util.ArrayList<>();
        
        for (int x = 0; x < gameMap.getXRange().max(); x++) {
            for (int y = 0; y < gameMap.getYRange().max(); y++) {
                try {
                    // Check if ground is Snow by checking its display character
                    if (gameMap.at(x, y).getGround().getDisplayChar() == '.') {
                        snowTiles.add(gameMap.at(x, y));
                    }
                } catch (Exception e) {
                    // Skip invalid coordinates
                    continue;
                }
            }
        }
        
        // If no snow tiles available, return
        if (snowTiles.isEmpty()) {
            return;
        }
        
        // Randomly select a snow tile to convert
        java.util.Random random = new java.util.Random();
        Location selectedTile = snowTiles.get(random.nextInt(snowTiles.size()));
        
        // Convert the selected snow tile to the required terrain type
        if (terrainClass == Cave.class) {
            selectedTile.setGround(new Cave());
        } else if (terrainClass == Meadow.class) {
            selectedTile.setGround(new Meadow());
        } else if (terrainClass == Tundra.class) {
            selectedTile.setGround(new Tundra());
        }
    }

    /**
     * Sets up the teleportation network between and within maps.
     *
     * @param forestMap the forest game map
     * @param plainsMap the plains game map
     * @param player the player character
     */
    private void setupTeleportation(GameMap forestMap, GameMap plainsMap, Player player) {
        // FOREST: TeleDoor at (5, 5)
        TeleDoor forestDoor = new TeleDoor();
        // Destination 1: To Plains (10, 3)
        forestDoor.addDestination(new TeleportDestination(
                plainsMap.at(10, 3),
                "Plains",
                "Plains Portal"
        ));
        // Destination 2: Within Forest to (35, 8)
        forestDoor.addDestination(new TeleportDestination(
                forestMap.at(35, 8),
                "Forest",
                "Forest East Gate"
        ));
        forestMap.at(5, 5).setGround(forestDoor);

        // PLAINS: TeleDoor at (15, 4)
        TeleDoor plainsDoor = new TeleDoor();
        // Destination 1: To Forest (25, 5)
        plainsDoor.addDestination(new TeleportDestination(
                forestMap.at(25, 5),
                "Forest",
                "Forest Portal"
        ));
        // Destination 2: Within Plains to (3, 1)
        plainsDoor.addDestination(new TeleportDestination(
                plainsMap.at(3, 1),
                "Plains",
                "Plains West"
        ));
        plainsMap.at(15, 4).setGround(plainsDoor);

        // FOREST: Teleportation Circle at (20, 2)
        TeleportationCircle forestCircle = new TeleportationCircle();
        // Destination 1: To Plains (5, 6)
        forestCircle.addDestination(new TeleportDestination(
                plainsMap.at(5, 6),
                "Plains",
                "Plains Circle"
        ));
        // Destination 2: Within Forest to (10, 8)
        forestCircle.addDestination(new TeleportDestination(
                forestMap.at(10, 8),
                "Forest",
                "Forest South"
        ));
        forestMap.at(20, 2).setGround(forestCircle);

        // PLAINS: Teleportation Circle at (20, 6)
        TeleportationCircle plainsCircle = new TeleportationCircle();
        // Destination 1: To Forest (15, 1)
        plainsCircle.addDestination(new TeleportDestination(
                forestMap.at(15, 1),
                "Forest",
                "Forest North"
        ));
        // Destination 2: Within Plains to (8, 3)
        plainsCircle.addDestination(new TeleportDestination(
                plainsMap.at(8, 3),
                "Plains",
                "Plains Center"
        ));
        plainsMap.at(20, 6).setGround(plainsCircle);

        // TELEPORT CUBE: Add to player inventory
        TeleportCube cube = new TeleportCube();
        // Destination 1: To Plains (12, 2)
        cube.addDestination(new TeleportDestination(
                plainsMap.at(12, 2),
                "Plains",
                "Plains via Cube"
        ));
        // Destination 2: To Forest (30, 6)
        cube.addDestination(new TeleportDestination(
                forestMap.at(30, 6),
                "Forest",
                "Forest via Cube"
        ));
        player.addItemToInventory(cube);
    }
}
