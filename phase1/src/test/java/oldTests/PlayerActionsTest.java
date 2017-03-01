package oldTests;

import java.util.Random;

import org.junit.Test;

import gitc.GameState;
import gitc.Player;
import gitc.situations.BB;
import gitc.situations.FB;
import gitc.situations.GameBuilder;
import gitc.situations.LB;
import gitc.situations.TB;

public class PlayerActionsTest {

  @Test
  public void player_should_not_send_2_bombs_on_same_factory() throws Exception {
    GameState state = new GameBuilder().l(new LB().from(0).to(1).d(2).build()).l(new LB().from(0).to(2).d(2).build()).l(new LB().from(0).to(3).d(4).build()).l(new LB().from(0).to(4).d(4).build()).l(new LB().from(0).to(5).d(6).build()).l(new LB().from(0).to(6).d(6).build()).l(new LB().from(0).to(7).d(2).build()).l(new LB().from(0).to(8).d(2).build()).l(new LB().from(0).to(9).d(1).build())
        .l(new LB().from(0).to(10).d(1).build()).l(new LB().from(0).to(11).d(8).build()).l(new LB().from(0).to(12).d(8).build()).l(new LB().from(1).to(2).d(6).build()).l(new LB().from(1).to(3).d(4).build()).l(new LB().from(1).to(4).d(7).build()).l(new LB().from(1).to(5).d(3).build()).l(new LB().from(1).to(6).d(10).build()).l(new LB().from(1).to(7).d(3).build()).l(new LB().from(1).to(8).d(4).build())
        .l(new LB().from(1).to(9).d(1).build()).l(new LB().from(1).to(10).d(5).build()).l(new LB().from(1).to(11).d(6).build()).l(new LB().from(1).to(12).d(10).build()).l(new LB().from(2).to(3).d(7).build()).l(new LB().from(2).to(4).d(4).build()).l(new LB().from(2).to(5).d(10).build()).l(new LB().from(2).to(6).d(3).build()).l(new LB().from(2).to(7).d(4).build()).l(new LB().from(2).to(8).d(3).build())
        .l(new LB().from(2).to(9).d(5).build()).l(new LB().from(2).to(10).d(1).build()).l(new LB().from(2).to(11).d(10).build()).l(new LB().from(2).to(12).d(6).build()).l(new LB().from(3).to(4).d(10).build()).l(new LB().from(3).to(5).d(2).build()).l(new LB().from(3).to(6).d(12).build()).l(new LB().from(3).to(7).d(2).build()).l(new LB().from(3).to(8).d(8).build()).l(new LB().from(3).to(9).d(2).build())
        .l(new LB().from(3).to(10).d(7).build()).l(new LB().from(3).to(11).d(2).build()).l(new LB().from(3).to(12).d(14).build()).l(new LB().from(4).to(5).d(12).build()).l(new LB().from(4).to(6).d(2).build()).l(new LB().from(4).to(7).d(8).build()).l(new LB().from(4).to(8).d(2).build()).l(new LB().from(4).to(9).d(7).build()).l(new LB().from(4).to(10).d(2).build()).l(new LB().from(4).to(11).d(14).build())
        .l(new LB().from(4).to(12).d(2).build()).l(new LB().from(5).to(6).d(14).build()).l(new LB().from(5).to(7).d(5).build()).l(new LB().from(5).to(8).d(9).build()).l(new LB().from(5).to(9).d(4).build()).l(new LB().from(5).to(10).d(9).build()).l(new LB().from(5).to(11).d(2).build()).l(new LB().from(5).to(12).d(15).build()).l(new LB().from(6).to(7).d(9).build()).l(new LB().from(6).to(8).d(5).build())
        .l(new LB().from(6).to(9).d(9).build()).l(new LB().from(6).to(10).d(4).build()).l(new LB().from(6).to(11).d(15).build()).l(new LB().from(6).to(12).d(2).build()).l(new LB().from(7).to(8).d(5).build()).l(new LB().from(7).to(9).d(1).build()).l(new LB().from(7).to(10).d(4).build()).l(new LB().from(7).to(11).d(5).build()).l(new LB().from(7).to(12).d(11).build()).l(new LB().from(8).to(9).d(4).build())
        .l(new LB().from(8).to(10).d(1).build()).l(new LB().from(8).to(11).d(11).build()).l(new LB().from(8).to(12).d(5).build()).l(new LB().from(9).to(10).d(4).build()).l(new LB().from(9).to(11).d(5).build()).l(new LB().from(9).to(12).d(10).build()).l(new LB().from(10).to(11).d(10).build()).l(new LB().from(10).to(12).d(5).build()).l(new LB().from(11).to(12).d(17).build())
        .withFactory(new FB().id(0).player(0).units(0).prod(0).disabled(0).build())
        .withFactory(new FB().id(1).player(1).units(3).prod(3).disabled(0).build())
        .withFactory(new FB().id(2).player(-1).units(20).prod(3).disabled(0).build())
        .withFactory(new FB().id(3).player(0).units(0).prod(0).disabled(0).build())
        .withFactory(new FB().id(4).player(0).units(0).prod(0).disabled(0).build())
        .withFactory(new FB().id(5).player(0).units(1).prod(3).disabled(0).build())
        .withFactory(new FB().id(6).player(0).units(1).prod(3).disabled(0).build())
        .withFactory(new FB().id(7).player(0).units(5).prod(3).disabled(0).build())
        .withFactory(new FB().id(8).player(0).units(5).prod(3).disabled(0).build())
        .withFactory(new FB().id(9).player(0).units(0).prod(0).disabled(0).build())
        .withFactory(new FB().id(10).player(0).units(0).prod(0).disabled(0).build())
        .withFactory(new FB().id(11).player(0).units(0).prod(0).disabled(0).build())
        .withFactory(new FB().id(12).player(0).units(0).prod(0).disabled(0).build())
        .withTroop(new TB().id(0).player(1).from(1).to(8).units(27).turnsLeft(4))
        .withTroop(new TB().id(1).player(1).from(1).to(5).units(2).turnsLeft(3))
        .withTroop(new TB().id(2).player(-1).from(2).to(6).units(2).turnsLeft(3))
        .withTroop(new TB().id(3).player(-1).from(2).to(8).units(6).turnsLeft(3))
        .withTroop(new TB().id(4).player(-1).from(2).to(4).units(1).turnsLeft(4))
        .withTroop(new TB().id(5).player(-1).from(2).to(10).units(1).turnsLeft(1))
        .withTroop(new TB().id(6).player(-1).from(2).to(12).units(1).turnsLeft(6))
        .withTroop(new TB().id(7).player(-1).from(2).to(0).units(1).turnsLeft(2))
        .withBomb(new BB().id(0).player(0).from(1).to(2).turnsLeft(5))
        .build();
    
      Player.getPossibleActions(new Random());
    }
}
