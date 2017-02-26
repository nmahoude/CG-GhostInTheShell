package gitc.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Factory extends Entity {
  public int[] distances; 
  public List<Troop> troops = new ArrayList<>();
  public int[] unitsReadyToFight  = { 0, 0 };

  public int units;
  public int productionRate; // 0->3
  public boolean willBeBombed;
  public int disabled = 0;
  public int[] unitsInTransit = { 0, 0 };

  public int b_units;
  public int b_productionRate; // 0->3
  public boolean b_willBeBombed;
  public int b_disabled = 0;
  public int[] b_unitsInTransit = { 0, 0 };

// backup
  public void backup() {
    super.backup();
    b_units = units;
    b_productionRate = productionRate;
    b_willBeBombed = willBeBombed;
    b_disabled = disabled;
    b_unitsInTransit[0] = unitsInTransit[0];
    b_unitsInTransit[1] = unitsInTransit[1];
  }
  public void restore() {
    super.restore();
    units = b_units;
    productionRate = b_productionRate;
    willBeBombed = b_willBeBombed;
    disabled = b_disabled;
    unitsInTransit[0] = b_unitsInTransit[0];
    unitsInTransit[1] = b_unitsInTransit[1];
  }
  
  public Factory(int id, int factoriesCount) {
    super(id, null);
    distances = new int[factoriesCount];
    distances[id] = 0; // own distance
  }
  
  public void setupDistance(Factory toFactory, int distance) {
    distances[toFactory.id] = distance;
  }
  
  @SuppressWarnings("unused")
  public void read(Scanner in) {
    readPlayer(in.nextInt());
    units = in.nextInt();
    productionRate = in.nextInt();
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
                  +owner.id+","
                  +units+","
                  +productionRate+");";
  }

  public void addTroop(Troop troop) {
    troops.add(troop);
    unitsInTransit[troop.owner.id] += troop.units;
  }

  public int getDistanceTo(Factory toFactory) {
    return distances[toFactory.id];
  }

  public int getCurrentProductionRate() {
    return productionRate;
  }

}
