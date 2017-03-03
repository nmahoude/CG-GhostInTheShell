package oldTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

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
  public void readBaseIsNotSendingTroops() throws Exception {GameState state = new GameBuilder().l(new LB().f(0).t(1).d(1).b()).l(new LB().f(0).t(2).d(1).b()).l(new LB().f(0).t(3).d(4).b()).l(new LB().f(0).t(4).d(4).b()).l(new LB().f(0).t(5).d(5).b()).l(new LB().f(0).t(6).d(5).b()).l(new LB().f(0).t(7).d(2).b()).l(new LB().f(0).t(8).d(2).b()).l(new LB().f(0).t(9).d(7).b())
  .l(new LB().f(0).t(10).d(7).b()).l(new LB().f(0).t(11).d(4).b()).l(new LB().f(0).t(12).d(4).b()).l(new LB().f(0).t(13).d(7).b()).l(new LB().f(0).t(14).d(7).b()).l(new LB().f(1).t(2).d(4).b()).l(new LB().f(1).t(3).d(1).b()).l(new LB().f(1).t(4).d(7).b()).l(new LB().f(1).t(5).d(3).b()).l(new LB().f(1).t(6).d(8).b())
  .l(new LB().f(1).t(7).d(1).b()).l(new LB().f(1).t(8).d(4).b()).l(new LB().f(1).t(9).d(5).b()).l(new LB().f(1).t(10).d(9).b()).l(new LB().f(1).t(11).d(3).b()).l(new LB().f(1).t(12).d(7).b()).l(new LB().f(1).t(13).d(4).b()).l(new LB().f(1).t(14).d(10).b()).l(new LB().f(2).t(3).d(7).b()).l(new LB().f(2).t(4).d(1).b())
  .l(new LB().f(2).t(5).d(8).b()).l(new LB().f(2).t(6).d(3).b()).l(new LB().f(2).t(7).d(4).b()).l(new LB().f(2).t(8).d(1).b()).l(new LB().f(2).t(9).d(9).b()).l(new LB().f(2).t(10).d(5).b()).l(new LB().f(2).t(11).d(7).b()).l(new LB().f(2).t(12).d(3).b()).l(new LB().f(2).t(13).d(10).b()).l(new LB().f(2).t(14).d(4).b())
  .l(new LB().f(3).t(4).d(9).b()).l(new LB().f(3).t(5).d(1).b()).l(new LB().f(3).t(6).d(10).b()).l(new LB().f(3).t(7).d(2).b()).l(new LB().f(3).t(8).d(6).b()).l(new LB().f(3).t(9).d(4).b()).l(new LB().f(3).t(10).d(11).b()).l(new LB().f(3).t(11).d(4).b()).l(new LB().f(3).t(12).d(9).b()).l(new LB().f(3).t(13).d(2).b())
  .l(new LB().f(3).t(14).d(12).b()).l(new LB().f(4).t(5).d(10).b()).l(new LB().f(4).t(6).d(1).b()).l(new LB().f(4).t(7).d(6).b()).l(new LB().f(4).t(8).d(2).b()).l(new LB().f(4).t(9).d(11).b()).l(new LB().f(4).t(10).d(4).b()).l(new LB().f(4).t(11).d(9).b()).l(new LB().f(4).t(12).d(4).b()).l(new LB().f(4).t(13).d(12).b())
  .l(new LB().f(4).t(14).d(2).b()).l(new LB().f(5).t(6).d(12).b()).l(new LB().f(5).t(7).d(2).b()).l(new LB().f(5).t(8).d(8).b()).l(new LB().f(5).t(9).d(1).b()).l(new LB().f(5).t(10).d(13).b()).l(new LB().f(5).t(11).d(2).b()).l(new LB().f(5).t(12).d(11).b()).l(new LB().f(5).t(13).d(1).b()).l(new LB().f(5).t(14).d(13).b())
  .l(new LB().f(6).t(7).d(8).b()).l(new LB().f(6).t(8).d(2).b()).l(new LB().f(6).t(9).d(13).b()).l(new LB().f(6).t(10).d(1).b()).l(new LB().f(6).t(11).d(11).b()).l(new LB().f(6).t(12).d(2).b()).l(new LB().f(6).t(13).d(13).b()).l(new LB().f(6).t(14).d(1).b()).l(new LB().f(7).t(8).d(5).b()).l(new LB().f(7).t(9).d(4).b())
  .l(new LB().f(7).t(10).d(10).b()).l(new LB().f(7).t(11).d(1).b()).l(new LB().f(7).t(12).d(8).b()).l(new LB().f(7).t(13).d(5).b()).l(new LB().f(7).t(14).d(9).b()).l(new LB().f(8).t(9).d(10).b()).l(new LB().f(8).t(10).d(4).b()).l(new LB().f(8).t(11).d(8).b()).l(new LB().f(8).t(12).d(1).b()).l(new LB().f(8).t(13).d(9).b())
  .l(new LB().f(8).t(14).d(5).b()).l(new LB().f(9).t(10).d(15).b()).l(new LB().f(9).t(11).d(1).b()).l(new LB().f(9).t(12).d(13).b()).l(new LB().f(9).t(13).d(4).b()).l(new LB().f(9).t(14).d(14).b()).l(new LB().f(10).t(11).d(13).b()).l(new LB().f(10).t(12).d(1).b()).l(new LB().f(10).t(13).d(14).b()).l(new LB().f(10).t(14).d(4).b())
  .l(new LB().f(11).t(12).d(10).b()).l(new LB().f(11).t(13).d(5).b()).l(new LB().f(11).t(14).d(12).b()).l(new LB().f(12).t(13).d(12).b()).l(new LB().f(12).t(14).d(5).b()).l(new LB().f(13).t(14).d(15).b())

  .f(new FB().id(0).player(-1).units(52).prod(3).disabled(0).build())
  .f(new FB().id(1).player(1).units(32).prod(1).disabled(0).build())
  .f(new FB().id(2).player(-1).units(9).prod(3).disabled(0).build())
  .f(new FB().id(3).player(0).units(10).prod(2).disabled(0).build())
  .f(new FB().id(4).player(-1).units(3).prod(3).disabled(0).build())
  .f(new FB().id(5).player(1).units(0).prod(0).disabled(0).build())
  .f(new FB().id(6).player(0).units(0).prod(0).disabled(0).build())
  .f(new FB().id(7).player(0).units(9).prod(2).disabled(0).build())
  .f(new FB().id(8).player(-1).units(6).prod(3).disabled(0).build())
  .f(new FB().id(9).player(1).units(5).prod(3).disabled(1).build())
  .f(new FB().id(10).player(-1).units(3).prod(3).disabled(0).build())
  .f(new FB().id(11).player(0).units(7).prod(3).disabled(0).build())
  .f(new FB().id(12).player(-1).units(6).prod(3).disabled(0).build())
  .f(new FB().id(13).player(1).units(34).prod(2).disabled(0).build())
  .f(new FB().id(14).player(-1).units(3).prod(3).disabled(0).build())
  .t(new TB().id(0).player(-1).from(10).to(0).units(3).turnsLeft(1))
  .t(new TB().id(1).player(-1).from(14).to(0).units(3).turnsLeft(1))
  .t(new TB().id(2).player(-1).from(10).to(0).units(3).turnsLeft(3))
  .t(new TB().id(3).player(-1).from(14).to(0).units(3).turnsLeft(3))
  .t(new TB().id(4).player(-1).from(4).to(0).units(3).turnsLeft(1))
  .t(new TB().id(5).player(-1).from(10).to(0).units(3).turnsLeft(4))
  .t(new TB().id(6).player(-1).from(12).to(0).units(3).turnsLeft(1))
  .t(new TB().id(7).player(-1).from(14).to(0).units(3).turnsLeft(4))
  .t(new TB().id(8).player(-1).from(4).to(0).units(3).turnsLeft(2))
  .t(new TB().id(9).player(-1).from(10).to(0).units(3).turnsLeft(5))
  .t(new TB().id(10).player(-1).from(12).to(0).units(3).turnsLeft(2))
  .t(new TB().id(11).player(-1).from(14).to(0).units(3).turnsLeft(5))
  .t(new TB().id(12).player(-1).from(8).to(0).units(3).turnsLeft(1))
  .t(new TB().id(13).player(-1).from(14).to(2).units(3).turnsLeft(3))
  .t(new TB().id(14).player(-1).from(0).to(6).units(27).turnsLeft(4))
  .t(new TB().id(15).player(-1).from(0).to(6).units(11).turnsLeft(5))
  .t(new TB().id(16).player(-1).from(2).to(6).units(1).turnsLeft(3))
  .t(new TB().id(17).player(-1).from(2).to(0).units(5).turnsLeft(1))
  .t(new TB().id(18).player(-1).from(4).to(6).units(1).turnsLeft(1))
  .t(new TB().id(19).player(-1).from(4).to(2).units(2).turnsLeft(1))
  .t(new TB().id(20).player(-1).from(8).to(0).units(3).turnsLeft(2))
  .t(new TB().id(21).player(-1).from(10).to(12).units(3).turnsLeft(1))
  .t(new TB().id(22).player(-1).from(12).to(8).units(3).turnsLeft(1))
  .t(new TB().id(23).player(-1).from(14).to(2).units(3).turnsLeft(4))
  .build();
  
  
  List<Action> actions = new ArrayList<>();
  AGPool.getPossibleActionsForFactory(GameState.factories[13], actions);
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
  public void pourquoiOnAttaquePasLa() throws Exception {
    GameState state = new GameBuilder().l(new LB().f(0).t(1).d(5).b()).l(new LB().f(0).t(2).d(5).b()).l(new LB().f(0).t(3).d(1).b()).l(new LB().f(0).t(4).d(1).b()).l(new LB().f(0).t(5).d(7).b()).l(new LB().f(0).t(6).d(7).b()).l(new LB().f(1).t(2).d(11).b()).l(new LB().f(1).t(3).d(2).b()).l(new LB().f(1).t(4).d(7).b())
        .l(new LB().f(1).t(5).d(3).b()).l(new LB().f(1).t(6).d(13).b()).l(new LB().f(2).t(3).d(7).b()).l(new LB().f(2).t(4).d(2).b()).l(new LB().f(2).t(5).d(13).b()).l(new LB().f(2).t(6).d(3).b()).l(new LB().f(3).t(4).d(4).b()).l(new LB().f(3).t(5).d(4).b()).l(new LB().f(3).t(6).d(10).b()).l(new LB().f(4).t(5).d(10).b())
        .l(new LB().f(4).t(6).d(4).b()).l(new LB().f(5).t(6).d(15).b())

        .f(new FB().id(0).player(1).units(10).prod(0).disabled(0).build())
        .f(new FB().id(1).player(1).units(7).prod(3).disabled(0).build())
        .f(new FB().id(2).player(1).units(233).prod(3).disabled(0).build())
        .f(new FB().id(3).player(1).units(14).prod(3).disabled(0).build())
        .f(new FB().id(4).player(1).units(299).prod(3).disabled(0).build())
        .f(new FB().id(5).player(1).units(6).prod(3).disabled(0).build())
        .f(new FB().id(6).player(-1).units(121).prod(3).disabled(0).build())
        .t(new TB().id(0).player(1).from(5).to(0).units(3).turnsLeft(2))
        .t(new TB().id(1).player(1).from(5).to(0).units(3).turnsLeft(3))
        .t(new TB().id(2).player(1).from(5).to(1).units(6).turnsLeft(1))
        .t(new TB().id(3).player(1).from(1).to(5).units(13).turnsLeft(1))
        .t(new TB().id(4).player(1).from(1).to(3).units(14).turnsLeft(1))
        .t(new TB().id(5).player(1).from(1).to(0).units(4).turnsLeft(4))
        .t(new TB().id(6).player(1).from(5).to(1).units(2).turnsLeft(2))
        .t(new TB().id(7).player(1).from(5).to(0).units(1).turnsLeft(6))
        .t(new TB().id(8).player(1).from(5).to(1).units(3).turnsLeft(3))
        .t(new TB().id(9).player(1).from(0).to(3).units(1).turnsLeft(1))
        .t(new TB().id(10).player(1).from(0).to(4).units(34).turnsLeft(1))



    .build();
        
    GameState.me.bombsLeft = 0;
    
    List<Action> actions = new ArrayList<>();
    AGPool.getPossibleActionsForFactory(GameState.factories[2], actions);
    
    List<AGSolution> solutions = new ArrayList<>();
    for (Action action : actions) {
      AGSolution solution = new AGSolution(action.toString());
      solution.players.get(0).addAction(action, 0);
      Player.simulation.simulate(solution);
      solutions.add(solution);
    }
    
    AGSolution nothing = new AGSolution("WAIT");
    Player.simulation.simulate(nothing);
    solutions.add(nothing);
    
    
    AGSolutionComparator.compare(solutions);
  }
  
}
