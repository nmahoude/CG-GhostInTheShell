package gitc.ag;

import java.util.ArrayList;
import java.util.List;

import gitc.simulation.actions.BombAction;
import gitc.simulation.actions.MoveAction;
import gitc.simulation.actions.UpgradeAction;

public class TurnAction {
  public List<BombAction> bombActions= new ArrayList<>();
  public List<MoveAction> moveActions = new ArrayList<>();
  public List<UpgradeAction> upgradeActions = new ArrayList<>();
}
