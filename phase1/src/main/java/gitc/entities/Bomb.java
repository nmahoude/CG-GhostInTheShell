package gitc.entities;

import java.util.Scanner;

public class Bomb extends Entity {

  public int fromFactory;
  public int toFactory; // can be -1 !
  private int turnsToTarget; // can be -1

  public Bomb(int id) {
    super(id);
  }
  
  public void read(Scanner in) {
    player = in.nextInt();
    fromFactory = in.nextInt();
    toFactory = in.nextInt();
    turnsToTarget = in.nextInt();
    int unused = in.nextInt();
    
  }
}
