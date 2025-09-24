package game.items;

import static game.capabilities.StatusAbilities.CAN_RECIEVE_STATUS;

import edu.monash.fit2099.engine.actions.ActionList;
import edu.monash.fit2099.engine.actors.Actor;
import edu.monash.fit2099.engine.items.Item;
import edu.monash.fit2099.engine.positions.Exit;
import edu.monash.fit2099.engine.positions.GameMap;
import edu.monash.fit2099.engine.positions.Location;
import game.actions.TorchAttackAction;
import game.actors.GameActor;

public class Torch extends Item {

  public Torch() {
    super("Torch", 'y', true);
  }

  @Override
  public ActionList allowableActions(Actor owner, GameMap map) {
    ActionList actions = new ActionList();
    Location ownerLocation = map.locationOf(owner);

    for (Exit exit : ownerLocation.getExits()) {
      Location adjacent = exit.getDestination();

      if (adjacent.containsAnActor()) {
        Actor maybeTarget = adjacent.getActor();
        if (maybeTarget.hasAbility(CAN_RECIEVE_STATUS)) {
          GameActor gameTarget = (GameActor) maybeTarget;
          actions.add(new TorchAttackAction(this, gameTarget, exit.getName()));
        }
      }
    }

    return actions;
  }

}
