package game.terrain;

/**
 * Shared Constants for Plant Lifecycles and Production Rules.
 *
 * @author Ahmed
 */

public final class PlantConstants {
  private PlantConstants() {}

  //Apple lifecycle in Forest
  public static final int FOREST_SPROUT_TO_SAPLING_TURNS = 3;
  public static final int SAPLING_TO_TREE_TURNS = 5;
  public static final int TREE_APPLE_DROP_INTERVAL = 3;

  //Plains Behaviour
  public static final int PLAINS_SPROUT_TO_TREE_TURNS = 3;
  public static final int PLAINS_SPROUT_DROP_INTERVAL = 1;

  //Yew Behaviour
  public static final int YEWBERRY_GROW_ATTEMPT_TURNS = 3;
  public static final int YEWBERRY_GROW_CHANCE_PERCENT = 50;
  public static final int PLAINS_YEWBERRY_DROP_INTERVAL = 2;

}
