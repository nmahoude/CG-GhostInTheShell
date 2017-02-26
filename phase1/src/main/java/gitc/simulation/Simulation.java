package gitc.simulation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import gitc.GameState;
import gitc.ag.AGPlayer;
import gitc.ag.AGSolution;
import gitc.ag.TurnAction;
import gitc.entities.Bomb;
import gitc.entities.Factory;
import gitc.entities.Troop;
import gitc.simulation.actions.BombAction;
import gitc.simulation.actions.MoveAction;
import gitc.simulation.actions.UpgradeAction;

public class Simulation {
  private static final int COST_INCREASE_PRODUCTION = 10;
  private static final int MAX_PRODUCTION_RATE = 3;
  private GameState state;
  List<Bomb> bombs = new ArrayList<>();
  List<Troop> troops = new ArrayList<>();
  List<Bomb> newBombs = new ArrayList<>();
  List<Troop> newTroops = new ArrayList<>();

  public Simulation(GameState state) {
    this.state = state;
  }

  public void simulate(AGSolution solution) {
    troops.clear();
    troops.addAll(state.getTroops());
    
    bombs.clear();
    bombs.addAll(state.getBombs());
    
    for (int turn = 0; turn < 1; turn++) {
      simulate(solution, 0);
    }
    
    state.restoreState();
  }

  public void simulate(AGSolution solution, int turnIndex) {
    newBombs.clear();
    newTroops.clear();
    
    // ---
    // Move Existing troops and bombs
    // ---
    for (Troop troop : troops) {
      troop.move();
    }
    for (Bomb bomb : bombs) {
      bomb.move();
    }

    // ---
    // Decrease disabled countdown
    // ---
    for (Factory factory : GameState.factories) {
      if (factory.disabled > 0) {
        factory.disabled--;
      }
    }

    // ---
    // Execute orders
    // ---
    for (AGPlayer player : solution.players) {
      TurnAction tAction = player.turnActions.get(turnIndex);
      // Send bombs
      for (BombAction bombAction : tAction.bombActions) {
        int distance = bombAction.src.getDistanceTo(bombAction.dst);
        Bomb bomb = new Bomb(player.owner, bombAction.src, bombAction.dst, distance);
        if (player.remainingBombs > 0 && bomb.findWithSameRouteInList(newBombs) == null) {
          newBombs.add(bomb);
          bombs.add(bomb);
          player.remainingBombs--;
        }
      }

      // Send troops
      for (MoveAction moveAction : tAction.moveActions) {
        int unitsToMove = Math.min(moveAction.src.units, moveAction.units);
        Troop troop = new Troop(player.owner, moveAction.src, moveAction.dst, unitsToMove);

        // forbid same route bombs & units
        if (unitsToMove > 0 && troop.findWithSameRouteInList(newBombs) == null) { 
          moveAction.src.units -= unitsToMove;

          Troop other = troop.findWithSameRouteInList(newTroops);
          if (other != null) {
            other.units += unitsToMove;
          } else {
            troops.add(troop);
            newTroops.add(troop);
          }
        }
      }

      // Increase
      for (UpgradeAction incAction : tAction.upgradeActions) {
        if (incAction.src.units >= COST_INCREASE_PRODUCTION && incAction.src.productionRate < MAX_PRODUCTION_RATE) {
          incAction.src.productionRate++;
          incAction.src.units -= COST_INCREASE_PRODUCTION;
        }
      }
    }

    // ---
    // Create new units
    // ---
    for (Factory factory : GameState.factories) {
      if (!factory.isNeutral()) {
        factory.units += factory.getCurrentProductionRate();
      }
    }

    // ---
    // Solve battles
    // ---
    for (Factory factory : GameState.factories) {
      factory.unitsReadyToFight[0] = factory.unitsReadyToFight[1] = 0;
    }

    for (Iterator<Troop> it = troops.iterator(); it.hasNext();) {
      Troop troop = it.next();
      if (troop.remainingTurns <= 0) {
        troop.destination.unitsReadyToFight[troop.owner.id] += troop.units;
        it.remove();
      }
    }
    for (Factory factory : GameState.factories) {
      // Units from both players fight first
      int units = Math.min(factory.unitsReadyToFight[0], factory.unitsReadyToFight[1]);
      factory.unitsReadyToFight[0] -= units;
      factory.unitsReadyToFight[1] -= units;

      // Remaining units fight on the factory
      for (AGPlayer player : solution.players) {
        if (factory.owner == player.owner) { // Allied
          factory.units += factory.unitsReadyToFight[player.owner.id];
        } else { // Opponent
          if (factory.unitsReadyToFight[player.owner.id] > factory.units) {
            factory.owner = player.owner;
            factory.units = factory.unitsReadyToFight[player.owner.id] - factory.units;
          } else {
            factory.units -= factory.unitsReadyToFight[player.owner.id];
          }
        }
      }
    }

    // ---
    // Solve bombs
    // ---
    for (Iterator<Bomb> it = bombs.iterator(); it.hasNext();) {
      Bomb bomb = it.next();
      if (bomb.remainingTurns <= 0) {
        bomb.explode();
        it.remove();
      }
    }

    // ---
    // Update score
    // ---
    for (AGPlayer player : solution.players) {
      player.score = 0;
    }
    for (Factory factory : GameState.factories) {
      if (factory.owner != null) {
        solution.players.get(factory.owner.id).score += factory.units;
      }
    }
    for (Troop troop : troops) {
      if (troop.owner != null) {
        solution.players.get(troop.owner.id).score += troop.units;
      }
    }

    // ---
    // Check end conditions
    // ---
    for (AGPlayer player : solution.players) {
      if (player.score == 0) {
        int production = 0;
        for (Factory factory : GameState.factories) {
          if (factory.owner == player.owner) {
            production += factory.productionRate;
          }
        }
        if (production == 0) {
          solution.energy = -1_000_000; // dead
        }
      }
    }
  }
}
