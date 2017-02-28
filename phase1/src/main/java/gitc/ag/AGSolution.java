package gitc.ag;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import gitc.GameState;
import gitc.entities.Factory;
import gitc.simulation.Simulation;
import gitc.simulation.actions.Action;

public class AGSolution {
  public static final int SIMULATION_DEPTH = 12; // 11 or + for the upgrade to activate
  public static DecimalFormat f = new DecimalFormat("#####.00");

  public double energy = 0;
  public List<AGPlayer> players = new ArrayList<>();

  private String message;

  private AGPlayer me;

  private AGPlayer opp;

  private Simulation simulation;


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
    output+="MSG "+message;
    return output;
  }

  public void calculateHeuristic(Simulation simulation) {
    this.simulation = simulation;
    me = players.get(0);
    opp = players.get(1);
    
    if (me.dead) {
      energy = -1_000_000;
    } else {
      // pseudo calcul of distance between my factories
      // double distance = getPseudoDistanceBetweenFactories();
      
      double unitScore = getUnitsCountScore();
      double productionScore = getProductionScore();
      double influenceScore = updateFactoriesInfluence();
      double bombRemainingScore = getBombRemainingScore();
      double factoryCountScore = getFactoryCountScore();
      double positioningScore = calculatePositioningOfUnitsScore();
      double troopsInTransitScore = getTroopsInTransitScore();

      energy = 0
          + (1.0 * unitScore) 
          + (15.0 * productionScore)
          + (0.1 * influenceScore)
          + (0.0 * bombRemainingScore)
          + (0.0 * factoryCountScore)
          + (0.0 * positioningScore)
          + (0.0 * troopsInTransitScore)
          ; 
      
      message = "e("+f.format(energy)+")"
                +" units("+f.format(unitScore)+")"
                +" bomb("+f.format(bombRemainingScore)+")"
                +" prod("+f.format(productionScore)+")"
                +" inf("+f.format(influenceScore)+")" 
                +" pos("+f.format(positioningScore)+")"
                +" troop("+f.format(troopsInTransitScore)+")"
                ;
    }
  }

  private double getTroopsInTransitScore() {
    int troopsInTransit = simulation.getTroopsInTransit(GameState.me);
    int troopsInFactory = simulation.getTroopsInFactory(GameState.me);
    return 1.0*troopsInTransit / (troopsInFactory+troopsInTransit);
  }

  private double getUnitsCountScore() {
    return 1.0 * me.units / (me.units+opp.units);
  }

  private double getProductionScore() {
    return 1.0*me.production / (me.production+opp.production);
  }

  private double getBombRemainingScore() {
    return me.remainingBombs / 2.0;
  }

  private double getFactoryCountScore() {
    int mine = 0;
    int neutral = 0;
    int opp = 0;
    
    for (Factory factory : GameState.factories) {
      if (factory.isMe()) {
        mine++;
      } else if (factory.isOpponent()) {
        opp++;
      } else {
        neutral++;
      }
    }
      
    return 1.0 * mine / (mine+opp);
  }

  /**
   * Check that far factories (from enemies) don't keep lot of units
   * @return
   */
  private double calculatePositioningOfUnitsScore() {
    int unitCount = 0;
    int minDistance = 1_000;
    double score = 0;
    
    for (Factory factory : GameState.factories) {
      if (factory.isMe()) {
        if (factory.unitsInTransit[1] > factory.units) {
          // don't take credit for this one, it's under attack
        } else {
          Factory closestEnemyFactory = factory.getClosestEnemyFactory();
          if (closestEnemyFactory != null) {
            int localDistance = factory.getDistanceTo(closestEnemyFactory);

            minDistance = Math.min(minDistance, localDistance);
            unitCount += factory.units;
            
            score += 1.0*factory.units / minDistance;
          }
        }
      }
    }
    if (unitCount ==0) {
      return 0;
    }
    double biggestScore = 1.0*unitCount / minDistance;
    double local = (1.0-score) / biggestScore; // score is inverse of what a good number is, so (1.0 - score)
    if (Double.isInfinite(local)) {
      throw new RuntimeException("biggestScore="+biggestScore+" / unitCount="+unitCount + " / minDistance="+minDistance+" / score="+score);
    }
    return  local;
  }
  
  private double updateFactoriesInfluence() {
    double total = 0;
    for (Factory factory : GameState.factories) {
      total += factory.calculateInfluence(simulation.troops);
    }
    return total / GameState.factories.length;
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
