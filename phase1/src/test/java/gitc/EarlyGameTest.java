package gitc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.List;
import java.util.Map;
import java.util.Random;

import org.junit.Test;

import gitc.ag.AGSolution;
import gitc.ag.AGSolutionComparator;
import gitc.simulation.actions.Action;
import gitc.simulation.actions.ActionType;
import gitc.simulation.actions.MoveAction;
import gitc.situations.FB;
import gitc.situations.GameBuilder;
import gitc.situations.LB;

public class EarlyGameTest {

  @Test
  public void pourquoi_pas_attaquer_3() throws Exception {
    GameState state = new GameBuilder().l(new LB().from(0).to(1).d(1).build()).l(new LB().from(0).to(2).d(1).build()).l(new LB().from(0).to(3).d(5).build()).l(new LB().from(0).to(4).d(5).build()).l(new LB().from(0).to(5).d(1).build()).l(new LB().from(0).to(6).d(1).build()).l(new LB().from(0).to(7).d(4).build()).l(new LB().from(0).to(8).d(4).build()).l(new LB().from(1).to(2).d(3).build())
        .l(new LB().from(1).to(3).d(3).build()).l(new LB().from(1).to(4).d(8).build()).l(new LB().from(1).to(5).d(2).build()).l(new LB().from(1).to(6).d(2).build()).l(new LB().from(1).to(7).d(3).build()).l(new LB().from(1).to(8).d(6).build()).l(new LB().from(2).to(3).d(8).build()).l(new LB().from(2).to(4).d(3).build()).l(new LB().from(2).to(5).d(2).build()).l(new LB().from(2).to(6).d(2).build())
        .l(new LB().from(2).to(7).d(6).build()).l(new LB().from(2).to(8).d(3).build()).l(new LB().from(3).to(4).d(12).build()).l(new LB().from(3).to(5).d(5).build()).l(new LB().from(3).to(6).d(7).build()).l(new LB().from(3).to(7).d(3).build()).l(new LB().from(3).to(8).d(10).build()).l(new LB().from(4).to(5).d(7).build()).l(new LB().from(4).to(6).d(5).build()).l(new LB().from(4).to(7).d(10).build())
        .l(new LB().from(4).to(8).d(3).build()).l(new LB().from(5).to(6).d(5).build()).l(new LB().from(5).to(7).d(2).build()).l(new LB().from(5).to(8).d(7).build()).l(new LB().from(6).to(7).d(7).build()).l(new LB().from(6).to(8).d(2).build()).l(new LB().from(7).to(8).d(10).build())
        .withFactory(new FB().id(0).player(0).units(0).prod(0).disabled(0).build())
        .withFactory(new FB().id(1).player(1).units(25).prod(2).disabled(0).build())
        .withFactory(new FB().id(2).player(-1).units(25).prod(2).disabled(0).build())
        .withFactory(new FB().id(3).player(0).units(5).prod(2).disabled(0).build())
        .withFactory(new FB().id(4).player(0).units(5).prod(2).disabled(0).build())
        .withFactory(new FB().id(5).player(0).units(1).prod(1).disabled(0).build())
        .withFactory(new FB().id(6).player(0).units(1).prod(1).disabled(0).build())
        .withFactory(new FB().id(7).player(0).units(1).prod(2).disabled(0).build())
        .withFactory(new FB().id(8).player(0).units(1).prod(2).disabled(0).build())
        .build();
    
    // AI choose to attack 8 instead of 3
    // 8 is farther and in ennemy control range, so why ?
    
    // 1. check action is avaibable
    Map<Integer, List<Action>> pActions = Player.getPossibleActions(new Random());
    List<Action> actions = pActions.get(1);
    boolean found = false;
    for (Action action : actions) {
      if (action.type == ActionType.MOVE) {
        MoveAction m = (MoveAction)action;
        if (m.dst.id == 3) {
          found = true;
        }
      }
    }
    assertThat(found, is(true));
    
    AGSolution solution1 = new AGSolution("ATT 8");
    solution1.players.get(0).addAction(new MoveAction(GameState.factories[1], GameState.factories[8], 15), 0);
    Player.simulation.simulateCalculateAndRestore(solution1);
    
    AGSolution solution2 = new AGSolution("ATT 3");
    solution2.players.get(0).addAction(new MoveAction(GameState.factories[1], GameState.factories[3], 15), 0);
    Player.simulation.simulateCalculateAndRestore(solution2);
    
    AGSolutionComparator.compare(solution1, solution2);
    assertThat(solution2.energy > solution1.energy, is(true));
  }
}
