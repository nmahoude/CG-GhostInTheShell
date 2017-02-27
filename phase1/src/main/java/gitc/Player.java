package gitc;

import java.util.ArrayList;
import java.util.Collections;
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
 * seed pour petit terrain : 813958030 (1 seul resource > 0 !) 203791280
 * 
 * @author nmahoude
 *
 * TODO : nombre d'units en transit
 * TODO : nombre d'units static
 * 
 * TODO : Influence
 */
public class Player {
  private static Scanner in;
  private static int turn = 0;
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
      long start = System.nanoTime();

      // find best action possible
      long timeLimit = start + turn != 0 ? 85_000_000L : 800_000_000L;
      // Some debug information
      // System.err.println("My forces : ["+gameState.cyborgs[0] + "/
      // prod:"+gameState.production[0]+"]" );
      // System.err.println("Op forces : ["+gameState.cyborgs[1] + "/
      // prod:"+gameState.production[1]+"]" );

      AGSolution agNoMoves = new AGSolution(); // no actions
      simulation.simulate(agNoMoves);
      System.err.println("ag energy (no move): " + agNoMoves.energy);

      AGSolution bestAG = null;

      Random random = new Random();
      long startSim = System.currentTimeMillis();
      // Build possible actions
      Map<Integer, List<Action> > possibleActions = new HashMap<>();
      for (Factory factory : GameState.factories) {
        List<Action> actions = new ArrayList<>();
        possibleActions.put(factory.id, actions);
        
        if (factory.isMe()) {
          // upgrade
          if (factory.units > 10) {
            actions.add(new UpgradeAction(factory));
          }
          // move
          if (factory.units > 0) {
            for (Factory otherFactory : GameState.factories) {
              if (otherFactory != factory && !otherFactory.isMe()) {
                // add some possible move actions
                actions.add(new MoveAction(factory, otherFactory, 1+random.nextInt(factory.units)));
              }
            }
          }
          // bomb
          if (GameState.me.bombsLeft > 0) {
            for (Factory otherFactory : GameState.factories) {
              if (otherFactory.isOpponent()) {
                actions.add(new BombAction(factory, otherFactory));
              }
            }
          }
        }
      }

      int simulations = 0;
      while (System.nanoTime() - start < 40_000_000) {
        simulations++;
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
      long endSim = System.currentTimeMillis();

      System.out.println(bestAG.output());

      System.err.println("Simulation took : " + (endSim - startSim) + " ms for " + simulations + " simulations");
      System.err.println("Best AG energy : " + bestAG.energy);
      cleanUp();
    }
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
    turn++;
  }
}
