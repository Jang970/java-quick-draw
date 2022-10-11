package nz.ac.auckland.se206.util.difficulties;

import nz.ac.auckland.se206.util.CategoryType;

public enum WordChoice {
  EASY("easy"),
  MEDIUM("medium"),
  HARD("hard"),
  MASTER("master");

  private final String label;

  private WordChoice(String label) {
    this.label = label;
  }

  public String getLabel() {
    return label;
  }

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
