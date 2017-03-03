package gitc.ag;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import gitc.GameState;
import gitc.entities.Bomb;
import gitc.entities.Factory;
import gitc.simulation.actions.Action;
import gitc.simulation.actions.BombAction;
import gitc.simulation.actions.MoveAction;
import gitc.simulation.actions.UpgradeAction;

public class AGPool {
//  private static Random random = new Random(System.nanoTime());
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
    return solution;
  }
  
  public AGSolution cross() {
    int index1 = findIndex(solutions, -1);
    int index2 = findIndex(solutions, index1);
    
    return cross(solutions[index1], solutions[index2]);
  }

  int findIndex(AGSolution[] pool, int otherThanIndex) {
    int aIndex, bIndex;
    do {
      aIndex = random.nextInt(AG_POOL);
    } while (aIndex == otherThanIndex);

    for (int i=0;i<3;i++) {
      do {
        bIndex = random.nextInt(AG_POOL);
      } while (bIndex == aIndex || bIndex == otherThanIndex);
      
      aIndex = pool[aIndex].energy > pool[bIndex].energy ? aIndex : bIndex;
    }
    return aIndex;
  }
  
  public AGSolution cross(AGSolution ag1, AGSolution ag2) {
    AGSolution newSolution = new AGSolution();
    TurnAction newActions = newSolution.players.get(0).turnActions[0];
    
    TurnAction a1 = ag1.players.get(0).turnActions[0];
    TurnAction a2 = ag2.players.get(0).turnActions[0];
    for (Action action : a1.actions) {
      if (random.nextBoolean()) {
        newActions.actions.add(action);
      }      
    }
    for (Action action : a2.actions) {
      if (random.nextBoolean()) {
        newActions.actions.add(action);
      }      
    }
    
    return newSolution;
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
          if (!factory.isFront) {
          for (Factory otherFactory : GameState.factories) {
            if (otherFactory != factory) {

              /* ------------------------
              // back Moves
              // -------------------------*/
              int action = actions.size();
              // move troops to front
              if ( otherFactory.isFront && otherFactory.isMe()) {
                int units = 1+random.nextInt(factory.units);
                // check if we can upgrade
//                if (factory.productionRate < 3 && factory.disabled == 0) {
//                  units = Math.min(Math.max(factory.units-10, 0), units);
//                }
                actions.add(new MoveAction(factory, otherFactory, units));
              }
              if (!otherFactory.isFront && otherFactory.isMe()) {
                if (otherFactory.getDistanceTo(factory) < 6) {
                  int units = 1+random.nextInt(factory.units);
                  if (factory.productionRate < 3 && factory.disabled == 0) {
                    units = Math.min(Math.max(factory.units-10, 0), units);
                  }
                  actions.add(new MoveAction(factory, otherFactory, units));
                }
              }
              if (factory.id == 3) {
                System.err.println("Actions added for Fac 3: "+(actions.size()-action));
              }
              }
            }
          }
          
          /* -------------
          // front Moves
          // -------------*/
          
          if (factory.isFront) {
          for (Factory otherFactory : GameState.factories) {
            if (otherFactory != factory) {
                boolean attack = true;
                for (Factory isNear : GameState.factories) {
                  if (isNear.isOpponent() && isNear.getDistanceTo(factory) < factory.getDistanceTo(otherFactory)) {
                    attack = false;
                  }
                }
                if (attack) {
                  // attack front ennemy or neutrals
                  MoveAction action = null;
                  if (!otherFactory.isMe() 
                      && otherFactory.isFront || otherFactory.isNeutral()) {
                    action = new MoveAction(factory, otherFactory, 1 + random.nextInt(factory.units));
                  }
                  if (otherFactory.isMe() && otherFactory.isFront) {
                    action = new MoveAction(factory, otherFactory, 1 + random.nextInt(factory.units));
                  }
                  if (action != null) {
                    if (bombWillNotDestroyOurTroops(action)) {
                      actions.add(action);
                    }
                  }
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

  private static boolean bombWillNotDestroyOurTroops(MoveAction action) {
    // check for a bomb that would destroy our army !
    boolean doAction = true;;
    for (Bomb bomb : GameState.bombs.values()) {
      if (bomb.destination == action.dst && bomb.remainingTurns >= action.dst.getDistanceTo(action.src)) {
        doAction = false;
      }
    }
    return doAction;
  }

  public void reset() {
    for (int i=0;i<AG_POOL;i++) {
      solutions[i] = new AGSolution();
    }
  }
}
