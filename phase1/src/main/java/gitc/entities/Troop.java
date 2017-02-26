package gitc.entities;

import java.util.Scanner;

public class Troop extends Entity {
  public int fromFactoryIndex;
  public int toFactoryIndex;
  public int units;
  public int turnsToTarget;
  
  public Troop(int id) {
    super(id);
  }
  public void read(Scanner in) {
    player = in.nextInt();
    fromFactoryIndex = in.nextInt();
    toFactoryIndex = in.nextInt();
    units = in.nextInt();
    turnsToTarget = in.nextInt();
  }
  public String tddOutput() {
      return "updateTroop("
              +id+","
              +player+","
              +fromFactoryIndex+","
              +toFactoryIndex+","
              +units+","
              +turnsToTarget+");";
  }
  public void affectToFactory(Factory[] factories) {
    factories[toFactoryIndex].addTroop(this);
  }
}
