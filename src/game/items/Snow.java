package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.CoatAction;
import game.coating.Coatable;
import game.coating.CoatingType;


/**
 * Snow item that can be used as a coating for {@link Coatable} weapons.
 *
 * <p>This item exposes one {@link CoatAction} per coatable weapon in the owner's
 * inventory via {@link #allowableActions(Actor, GameMap)}. Executing the coat
 * action consumes this Snow item and sets the weapon's coating to
 * {@link CoatingType#SNOW}.</p>
 *
 * <p>Design notes:
 * <ul>
 *   <li>Snow is a portable item (can be picked up and carried).</li>
 *   <li>This class intentionally uses {@code item.asCapability(Coatable.class)}
 *       to avoid instanceof checks where possible.</li>
 * </ul>
 *
 * @author Ahmed
 */
public class Snow extends Item{

  public Snow() {
    super("Snow", 's', true);
  }

  /**
   * Offer coat actions for every coatable weapon in the owner's inventory.
   *
   * @param owner the actor carrying this Snow
   * @param map the current game map (unused but part of contract)
   * @return list of actions (one CoatAction per Coatable item found)
   */
  public ActionList allowableActions(Actor owner, GameMap map) {
    ActionList actions = new ActionList();

    for (Item item : owner.getItemInventory()) {
      //adding one CoatAction per coatable weapon
      item.asCapability(Coatable.class).ifPresent(coatable -> actions.add(new CoatAction(this, coatable, CoatingType.SNOW, item.toString())));
    }

    return actions;
  }

}
