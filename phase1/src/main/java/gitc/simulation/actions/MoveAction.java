package gitc.simulation.actions;

import gitc.entities.Factory;

public class MoveAction {
  public final Factory src;
  public final Factory dst;
  public int units;
  
  public MoveAction(Factory src, Factory dst, int units) {
    super();
    this.src = src;
    this.dst = dst;
    this.units = units;
  }

  public String output() {
    return "MOVE "+src.id+" "+dst.id+" "+units;
  }
}
