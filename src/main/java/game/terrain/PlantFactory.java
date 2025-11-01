package game.terrain;

/**
 * Simple factory for creating plant-ground instances used by REQ1 (Flora II).
 *
 * <p>This centralises creation logic so callers (e.g. Earth, tests) don't need to know
 * concrete constructors or map-specific semantics. If lifecycle rules diverge further
 * (e.g. additional constructor args), this is the single place to adapt.</p>
 *
 * <p>All methods are static and the class is non-instantiable.</p>
 *
 * @author Ahmed
 */
public final class PlantFactory {

  /**
   * Private constructor to prevent instantiation.
   */
  private PlantFactory() {}

  /**
   * Create a Wild Apple Sprout tuned for the given map type.
   *
   * @param isPlains true if the sprout is for the Plains map (plains behaviour)
   * @return the created Wild Apple Sprout as a Ground instance
   */
  public static AppleSprout createAppleSproutForMap(boolean isPlains) {
    return new AppleSprout(isPlains);
  }

  /**
   * Create an Apple Sapling tuned for the given map type.
   *
   * @param isPlains true if the sapling is for the Plains map
   * @return the created Apple Sapling as a Ground instance
   */
  public static AppleSapling createAppleSaplingForMap(boolean isPlains) {
    // Create and return a sapling instance. Implementation details (growth, appearance)
    // are encapsulated in {@code AppleSapling}.
    return new AppleSapling(isPlains);
  }

  /**
   * Create a Yew Berry Sapling tuned for the given map type.
   *
   * @param isPlains true if the sapling is for the Plains map
   * @return the created Yew Berry Sapling as a Ground instance
   */
  public static YewBerrySapling createYewSaplingForMap(boolean isPlains) {
    // Forward the map-type hint to the sapling constructor.
    return new YewBerrySapling(isPlains);
  }

  /**
   * Create a mature Wild Apple Tree (fruit-producing).
   *
   * @return a WildAppleTree instance
   */
  public static WildAppleTree createAppleTree() {
    return new WildAppleTree();
  }

  /**
   * Create a mature Yew Berry Tree (fruit-producing).
   *
   * @return a YewBerryTree instance
   */
  public static YewBerryTree createYewTree() {
    return new YewBerryTree();
  }
}
