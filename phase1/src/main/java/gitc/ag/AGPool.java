package gitc.ag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gitc.GameState;
import gitc.Player;
import gitc.entities.Factory;
import gitc.simulation.actions.Action;
import gitc.simulation.actions.BombAction;
import gitc.simulation.actions.MoveAction;
import gitc.simulation.actions.UpgradeAction;

public class AGPool {
  private static Random random = new Random();
  private static final int AG_POOL = 10;
  AGSolution[] solutions = new AGSolution[AG_POOL];
  
  public AGPool() {
    reset();
  }
  
  public void createInitialPopulation() {
    for (int i=0;i<AG_POOL;i++) {
      solutions[i] = createRandom();
    }
  }
  
  public AGSolution createRandom() {
    Map<Integer, List<Action>> possibleActions = getPossibleActions();

    AGSolution solution = new AGSolution();
    for (Factory factory : GameState.factories) {
      List<Action> actions = possibleActions.get(factory.id);
      if (actions.size() > 0) {
        int actionNumber = random.nextInt(actions.size());
        Collections.shuffle(actions);
        for (int i=0;i<actionNumber;i++) {
          if (random.nextBoolean()) {
            solution.players.get(0).addAction(actions.get(i), 0);
          }
        }
      }
    }

    Player.simulation.simulate(solution);
    
    propose(solution);
    return solution;
  }
  
  public void propose(AGSolution newSolution) {
    int minIndex = -1;
    double minEnergy = newSolution.energy;
    
    for (int i=0;i<AG_POOL;i++) {
      if (solutions[i].energy < minEnergy) {
        minEnergy = solutions[i].energy;
        minIndex = i;
      }
    }
    if (minIndex != -1) {
      solutions[minIndex] = newSolution;
    } else {
      // forget about this solution, not good enough
    }
  }
  
  public AGSolution getBest() {
    AGSolution best = null;
    for (int i=0;i<AG_POOL;i++) {
      if (best == null || best.energy < solutions[i].energy) {
        best = solutions[i];
      }
    }
    
    return best;
  }
  
  public static Map<Integer, List<Action>> getPossibleActions() {
    Map<Integer, List<Action> > possibleActions = new HashMap<>();
    for (Factory factory : GameState.factories) {
      List<Action> actions = new ArrayList<>();
      possibleActions.put(factory.id, actions);
      
      if (factory.isMe()) {
        // upgrade
        if (!factory.isFront && factory.units >= 10 && factory.productionRate < 3) {
          actions.add(new UpgradeAction(factory));
        }
        // move : add some possible move actions
        if (factory.units > 0) {
          for (Factory otherFactory : GameState.factories) {
            if (otherFactory != factory) {
              /* -------------
              // front Moves
              // -------------*/
              if (factory.isFront) {
                // attack front ennemy or neutrals
                if (!otherFactory.isMe() 
                    && otherFactory.isFront || otherFactory.isNeutral()) {
                  actions.add(new MoveAction(factory, otherFactory, 1 + random.nextInt(factory.units)));
                }
              }
              
              /* ------------------------
              // back Moves
              // -------------------------*/
              if (!factory.isFront) {
                // move troops to front
                if ( otherFactory.isFront && otherFactory.isMe()) {
                  int units = 1+random.nextInt(factory.units);
                  // check if we can upgrade
                  if (factory.productionRate < 3 && factory.disabled == 0) {
                    units = Math.min(Math.max(factory.units-10, 0), units);
                  }
                  actions.add(new MoveAction(factory, otherFactory, units));
                }
              }
            }
          }
        }
        // bomb
        if (GameState.me.bombsLeft > 0) {
          for (Factory otherFactory : GameState.factories) {
            if (otherFactory.isOpponent() && !otherFactory.bombIncomming && otherFactory.productionRate > 1) {
              actions.add(new BombAction(factory, otherFactory));
            }
          }
        }
      }
    }
    return possibleActions;
  }

  public void reset() {
    for (int i=0;i<AG_POOL;i++) {
      solutions[i] = new AGSolution();
    }
  }

}
