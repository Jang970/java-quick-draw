package nz.ac.auckland.se206.gamelogicmanager;

public record GameEndInfo(
    EndGameState winState, String category, int timeTaken, int secondsRemaining) {}
