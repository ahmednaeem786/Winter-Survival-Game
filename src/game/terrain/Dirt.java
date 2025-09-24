package game.terrain;

import edu.monash.fit2099.engine.positions.Ground;

/**
 * Represents ordinary dirt ground on the map.
 *
 * <p>Serves as a default terrain type and is used as the underlying ground
 * when fire or other temporary terrains expire and revert.</p>
 *
 * <p>Symbol: '.'</p>
 *
 * @author Ahmed
 */
public class Dirt extends Ground {

  /**
   * Creates a Dirt ground tile.
   */
  public Dirt() {
    super('.', "Dirt");
  }

}
