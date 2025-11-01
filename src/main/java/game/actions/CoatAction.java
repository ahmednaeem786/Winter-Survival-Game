package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.coating.Coatable;
import game.coating.CoatingType;

/**
 * An {@link Action} that applies a {@link CoatingType} to a {@link Coatable}
 * weapon carried by an actor.
 *
 * <p>Usage pattern:
 * <ul>
 *   <li>Coating items (e.g., YewBerry or Snow) expose one {@code CoatAction}
 *       per coatable weapon via their {@code allowableActions}.</li>
 *   <li>When executed, this action consumes the coating item from the actor's
 *       inventory and sets the chosen weapon's coating.</li>
 * </ul>
 *
 * <p>Design notes:
 * <ul>
 *   <li>The action intentionally does not perform any validation beyond what
 *       the caller (the coating item's allowableActions) has already performed.
 *       This keeps {@code execute} simple and deterministic.</li>
 *   <li>Coating semantics (persistent vs one-use on first attack) are handled
 *       by the weapon implementation; {@code CoatAction} only sets/overwrites
 *       the coating value and removes the coating item.</li>
 * </ul>
 *
 * @author Ahmed
 */

public class CoatAction extends Action {

  /** The item used as the coating; removed from owner's inventory on execute. */
  private final Item coatingItem;

  /** The target weapon (implements {@link Coatable}) that will receive the coating. */
  private final Coatable weapon;

  /** The coating to apply to the weapon. */
  private final CoatingType coatingType;

  /** Human-readable weapon name used in menu/return messages. */
  private final String weaponName;

  /**
   * Create a CoatAction.
   *
   * @param coatingItem the item that will be consumed to coat the weapon
   * @param weapon the coatable weapon instance
   * @param coatingType the type of coating to apply
   * @param weaponName display name for the weapon (used in menuDescription)
   */
  public CoatAction(Item coatingItem, Coatable weapon, CoatingType coatingType, String weaponName) {
    this.coatingItem = coatingItem;
    this.weapon = weapon;
    this.coatingType = coatingType;
    this.weaponName = weaponName;
  }

  /**
   * Execute the coat action: remove the coating item from the actor's inventory
   * and set the weapon's coating. Returns a short message describing the result.
   *
   * @param actor the actor performing the action (owner of the coating item)
   * @param map the current game map (unused but part of the contract)
   * @return message describing the coating operation
   */
  @Override
  public String execute(Actor actor, GameMap map) {

    //Consuming the coating item from actor's inventory
    actor.removeItemFromInventory(coatingItem);

    //Applying/replacing coating on the weapon
    weapon.setCoating(coatingType);
    return actor + " coats " + weaponName + " with " + coatingType;
  }

  /**
   * Menu description shown to the player.
   *
   * @param actor the actor that would perform this action (unused)
   * @return a concise menu label
   */
  @Override
  public String menuDescription(Actor actor) {
    return actor + " coats " + weaponName + " with " + coatingType;
  }
}
