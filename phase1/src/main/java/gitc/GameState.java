package gitc;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import gitc.entities.EntityEnum;
import gitc.entities.Factory;
import gitc.entities.Troop;

public class GameState {
  List<String> inputSetupBackup = new ArrayList<>();
  List<String> inputBackup = new ArrayList<>();

  private int factoryCount;
  Factory[] factories;
  Troop[] troops;
  
  public void readSetup(Scanner in) {
    factoryCount = in.nextInt();
    factories = new Factory[factoryCount];
    for (int i=0;i<factoryCount;i++) {
      factories[i] = new Factory(i);
    }
    int linkCount = in.nextInt(); // the number of links between factories
    inputSetupBackup.add("setup("+factoryCount+","+linkCount+");");
    
    for (int i = 0; i < linkCount; i++) {
        int factory1 = in.nextInt();
        int factory2 = in.nextInt();
        int distance = in.nextInt();

        inputSetupBackup.add("addLink("+factory1+","+factory2+","+distance+");");
    }
  }
  
  public void read(Scanner in) {
    inputBackup.clear();
    
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
        if (entityType.equals(EntityEnum.FACTORY.name())) {
          factories[entityId].read(in);
          inputBackup.add(factories[entityId].tddOutput());
        } else {
          int troopId = i-factoryCount;
          troops[troopId].read(in);
          inputBackup.add(troops[troopId].tddOutput());
        }
    }

    tddOuput();
    
    backupState();
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

}
