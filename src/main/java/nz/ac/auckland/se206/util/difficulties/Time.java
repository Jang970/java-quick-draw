package nz.ac.auckland.se206.util.difficulties;

public enum Time {
  EASY("easy", 60),
  MEDIUM("medium", 45),
  HARD("hard", 30),
  MASTER("master", 15);

  private final int timeToDraw;
  private final String label;

  private Time(String label, int time) {
    this.label = label;
    this.timeToDraw = time;
  }

  /**
   * Call this method when you want to set the timer value to the value associated at a wanted Time
   * difficulty level e.g say the Time difficulty wanted is level easy, then this will return 60 as
   * that is how much time is allowed at that given level.
   *
   * @param difficulty enum of type time specifying the level wanted e.g Time.EASY
   * @return int of the amount of time given at stated difficulty level
   */
  public int getTimeToDraw() {
    return this.timeToDraw;
  }

  /**
   * Call this method when you want to get the label of a specified time difficulty level
   *
   * @param difficulty enum of type Time e.g Time.HARD
   * @return string that specifies the level and difficulty e.g hardTime
   */
  public String getLabel() {
    return this.label;
  }
}
