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
  public static Random random = new Random();
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
        getPossibleActionsForFactory(factory, actions);
      }
    }
    return possibleActions;
  }

  public static void getPossibleActionsForFactory(Factory factory, List<Action> actions) {
    // upgrade
    if (factory.productionRate < 3) {
      boolean canUpgrade = true;
      for (int units : factory.future) {
        if (units <= 10) {
          canUpgrade = false;
        }
      }
      if (canUpgrade && GameState.center != factory) {
        actions.add(new UpgradeAction(factory));
      }
    }
    // move : add some possible move actions
    if (factory.units > 0) {
      if (!factory.isFront) {
        for (Factory otherFactory : GameState.factories) {
          if (otherFactory != factory) {

            /* ------------------------
            // back Moves
            // -------------------------*/
            if (otherFactory.isNeutral() && otherFactory.productionRate > 0) {
              int units = otherFactory.units+1;
              if (factory.units >= units) {
                MoveAction action = new MoveAction(factory, otherFactory, units);
                action = findBetterRouteForMove(action);
                actions.add(action);
              }
            }
            
            // move troops to front
            if ( otherFactory.isFront && otherFactory.isMe()) {
              int units = factory.units; //1+random.nextInt(factory.units);
              // check if we can upgrade
              //                if (factory.productionRate < 3 && factory.disabled == 0) {
              //                  units = Math.min(Math.max(factory.units-10, 0), units);
              //                }
              MoveAction action = new MoveAction(factory, otherFactory, units);
              action = findBetterRouteForMove(action);
              actions.add(action);
            }
            if (!otherFactory.isFront && otherFactory.isMe()) {
              if (otherFactory.getDistanceTo(factory) < 6) {
                int units = 1+random.nextInt(factory.units);
                if (factory.productionRate < 3 && factory.disabled == 0) {
                  units = Math.min(Math.max(factory.units-10, 0), units);
                }
                MoveAction action = new MoveAction(factory, otherFactory, units);
                action = findBetterRouteForMove(action);
                actions.add(action);
              }
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
            for (Factory isNearer : GameState.factories) {
              if (isNearer.isOpponent() && isNearer.getDistanceTo(factory) < factory.getDistanceTo(otherFactory)) {
                attack = false;
              }
            }
            if (attack) {
              // attack front ennemy or neutrals
              MoveAction action = null;
              if ((!otherFactory.isMe() && otherFactory.isFront) 
                  || otherFactory.isNeutral()) {
                
                int unitsToSend = 1 + random.nextInt(factory.units);

                if (otherFactory.isOpponent()) {
                  // check that we wont send more units than we can defend with !
                  if (factory.productionRate<=otherFactory.productionRate) {
                    int remainingUnits = factory.units-unitsToSend;
                    if (remainingUnits < otherFactory.units) {
                      unitsToSend = Math.max(0, factory.units - otherFactory.units);
                    }
                  }
                }
                action = new MoveAction(factory, otherFactory, unitsToSend);
                addAction(actions, action);
              }
              if (otherFactory.isMe() && otherFactory.isFront) {
                action = new MoveAction(factory, otherFactory, 1 + random.nextInt(factory.units));
                addAction(actions, action);
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

  private static void addAction(List<Action> actions, MoveAction action) {
    if (action != null) {
      if (bombWillNotDestroyOurTroops(action)) {
        action = findBetterRouteForMove(action);
      }
      actions.add(action);
    }
  }

  private static MoveAction findBetterRouteForMove(MoveAction action) {
    int distance = action.src.getDistanceTo(action.dst);
    int minNewDistance = distance;
    Factory bestFactory = null;
    
    for (Factory factory : GameState.factories) {
      if (factory == action.src || factory == action.dst) continue;
      if (!factory.isMe()) continue;
      
      if (factory.getDistanceTo(action.src) < distance && factory.getDistanceTo(action.dst) < distance) {
        return new MoveAction(action.src, factory, action.units);
      }
    }
    return action;
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
