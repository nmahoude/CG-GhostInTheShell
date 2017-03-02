package gitc.situations;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import gitc.GameState;
import gitc.Player;
import gitc.simulation.Simulation;

public class GameBuilder {
  List<FB> factories = new ArrayList<>();
  List<LB> links = new ArrayList<>();
  List<TB> troops = new ArrayList<>();
  List<BB> bombs = new ArrayList<>();
  
  public GameBuilder withFactory(FB factory) {
    factories.add(factory);
    return this;
  }

  public GameBuilder l(LB link) {
    links.add(link);
    return this;
  }
  
  public GameBuilder withTroop(TB troop) {
    troops.add(troop);
    return this;
  }

  public GameState build() {
    GameState.TDD_OUPUT = false;
    GameState state = new GameState();
    Player.gameState = state;
    Player.simulation = new Simulation(state);
    
    /** SETUP */
    String input = ""+factories.size() +" "+links.size() +" \n";
    for (LB link : links) {
      input += ""+link.srcId+" "+link.dstId+" "+link.distance+" \n";
    }
    state.readSetup(new Scanner(input));
    
    /** TURN INPUT */
    input=""+(factories.size()+ troops.size() + bombs.size())+" \n";
    for (FB fBuilder : factories) {
      input +=""+fBuilder.id+" FACTORY "+fBuilder.player+" "+fBuilder.units+" "+fBuilder.prod+" 0 0 \n";
    }
    for (TB tBuilder : troops) {
      input +=""+tBuilder.id+" TROOP "+tBuilder.player+" "+tBuilder.srcId+" "+tBuilder.dstId+" "+tBuilder.units+" "+tBuilder.remainingTime+" \n";
    }
    for (BB bBuilder : bombs) {
      input +=""+bBuilder.id+" BOMB "+bBuilder.player+" "+bBuilder.srcId+" "+bBuilder.dstId+" "+bBuilder.remainingTurns+" "+bBuilder.remainingTurns+" \n";
    }
    state.read(new Scanner(input));
    

    return state;
  }

  public GameBuilder withBomb(BB bomb) {
    bombs.add(bomb);
    return this;
  }

}
