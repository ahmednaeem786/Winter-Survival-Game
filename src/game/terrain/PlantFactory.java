package game.terrain;

public final class PlantFactory {

  private PlantFactory() {}

  public static AppleSprout createAppleSproutForMap(boolean isPlains) {
    return new AppleSprout(isPlains);
  }

  public static AppleSapling createAppleSaplingForMap(boolean isPlains) {
    return new AppleSapling(isPlains);
  }

  public static YewSapling createYewSaplingForMap(boolean isPlains) {
    return new YewSapling(isPlains);
  }

  public static AppleTree createAppleTree() {
    return new AppleTree();
  }

  public static YewTree createYewTree() {
    return new YewTree();
  }
}
