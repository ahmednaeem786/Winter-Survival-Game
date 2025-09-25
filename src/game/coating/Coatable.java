package game.coating;

/**
 * Marker interface for items (typically weapons) that can hold a {@link CoatingType}.
 *
 * <p>Implementations store a coating state and expose simple mutators/accessors:
 * <ul>
 *   <li>{@link #setCoating(CoatingType)} — apply or replace the current coating</li>
 *   <li>{@link #getCoating()} — return the current coating (never null; use CoatingType.NONE)</li>
 *   <li>{@link #clearCoating()} — remove any existing coating (set to CoatingType.NONE)</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *   <li>Keeping this as a small interface keeps weapon classes decoupled from
 *       coating implementation details and allows non-weapon items to implement
 *       the same contract if needed in future.</li>
 *   <li>Implementations should ensure thread-safety is not required in this
 *       single-threaded engine environment.</li>
 * </ul>
 *
 * @author Ahmed
 */
public interface Coatable {

  /**
   * Apply or replace the coating on this item.
   *
   * @param coating the coating to apply; implementations should handle {@code null}
   *                 defensively (e.g., treat as {@link CoatingType#NONE}).
   */
  void setCoating(CoatingType coating);

  /**
   * Return the currently applied coating.
   *
   * @return the coating type; implementations should never return {@code null}
   *         and should use {@link CoatingType#NONE} when no coating is present.
   */
  CoatingType getCoating();

  /**
   * Remove any existing coating, setting the coating to {@link CoatingType#NONE}.
   */
  void clearCoating();

}
