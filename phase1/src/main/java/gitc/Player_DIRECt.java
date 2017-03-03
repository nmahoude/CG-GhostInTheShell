package gitc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import gitc.ag.AG;
import gitc.ag.AGParameters;
import gitc.entities.Factory;
import gitc.simulation.Simulation;

/**
 * seed pour petit terrain : 813958030
 * 203791280
 * 
 * @author nmahoude
 *
 */
public class Player_DIRECT {
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
    while(true) {
      gameState.read(in);
      long start = System.nanoTime();

      // find best action possible
      long timeLimit = start + turn != 0 ? 85_000_000L : 800_000_000L;
      //AGSolution solution = ag.evolution(timeLimit);

      //System.out.println(solution.output());
      
      /*
       * Some debug information
       */
      System.err.println("My forces : ["+gameState.units[0] + "/ prod:"+gameState.production[0]+"]" );
      System.err.println("Op forces : ["+gameState.units[1] + "/ prod:"+gameState.production[1]+"]" );

      /*
       * First iteration : find the best closest factory
       */
      String bestOutput ="WAIT";
      double bestScore = 0;
      
      List<Factory> myFactoriesUnderAttack = new ArrayList<>(10);
      List<Factory> oppFactoriesUnderAttack = new ArrayList<>(10);
      List<Factory> myFactoriesSupplier = new ArrayList<>(10);
      // DEFEND
      for (Factory factory : gameState.getFactories()) {
        if (factory.owner == GameState.me) {
          if (factory.isUnderAttackBy(GameState.opp)) {
            myFactoriesUnderAttack.add(factory);
          } else {
            myFactoriesSupplier.add(factory);
          }
        }
        if (factory.owner == GameState.opp && factory.isUnderAttackBy(GameState.me)) {
          oppFactoriesUnderAttack.add(factory);
        }
      }
      System.err.println("My factories under attack : ");
      for (Factory factory : myFactoriesUnderAttack) {
        System.err.println(factory.id + " - help needed : "+factory.neededUnit());
      }
      System.err.println("My supplier factories : ");
      for (Factory factory : myFactoriesSupplier) {
        System.err.println(factory.id + " - disposable units :"+(factory.units-1));
      }
      
      
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
                  double score = 5*(oppFactory.productionRate+0.1) / distance; 
                  if (score > bestScore) {
                    bestScore = score;
                    bestOutput = "MOVE "+myFactory.id+" "+oppFactory.id+ " "+neededCyborgs;
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
              if (neededCyborgs > 0 && neededCyborgs < myFactory.units) {
                  if (score > bestScore) {
                  bestScore = score;
                  bestOutput = "MOVE "+myFactory.id+" "+factory.id+ " "+neededCyborgs;
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
              && ( (turn == 0 && factory.productionRate >= 2) 
              || factory.units > gameState.unitsTotal / (gameState.factoryCount * 2))
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
        }
      }
      
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
