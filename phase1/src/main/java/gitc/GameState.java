package gitc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import gitc.entities.Bomb;
import gitc.entities.EntityType;
import gitc.entities.Factory;
import gitc.entities.Troop;

public class GameState {
  private static final boolean TDD_OUPUT = false;
  List<String> inputSetupBackup = new ArrayList<>();
  List<String> inputBackup = new ArrayList<>();

  public int factoryCount;
  public int bombs[] = new int[2];
  Factory[] factories;
  Troop[] troops;
  
  public int cyborgs[] = new int[2];
  public int production[] = new int [2];
  public int cyborgsTotal;
  
  public void readSetup(Scanner in) {
    bombs[0] = bombs[1] = 2;
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
    troops = new Troop[troopCount];
    for (int i=0;i<troopCount;i++) {
      troops[i] = new Troop(i+factoryCount);
    }
    
    for (int i = 0; i < entityCount; i++) {
        int entityId = in.nextInt();
        String entityType = in.next();
//        System.err.println("Reading entityType : "+entityType+" with Id :"+entityId);
        if (entityType.equals(EntityType.FACTORY.name())) {
          Factory factory = factories[entityId];
          factory.read(in);
          if (factory.player != 0) {
            production[getPlayerIndex(factory.player)]+=factory.production;
            cyborgs[getPlayerIndex(factory.player)]+=factory.units;
          }
          if (TDD_OUPUT) {
            inputBackup.add(factories[entityId].tddOutput());
          }
        } else if (entityType.equals(EntityType.TROOP.name())){
          int troopId = i-factoryCount;
          Troop troop = troops[troopId];
          troop.read(in);
          troop.affectToFactory(factories);
          cyborgs[getPlayerIndex(troop.player)]+=troop.units;
          
          if (TDD_OUPUT) {
            inputBackup.add(troops[troopId].tddOutput());
          }
        } else {
          Bomb bomb = new Bomb(0);
          bomb.read(in);
          if (bomb.toFactory != -1) {
            factories[bomb.toFactory].willBeBombed = true;
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

  private int getPlayerIndex(int player) {
    return player == 1 ? 0 : 1;
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
    
  }
  
  public void restoreState() {
    throw new RuntimeException("Not implemented yet");
  }
  public Factory[] getFactories() {
    return factories;
  }

  public Troop[] getTroops() {
    return troops;
  }


}
