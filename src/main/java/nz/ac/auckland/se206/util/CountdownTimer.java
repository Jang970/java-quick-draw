package nz.ac.auckland.se206.util;

import java.util.Timer;
import java.util.TimerTask;

/** This class creates a timers which count down from a given time */
public class CountdownTimer {

  public interface OnChangeFunction {
    /**
     * @param secondsRemaining the number of seconds until the timer reaches 0.
     */
    void run(int secondsRemaining);
  }

  private Runnable onComplete = null;
  private OnChangeFunction onChange = null;

  // Usually number of seconds but time between each decrement may not be a second so using a more
  // generic term instead
  private int totalUnits;
  private Timer timer;

  /**
   * Sets a function to be called when the timer changes..
   *
   * @param onChange A class which implements OnChangeFunction - this implements one method <code>
   *     void run(int secondsRemaining)</code> @see {@link OnChangeFunction}.
   */
  public void setOnChange(OnChangeFunction onChange) {
    this.onChange = onChange;
  }

  /**
   * Sets a function to be called when the timer reaches 0
   *
   * @param onComplete A class which implements Runnable
   */
  public void setOnComplete(Runnable onComplete) {
    this.onComplete = onComplete;
  }

  /**
   * Starts a new countdown
   *
   * @param units the number of units to countdown from
   * @param delay the delay before starting the countdown in milliseconds
   * @param period the preiod of each decrement in milliseconds
   */
  public void startCountdown(int units, int delay, int period) {
    if (timer != null) {
      timer.cancel();
    }
    // Set isDaemon to true so that it cancels with the rest of the app
    timer = new Timer(true);
    totalUnits = units;
    timer.scheduleAtFixedRate(
        // A little anonymous class trickery
        new TimerTask() {

          @Override
          public void run() {

            // This is a matter of preference but it makes it explicit
            // that I am refering to the outer class. I think it is safer
            CountdownTimer outer = CountdownTimer.this;

            if (outer.onChange != null) {
              outer.onChange.run(totalUnits);
            }
            if (totalUnits == 0) {
              if (outer.onComplete != null) {
                outer.onComplete.run();
              }
              outer.timer.cancel();
            }
            outer.totalUnits--;
          }
        },
        0,
        1000);
  }

  /**
   * Starts a countdown with no delay
   *
   * @param seconds the number of seconds to countdown from
   */
  public void startCountdown(int seconds) {
    this.startCountdown(seconds, 0, 1000);
  }

  /** Cancels the countdown */
  public void cancelCountdown() {
    if (timer != null) {
      timer.cancel();
    }
  }
}
