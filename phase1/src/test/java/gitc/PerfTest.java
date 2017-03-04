package gitc;

import java.util.Scanner;

import gitc.simulation.Simulation;

public class PerfTest {

  public void setup(GameState state) {
    
  }
  
  public static void main(String[] args) throws Exception {
    GameState.TDD_OUPUT = false;
    
    GameState state = new GameState();
    String setup = "9 36\n";
    setup +="0 1 1\n";
    setup +="0 2 1\n";
    setup +="0 3 5\n";
    setup +="0 4 5\n";
    setup +="0 5 7\n";
    setup +="0 6 7\n";
    setup +="0 7 2\n";
    setup +="0 8 2\n";
    setup +="1 2 5\n";
    setup +="1 3 4\n";
    setup +="1 4 8\n";
    setup +="1 5 4\n";
    setup +="1 6 10\n";
    setup +="1 7 3\n";
    setup +="1 8 4\n";
    setup +="2 3 8\n";
    setup +="2 4 4\n";
    setup +="2 5 10\n";
    setup +="2 6 4\n";
    setup +="2 7 4\n";
    setup +="2 8 3\n";
    setup +="3 4 13\n";
    setup +="3 5 2\n";
    setup +="3 6 14\n";
    setup +="3 7 2\n";
    setup +="3 8 9\n";
    setup +="4 5 14\n";
    setup +="4 6 2\n";
    setup +="4 7 9\n";
    setup +="4 8 2\n";
    setup +="5 6 15\n";
    setup +="5 7 4\n";
    setup +="5 8 10\n";
    setup +="6 7 10\n";
    setup +="6 8 4\n";
    setup +="7 8 6\n";
    Scanner in = new Scanner(setup);
    state.readSetup(in);
    /************/
    String source="";
    source+="9 \n";
    source+="0 FACTORY 0 0 0 0 0\n";
    source+="1 FACTORY 1 17 0 0 0\n";
    source+="2 FACTORY -1 17 0 0 0\n";
    source+="3 FACTORY 0 0 0 0 0\n";
    source+="4 FACTORY 0 0 0 0 0\n";
    source+="5 FACTORY 0 10 2 0 0\n";
    source+="6 FACTORY 0 10 2 0 0\n";
    source+="7 FACTORY 0 8 2 0 0\n";
    source+="8 FACTORY 0 8 2 0 0\n";
    in = new Scanner(source);
    state.read(in);

    
    Player.NANOSECONDS_THINK_TIME = 4*1_000_000_000L;
    Player.gameState = state;
    Player.simulation = new Simulation(state);

    Player.start = System.nanoTime();
    Player.doOneTurn();
    
    System.out.println("Finished");
  }
}
