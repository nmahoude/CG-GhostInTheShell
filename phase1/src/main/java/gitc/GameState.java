package gitc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import gitc.entities.Bomb;
import gitc.entities.EntityType;
import gitc.entities.Factory;
import gitc.entities.Owner;
import gitc.entities.Troop;

public class GameState {
  private static final boolean TDD_OUPUT = false;
  List<String> inputSetupBackup = new ArrayList<>();
  List<String> inputBackup = new ArrayList<>();

  public static final Owner me = new Owner(0);
  public static final Owner opp = new Owner(1);
  
  
  public int factoryCount;
  public static Factory[] factories;
  public static Factory unkownFactory = new Factory(0, 1);
  public List<Troop> troops = new ArrayList<>();
  public List<Bomb> bombs = new ArrayList<>();
  
  public int cyborgs[] = new int[2];
  public int production[] = new int [2];
  public int cyborgsTotal;
  
  public void readSetup(Scanner in) {
    factoryCount = in.nextInt();
    int linkCount = in.nextInt(); // the number of links between factories

    factories = new Factory[factoryCount];
    for (int id=0;id<factoryCount;id++) {
      factories[id] = new Factory(id, factoryCount);
    }
    
    if (TDD_OUPUT) {
      inputSetupBackup.add("setup("+factoryCount+","+linkCount+");");
    }
    
    for (int i = 0; i < linkCount; i++) {
        int factory1 = in.nextInt();
        int factory2 = in.nextInt();
        int distance = in.nextInt();
        factories[factory1].setupDistance(factories[factory2], distance);
        factories[factory2].setupDistance(factories[factory1], distance);
        
        if (TDD_OUPUT) {
          inputSetupBackup.add("addLinks("+factory1+","+factory2+","+distance+");");
        }
    }
  }
  
  public void read(Scanner in) {
    inputBackup.clear();
    
    clearRound();
    
    // read from game
    int entityCount = in.nextInt(); // the number of entities (e.g. factories and troops)
    int troopCount = entityCount-factoryCount;
    troops.clear();

    for (int i = 0; i < entityCount; i++) {
        int entityId = in.nextInt();
        String entityType = in.next();
//        System.err.println("Reading entityType : "+entityType+" with Id :"+entityId);
        if (entityType.equals(EntityType.FACTORY.name())) {
          Factory factory = factories[entityId];
          factory.read(in);
          if (factory.owner != null) {
            production[factory.owner.id]+=factory.productionRate;
            cyborgs[factory.owner.id]+=factory.units;
          }
          if (TDD_OUPUT) {
            inputBackup.add(factories[entityId].tddOutput());
          }
        } else if (entityType.equals(EntityType.TROOP.name())){
          int troopId = i-factoryCount;
          Troop troop = new Troop(troopId);
          troop.read(in);
          troop.affectToFactory(factories);
          cyborgs[troop.owner.id]+=troop.units;
          troops.add(troop);
          if (TDD_OUPUT) {
            inputBackup.add(troop.tddOutput());
          }
        } else {
          Bomb bomb = new Bomb(0);
          bomb.read(in);
          if (bomb.destination != null) {
            bomb.destination.willBeBombed = true;
          } else {
            Factory myOnlyFactory = onlyOneFactoryOwned();
            if (myOnlyFactory != null) {
              myOnlyFactory.willBeBombed = true;
            }
          }
        }
    }

    cyborgsTotal = cyborgs[0] + cyborgs[1];
    
    if (TDD_OUPUT) {
      tddOuput();
    }
    
    backupState();
  }

  private Factory onlyOneFactoryOwned() {
    Factory myOnlyFactory = null;
    for (Factory factory : factories) {
      if (factory.isMe()) {
        if (myOnlyFactory != null) {
          return null; // at least 2
        } else {
          myOnlyFactory = factory;
        }
      }
    }
    return myOnlyFactory;
  }

  private void clearRound() {
    cyborgsTotal = 0;
    cyborgs[0] = cyborgs[1] = 0;
    production[0] = production[1] = 0;
    for (Factory factory : factories) {
      factory.clear();
    }
  }

  private void tddOuput() {
    for (String setupLine : inputSetupBackup) {
      System.err.println(setupLine);
    }
    System.err.println("/************/");
    for (String line : inputBackup) {
      System.err.println(line);
    }
  }

  /** prepare for restore */
  private void backupState() {
    for (Factory factory : factories) {
      factory.backup();
    }
    for (Troop troop : troops) {
      troop.backup();
    }
    for (Bomb bomb : bombs) {
      bomb.backup();
    }
  }
  
  public void restoreState() {
    throw new RuntimeException("Not implemented yet");
  }
  public Factory[] getFactories() {
    return factories;
  }

  public List<Troop> getTroops() {
    return troops;
  }
  public List<Bomb> getBombs() {
    return bombs;
  }
}
