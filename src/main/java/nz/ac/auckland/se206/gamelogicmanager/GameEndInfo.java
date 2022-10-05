package nz.ac.auckland.se206.gamelogicmanager;

import nz.ac.auckland.se206.util.Category;

public record GameEndInfo(
    EndGameState winState, Category category, int timeTaken, int secondsRemaining) {}
