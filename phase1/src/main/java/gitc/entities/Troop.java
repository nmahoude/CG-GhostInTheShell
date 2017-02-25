package gitc.entities;

import java.util.Scanner;

public class Troop extends Entity {
  int fromFactory;
  int toFactory;
  int cyborgsCount;
  int turnsToTarget;
  
  public Troop(int id) {
    super(id);
  }
  public void read(Scanner in) {

    player = in.nextInt();
    fromFactory = in.nextInt();
    toFactory = in.nextInt();
    cyborgsCount = in.nextInt();
    turnsToTarget = in.nextInt();
  }
  public String tddOutput() {
      return "updateTroop("
              +id+","
              +player+","
              +fromFactory+","
              +toFactory+","
              +cyborgsCount+","
              +turnsToTarget+");";
  }

}
