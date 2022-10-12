package nz.ac.auckland.se206.util;

import java.util.Timer;
import java.util.TimerTask;

public class CountdownTimer {

  public interface CountdownTimerChangeEvent {
    void run(int secondsRemaining);
  }

  public interface CountdownTimerCompleteEvent {
    void run();
  }

  private CountdownTimerCompleteEvent onCompleteEvent;
  private CountdownTimerChangeEvent onChangeEvent;

  private int countdownCurrentCount = 0;
  ;
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
   * @param onComplete the function to run when the timer completes
   */
  public void setOnComplete(CountdownTimerCompleteEvent onComplete) {
    this.onCompleteEvent = onComplete;
  }

  /**
   * Gets the remaining count. You can guarantee the countdown has ended when this reaches 0.
   *
   * @return the remaining count
   */
  public int getRemainingCount() {
    return countdownCurrentCount;
  }

  /**
   * Starts a new countdown. Cancels the previous countdown if it was already running.
   *
   * @param countToCountdownFrom the number to countdown from
   * @param delayBeforeStarting the delay before starting the countdown in milliseconds
   * @param period the time between each decrement (including time taken to decrement)
   */
  public void startCountdown(int countToCountdownFrom, int delayBeforeStarting, int period) {
    if (timer != null) {
      timer.cancel();
    }

    // Make this a background task by setting isDaemon to true
    timer = new Timer(true);

    countdownCurrentCount = countToCountdownFrom;

    // this sets a timer to decrease the count every period milliseconds
    timer.scheduleAtFixedRate(
        new TimerTask() {

          @Override
          public void run() {

            // run relevant functions
            if (onChangeEvent != null) {
              onChangeEvent.run(countdownCurrentCount);
            }
            if (countdownCurrentCount == 0) {
              if (onCompleteEvent != null) {
                onCompleteEvent.run();
              }
              timer.cancel();
            }
            // Decrement count at the end
            countdownCurrentCount--;
          }
        },
        0,
        1000);
  }

  /**
   * Starts a countdown with no delay and a period of one second
   *
   * @param seconds the number of seconds to countdown from
   */
  public void startCountdown(int seconds) {
    this.startCountdown(seconds, 0, 1000);
  }

  /** This will end the current countdown */
  public void cancelCountdown() {
    if (timer != null) {
      timer.cancel();
    }
  }
}
