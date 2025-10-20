package game.terrain;

public final class PlantFactory {

  private PlantFactory() {}

  public static AppleSprout createAppleSproutForMap(boolean isPlains) {
    return new AppleSprout(isPlains);
  }

  public static AppleSapling createAppleSaplingForMap(boolean isPlains) {
    return new AppleSapling(isPlains);
  }

  public static YewBerrySapling createYewSaplingForMap(boolean isPlains) {
    return new YewBerrySapling(isPlains);
  }

  public static WildAppleTree createAppleTree() {
    return new WildAppleTree();
  }

  public static YewBerryTree createYewTree() {
    return new YewBerryTree();
  }
}
