package gitc.entities;

import java.util.Scanner;

public class Factory extends Entity {
  public int cyborgs;
  public int production; // 0->3
  public Link[] links;
  public boolean willBeBombed;
  public int incommingCyborgs;
  
  public Factory(int id, int linkCount) {
    super(id);
    links = new Link[linkCount];
  }
  
  public void read(Scanner in) {
    clear();
    
    player = in.nextInt();
    cyborgs = in.nextInt();
    production = in.nextInt();
    int unused1 = in.nextInt();
    int unused2 = in.nextInt();
  }

  private void clear() {
    willBeBombed = false;
    incommingCyborgs = 0;
  }

  public String tddOutput() {
    return "updateFactory("+id+","
                  +player+","
                  +cyborgs+","
                  +production+");";
  }

  public Link getLinkToFactory(int toFactory) {
    return links[toFactory];
  }

  public boolean isOpponent() {
    return player == -1;
  }
  public boolean isMe() {
    return player == 1;
  }
  public boolean isNeutral() {
    return player == 0;
  }

}
