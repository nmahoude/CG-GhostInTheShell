package gitc.ag;

import java.util.ArrayList;
import java.util.List;

import gitc.GameState;
import gitc.simulation.actions.MoveAction;

public class AGSolution {
  public static final int DEPTH = 30;

  public double energy = 0;
  public List<AGPlayer> players = new ArrayList<>();


  public AGSolution() {
    players.add(new AGPlayer(GameState.me));
    players.add(new AGPlayer(GameState.opp));
  }
  
  public void copyFromPreviousTurnBest(AGSolution lastBest) {
    throw new RuntimeException("Method not implemented");
  }
  
  public void cross(AGSolution solution1, AGSolution solution2) {
    throw new RuntimeException("Method not implemented");
  }

  public void mutate() {
    throw new RuntimeException("Method not implemented");
  }
  
  public void copy(AGSolution solution) {
    throw new RuntimeException("Method not implemented");
  }

  public void randomize() {
    throw new RuntimeException("Method not implemented");
  }

  public void randomizeLastMove() {
    throw new RuntimeException("Method not implemented");
  }

  public String output() {
    String output = "";
    TurnAction tAction = players.get(0).turnActions[0];
    for (MoveAction action  :tAction.moveActions) {
      output += action.output()+";";
    }
    output+="MSG from AG";
    return output;
  }

}
