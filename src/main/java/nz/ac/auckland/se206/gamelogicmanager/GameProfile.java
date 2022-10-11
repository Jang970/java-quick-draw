package nz.ac.auckland.se206.gamelogicmanager;

import java.util.List;
import nz.ac.auckland.se206.util.Settings;

public record GameProfile(Settings settings, GameMode gameMode, List<GameInfo> gameHistory) {}
