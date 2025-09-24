package game.capabilities;

/**
 * Capability flags related to status/effect handling.
 *
 * <p>Actors that can receive timed status effects (e.g., burn, bleed) should
 * enable this ability so game systems can detect them via {@code hasAbility(...)}.
 *
 * @author Ahmed
 */
public enum StatusAbilities {
  CAN_RECIEVE_STATUS
}
