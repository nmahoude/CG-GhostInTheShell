package gitc.entities;

import java.util.Scanner;

public class Factory extends Entity {
  int cyborgs;
  int production; // 0->3
  
  public Factory(int id) {
    super(id);
  }
  
  public void read(Scanner in) {
    player = in.nextInt();
    cyborgs = in.nextInt();
    production = in.nextInt();
    int unused1 = in.nextInt();
    int unused2 = in.nextInt();
  }

  public String tddOutput() {
    return "updateFactory("+id+","
                  +player+","
                  +cyborgs+","
                  +production+");";
  }

}
