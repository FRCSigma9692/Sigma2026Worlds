package frc.robot.util;

import java.util.Optional;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;

public class GameDataUtil {

    public static String getGameData() {
        return DriverStation.getGameSpecificMessage();
    }

    public static boolean isDataReady() {
        String data = getGameData();
        return !data.isEmpty();
    }

    public static boolean redInactiveFirst() {
        String data = getGameData();
        if (data.isEmpty()) return false;
        return data.charAt(0) == 'R';
    }

    // Get alliance color
    public static Alliance getAllianceColor() {
        Optional<Alliance> alliance = DriverStation.getAlliance();
        return alliance.orElse(null);
    }
}
