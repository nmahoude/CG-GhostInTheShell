package gitc.simulation.actions;

import gitc.entities.Factory;

public class BombAction {

  public final Factory src;
  public final Factory dst;
  
  public BombAction(Factory src, Factory dst) {
    super();
    this.src = src;
    this.dst = dst;
  }

}
