package gitc.ag;

import java.util.ArrayList;
import java.util.List;

import gitc.entities.Owner;
import gitc.simulation.actions.Action;

public class AGPlayer {
  public final Owner owner;
  public List<Action> actions = new ArrayList<>();
  
  public int remainingBombs = 2;
  
  // part of scoring
  public int units = 0;
  public int production = 0;
  public boolean dead = false;
  
  public AGPlayer(Owner owner) {
    this.owner = owner;
    remainingBombs = owner.bombsLeft;
  }
  
  public void addAction(Action action, int turn) {
    actions.add(action);
  }
}
