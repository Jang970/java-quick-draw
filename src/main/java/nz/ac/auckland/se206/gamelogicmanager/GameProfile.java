package nz.ac.auckland.se206.gamelogicmanager;

import java.util.List;

public record GameProfile(
    int gameLengthSeconds,
    int numTopGuessNeededToWin,
    GameMode gameMode,
    List<GameEndInfo> gameHistory) {}
