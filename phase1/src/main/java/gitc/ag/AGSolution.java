package gitc.ag;

import java.util.ArrayList;
import java.util.List;

import gitc.GameState;
import gitc.entities.Factory;
import gitc.simulation.actions.Action;

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

    if (tAction.actions.size() == 0) {
      output += "WAIT"+";";
    } else {
      for (Action action  :tAction.actions) {
        output += action.output()+";";
      }
    }
    output+="MSG from AG";
    return output;
  }

  public void calculateHeuristic() {
    AGPlayer me = players.get(0);
    AGPlayer opp = players.get(1);
    if (me.dead) {
      energy = -1_000_000;
    } else {
      // pseudo calcul of distance between my factories
      double distance = getPseudoDistanceBetweenFactories();
      
      energy = 
          (1.0*me.units / (me.units+opp.units)) 
          + (10.0*(1.0*me.production / (me.production+opp.production)))
//          - (0.1 * distance)
          ; 
    }
  }

  private double getPseudoDistanceBetweenFactories() {
    double distance = 0;
    Factory previousFactory = null;
    for (Factory factory : GameState.factories) {
      if (factory.isMe()) {
        if (previousFactory != null) {
          distance = 1.0*factory.getDistanceTo(previousFactory) / (factory.productionRate+1);
        }
        previousFactory = factory;
      }
    }
    return distance;
  }

}
