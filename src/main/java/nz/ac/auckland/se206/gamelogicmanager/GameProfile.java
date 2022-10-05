package nz.ac.auckland.se206.gamelogicmanager;

import java.util.List;

public record GameProfile(
    int gameLengthSeconds,
    int numTopGuessNeededToWin,
    Difficulty difficulty,
    GameMode gameMode,
    List<GameEndInfo> gameHistory) {}
