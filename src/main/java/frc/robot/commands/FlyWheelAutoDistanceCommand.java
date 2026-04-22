package frc.robot.commands;

import com.ctre.phoenix6.swerve.SwerveRequest;

import edu.wpi.first.math.filter.LinearFilter;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj2.command.Command;

import frc.robot.subsystems.FlyWheel;
import frc.robot.subsystems.ShotCalculator;
import frc.robot.generated.FieldConstants;
import frc.robot.generated.TargetType;
import frc.robot.subsystems.CommandSwerveDrivetrain;
import edu.wpi.first.units.measure.*;
import edu.wpi.first.wpilibj.PS5Controller;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class FlyWheelAutoDistanceCommand extends Command {

    private final FlyWheel flywheel;
    private CommandSwerveDrivetrain drive;
    ShotCalculator shotCalculator;

    private final LinearFilter distanceFilter = LinearFilter.singlePoleIIR(0.2, 0.02);

    public FlyWheelAutoDistanceCommand(FlyWheel flywheel, CommandSwerveDrivetrain drive,
            ShotCalculator shotCalculator) {
        this.flywheel = flywheel;
        this.drive = drive;
        this.shotCalculator = shotCalculator;
        addRequirements(flywheel);
    }

    @Override
    public void execute() {

        double robotX = drive.getState().Pose.getX();
        double robotY = drive.getState().Pose.getY();

        TargetType type = StrategyManager.getTargetType(robotX, robotY);

        Translation2d target = FieldConstants.getTarget(type);

        SmartDashboard.putString("TargetType", target.toString());

        // var speeds = drive.getState().Speeds;
        // double vx = speeds.vxMetersPerSecond;
        // double vy = speeds.vyMetersPerSecond;

        // // ball flight time (tune later)
        // double flightTime = 0.5;

        // // predict robot movement
        // double futureX = vx * flightTime;
        // double futureY = vy * flightTime;

        double distanceFeet = 3.28084 *
                Math.hypot(target.getX() - (robotX),
                        target.getY() - (robotY));

        double targetRPM;

        if (type == TargetType.HUB) {
            targetRPM = flywheel.getTargetRPMFromDistance(distanceFeet);
            // LedSubSystem.getInstance().setColor(-0.59); // green = shoot
        } else {
            targetRPM = flywheel.getTargetRPMFromDistance(distanceFeet);
            // LedSubSystem.getInstance().setColor(0.35); // blue = pass
        }

        // set rpm continuously
        flywheel.setFlyWheelRpm(shotCalculator.GetFlywheelSpeed());
    }

    @Override
    public void end(boolean interrupted) {
        // flywheel.stop();
    }

    @Override
    public boolean isFinished() {
        return true;
    }
}
