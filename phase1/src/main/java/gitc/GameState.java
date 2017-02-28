package gitc;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

import gitc.entities.Bomb;
import gitc.entities.EntityType;
import gitc.entities.Factory;
import gitc.entities.Owner;
import gitc.entities.Troop;

public class GameState {
  public static boolean TDD_OUPUT = false;
  List<String> inputSetupBackup = new ArrayList<>();
  List<String> inputBackup = new ArrayList<>();

  public static final Owner me = new Owner(0);
  public static final Owner opp = new Owner(1);
  
  
  public int factoryCount;
  public static Factory[] factories;
  public static Factory unkownFactory = new Factory(0, 1);
  public List<Troop> troops = new ArrayList<>();
  public Map<Integer, Bomb> bombs = new HashMap<>();
  
  public int cyborgs[] = new int[2];
  public int production[] = new int [2];
  public int cyborgsTotal;
  private int entityCount;
  
  public void readSetup(Scanner in) {
    factoryCount = in.nextInt();
    int linkCount = in.nextInt(); // the number of links between factories

    factories = new Factory[factoryCount];
    for (int id=0;id<factoryCount;id++) {
      factories[id] = new Factory(id, factoryCount);
    }
    
    inputSetupBackup.add("GameState state = new GameState();");
    inputSetupBackup.add("String setup = \""+factoryCount+" "+linkCount+"\\n\";");
    
    for (int i = 0; i < linkCount; i++) {
      int factory1 = in.nextInt();
      int factory2 = in.nextInt();
      int distance = in.nextInt();
      factories[factory1].setupDistance(factories[factory2], distance);
      factories[factory2].setupDistance(factories[factory1], distance);
      
      inputSetupBackup.add("setup +=\""+factory1+" "+factory2+" "+distance+"\\n\";");
    }
    setupTddOutput();
  }
  
  public void read(Scanner in) {
    inputBackup.clear();
    
    clearRound();
    
    entityCount = in.nextInt();
    int troopCount = entityCount-factoryCount;
    troops.clear();

    Map<Integer, Bomb> newBombs = new HashMap<>();

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
        } else if (entityType.equals(EntityType.BOMB.name())){
          Bomb bomb = bombs.get(entityId);
          if (bomb == null) {
            bomb = new Bomb(entityId);
            bomb.read(in);
            if (bomb.destination != unkownFactory) {
            } else {
              getBombDestinationFromKnowledge(bomb);
            }
            if (bomb.destination != unkownFactory) {
              bomb.destination.bombIncomming = true;
            }
          } else {
            // only read, we already know the bomb
            bomb.read(in);
          }
          newBombs.put(entityId, bomb);
        }
    }

    // replace bombs
    bombs = newBombs;
    
    cyborgsTotal = cyborgs[0] + cyborgs[1];
    
    if (TDD_OUPUT) {
      tddOuput();
    }
    preTurnUpdate();
    backupState();
  }

  private void preTurnUpdate() {
    updateFactoryInfluence();
    updateFactoryFront();
  }

  private void updateFactoryFront() {
    //System.err.println("Factory front : ");
    for (Factory factory : factories) {
      if (factory.isMe()) {
        factory.calculateFront();
        //System.err.println(""+factory.id+": "+(factory.isFront ? " FRONT" : "BACK"));
      }
    }
  }

  private void updateFactoryInfluence() {
    // System.err.println("Factory influences : ");
    for (Factory factory : factories) {
      factory.calculateInfluence(troops);
       // System.err.println("   "+factory.id+" = "+ factory.influence);
    }
  }

  private void getBombDestinationFromKnowledge(Bomb bomb) {
    Factory myOnlyFactory = onlyOneFactoryOwned();
    if (myOnlyFactory != null) {
      bomb.destination = myOnlyFactory;
      bomb.remainingTurns = myOnlyFactory.getDistanceTo(bomb.source);
      System.err.println("I know where the bomb will hit ! id="+myOnlyFactory.id);
    } else {
      System.err.println("I don't know where the bomb will hit");
    }
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
    System.err.println("String source=\"\";");
    System.err.println("source+=\""+entityCount+"\\n\";");
    for (String line : inputBackup) {
      System.err.println(line);
    }
    System.err.println("in = new Scanner(source);");
    System.err.println("state.read(in);");
  }

  private void setupTddOutput() {
    for (String setupLine : inputSetupBackup) {
      System.err.print(setupLine);
    }
    System.err.println("Scanner in = new Scanner(setup);");
    System.err.println("state.readSetup(in);");
  }

  /** prepare for restore */
  private void backupState() {
    for (Factory factory : factories) {
      factory.backup();
    }
    for (Troop troop : troops) {
      troop.backup();
    }
    for (Bomb bomb : bombs.values()) {
      bomb.backup();
    }
  }
  
  public void restoreState() {
    for (Factory factory : factories) {
      factory.restore();
    }
    for (Troop troop : troops) {
      troop.restore();
    }
    for (Bomb bomb : bombs.values()) {
      bomb.restore();
    }
  }
  public Factory[] getFactories() {
    return factories;
  }

  public List<Troop> getTroops() {
    return troops;
  }
  public Collection<Bomb> getBombs() {
    return bombs.values();
  }

  public int willBombHitFactory(Factory attackFactory) {
    for (Bomb bomb : bombs.values()) {
      if (bomb.destination == attackFactory) {
        return bomb.remainingTurns;
      }
    }
    return -1;
  }
}
