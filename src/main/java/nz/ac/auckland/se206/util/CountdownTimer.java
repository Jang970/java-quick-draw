package nz.ac.auckland.se206.util;

import java.util.Timer;
import java.util.TimerTask;

/** This class creates a timers which count down from a given time */
public class CountdownTimer {

  public interface CountdownTimerChangeEvent {
    /**
     * @param secondsRemaining the number of seconds until the timer reaches 0.
     */
    void run(int secondsRemaining);
  }

  public interface CountdownTimerCompleteEvent {
    void run();
  }

  private CountdownTimerCompleteEvent onCompleteEvent = null;
  private CountdownTimerChangeEvent onChangeEvent = null;

  // The number we are counting down from
  private int remainingCount;
  private Timer timer;

  /**
   * Sets a function to be called when the timer changes..
   *
   * @param onChange A class which implements OnChangeFunction - this implements one method <code>
   *     void run(int secondsRemaining)</code> @see {@link CountdownTimerChangeEvent}.
   */
  public void setOnChange(CountdownTimerChangeEvent onChange) {
    this.onChangeEvent = onChange;
  }

  /**
   * Sets a function to be called when the timer reaches 0
   *
   * @param onComplete A class which implements Runnable
   */
  public void setOnComplete(CountdownTimerCompleteEvent onComplete) {
    this.onCompleteEvent = onComplete;
  }

  /**
   * Starts a new countdown
   *
   * @param count the number to countdown from
   * @param delay the delay before starting the countdown in milliseconds
   * @param period the preiod of each decrement in milliseconds
   */
  public void startCountdown(int count, int delay, int period) {
    if (timer != null) {
      timer.cancel();
    }
    // Set isDaemon to true so that it cancels with the rest of the app
    timer = new Timer(true);
    remainingCount = count;
    timer.scheduleAtFixedRate(
        new TimerTask() {

          @Override
          public void run() {

            if (CountdownTimer.this.onChangeEvent != null) {
              CountdownTimer.this.onChangeEvent.run(remainingCount);
            }
            if (remainingCount == 0) {
              if (CountdownTimer.this.onCompleteEvent != null) {
                CountdownTimer.this.onCompleteEvent.run();
              }
              CountdownTimer.this.timer.cancel();
            }
            CountdownTimer.this.remainingCount--;
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
