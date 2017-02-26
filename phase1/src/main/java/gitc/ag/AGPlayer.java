package gitc.ag;

import java.util.ArrayList;
import java.util.List;

import gitc.entities.Owner;
import gitc.simulation.actions.MoveAction;

public class AGPlayer {
  public final Owner owner;
  public TurnAction[] turnActions = new TurnAction[AGSolution.DEPTH];
  public int remainingBombs = 2;
  
  // part of scoring
  public int units;
  public int production;
  public boolean dead;
  
  public AGPlayer(Owner owner) {
    this.owner = owner;
    for (int i=0;i<AGSolution.DEPTH;i++) {
      turnActions[i] = new TurnAction();
    }
  }
  
  public void addMoveAction(MoveAction action, int turn) {
    TurnAction tAction = turnActions[turn];
    tAction.moveActions.add(action);
  }
}
