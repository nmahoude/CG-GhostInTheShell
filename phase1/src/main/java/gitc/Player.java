package gitc;

import java.util.Scanner;

import gitc.ag.AG;
import gitc.ag.AGParameters;
import gitc.entities.Factory;
import gitc.entities.Link;
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
      
      /*
       * Some debug information
       */
      System.err.println("My forces : ["+gameState.cyborgs[0] + "/ + prod:"+gameState.production[0] );
      System.err.println("Op forces : ["+gameState.cyborgs[1] + "/ + prod:"+gameState.production[1] );

      /*
       * First iteration : find the best closest factory
       */
      String bestOutput ="WAIT";
      Link usedLink = null;
      double bestScore = 0;
      // ATTACK
      for (Factory factory : gameState.getFactories()) {
        if (factory.isOpponent()) {
          // System.err.println("Factory "+factory.id+" is opponent" );
          Factory oppFactory = factory;
          for (Link oppLink : oppFactory.links) {
            if (oppLink != null && oppLink.toFactory.isMe() && oppLink.toFactory.cyborgs > 0) {
              Factory myFactory = oppLink.toFactory;
              Link myLink = myFactory.getLinkToFactory(oppFactory.id);
              if (myFactory.cyborgs + myLink.cyborgsInTransit > oppFactory.cyborgs + oppFactory.production*oppLink.distance) {
                int neededCyborgs = oppFactory.cyborgs + oppFactory.production*oppLink.distance - oppLink.cyborgsInTransit;
                int remainingCyborgs = myFactory.cyborgs - neededCyborgs;
                if (neededCyborgs > 0 && remainingCyborgs > myFactory.incommingCyborgs) {
                  usedLink = oppLink;
                  bestScore = 5*oppFactory.production * 100.0 / oppLink.distance;
                  bestOutput = "MOVE "+myFactory.id+" "+oppFactory.id+ " "+neededCyborgs;
                }
              }
            }
          }          
        }
        // TAKE EMPTY
        if (factory.isNeutral()) {
          Factory neutralFactory = factory;
          // System.err.println("Factory "+factory.id+" is free" );
          for (Link link : neutralFactory.links) {
            if (link != null && link.toFactory.isMe() && link.toFactory.cyborgs > 0) {
              Factory myFactory = link.toFactory;
             // System.err.println("One link go to one of my factory ("+link.toFactory.id+")");
              double score = neutralFactory.production * neutralFactory.production / link.distance;
             // System.err.println("Score :"+score+ " bc: "+factory.production+" / "+link.distance);
              int neededCyborgs = neutralFactory.cyborgs + 1;
              if (neededCyborgs < myFactory.cyborgs) {
                  if (score > bestScore) {
                  usedLink = link;
                  bestScore = score;
                  bestOutput = "MOVE "+link.toFactory.id+" "+factory.id+ " "+neededCyborgs;
                }
              }
            }
          }
        }
      }
      
      // TODO watch if more than one link used !
      if (usedLink != null) {
        usedLink.usedInThisTurn = true;
      }
      
      // check for bombs !
      if (gameState.bombs[0] > 0) {
        Factory toBomb = null;
        Factory from = null;
        for (Factory factory : gameState.getFactories()) {
          if (factory.isOpponent() 
              && 
                !factory.willBeBombed
              && (turn == 0
                  || factory.cyborgs > gameState.cyborgsTotal / (gameState.factoryCount * 2))
              ) {
            toBomb = factory;
          }
        }
        if (toBomb != null) {
          for (Factory factory : gameState.getFactories()) {
            if (factory.isMe() && !factory.links[toBomb.id].usedInThisTurn) {
              from = factory;
            }
          }
        }
        if (toBomb != null && from != null) {
          bestOutput+="; BOMB "+from.id+" "+toBomb.id;
          
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
