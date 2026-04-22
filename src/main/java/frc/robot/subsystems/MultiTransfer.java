package frc.robot.subsystems;

import com.revrobotics.RelativeEncoder;
import com.revrobotics.spark.SparkMax;
import com.revrobotics.spark.SparkBase.ControlType;
import com.revrobotics.spark.SparkBase.PersistMode;
import com.revrobotics.spark.SparkBase.ResetMode;
import com.revrobotics.spark.SparkClosedLoopController;
import com.revrobotics.spark.SparkLowLevel.MotorType;
import com.revrobotics.spark.config.SparkBaseConfig.IdleMode;
import com.revrobotics.spark.config.SparkMaxConfig;

import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DigitalSource;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.Alert.AlertType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import frc.robot.util.RobotHealthManager;

public class MultiTransfer extends SubsystemBase {

        SparkMax agitator = new SparkMax(25, MotorType.kBrushless);
        private SparkMax transfer = new SparkMax(17, MotorType.kBrushless);
        private SparkMax feederLeft = new SparkMax(19, MotorType.kBrushless);
        private SparkMax feederRight = new SparkMax(20, MotorType.kBrushless);
        private FlyWheel flyWheel;
        private CommandXboxController User2;

        private RelativeEncoder feederLeftEncoder;
        private final SparkClosedLoopController feederController;
        private final SparkClosedLoopController transfController;
        private final SparkClosedLoopController agitatController;

        private double feederOutputCurrent = 0;
        private double agitatorOutputCurrent = 0;
        private double transferOutputCurrent = 0;

        private boolean isFeederRunning = false;
        private boolean isAgitatorRunning = false;
        private boolean isTransferRunning = false;
        private boolean checkcase = false;

        // Alerts
        private final Alert agitatorError = new Alert("Agitator Motor Fault", AlertType.kError);

        public MultiTransfer(FlyWheel flyWheel, CommandXboxController User2) {
                this.User2 = User2;
                this.flyWheel = flyWheel;

                SparkMaxConfig feederLeftConfig = new SparkMaxConfig();
                SparkMaxConfig feederRightConfig = new SparkMaxConfig();
                SparkMaxConfig agitatorConfig = new SparkMaxConfig();
                SparkMaxConfig transConfig = new SparkMaxConfig();

                feederLeftEncoder = feederLeft.getEncoder();
                // PusherEncoder = Pusher.getEncoder();
                agitatorConfig
                                .inverted(false)
                                .secondaryCurrentLimit(60)
                                .idleMode(IdleMode.kBrake);

                transConfig
                                .secondaryCurrentLimit(60)
                                .idleMode(IdleMode.kBrake);

                feederLeftConfig
                                .secondaryCurrentLimit(60)
                                .idleMode(IdleMode.kBrake);

                feederLeftConfig.closedLoop
                                .pid(0.00008, 0, 0)
                                .velocityFF(0.0001)
                                .minOutput(-1)
                                .maxOutput(1);
                agitatorConfig.closedLoop
                                .pid(0.00008, 0, 0)
                                .velocityFF(0.0001)
                                .minOutput(-1)
                                .maxOutput(1);
                transConfig.closedLoop
                                .pid(0.00008, 0, 0)
                                .velocityFF(0.0001)
                                .minOutput(-1)
                                .maxOutput(1);
                feederLeftConfig.closedLoop.maxMotion
                                .allowedProfileError(50)
                                .maxAcceleration(10000);
                feederLeftConfig.encoder
                                .velocityConversionFactor(1.0)
                                .positionConversionFactor(1.0);
                feederRightConfig
                                .smartCurrentLimit(60)
                                .follow(feederLeft, true)
                                .apply(feederLeftConfig);

                agitator.configure(agitatorConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
                transfer.configure(transConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);

                feederLeft.configure(feederLeftConfig, ResetMode.kResetSafeParameters, PersistMode.kPersistParameters);
                feederRight.configure(feederRightConfig, ResetMode.kResetSafeParameters,
                                PersistMode.kPersistParameters);
                feederController = feederLeft.getClosedLoopController();
                transfController = transfer.getClosedLoopController();
                agitatController = agitator.getClosedLoopController();

                feederOutputCurrent = feederLeft.getOutputCurrent();
                agitatorOutputCurrent = agitator.getOutputCurrent();
                transferOutputCurrent = transfer.getOutputCurrent();
                SmartDashboard.putBoolean("FeederSubStatus", isFeederRunning);
                SmartDashboard.putNumber("FeederSub_Current ", feederOutputCurrent);
                SmartDashboard.putNumber("Agitator_Current ", agitatorOutputCurrent);
                SmartDashboard.putNumber("Transfer_Current ", transferOutputCurrent);

                SmartDashboard.putBoolean("AgitatorStatus", isAgitatorRunning);
                SmartDashboard.putBoolean("TransferStatus", isTransferRunning);
                SmartDashboard.putNumber("Agitator_Current ", agitatorOutputCurrent);

        }

        @Override

        public void periodic() {

                feederOutputCurrent = feederLeft.getAppliedOutput();
                agitatorOutputCurrent = agitator.getAppliedOutput();
                transferOutputCurrent = transfer.getOutputCurrent();

                isFeederRunning = feederOutputCurrent > 0.2;
                isAgitatorRunning = agitatorOutputCurrent > 0.2;
                isTransferRunning = transferOutputCurrent > 0.2;

                SmartDashboard.putNumber("Agitator_Current ", agitatorOutputCurrent);
                SmartDashboard.putNumber("FeederSub_Current ", feederLeft.getOutputCurrent());
                SmartDashboard.putNumber("Agitator_Current ", agitatorOutputCurrent);
                SmartDashboard.putNumber("Transfer_Current ", transferOutputCurrent);
                SmartDashboard.putBoolean("AgitatorStatus", isAgitatorRunning);
                SmartDashboard.putBoolean("FeederSubStatus", isFeederRunning);
                SmartDashboard.putBoolean("TransferStatus", isTransferRunning);

                if (DriverStation.isDisabled()) {

                        String leftFeederFaulString = RobotHealthManager.getFaultString(feederLeft.getFaults());
                        String rightFeederFaulString = RobotHealthManager.getFaultString(feederRight.getFaults());
                        String agitatorFaultString = RobotHealthManager
                                        .getFaultString(agitator.getFaults());
                        String transferFaultString = RobotHealthManager
                                        .getFaultString(transfer.getFaults());

                        RobotHealthManager.updateLeftFeeder(feederLeft
                                        .hasActiveFault(), false,
                                        "Left Feeder Can Id - " + feederLeft.getDeviceId() + "Faults :- "
                                                        + leftFeederFaulString);

                        RobotHealthManager.updateRightFeeder(feederRight
                                        .hasActiveFault(), false,
                                        "Right Feeder Can Id - " + feederRight.getDeviceId()
                                                        + "Faults :- "
                                                        + rightFeederFaulString);

                        RobotHealthManager.updateAgitator(agitator
                                        .hasActiveFault(),
                                        "Agitator Can Id - " + agitator.getDeviceId() + "Faults :- "
                                                        + agitatorFaultString);

                        RobotHealthManager.updateTransfer(transfer.hasActiveFault(),
                                        "Transfer Can Id - " + transfer.getDeviceId() + "Faults :- "
                                                        + transferFaultString);
                }

        }

        public void forward() {

                agitator.set(-0.8);
                transfer.set(-0.8);
                feederLeft.set(-0.8);
                // agitatController.setReference(-4500, ControlType.kVelocity);
                // transfController.setReference(-4500, ControlType.kVelocity);
                // feederController.setReference(-4500, ControlType.kVelocity);
        }

        public void run(double agitatorSpeed, double transferSpeed, double feederSpeed) {

                agitator.set(agitatorSpeed);
                transfer.set(transferSpeed);
                feederLeft.set(feederSpeed);

        }

        public void reverse() {
                if (flyWheel.atSpeed(flyWheel.calculatedRPM)) {
                        agitator.set(0.75);
                        transfer.set(0.75);
                        feederLeft.set(0.75);
                }
        }

        public void reverseAuto() {

                agitator.set(0.75);
                transfer.set(0.75);
                feederLeft.set(0.75);

        }

        public void reverse2() {
                if (flyWheel.atSpeed(1000)) {
                        agitator.set(0.75);
                        transfer.set(0.75);
                        feederLeft.set(0.75);
                }
        }

        public void stop() {
                agitator.stopMotor();
                transfer.stopMotor();
                feederLeft.stopMotor();
                feederRight.stopMotor();
        }

        public void clearFaults() {
                agitator.clearFaults();
                transfer.clearFaults();
                feederLeft.clearFaults();
                feederRight.clearFaults();
        }

}
