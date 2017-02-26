package gitc.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Factory extends Entity {
  public int units;
  public int production; // 0->3
  public int[] distances; 
  public boolean willBeBombed;
  public int disabled = 0;
  public List<Troop> troops = new ArrayList<>();
  public int[] unitsInTransit = { 0, 0 };
  
  public Factory(int id, int factoriesCount) {
    super(id);
    distances = new int[factoriesCount];
    distances[id] = 0; // own distance
  }
  
  public void setupDistance(Factory toFactory, int distance) {
    distances[toFactory.id] = distance;
  }
  
  public void read(Scanner in) {
    player = in.nextInt();
    units = in.nextInt();
    production = in.nextInt();
    int unused1 = in.nextInt();
    int unused2 = in.nextInt();
  }

  public void clear() {
    willBeBombed = false;
    troops.clear();
    unitsInTransit[0] = unitsInTransit[1] = 0;
  }

  public String tddOutput() {
    return "updateFactory("+id+","
                  +player+","
                  +units+","
                  +production+");";
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

  public void addTroop(Troop troop) {
    troops.add(troop);
    unitsInTransit[troop.player == 1 ? 0 : 1] += troop.units;
  }

  public int getDistanceTo(Factory toFactory) {
    return distances[toFactory.id];
  }

}
