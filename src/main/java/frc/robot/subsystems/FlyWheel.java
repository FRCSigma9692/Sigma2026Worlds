package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.ClosedLoopSlot;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkFlex;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkFlexConfig;
import com.revrobotics.spark.config.SparkMaxConfig;
import com.revrobotics.spark.FeedbackSensor;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.math.interpolation.InterpolatingDoubleTreeMap;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.commands.StrategyManager;
import frc.robot.generated.FieldConstants;
import frc.robot.generated.TargetType;
import frc.robot.util.RobotHealthManager;

public class FlyWheel extends SubsystemBase {

    // private SparkFlex motor = new SparkFlex(27, MotorType.kBrushless);
    // private SparkFlex motor1 = new SparkFlex(21, MotorType.kBrushless);
    private SparkFlex motor = new SparkFlex(23, MotorType.kBrushless);
    private SparkFlex motor1 = new SparkFlex(22, MotorType.kBrushless);
    public static double Kp = 0.0009;// 0.001; // 0.00055 //0.00072
    public static double Ki = 0;// 0;// 1e-7;//0 // 1e-9
    public static double Kd = 0.05;// 0.0007;// 0.011; // 0.0007
    public static double Kf = 0.0002;

    CommandSwerveDrivetrain csd;// 0.00025; // 0.00004; // 0.00006
    private SparkClosedLoopController controller;
    private RelativeEncoder encoder;
    private double intakeRpm = 2500;
    private double a = 143.8;// 96.08;//
    private double b = 1422; // 3570
    // private double a = 171.4;
    // private double b = 1286;
    private double x = 0.0;
    private double y = 0.0;
    private double hubx = 0.0;
    private double huby = 0.0;
    private double calculatedDistance = 0.0;
    public double calculatedRPM = 0.0;
    private boolean isRunning = false;
    private double outputCurrent = 0;
    private Translation2d target;
    private double robotX;
    private double robotY;

    public FlyWheel(CommandSwerveDrivetrain cd) {
        this.csd = cd;

        SparkFlexConfig config = new SparkFlexConfig();
        SparkFlexConfig config1 = new SparkFlexConfig();

        encoder = motor.getEncoder();

        config.closedLoop.maxMotion
                .cruiseVelocity(4000)
                .maxAcceleration(20000)
                .allowedClosedLoopError(50);

        config.encoder
                .positionConversionFactor(1.0)
                .velocityConversionFactor(1.0);

        config
                .smartCurrentLimit(60)
                .idleMode(IdleMode.kCoast)
                .closedLoopRampRate(0.02)
                .openLoopRampRate(0.02);

        config.closedLoop
                .feedbackSensor(FeedbackSensor.kPrimaryEncoder)
                .p(Kp)
                .i(0.0)
                .d(Kd)
                .velocityFF(Kf)
                .outputRange(-1, 1);

        motor.configure(config, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);

        config1

                .idleMode(IdleMode.kCoast)
                .closedLoopRampRate(0.02)
                .openLoopRampRate(0.02);

        config1.follow(motor, true);
        motor1.configure(config1, ResetMode.kResetSafeParameters, PersistMode.kNoPersistParameters);

        controller = motor.getClosedLoopController();

        SmartDashboard.putNumber("X Value", x);
        SmartDashboard.putNumber("Y Value", y);
        SmartDashboard.putNumber("Calculated Distance ", calculatedDistance);
        SmartDashboard.putNumber("Calculated RPM ", calculatedRPM);

        outputCurrent = motor.getOutputCurrent();
        SmartDashboard.putBoolean("FlywheelStatus", isRunning);
        SmartDashboard.putNumber("Flywheel_Current ", outputCurrent);

    }

    @Override
    public void periodic() {
        robotX = csd.getState().Pose.getX();
        robotY = csd.getState().Pose.getY();

        TargetType type = StrategyManager.getTargetType(robotX, robotY);

        target = FieldConstants.getTarget(type);

        if (DriverStation.isDisabled()) {

            boolean leftFault = motor.hasActiveFault();
            boolean rightFault = motor1.hasActiveFault();
            boolean highCurrentWarning = motor.getOutputCurrent() > 60 ||
                    motor1.getOutputCurrent() > 60;

            String lefString = RobotHealthManager
                    .getFaultString(motor.getFaults());
            String rightString = RobotHealthManager
                    .getFaultString(motor1.getFaults());

            RobotHealthManager.updateLeftFlywheel(leftFault, false,
                    "FlyWheel Left Can Id - " + motor.getDeviceId() + "Faults :- " + lefString);

            RobotHealthManager.updateLeftFlywheel(rightFault, false,
                    "FlyWheel Right Can Id - " + motor1.getDeviceId() + "Faults :- " +
                            rightString);
        }

        SmartDashboard.putNumber("Flywheel RPM", getFlywheelRpm());
        SmartDashboard.putNumber("Calculated Distance ",
                3.28084 * Math.hypot(target.getX() - Math.abs(csd.getXPose()),
                        target.getY() - Math.abs(csd.getYPose())));
        SmartDashboard.putNumber("NormalCalcDist", calculatedDistance);
        SmartDashboard.putNumber("Calculated RPM ", calculatedRPM);

        outputCurrent = motor.getOutputCurrent();

        isRunning = outputCurrent > 0.2;

        SmartDashboard.putBoolean("FlywheelStatus", isRunning);
        SmartDashboard.putNumber("Flywheel_Current ", outputCurrent);

    }

    public void setFlyWheelRpm(double rpm) {

        double newrpm = rpm;

        controller.setSetpoint(newrpm, ControlType.kVelocity);
    }

    public void stop() {
        motor.stopMotor();
    }

    public double getFlywheelRpm() {

        return encoder.getVelocity();
    }

    public double getTargetRPMFromDistance(double distanceMeters) {
        calculatedDistance = distanceMeters;
        double rpm = a * distanceMeters + b;
        double newrpm = rpm + 450;
        double clampRPM;

        clampRPM = MathUtil.clamp(newrpm, 2000, 6000);

        calculatedRPM = clampRPM;
        return clampRPM;
    }

    public double getCurrent() {
        return motor.getOutputCurrent();
    }

    public double getDistance(double robotX, double robotY) {
        double distanceFeet = 3.28084 *
                Math.hypot(target.getX() - (robotX),
                        target.getY() - (robotY));
        return distanceFeet;

    }

    public void setFlywheelFromDistance(double distanceMeters) {
        setFlyWheelRpm(getTargetRPMFromDistance(distanceMeters));
    }

    public boolean atSpeed(double targetRPM) {
        return Math.abs(getFlywheelRpm() - targetRPM) < 450; // tolerance
    }

    public void powerRun() {
        motor.set(0.4);
    }

    public void clearFaults() {
        motor.clearFaults();
        motor1.clearFaults();
    }
}
