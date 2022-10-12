package nz.ac.auckland.se206.util.difficulties;

import nz.ac.auckland.se206.util.CategoryType;

public enum WordChoice {
  EASY("easy"),
  MEDIUM("medium"),
  HARD("hard"),
  MASTER("master");

  private final String label;

  /**
   * Constructor for enum WordChoice that is used to determine what type of categories are playable
   * depending on level of difficulty.
   *
   * @param label string that contains the level of difficulty of the given enum
   */
  private WordChoice(String label) {
    this.label = label;
  }

  /**
   * Method used when wanting to get the label of a specific WordChoice enum level.
   *
   * @return string of that level e.g if it was WordChoice.EASY, it will return easy
   */
  public String getLabel() {
    return label;
  }

  /**
   * This method will be used to determine which level of the WordChoice difficulty will be used and
   * from that, give what type of categories can be played.
   *
   * @param type what category type is required / needed
   * @return true or false depending on if a specific category type should be included in playable
   *     categories or not.
   */
  public boolean categoryShouldBeIncluded(CategoryType type) {
    // TODO: This is probably not the best way of doing this. LMK if you can think of a better
    // solution

    // Bools for meeting each criteria
    boolean easy = type == CategoryType.EASY;
    boolean medium = type == CategoryType.MEDIUM || easy;
    boolean hard = type == CategoryType.HARD || medium;
    boolean master = type == CategoryType.HARD;

    return (this == EASY && easy)
        || (this == MEDIUM && medium)
        || (this == HARD && hard)
        || (this == MASTER && master);
  }
}
