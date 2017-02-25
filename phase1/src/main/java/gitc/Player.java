package gitc;

import java.util.Scanner;

import gitc.ag.AG;
import gitc.ag.AGParameters;
import gitc.ag.AGSolution;
import gitc.simulation.Simulation;

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
    while(true) {
      // TODO read gameTurn informations
      gameState.read(in);
      long start = System.nanoTime();

      // find best action possible
      long timeLimit = start + turn != 0 ? 85_000_000L : 800_000_000L;
      //AGSolution solution = ag.evolution(timeLimit);

      //System.out.println(solution.output());
      System.out.println("WAIT");

      cleanUp();
    }
  }

  private static void setupAG() {
    AGParameters parameters = new AGParameters();
    ag = new AG(simulation, parameters);
  }
  
  private static void cleanUp() {
    turn ++;
  }
}
