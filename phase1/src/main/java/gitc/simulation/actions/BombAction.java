package gitc.simulation.actions;

import gitc.entities.Factory;

public class BombAction extends Action {

  public final Factory src;
  public final Factory dst;

  public BombAction(Factory src, Factory dst) {
    super(ActionType.BOMB);
    this.src = src;
    this.dst = dst;
  }

  @Override
  public String output() {
    return "BOMB "+src.id+" "+dst.id;
  }
}
