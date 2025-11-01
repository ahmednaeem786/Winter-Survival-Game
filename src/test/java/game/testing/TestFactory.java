package game.testing;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import game.terrain.Snow;

import java.util.Arrays;

/**
 * Small helpers for unit tests to create maps quickly.
 */
public final class TestFactory {
    private TestFactory() {}

    /**
     * Builds a GameMap with '.' mapped to Snow ground.
     */
    public static GameMap createSnowMap(String name, String... rows) throws GameEngineException {
        DefaultGroundCreator creator = new DefaultGroundCreator();
        creator.registerGround('.', Snow::new);
        return new GameMap(name, creator, Arrays.asList(rows));
    }
}

