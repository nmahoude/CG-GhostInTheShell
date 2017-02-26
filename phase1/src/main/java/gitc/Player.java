package gitc;

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
 * seed pour petit terrain : 813958030
 * 203791280
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
    while(true) {
      // TODO read gameTurn informations
      gameState.read(in);
      long start = System.nanoTime();

      // find best action possible
      long timeLimit = start + turn != 0 ? 85_000_000L : 800_000_000L;
      //AGSolution solution = ag.evolution(timeLimit);

      //System.out.println(solution.output());
      
      /*
       * Some debug information
       */
      System.err.println("My forces : ["+gameState.cyborgs[0] + "/ prod:"+gameState.production[0]+"]" );
      System.err.println("Op forces : ["+gameState.cyborgs[1] + "/ prod:"+gameState.production[1]+"]" );

      /*
       * First iteration : find the best closest factory
       */

      AGSolution ag = new AGSolution(); // no actions

      String bestOutput ="WAIT";
      double bestScore = 0;
      // ATTACK
      for (Factory factory : gameState.getFactories()) {
        if (factory.isOpponent()) {
          // System.err.println("Factory "+factory.id+" is opponent" );
          Factory oppFactory = factory;
          for (Factory myFactory : gameState.getFactories()) {
            if (myFactory.isMe() && myFactory.units > 0) {
              int distance = myFactory.getDistanceTo(oppFactory);
              int oppUnitsEvaluation = oppFactory.units + oppFactory.unitsInTransit[1] + oppFactory.productionRate*(distance+1);
              int myUnitsEvaluation = oppFactory.unitsInTransit[0];
              if (myUnitsEvaluation < oppUnitsEvaluation && myFactory.units +  myUnitsEvaluation > oppUnitsEvaluation) {
                int neededCyborgs = oppUnitsEvaluation - myUnitsEvaluation+1;
                int remainingCyborgs = myFactory.units - neededCyborgs;
                if (neededCyborgs > 0 && remainingCyborgs > myFactory.unitsInTransit[1]) {
                  double score = 5*oppFactory.productionRate / distance; 
                  if (score > bestScore) {
                    bestScore = score;
                    bestOutput = "MOVE "+myFactory.id+" "+oppFactory.id+ " "+neededCyborgs;
                    ag.players.get(0).turnActions[0].clear();
                    ag.players.get(0).turnActions[0].moveActions.add(new MoveAction(myFactory, oppFactory, neededCyborgs));
                  }
                }
              }
            }
          }
        }
        // TAKE EMPTY
        if (factory.isNeutral()) {
          Factory neutralFactory = factory;
          // System.err.println("Factory "+factory.id+" is free" );
          for (Factory myFactory : GameState.factories) {
            if (myFactory.isMe() && myFactory.units > 0) {
              int distance = myFactory.getDistanceTo(factory);
             // System.err.println("One link go to one of my factory ("+link.toFactory.id+")");
              double score = neutralFactory.productionRate * neutralFactory.productionRate / distance;
             // System.err.println("Score :"+score+ " bc: "+factory.production+" / "+link.distance);
              int neededCyborgs = neutralFactory.units + 1 - neutralFactory.unitsInTransit[0] + neutralFactory.unitsInTransit[1];
              if (neededCyborgs < myFactory.units) {
                  if (score > bestScore) {
                  bestScore = score;
                  bestOutput = "MOVE "+myFactory.id+" "+factory.id+ " "+neededCyborgs;
                  ag.players.get(0).turnActions[0].clear();
                  ag.players.get(0).turnActions[0].moveActions.add(new MoveAction(myFactory, neutralFactory, neededCyborgs));
                }
              }
            }
          }
        }
      }
      
      // check for bombs !
      if (GameState.me.bombsLeft > 0) {
        Factory toBomb = null;
        Factory from = null;
        for (Factory factory : gameState.getFactories()) {
          if (factory.isOpponent() 
              && 
                !factory.willBeBombed
              && ( (turn == 0 && factory.productionRate >= 2) 
              || factory.units > gameState.cyborgsTotal / (gameState.factoryCount * 2))
              ) {
            toBomb = factory;
          }
        }
        if (toBomb != null) {
          for (Factory factory : gameState.getFactories()) {
            if (factory.isMe()) {
              from = factory;
            }
          }
        }
        if (toBomb != null && from != null) {
          bestOutput+="; BOMB "+from.id+" "+toBomb.id;
          GameState.me.bombsLeft--;
          ag.players.get(0).turnActions[0].bombActions.add(new BombAction(from, toBomb));
        }
      }
      
      AGSolution agNoMoves = new AGSolution(); // no actions
      simulation.simulate(agNoMoves);
      simulation.simulate(ag);

      AGSolution bestAG = ag;

      System.err.println("ag energy (no move): "+agNoMoves.energy);
      System.err.println("ag energy (my move): "+ag.energy);
      
      Random random = new Random();
      long startSim = System.currentTimeMillis();
      for (int i=0;i<300;i++) {
        AGSolution agRand = new AGSolution() ;
        for (int turn=0;turn < 10;turn++) {
          for (Factory factory : GameState.factories) {
            if (factory.isMe() && factory.units > 0) {
              int otherFactory;
              do {
                otherFactory = random.nextInt(GameState.factories.length);
              } while(otherFactory == factory.id);
              if (!GameState.factories[otherFactory].isMe()) {
                int units = random.nextInt(3*factory.units);
                agRand.players.get(0).turnActions[turn].moveActions.add(new MoveAction(factory, GameState.factories[otherFactory], units));
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
      System.err.println("Simulation took : "+(endSim-startSim)+" ms");
      
      bestOutput = bestAG.output();
      System.out.println(bestOutput);

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
