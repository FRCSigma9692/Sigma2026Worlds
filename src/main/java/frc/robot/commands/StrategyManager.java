package frc.robot.commands;

import java.util.Optional;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import frc.robot.generated.TargetType;
public class StrategyManager {

    public static TargetType getTargetType(double robotX, double robotY) {

        Optional<Alliance> alliance = DriverStation.getAlliance();

        boolean inNeutralZone = robotX > 5 && robotX < 10.5;

        if (!alliance.isPresent())
            return TargetType.HUB;

        if (inNeutralZone) {

            boolean onLeftSide = robotY > 4;

            if (onLeftSide)
                return TargetType.PASS_LEFT;
            else
                return TargetType.PASS_RIGHT;
        }

        return TargetType.HUB;
    }
}