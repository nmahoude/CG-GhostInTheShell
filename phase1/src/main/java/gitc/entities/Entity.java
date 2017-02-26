package gitc.entities;

import gitc.GameState;

public class Entity {
  public int id;
  public Owner owner;

  // backup
  public Owner b_owner;
  public void backup() {
    b_owner = owner;
  }
  public void restore() {
    owner = b_owner;
  }
  
  public Entity(int id, Owner owner) {
    this.id = id;
    this.owner = owner;
  }
  
  public void readPlayer (int player) {
    if (player == 0) {
      owner = null;
    } else if (player == 1) {
      owner = GameState.me;
    } else {
      owner = GameState.opp;
    }
  }
  public boolean isOpponent() {
    return owner == GameState.opp;
  }
  public boolean isMe() {
    return owner == GameState.me;
  }
  public boolean isNeutral() {
    return owner == null;
  }


}
