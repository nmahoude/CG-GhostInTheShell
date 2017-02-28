package gitc.entities;

import java.util.List;
import java.util.Scanner;

import gitc.GameState;

public class Factory extends Entity {
  public int[] distances; 
  public int[] unitsReadyToFight  = { 0, 0 };

  public int units;
  public int productionRate; // 0->3
  public int disabled = 0;
  public int[] unitsInTransit = { 0, 0 };
  public double influence;
  
  public int b_units;
  public int b_productionRate; // 0->3
  public int b_disabled = 0;
  public int[] b_unitsInTransit = { 0, 0 };
  public double b_influence;

// backup
  public void backup() {
    super.backup();
    b_units = units;
    b_productionRate = productionRate;
    b_disabled = disabled;
    b_unitsInTransit[0] = unitsInTransit[0];
    b_unitsInTransit[1] = unitsInTransit[1];
    b_influence = influence;
  }
  public void restore() {
    super.restore();
    units = b_units;
    productionRate = b_productionRate;
    disabled = b_disabled;
    unitsInTransit[0] = b_unitsInTransit[0];
    unitsInTransit[1] = b_unitsInTransit[1];
    influence = b_influence;
  }
  
  public Factory(int id, int factoriesCount) {
    super(id, null);
    distances = new int[factoriesCount];
    distances[id] = 0; // own distance
  }
  
  public void setupDistance(Factory toFactory, int distance) {
    distances[toFactory.id] = distance;
  }
  
  public double calculateInfluence(List<Troop> troops) {
    // current units
    double totalUnits = units;
    double currentUnitsInfluence = units * (this.owner == GameState.me ? 1.0 : -1.0);
    
    // incomming troops
    for (Troop troop : troops) {
      if (troop.destination == this) {
        double local = troop.units / troop.remainingTurns;
        currentUnitsInfluence += (troop.owner == GameState.me ? 1.0 : -1.0) * local;
        totalUnits += Math.abs(local);
      }
    }
    
    // neighbors factory
    for (Factory factory : GameState.factories) {
      if (factory != this && !factory.isNeutral()) {
        double local = 0.25 * factory.units / factory.getDistanceTo(this);
        currentUnitsInfluence += (factory.owner == GameState.me ? 1.0 : -1.0) * local;
        totalUnits += Math.abs(local);
      }
    }
    
    this.influence = currentUnitsInfluence / totalUnits;
    return influence;
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
    unitsInTransit[0] = unitsInTransit[1] = 0;
  }

  public String tddOutput() {
    return "updateFactory("+id+","
                  //+owner != null ? ""+owner.id : "None" +","
                  +units+","
                  +productionRate+");";
  }

  public void addTroop(Troop troop) {
    unitsInTransit[troop.owner.id] += troop.units;
  }

  public int getDistanceTo(Factory toFactory) {
    return distances[toFactory.id];
  }

  public int getCurrentProductionRate() {
    return (disabled == 0) ? this.productionRate : 0;
  }
  
  public int neededUnit() {
    int neededUnits = unitsInTransit[GameState.opp.id]-this.units;
    return neededUnits <= 0 ? 0 : neededUnits;
  }
  
  public boolean isUnderAttackBy(Owner attacker) {
    if (owner == attacker) {
      return false;
    }
    if (unitsInTransit[attacker.id] > 0) {
      return true;
    }
    return false;
  }
  public Factory getClosestEnemyFactory() {
    Factory closest = null;
    int minDistance = 1_000;
    
    for (Factory factory : GameState.factories) {
      if (factory.isMe()) continue;
      
      if (closest == null) {
        closest = factory;
      } else {
        int distance = closest.getDistanceTo(this);
        if (distance < minDistance) {
          minDistance = distance;
          closest = factory;
        }
      }
    }
    return closest;
  }
}
