package game.actions;

import edu.monash.fit2099.engine.actions.Action;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.coating.Coatable;
import game.coating.CoatingType;

/**
 * Action: coat a weapon in the actor's inventory with a coat item.
 * - The coat item should create one CoatAction per coatable weapon in the owner's inventory
 *   (or you can make the coat's allowableActions show choices).
 * On execute: setCoating on the weapon and remove the coat item from actor inventory.
 *
 * @author Ahmed
 */

public class CoatAction extends Action {

  private final Item coatingItem;
  private final Coatable weapon;
  private final CoatingType coatingType;
  private final String weaponName;

  public CoatAction(Item coatingItem, Coatable weapon, CoatingType coatingType, String weaponName) {
    this.coatingItem = coatingItem;
    this.weapon = weapon;
    this.coatingType = coatingType;
    this.weaponName = weaponName;
  }

  @Override
  public String execute(Actor actor, GameMap map) {
    actor.removeItemFromInventory(coatingItem);
    weapon.setCoating(coatingType);
    return actor + " coats " + weaponName + " with " + coatingType;
  }

  @Override
  public String menuDescription(Actor actor) {
    return actor + " coats " + weaponName + " with " + coatingType;
  }
}
