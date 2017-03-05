package oldTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Ignore;
import org.junit.Test;

import gitc.GameState;
import gitc.Player;
import gitc.ag.AGPool;
import gitc.ag.AGSolution;
import gitc.ag.AGSolutionComparator;
import gitc.simulation.actions.Action;
import gitc.situations.FB;
import gitc.situations.GameBuilder;
import gitc.situations.LB;
import gitc.situations.TB;

public class DontDoWhatIExpectTest {

  @Test
  @Ignore
  public void readBaseIsNotSendingTroops() throws Exception {
    GameState state = new GameBuilder().l(new LB().f(0).t(1).d(3).b()).l(new LB().f(0).t(2).d(3).b()).l(new LB().f(0).t(3).d(4).b()).l(new LB().f(0).t(4).d(4).b()).l(new LB().f(0).t(5).d(5).b()).l(new LB().f(0).t(6).d(5).b()).l(new LB().f(0).t(7).d(1).b()).l(new LB().f(0).t(8).d(1).b()).l(new LB().f(1).t(2).d(7).b())
        .l(new LB().f(1).t(3).d(2).b()).l(new LB().f(1).t(4).d(7).b()).l(new LB().f(1).t(5).d(1).b()).l(new LB().f(1).t(6).d(9).b()).l(new LB().f(1).t(7).d(4).b()).l(new LB().f(1).t(8).d(4).b()).l(new LB().f(2).t(3).d(7).b()).l(new LB().f(2).t(4).d(2).b()).l(new LB().f(2).t(5).d(9).b()).l(new LB().f(2).t(6).d(1).b())
        .l(new LB().f(2).t(7).d(4).b()).l(new LB().f(2).t(8).d(4).b()).l(new LB().f(3).t(4).d(9).b()).l(new LB().f(3).t(5).d(2).b()).l(new LB().f(3).t(6).d(10).b()).l(new LB().f(3).t(7).d(2).b()).l(new LB().f(3).t(8).d(6).b()).l(new LB().f(4).t(5).d(10).b()).l(new LB().f(4).t(6).d(2).b()).l(new LB().f(4).t(7).d(6).b())

        .f(new FB().id(0).player(-1).units(3).prod(0).disabled(0).build())
        .f(new FB().id(1).player(1).units(9).prod(2).disabled(0).build())
        .f(new FB().id(2).player(-1).units(2).prod(2).disabled(0).build())
        .f(new FB().id(3).player(1).units(1).prod(1).disabled(0).build())
        .f(new FB().id(4).player(-1).units(9).prod(1).disabled(0).build())
        .f(new FB().id(5).player(1).units(4).prod(2).disabled(0).build())
        .f(new FB().id(6).player(-1).units(2).prod(2).disabled(0).build())
        .f(new FB().id(7).player(-1).units(2).prod(1).disabled(0).build())
        .f(new FB().id(8).player(-1).units(2).prod(1).disabled(0).build())
        .t(new TB().id(0).player(1).from(1).to(4).units(11).turnsLeft(2))
        .t(new TB().id(1).player(-1).from(2).to(1).units(1).turnsLeft(3))
        .t(new TB().id(2).player(1).from(1).to(8).units(2).turnsLeft(1))
        .t(new TB().id(3).player(1).from(3).to(8).units(1).turnsLeft(3))
        .t(new TB().id(4).player(-1).from(6).to(5).units(1).turnsLeft(9))
        .t(new TB().id(5).player(1).from(5).to(3).units(2).turnsLeft(1))
        .t(new TB().id(6).player(1).from(1).to(0).units(5).turnsLeft(2))
        .t(new TB().id(7).player(-1).from(2).to(4).units(3).turnsLeft(1))
        .t(new TB().id(8).player(-1).from(6).to(4).units(2).turnsLeft(1))
        .t(new TB().id(9).player(-1).from(7).to(3).units(1).turnsLeft(1))
        .t(new TB().id(10).player(1).from(3).to(7).units(2).turnsLeft(2))
        .t(new TB().id(11).player(-1).from(2).to(4).units(2).turnsLeft(2))
        .t(new TB().id(12).player(-1).from(6).to(4).units(2).turnsLeft(2))
        .t(new TB().id(13).player(-1).from(8).to(4).units(3).turnsLeft(2))
        .t(new TB().id(14).player(-1).from(8).to(1).units(1).turnsLeft(4))
  .build();
  
  
  List<Action> actions = new ArrayList<>();
  AGPool.getPossibleActionsForFactory(GameState.factories[2], actions);
  assertThat(actions.size() > 0, is(true));
  
  AGSolution solution1 = new AGSolution("WAIT");
  Player.simulation.simulate(solution1);
  
  List<AGSolution> solutions = new ArrayList<>();
  solutions.add(solution1);
  
  for (Action action : actions) {
    AGSolution solution = new AGSolution();
    solution.players.get(0).addAction(action, 0);
    Player.simulation.simulate(solution);
    solutions.add(solution);
  }
//  AGSolution solution2 = new AGSolution("MOVE");
//  solution2.players.get(0).addAction(new MoveAction(GameState.factories[13], GameState.factories[1], 30), 0);
//  Player.simulation.simulate(solution2);
  
  AGSolutionComparator.compare(solutions);
  //assertThat(solution2.energy > solution1.energy, is(true));
  }
  
  
  @Test
  public void noActionFromBackToFront() throws Exception {
    GameState state = new GameBuilder().l(new LB().f(0).t(1).d(3).b()).l(new LB().f(0).t(2).d(3).b()).l(new LB().f(0).t(3).d(4).b()).l(new LB().f(0).t(4).d(4).b()).l(new LB().f(0).t(5).d(5).b()).l(new LB().f(0).t(6).d(5).b()).l(new LB().f(0).t(7).d(1).b()).l(new LB().f(0).t(8).d(1).b()).l(new LB().f(1).t(2).d(7).b())
        .l(new LB().f(1).t(3).d(2).b()).l(new LB().f(1).t(4).d(7).b()).l(new LB().f(1).t(5).d(1).b()).l(new LB().f(1).t(6).d(9).b()).l(new LB().f(1).t(7).d(4).b()).l(new LB().f(1).t(8).d(4).b()).l(new LB().f(2).t(3).d(7).b()).l(new LB().f(2).t(4).d(2).b()).l(new LB().f(2).t(5).d(9).b()).l(new LB().f(2).t(6).d(1).b())
        .l(new LB().f(2).t(7).d(4).b()).l(new LB().f(2).t(8).d(4).b()).l(new LB().f(3).t(4).d(9).b()).l(new LB().f(3).t(5).d(2).b()).l(new LB().f(3).t(6).d(10).b()).l(new LB().f(3).t(7).d(2).b()).l(new LB().f(3).t(8).d(6).b()).l(new LB().f(4).t(5).d(10).b()).l(new LB().f(4).t(6).d(2).b()).l(new LB().f(4).t(7).d(6).b())
        .l(new LB().f(4).t(8).d(2).b()).l(new LB().f(5).t(6).d(12).b()).l(new LB().f(5).t(7).d(5).b()).l(new LB().f(5).t(8).d(6).b()).l(new LB().f(6).t(7).d(6).b()).l(new LB().f(6).t(8).d(5).b()).l(new LB().f(7).t(8).d(5).b())

        .f(new FB().id(0).player(-1).units(0).prod(0).disabled(0).build())
        .f(new FB().id(1).player(1).units(6).prod(2).disabled(0).build())
        .f(new FB().id(2).player(-1).units(2).prod(2).disabled(0).build())
        .f(new FB().id(3).player(1).units(2).prod(1).disabled(0).build())
        .f(new FB().id(4).player(-1).units(3).prod(1).disabled(0).build())
        .f(new FB().id(5).player(1).units(2).prod(2).disabled(0).build())
        .f(new FB().id(6).player(-1).units(2).prod(2).disabled(0).build())
        .f(new FB().id(7).player(-1).units(1).prod(1).disabled(0).build())
        .f(new FB().id(8).player(-1).units(4).prod(1).disabled(0).build())
        .t(new TB().id(0).player(1).from(1).to(4).units(11).turnsLeft(3))
        .t(new TB().id(1).player(1).from(1).to(7).units(2).turnsLeft(1))
        .t(new TB().id(2).player(-1).from(2).to(1).units(1).turnsLeft(4))
        .t(new TB().id(3).player(-1).from(2).to(7).units(1).turnsLeft(1))
        .t(new TB().id(4).player(1).from(1).to(8).units(2).turnsLeft(2))
        .t(new TB().id(5).player(1).from(3).to(8).units(1).turnsLeft(4))
        .t(new TB().id(6).player(-1).from(6).to(5).units(1).turnsLeft(10))
        .t(new TB().id(7).player(-1).from(2).to(0).units(2).turnsLeft(1))
        .t(new TB().id(8).player(1).from(3).to(1).units(1).turnsLeft(1))
        .t(new TB().id(9).player(-1).from(6).to(4).units(2).turnsLeft(1))
        .t(new TB().id(10).player(-1).from(2).to(4).units(3).turnsLeft(1))
        .t(new TB().id(11).player(1).from(5).to(3).units(2).turnsLeft(2))
        .t(new TB().id(12).player(1).from(1).to(0).units(5).turnsLeft(3))
        .t(new TB().id(13).player(-1).from(0).to(7).units(1).turnsLeft(1))
        .t(new TB().id(14).player(-1).from(0).to(8).units(1).turnsLeft(1))
        .t(new TB().id(15).player(-1).from(2).to(4).units(3).turnsLeft(2))
        .t(new TB().id(16).player(-1).from(6).to(4).units(2).turnsLeft(2))
        .t(new TB().id(17).player(-1).from(7).to(0).units(1).turnsLeft(1))
        .t(new TB().id(18).player(-1).from(7).to(3).units(1).turnsLeft(2))
  .build();
  
    GameState.me.bombsLeft = 0;
    Random random = new Random(System.nanoTime());
    
    AGPool AGPool = new AGPool();
    AGPool.reset();

    List<Action> actions = new ArrayList<>();
    AGPool.getPossibleActionsForFactory(GameState.factories[1], actions);
    assertThat(actions.size() > 0, is(true));
    for (Action action : actions) {
      System.out.println(action);
    }
    
    for (int i=0;i<4000;i++) {
      AGSolution solution;
      if (random.nextInt(1) == 0) { /* TODO try to find a good value 100 -> 75 GOLD le jeudi soir*/
        solution = AGPool.createRandom();
      } else {
        solution = AGPool.cross();
      }
      Player.simulation.simulate(solution);
      AGPool.propose(solution);
    }
    
    AGSolution best = AGPool.getBest();
    System.err.println(best.message);
    System.err.println(best.output());
  }
}
