package gitc.entities;

import java.util.Scanner;

public class Troop extends Entity {
  public int fromFactoryIndex;
  public int toFactoryIndex;
  public int cyborgs;
  public int turnsToTarget;
  
  public Troop(int id) {
    super(id);
  }
  public void read(Scanner in) {
    player = in.nextInt();
    fromFactoryIndex = in.nextInt();
    toFactoryIndex = in.nextInt();
    cyborgs = in.nextInt();
    turnsToTarget = in.nextInt();
  }
  public String tddOutput() {
      return "updateTroop("
              +id+","
              +player+","
              +fromFactoryIndex+","
              +toFactoryIndex+","
              +cyborgs+","
              +turnsToTarget+");";
  }
  public void affectToLink(Factory[] factories) {
    Factory fromFactory = factories[fromFactoryIndex];
    Factory toFactory = factories[toFactoryIndex];
    
    toFactory.incommingCyborgs += cyborgs;
    
    Link link = fromFactory.getLinkToFactory(toFactoryIndex);
    link.addTroop(this);
  }

}
