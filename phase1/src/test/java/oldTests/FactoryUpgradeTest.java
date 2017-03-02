package oldTests;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Scanner;

import org.junit.Test;

import gitc.GameState;
import gitc.Player;
import gitc.ag.AGSolution;
import gitc.ag.AGSolutionComparator;
import gitc.simulation.actions.MoveAction;
import gitc.situations.FB;
import gitc.situations.GameBuilder;
import gitc.situations.LB;
import gitc.situations.TB;

public class FactoryUpgradeTest {

  @Test
  public void whateDoFactory5() throws Exception {
    GameState state = new GameBuilder()
        .l(new LB().from(0).to(1).d(3).build())
        .l(new LB().from(0).to(2).d(3).build())
        .l(new LB().from(0).to(3).d(6).build())
        .l(new LB().from(0).to(4).d(6).build())
        .l(new LB().from(0).to(5).d(5).build())
        .l(new LB().from(0).to(6).d(5).build())
        .l(new LB().from(1).to(2).d(9).build())
        .l(new LB().from(1).to(3).d(1).build())
        .l(new LB().from(1).to(4).d(11).build())
        .l(new LB().from(1).to(5).d(1).build())
        .l(new LB().from(1).to(6).d(10).build())
        .l(new LB().from(2).to(3).d(11).build())
        .l(new LB().from(2).to(4).d(1).build())
        .l(new LB().from(2).to(5).d(10).build())
        .l(new LB().from(2).to(6).d(1).build())
        .l(new LB().from(3).to(4).d(14).build())
        .l(new LB().from(3).to(5).d(3).build())
        .l(new LB().from(3).to(6).d(12).build())
        .l(new LB().from(4).to(5).d(12).build())
        .l(new LB().from(4).to(6).d(3).build())
        .l(new LB().from(5).to(6).d(12).build())
    .withFactory(new FB().id(0).player(1).units(0).prod(0).disabled(0).build())
    .withFactory(new FB().id(1).player(-1).units(21).prod(1).disabled(0).build())
    .withFactory(new FB().id(2).player(-1).units(50).prod(3).disabled(0).build())
    .withFactory(new FB().id(3).player(-1).units(31).prod(3).disabled(0).build())
    .withFactory(new FB().id(4).player(-1).units(51).prod(3).disabled(0).build())
    .withFactory(new FB().id(5).player(1).units(34).prod(3).disabled(0).build())
    .withFactory(new FB().id(6).player(-1).units(42).prod(3).disabled(0).build())
    .withTroop(new TB().id(0).player(-1).from(4).to(1).units(12).turnsLeft(1))
    .withTroop(new TB().id(1).player(1).from(1).to(6).units(1).turnsLeft(3))
    .withTroop(new TB().id(2).player(1).from(1).to(6).units(1).turnsLeft(4))
    .withTroop(new TB().id(3).player(1).from(1).to(6).units(1).turnsLeft(5))
    .withTroop(new TB().id(4).player(1).from(1).to(6).units(1).turnsLeft(6))
    .withTroop(new TB().id(5).player(1).from(1).to(6).units(1).turnsLeft(7))
    .withTroop(new TB().id(6).player(1).from(1).to(6).units(1).turnsLeft(8))
    .withTroop(new TB().id(7).player(1).from(1).to(6).units(1).turnsLeft(9))
    .build();
    
    AGSolution solution1 = new AGSolution("WAIT");
    Player.simulation.simulateCalculateAndRestore(solution1);
    
    AGSolution solution2 = new AGSolution("MOVE");
    solution2.players.get(0).addAction(new MoveAction(GameState.factories[5], GameState.factories[0], 15), 0);
    Player.simulation.simulateCalculateAndRestore(solution2);
    
    AGSolutionComparator.compare(solution1, solution2);
    assertThat(solution2.energy > solution1.energy, is(true));
  }
}
