package gitc.ag;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import gitc.GameState;
import gitc.entities.Factory;
import gitc.simulation.Simulation;
import gitc.simulation.actions.Action;

public class AGSolution {
  public static int SIMULATION_DEPTH = 10; // 11 or + for the upgrade to activate
  public static DecimalFormat f = new DecimalFormat("#####.00");

  public double energy = 0;
  public List<AGPlayer> players = new ArrayList<>();

  public String message;

  private AGPlayer me;

  private AGPlayer opp;

  private Simulation simulation;
  public double unitScore;
  public double productionScore;
  public double influenceScore;
  public double bombRemainingScore;
  public double factoryCountScore;
  public double positioningScore;
  public double troopsInTransitScore;
  public double troopsConvergenceScore;
  public double distanceBetweenFactoryScore;
  public String name;


  public AGSolution() {
    players.add(new AGPlayer(GameState.me));
    players.add(new AGPlayer(GameState.opp));
  }

  public AGSolution(String name) {
    this();
    this.name = name;
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

  public static final double UNIT_SCORE_MULT = 1.0;
  public static final double PRODUCTION_SCORE_MULT = 3.0;
  public static final double INFLUENCE_MULT = 0.1;
  public static final double BOMB_MULT = 0.0;
  public static final double FACTORY_COUNT_MULT = 1.0;
  public static final double POSITIONING_MULT = 0.0;
  public static final double TROOP_TANSIT_MULT = 0.0;
  public static final double TROOP_CONVERGENCE_MULT = 2.0;
  public static final double DISTANCE_MULT = 0.0;
  
  public void calculateHeuristic(Simulation simulation) {
    this.simulation = simulation;
    me = players.get(0);
    opp = players.get(1);
    
    if (me.dead) {
      energy = -1_000_000;
    } else {
      // pseudo calcul of distance between my factories
      // double distance = getPseudoDistanceBetweenFactories();
      
      unitScore = getUnitsCountScore();
      productionScore = getProductionScore();
      influenceScore = updateFactoriesInfluence();
      bombRemainingScore = getBombRemainingScore();
      factoryCountScore = getFactoryCountScore();
      positioningScore = calculatePositioningOfUnitsScore();
      troopsInTransitScore = getTroopsInTransitScore();
      troopsConvergenceScore = getTroopConvergenceScore();
      distanceBetweenFactoryScore = getDistanceBetweenFactoryScore();
      
      energy = 0
          + (UNIT_SCORE_MULT * unitScore) 
          + (PRODUCTION_SCORE_MULT * productionScore)
          + (INFLUENCE_MULT * influenceScore)
          + (BOMB_MULT * bombRemainingScore)
          + (FACTORY_COUNT_MULT * factoryCountScore)
          + (POSITIONING_MULT * positioningScore)
          + (TROOP_TANSIT_MULT * troopsInTransitScore)
          + (TROOP_CONVERGENCE_MULT * troopsConvergenceScore)
          + (DISTANCE_MULT * distanceBetweenFactoryScore)
          ; 
      message = "e("+f.format(energy)+")"
                +" units("+f.format(unitScore)+")"
                +" bomb("+f.format(bombRemainingScore)+")"
                +" prod("+f.format(productionScore)+")"
                +" inf("+f.format(influenceScore)+")" 
                +" pos("+f.format(positioningScore)+")"
                +" troop("+f.format(troopsInTransitScore)+")"
                ;
      // debug
      //message = " prod: "+me.production+" / "+opp.production; 
      // prod
      message = "Où vais-je aller ?! Le réseau est si vaste...";
    }
  }

  private double getDistanceBetweenFactoryScore() {
    int distance = 0; // will be squared
    int totalDistance = 0;
    
    for (Factory f1 : GameState.factories) {
      if (!f1.isMe()) {
        continue;
      }
      for (Factory f2: GameState.factories) {
        int localDistance = f1.getDistanceTo(f2);
        totalDistance+=localDistance;
        if (!f2.isMe()) {
          continue;
        }
        distance += localDistance;
      }
    }
    if (totalDistance == 0 ) {
      return 0;
    }
    return 1.0-1.0*distance / totalDistance;
  }

  private double getTroopConvergenceScore() {
    int total = 0;
    int max = 0;
    for (Factory factory : GameState.factories) {
      if (factory.isOpponent()) {
        max = Math.max(max, factory.unitsInTransit[0]);
        total +=factory.unitsInTransit[0];
      }
    }
    if (total == 0) {
      return 0;
    }
    return 1.0 * max / total;
  }

  private double getTroopsInTransitScore() {
    int troopsInTransit = simulation.getTroopsInTransit(GameState.me);
    int troopsInFactory = simulation.getTroopsInFactory(GameState.me);
    return 1.0*troopsInTransit / (troopsInFactory+troopsInTransit);
  }

  private double getUnitsCountScore() {
    return 2.0 * me.units / (me.units+opp.units) - 1 ;
  }

  private double getProductionScore() {
    if (me.production+opp.production == 0) {
      return 0;
    }
    return 2.0*me.production / (me.production+opp.production) - 1 ;
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
      double calculateInfluence = factory.calculateInfluence(simulation.troops);
      total += calculateInfluence;
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

  public static void updateHorizon(int maxDistanceForHorizon) {
    SIMULATION_DEPTH = maxDistanceForHorizon;
  }
}
