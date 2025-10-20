package game.testing;

import edu.monash.fit2099.engine.GameEngineException;
import edu.monash.fit2099.engine.positions.DefaultGroundCreator;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.terrain.Snow;

import java.util.Arrays;

/**
 * Small test factory for creating maps and commonly used test objects.
 *
 * <p>
 * This class provides lightweight helpers used by unit tests to construct
 * {@link GameMap} instances pre-populated with {@link Snow} ground and to
 * access {@link Location}s conveniently.
 * </p>
 *
 * @author Ahmed
 */
public final class TestFactory {

  /**
   * Builds a small {@link GameMap} with {@link Snow} ground ('.') using the provided row strings.
   *
   * <p>
   * Each string in {@code rows} corresponds to one row in the map. The character '.'
   * will be mapped to {@link Snow} ground via a {@link DefaultGroundCreator}.
   * </p>
   *
   * @param name the name for the created map (useful for debug/test output)
   * @param rows the textual layout rows (each row is a String)
   * @return a {@link GameMap} with Snow ground for every '.' character
   * @throws GameEngineException if the underlying engine rejects the map construction
   */
  public static GameMap createSnowMap(String name, String... rows) throws GameEngineException {
    DefaultGroundCreator gc = new DefaultGroundCreator();
    gc.registerGround('.', Snow::new);
    return new GameMap(name, gc, Arrays.asList(rows));
  }

  /**
   * Convenience accessor to get a {@link Location} from a {@link GameMap}.
   *
   * @param map the map to query
   * @param x the x coordinate (column)
   * @param y the y coordinate (row)
   * @return the {@link Location} at the given coordinates
   */
  public static Location location(GameMap map, int x, int y) {
    return map.at(x, y);
  }
}
