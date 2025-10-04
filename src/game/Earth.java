package game;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.displays.Display;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.World;
import game.actors.*;
import game.items.TeleportCube;
import game.teleportation.TeleportDestination;
import game.terrain.*;

import java.util.Arrays;
import java.util.List;

/**
 * Class representing the game world (Earth) for the winter survival game.
 *
 * This class constructs and initializes the entire game world, including
 * the terrain, player character, and all wildlife.
 * It serves as the main world builder that
 * creates the survival scenario.
 *
 * @author Muhamad Shafy Dimas Rafarrel
 * @version 3.4
 */
public class Earth extends World {

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

        Player player = new Player("Explorer", 'ඞ', 100);
        this.addPlayer(player, gameMap.at(1, 1));

        populateWithAnimals(gameMap);
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
