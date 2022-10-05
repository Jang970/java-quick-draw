package nz.ac.auckland.se206.util.badges;

/**
 * This will be the interface that is implemented by each badge we want in our game It will contain
 * methods that must be implemented by each badge.
 */
public interface Badge {

  public String getName();

  public String getDescription();

  public Boolean isEarned();
}
