package nz.ac.auckland.se206.util.difficulties;

public enum Time {
  EASY("easy", 60),
  MEDIUM("medium", 45),
  HARD("hard", 30),
  MASTER("master", 15);

  private final int timeToDraw;
  private final String label;

  /**
   * This is the constructor for the Time enum. It stores within it the level of difficulty as a
   * string as well as the relevant time allowed for that level.
   *
   * @param label string that contains the relevant Time level
   * @param time int that stores the time allowed at each relevant Time difficulty level
   */
  private Time(String label, int time) {
    this.label = label;
    this.timeToDraw = time;
  }

  /**
   * Call this method when you want to set the timer value to the value associated at a wanted Time
   * difficulty level e.g say the Time difficulty wanted is level easy, then this will return 60 as
   * that is how much time is allowed at that given level.
   *
   * @return int of the amount of time given at stated difficulty level
   */
  public int getTimeToDraw() {
    return this.timeToDraw;
  }

  /**
   * Call this method when you want to get the label of a specified time difficulty level
   *
   * @return string that specifies the level and difficulty e.g Time.HARD.getLabel() = hard
   */
  public String getLabel() {
    return this.label;
  }
}
