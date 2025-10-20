package game.testing;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.Snow;

import java.util.Arrays;

public final class TestFactory {
  /** builds a small GameMap with Snow ground (.) of supplied layout rows */
  public static GameMap createSnowMap(String name, String... rows) throws GameEngineException {
    DefaultGroundCreator gc = new DefaultGroundCreator();
    gc.registerGround('.', Snow::new);
    return new GameMap(name, gc, Arrays.asList(rows));
  }

  /** convenience to get a location */
  public static Location location(GameMap map, int x, int y) {
    return map.at(x, y);
  }
}
