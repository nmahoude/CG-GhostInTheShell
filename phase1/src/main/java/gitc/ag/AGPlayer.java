package gitc.ag;

import java.util.ArrayList;
import java.util.List;

import gitc.entities.Owner;
import gitc.simulation.actions.MoveAction;

public class AGPlayer {
  public final Owner owner;
  public List<TurnAction> turnActions = new ArrayList<>();
  public int remainingBombs = 2;
  public int score;
  
  public AGPlayer(Owner owner) {
    this.owner = owner;
  }
  
  public void addMoveAction(MoveAction action, int turn) {
    TurnAction tAction = turnActions.get(turn);
    tAction.moveActions.add(action);
  }
}
