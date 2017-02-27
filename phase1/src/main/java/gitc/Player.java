package gitc;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import gitc.ag.AG;
import gitc.ag.AGParameters;
import gitc.ag.AGSolution;
import gitc.entities.Factory;
import gitc.simulation.Simulation;
import gitc.simulation.actions.BombAction;
import gitc.simulation.actions.MoveAction;

/**
 * seed pour petit terrain : 813958030 (1 seul resource > 0 !) 203791280
 * 
 * @author nmahoude
 *
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
    // TODO read setup
    gameState.readSetup(in);
    while (true) {
      // TODO read gameTurn informations
      gameState.read(in);
      long start = System.nanoTime();

      // find best action possible
      long timeLimit = start + turn != 0 ? 85_000_000L : 800_000_000L;
      // AGSolution solution = ag.evolution(timeLimit);

      // Some debug information
      // System.err.println("My forces : ["+gameState.cyborgs[0] + "/
      // prod:"+gameState.production[0]+"]" );
      // System.err.println("Op forces : ["+gameState.cyborgs[1] + "/
      // prod:"+gameState.production[1]+"]" );

      List<Factory> myFactoriesUnderAttack = new ArrayList<>(10);
      List<Factory> oppFactoriesUnderAttack = new ArrayList<>(10);
      List<Factory> myFactoriesSupplier = new ArrayList<>(10);
      getFactoriesStatus(myFactoriesUnderAttack, oppFactoriesUnderAttack, myFactoriesSupplier);

      AGSolution agNoMoves = new AGSolution(); // no actions
      simulation.simulate(agNoMoves);
      System.err.println("ag energy (no move): " + agNoMoves.energy);

      AGSolution bestAG = null;

      // try some random moves
      Random random = new Random();
      long startSim = System.currentTimeMillis();
      int simulations = 0;
      while (System.nanoTime() - start < 45_000_000) {
        simulations++;
        AGSolution agRand = new AGSolution();
        for (int turn = 0; turn < 1; turn++) {
          for (Factory factory : myFactoriesSupplier) {
            if (oppFactoriesUnderAttack.size() > 0 && random.nextInt(30) == 0) {
              int attackFactoryIndex = random.nextInt(oppFactoriesUnderAttack.size());
              Factory attackFactory = oppFactoriesUnderAttack.get(attackFactoryIndex);
              if (gameState.willBombHitFactory(attackFactory) == -1) {
                agRand.players.get(0).turnActions[turn].actions.add(new BombAction(factory, attackFactory));
              }
            }
            if (factory.units > 0) {
              if (myFactoriesUnderAttack.size() > 0 && random.nextInt(2) == 0) {
                // help
                int helpFactoryIndex = random.nextInt(myFactoriesUnderAttack.size());
                Factory helpFactory = myFactoriesUnderAttack.get(helpFactoryIndex);
                int units = factory.units / 2;
                agRand.players.get(0).turnActions[turn].actions.add(new MoveAction(factory, helpFactory, units));
              } else if (oppFactoriesUnderAttack.size() > 0 && random.nextInt(2) == 0) {
                // attack
                int attackFactoryIndex = random.nextInt(oppFactoriesUnderAttack.size());
                Factory attackFactory = oppFactoriesUnderAttack.get(attackFactoryIndex);
                int units = factory.units / 2;
                agRand.players.get(0).turnActions[turn].actions.add(new MoveAction(factory, attackFactory, units));
              } else {
                // nothing
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
