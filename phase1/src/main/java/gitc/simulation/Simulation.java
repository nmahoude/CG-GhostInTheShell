package gitc.simulation;

import gitc.GameState;
import gitc.ag.AGSolution;

public class Simulation {
  private GameState state;
  
  public Simulation(GameState state) {
    this.state = state;
  }
  
  public void simulate(AGSolution solution) {
    
    state.restoreState();
  }

}
