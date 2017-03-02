package gitc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import gitc.ag.AG;
import gitc.ag.AGParameters;
import gitc.ag.AGSolution;
import gitc.entities.Factory;
import gitc.simulation.Simulation;
import gitc.simulation.actions.Action;
import gitc.simulation.actions.BombAction;
import gitc.simulation.actions.MoveAction;
import gitc.simulation.actions.UpgradeAction;

/**
 * seed pour petit terrain : 
 * 
 * 813958030 (1 seul resource > 0 !)
 * 
 * 196562510 (5 factories, prod moyenne)
 * 
 * 601738221 distribution geographique sympa pour regarder ce qui se passe
 * 725226336 sympa aussi
 * 
 * @author nmahoude
 *
 */
public class Player {
  private static Scanner in;
  public static long NANOSECONDS_THINK_TIME = 40_000_000;
  public static int turn = 0;
  public static AG ag;
  public static GameState gameState;
  public static Simulation simulation;

  public static void main(String[] args) {
    System.err.println("Ready to go");
    gameState = new GameState();
    simulation = new Simulation(gameState);
    in = new Scanner(System.in);

    setupAG();
    gameState.readSetup(in);
    while (true) {
      gameState.read(in);
      if (turn == 0) {
        doFirstTurn();
      } else {
        doOneTurn();
      }
      turn++;
    }
  }

  private static void doFirstTurn() {
    Factory myBase = null;
    Factory oppBase = null;
    for (Factory factory : GameState.factories) {
      if (factory.owner == GameState.me) {
        myBase = factory;
      }
      if (factory.owner == GameState.opp) {
        oppBase = factory;
      }
    }
    
    List<Factory> mySideFactories = new ArrayList<>();
    List<Factory> oppSideFactories = new ArrayList<>();
    for (Factory factory : GameState.factories) {
      if (factory != myBase) {
        if (factory.getDistanceTo(myBase) < factory.getDistanceTo(oppBase)) {
          mySideFactories.add(factory);
        } else {
          oppSideFactories.add(factory);
        }
      }
    }
    
    List<Factory> optimalChoice = new ArrayList<>();
    KnapSack.fillPackage(myBase.units, mySideFactories, optimalChoice, mySideFactories.size());
    
    String output = "";
    for (Factory factory : optimalChoice) {
      output+="MOVE "+myBase.id+" "+factory.id+" "+(factory.units+1)+";";
    }
    oppSideFactories.sort(new Comparator<Factory>() {
      public int compare(Factory f1, Factory f2) {
        return Integer.compare(f2.productionRate, f1.productionRate);
      };
    });

    for (int i=0;i<2;i++) {
      if (oppSideFactories.get(i).productionRate == 3) {
        output+="BOMB "+myBase.id+" "+oppSideFactories.get(i).id+";";
      }
    }
    output+="MSG KNAPSACK";
    System.out.println(output);
  }

  public static void doOneTurn() {
    Random random = new Random();

    long start = System.nanoTime();
    AGSolution bestAG = null;

    long startSim = System.nanoTime();
    int simulations = 0;
    while (System.nanoTime() - startSim < NANOSECONDS_THINK_TIME) {
      simulations++;
      // Build possible actions
      Map<Integer, List<Action>> possibleActions = getPossibleActions(random);

      AGSolution agRand = new AGSolution();
      for (Factory factory : GameState.factories) {
        List<Action> actions = possibleActions.get(factory.id);
        if (actions.size() > 0) {
          int actionNumber = random.nextInt(actions.size());
          Collections.shuffle(actions);
          for (int i=0;i<actionNumber;i++) {
            if (random.nextBoolean()) {
              agRand.players.get(0).addAction(actions.get(i), 0);
            }
          }
        }
      }
      simulation.simulate(agRand);
      if (bestAG == null || agRand.energy > bestAG.energy) {
        bestAG = agRand;
      }
    }
    long endSim = System.nanoTime();
    if (bestAG == null) {
      System.out.println("WAIT");
    } else {
      System.out.println(bestAG.output());
    }
    System.err.println("Simulation took : " + (int)((endSim - startSim)/1_000_000) + " ms for " + simulations + " simulations");
    //System.err.println("ag energy (no move): " + agNoMoves.energy);
    //System.err.println("Best AG energy : " + bestAG.energy);
    cleanUp();
  }

  public static Map<Integer, List<Action>> getPossibleActions(Random random) {
    Map<Integer, List<Action> > possibleActions = new HashMap<>();
    for (Factory factory : GameState.factories) {
      List<Action> actions = new ArrayList<>();
      possibleActions.put(factory.id, actions);
      
      if (factory.isMe()) {
        // upgrade
        if (!factory.isFront && factory.unitsNeededAt==0 && factory.units >= 10 && factory.productionRate < 3) {
          actions.add(new UpgradeAction(factory));
        }
        // move : add some possible move actions
        if (factory.units > 0) {
          for (Factory otherFactory : GameState.factories) {
            if (otherFactory != factory) {
              // front attacks
              if (factory.isFront) {
                // attack other front
                if (!otherFactory.isMe() /*&& otherFactory.isFront*/) {
                  int units = 1 + random.nextInt(factory.units);
                  actions.add(new MoveAction(factory, otherFactory, units));
                }
              }

              // Back troops
              if ((!factory.isFront && factory.unitsDisposable>0) ) {
                // back send troops to front
                if (otherFactory.isFront && otherFactory.isMe()) {
                  int units = 1+random.nextInt(factory.units);
//                  // check if we can upgrade
//                  if (factory.productionRate < 3 && factory.disabled == 0) {
//                    units = Math.min(Math.max(factory.units-10, 0), units);
//                  }
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

  private static void getFactoriesStatus(List<Factory> myFactoriesUnderAttack, List<Factory> oppFactoriesUnderAttack, List<Factory> myFactoriesSupplier) {
    for (Factory factory : gameState.getFactories()) {
      if (factory.owner == GameState.me) {
        if (factory.isUnderAttackBy(GameState.opp) && factory.neededUnit() > 0) {
          for (int i = 0; i < factory.productionRate + 1; i++) {
            myFactoriesUnderAttack.add(factory);
          }
        } else {
          myFactoriesSupplier.add(factory);
        }
      }
      if (factory.isOpponent() && factory.isUnderAttackBy(GameState.me)) {
        for (int i = 0; i < factory.productionRate + 1; i++) {
          oppFactoriesUnderAttack.add(factory);
        }
      }
      if (factory.isNeutral()) {
        for (int i = 0; i < factory.productionRate + 1; i++) {
          oppFactoriesUnderAttack.add(factory);
        }
      }
    }
    if (oppFactoriesUnderAttack.isEmpty()) {
      for (Factory factory : gameState.getFactories()) {
        if (!factory.isMe()) {
          for (int i = 0; i < factory.productionRate + 1; i++) {
            oppFactoriesUnderAttack.add(factory);
          }
        }
      }
    }
  }

  private static void setupAG() {
    AGParameters parameters = new AGParameters();
    ag = new AG(simulation, parameters);
  }

  private static void cleanUp() {
  }
}
