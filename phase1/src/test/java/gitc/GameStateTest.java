package gitc;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.util.Scanner;

import org.junit.Ignore;
import org.junit.Test;

import gitc.situations.FB;
import gitc.situations.GameBuilder;
import gitc.situations.LB;
import gitc.situations.TB;

public class GameStateTest {

  @Test
  @Ignore
  public void readBackupLine() throws Exception {
    Scanner in = new Scanner("String to backup");

    GameState state = new GameState();
    state.read(in);

    assertThat(state.inputBackup, is("String to backup"));
  }

  @Test
  public void futureAllRight() throws Exception {
    GameState state = new GameBuilder()
        .withFactory(new FB().id(0).mine().units(3).prod(0).build())
        .withFactory(new FB().id(1).opp().units(3).prod(0).build())
        .l(new LB().from(0).to(1).d(2).build())
        .withTroop(new TB().opp().from(1).to(0).units(1).turnsLeft(1).build())
        .withTroop(new TB().opp().from(1).to(0).units(2).turnsLeft(2).build())
        .build();

    assertThat(state.factories[0].unitsNeededAt, is(0));
  }

  @Test
  public void willLooseFactoryInFuture() throws Exception {
    GameState state = new GameBuilder()
        .withFactory(new FB().id(0).mine().units(3).prod(0).build())
        .withFactory(new FB().id(1).opp().units(3).prod(0).build())
        .l(new LB().from(0).to(1).d(2).build())
        .withTroop(new TB().opp().from(1).to(0).units(2).turnsLeft(1).build())
        .withTroop(new TB().opp().from(1).to(0).units(2).turnsLeft(2).build())
        .build();

    assertThat(state.factories[0].unitsNeededAt, is(2));
  }

  @Test
  public void needSevenUnitsToSaveFactory() throws Exception {
    GameState state = new GameBuilder()
        .withFactory(new FB().id(0).mine().units(3).prod(0).build())
        .withFactory(new FB().id(1).opp().units(3).prod(0).build())
        .l(new LB().from(0).to(1).d(2).build())
        .withTroop(new TB().opp().from(1).to(0).units(10).turnsLeft(1).build())
        .withTroop(new TB().opp().from(1).to(0).units(10).turnsLeft(2).build())
        .build();

    assertThat(state.factories[0].unitsNeededCount, is(7));
  }
  
  @Test
  public void gotSomeUnitsAtDisposal() throws Exception {
    GameState state = new GameBuilder()
        .withFactory(new FB().id(0).mine().units(25).prod(0).build())
        .withFactory(new FB().id(1).opp().units(3).prod(0).build())
        .l(new LB().from(0).to(1).d(2).build())
        .withTroop(new TB().opp().from(1).to(0).units(10).turnsLeft(1).build())
        .withTroop(new TB().opp().from(1).to(0).units(10).turnsLeft(2).build())
        .build();

    assertThat(state.factories[0].unitsDisposable, is(5));
  }
  
  
}
