package game.items;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.GameMap;
import game.actions.CoatAction;
import game.coating.Coatable;
import game.coating.CoatingType;

public class Snow extends Item{

  public Snow() {
    super("Snow", 's', true);
  }

  public ActionList allowableActions(Actor owner, GameMap map) {
    ActionList actions = new ActionList();

    for (Item item : owner.getItemInventory()) {
      item.asCapability(Coatable.class).ifPresent(coatable -> actions.add(new CoatAction(this, coatable, CoatingType.SNOW, item.toString())));
    }

    return actions;
  }

}
