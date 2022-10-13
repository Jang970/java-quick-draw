package nz.ac.auckland.se206.util.badges;

import nz.ac.auckland.se206.util.Profile;

/**
 * This will be the abstract class that is extendeds by each badge we want in our game It will
 * contain methods that are the same for each badge isEarned will be overriden by each badge with
 * their own corresponding logic for it.
 */
public abstract class Badge {

  private final String displayTitle;
  private final String description;
  private final String id;

  /**
   * This is the constructor for the abstract class Badge which will be inherited by all the badges
   * we create in our app.
   *
   * @param id id of the badge
   * @param name name of the badge
   * @param desciption description of the badge
   */
  public Badge(String id, String name, String desciption) {
    this.id = id;
    this.displayTitle = name;
    this.description = desciption;
  }

  /**
   * This method will get the id of the badge
   *
   * @return id of the badge
   */
  public final String getId() {
    return this.id;
  }

  /**
   * This method will get the name of the badge
   *
   * @return name of the badge
   */
  public final String getDisplayTitle() {
    return this.displayTitle;
  }

  /**
   * This method will get the description of the badge
   *
   * @return description of the badge
   */
  public final String getDescription() {
    return this.description;
  }

  /**
   * This method will check if the profile in question has gotten the badge
   *
   * @param profile profile to check
   * @return if the profile has earned the badge
   */
  public abstract boolean earned(Profile profile);
}
