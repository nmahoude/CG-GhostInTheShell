package gitc.simulation;

import org.junit.Test;
import org.mockito.Mockito;

import gitc.GameState;
import gitc.ag.AGSolution;

public class SimulationTest {

  @Test
  public void simulation_should_call_state_restore_once() throws Exception {
    GameState state = Mockito.mock(GameState.class);
    Simulation simulation = new Simulation(state);

    simulation.simulate(new AGSolution());
    
    Mockito.verify(state).restoreState();
  }
}
