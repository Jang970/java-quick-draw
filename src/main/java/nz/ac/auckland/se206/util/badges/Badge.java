package nz.ac.auckland.se206.util.badges;

import java.util.List;
import nz.ac.auckland.se206.gamelogicmanager.GameEndInfo;

/**
 * This will be the abstract class that is extendeds by each badge we want in our game It will
 * contain methods that are the same for each badge isEarned will be overriden by each badge with
 * their own corresponding logic for it.
 */
public abstract class Badge {

  private final String displayTitle;
  private final String description;
  private final String id;

  public Badge(String id, String name, String desciption) {
    this.id = id;
    this.displayTitle = name;
    this.description = desciption;
  }

  public final String getId() {
    return this.id;
  }

  public final String getDisplayTitle() {
    return this.displayTitle;
  }

  public final String getDescription() {
    return this.description;
  }

  public abstract boolean earned(List<GameEndInfo> gameHistory);
}
