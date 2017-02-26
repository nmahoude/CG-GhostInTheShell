package gitc.entities;

import java.util.ArrayList;
import java.util.List;

public class Link {
  public boolean usedInThisTurn;
  public Factory fromFactory;
  public Factory toFactory;
  public int distance;
  public int cyborgsInTransit;
  
  List<Troop> troops = new ArrayList<>(20);
  
  public Link(Factory fromFactory, Factory toFactory, int distance) {
    this.fromFactory = fromFactory;
    this.toFactory = toFactory;
    this.distance = distance;

    fromFactory.links[toFactory.id] = this;
  }
  
  public void clear() {
    usedInThisTurn = false;
    cyborgsInTransit = 0;
    troops.clear();
  }

  public void addTroop(Troop troop) {
    troops.add(troop);
    cyborgsInTransit+=troop.cyborgs;
  }
}
