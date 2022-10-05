package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.GameLogicManager.GameEndInfo;

/**
 * This will be the abstract class that is extendeds by each badge we want in our game It will
 * contain methods that are the same for each badge isEarned will be overriden by each badge with
 * their own corresponding logic for it.
 */
public abstract class Badge {

  protected String name;
  protected String description;
  protected int id;
  protected GameEndInfo gameInfo;

  public Badge(GameEndInfo gameInfo) {
    this.gameInfo = gameInfo;
  }

  public String getName() {
    return this.name;
  }

  public String getDescription() {
    return this.description;
  }

  public int getId() {
    return this.id;
  }

  public Boolean isEarned() {
    return null;
  }
}
