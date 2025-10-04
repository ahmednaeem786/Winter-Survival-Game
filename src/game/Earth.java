package game;

import edu.monash.fit2099.engine.GameEngineException;
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
 * @version 1.8
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
    }
}
