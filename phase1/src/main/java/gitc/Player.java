package gitc;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

import gitc.ag.AG;
import gitc.ag.AGParameters;
import gitc.ag.AGPool;
import gitc.ag.AGSolution;
import gitc.entities.Factory;
import gitc.simulation.Simulation;

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
  private static Random random = new Random();

  private static Scanner in;
  public static long NANOSECONDS_THINK_TIME = 45_000_000;
  public static int turn = 0;
  public static AG ag;
  public static GameState gameState;
  public static Simulation simulation;
  public static long start;
  private static AGPool AGPool = new AGPool();
  
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

    int simulations = 0;
    AGPool.reset();
    while (System.nanoTime() - start < NANOSECONDS_THINK_TIME) {
      simulations++;
      AGSolution solution;
      if (random.nextInt(100) == 0) { /* TODO try to find a good value 100 -> 75 GOLD le jeudi soir*/
        solution = AGPool.createRandom();
      } else {
        solution = AGPool.cross();
      }
      
      simulation.simulate(solution);
      AGPool.propose(solution);
      
    }
    AGSolution bestAG = AGPool.getBest();
    if ( bestAG == null) {
      System.out.println("WAIT");
    } else {
      System.out.println(bestAG.output()+";MSG "+simulations);
    }
    //System.err.println("Simulation took : " + (int)((endSim - start)/1_000_000) + " ms for " + simulations + " simulations");
    cleanUp();
  }

  private static void setupAG() {
    AGParameters parameters = new AGParameters();
    ag = new AG(simulation, parameters);
  }

  private static void cleanUp() {
  }
}
