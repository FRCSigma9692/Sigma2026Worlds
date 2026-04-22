// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import static edu.wpi.first.units.Units.*;
import java.util.function.BooleanSupplier;
import java.util.jar.Attributes.Name;

import com.ctre.phoenix6.swerve.SwerveModule.DriveRequestType;
import com.ctre.phoenix6.swerve.SwerveRequest;
import com.pathplanner.lib.auto.AutoBuilder;
import com.pathplanner.lib.auto.NamedCommands;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.math.geometry.Rotation2d;
import edu.wpi.first.math.geometry.Translation2d;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.LEDPattern.GradientType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.Field2d;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.Commands;
import edu.wpi.first.wpilibj2.command.InstantCommand;
import edu.wpi.first.wpilibj2.command.ParallelCommandGroup;
import edu.wpi.first.wpilibj2.command.ParallelDeadlineGroup;
import edu.wpi.first.wpilibj2.command.RunCommand;
import edu.wpi.first.wpilibj2.command.WaitCommand;
import edu.wpi.first.wpilibj2.command.button.CommandPS5Controller;
import edu.wpi.first.wpilibj2.command.button.CommandXboxController;
import edu.wpi.first.wpilibj2.command.button.RobotModeTriggers;
import edu.wpi.first.wpilibj2.command.button.Trigger;
import frc.robot.commands.FlyWheelAutoDistanceCommand;

import frc.robot.commands.FlyWheelAutoDistanceCommandTele2;
import frc.robot.commands.FlywheelPower;
import frc.robot.commands.HopperCmd;
import frc.robot.commands.HopperCmdshoot;
import frc.robot.commands.IntakCmd;
import frc.robot.commands.IntakStopCmd;
import frc.robot.commands.MultiTransferCmd;
import frc.robot.generated.TunerConstants;
import frc.robot.subsystems.CommandSwerveDrivetrain;

import frc.robot.subsystems.FlyWheel;
import frc.robot.subsystems.Hopper;
import frc.robot.subsystems.Intake;
import frc.robot.subsystems.LEDSub2;
import frc.robot.subsystems.MultiTransfer;
import frc.robot.subsystems.ShotCalculator;
import frc.robot.util.HubLogic;

public class RobotContainer {

        public double AlignmentSpeed = 0.25;

        public double IntakeRobotSpeed = 1;
        public double TransferRPM = 0.9;
        public double FeederRPM = 3100;
        public double rpm = 0;
        public BooleanSupplier override = () -> true;
        public double output;
        public double RotPow45;
        private double speed = 0.5;

        private double MaxSpeed = TunerConstants.kSpeedAt12Volts.in(MetersPerSecond); // kSpeedAt12Volts desired top
                                                                                      // speed
        private double MaxAngularRate = RotationsPerSecond.of(0.75).in(RadiansPerSecond); // 3/4 of a rotation per
                                                                                          // second
                                                                                          // max angular velocity
        // private Pushing transfer;

        /* Setting up bindings for necessary control of the swerve drive platform */
        private final SwerveRequest.FieldCentric drive = new SwerveRequest.FieldCentric()
                        .withDeadband(MaxSpeed * 0.055).withRotationalDeadband(MaxAngularRate * 0.055) // Add a 10%
                                                                                                       // deadband
                        .withDriveRequestType(DriveRequestType.OpenLoopVoltage); // Use open-loop control for drive
                                                                                 // motors

        private final SwerveRequest.SwerveDriveBrake brake = new SwerveRequest.SwerveDriveBrake();
        private final SwerveRequest.PointWheelsAt point = new SwerveRequest.PointWheelsAt();
        private final SwerveRequest.RobotCentric forwardStraight = new SwerveRequest.RobotCentric()
                        .withDriveRequestType(DriveRequestType.OpenLoopVoltage);

        private final Telemetry logger = new Telemetry(MaxSpeed);
        public CommandPS5Controller Drive = new CommandPS5Controller(0);
        public CommandXboxController Mechanism = new CommandXboxController(1);
        public CommandPS5Controller test = new CommandPS5Controller(2);

        // private final CommandXboxController Mechanism = new CommandXboxController(1);

        public final CommandSwerveDrivetrain drivetrain = TunerConstants.createDrivetrain();
        // LEDPattern base = LEDPattern.gradient(GradientType.kDiscontinuous,
        // Color.kRed, Color.kGreen);
        // LEDPattern pattern = LEDPattern.progressMaskLayer(() ->
        // HubLogic.getShiftTimeRemaining() / 25);
        // LEDPattern display = base.mask(pattern);

        public Hopper hopper = new Hopper();

        private Intake intake = new Intake();

        private LEDSub2 ledsub = new LEDSub2();
        public Field2d field = new Field2d();

        private final SendableChooser<Command> autoChooser;

        private final FlyWheel flywheel = new FlyWheel(drivetrain);
        private ShotCalculator shotCalculator = new ShotCalculator(drivetrain, flywheel);

        private MultiTransfer multiTransferSubsystem = new MultiTransfer(flywheel, Mechanism);

        private Trigger triggerDown = new Trigger(() -> MathUtil.applyDeadband(Mechanism.getRightY(), 0.1) > 0.3);
        private Trigger triggerUp = new Trigger(() -> MathUtil.applyDeadband(Mechanism.getRightY(), 0.1) < -0.3);
        private Trigger triggerstop = new Trigger(() -> Mechanism.getRightY() > -0.3 && Mechanism.getRightY() < 0.3);
        private Trigger hopperstop = new Trigger(
                        () -> Mechanism.getLeftY() > -0.3 && Mechanism.getLeftY() < 0.3 && Mechanism.getRightY() > -0.3
                                        && Mechanism.getRightY() < 0.3);
        private Trigger hopperup = new Trigger(() -> Mechanism.getLeftY() < -0.3);
        private Trigger hopperdown = new Trigger(() -> Mechanism.getLeftY() > 0.3);

        public RobotContainer() {

                NamedCommands.registerCommand("Intake", new IntakCmd(intake, 0.95));
                NamedCommands.registerCommand("IntakeStop", new IntakStopCmd(intake));
                NamedCommands.registerCommand("HopperOut", new HopperCmd(hopper, 0.3));
                NamedCommands.registerCommand("HopperStop", new InstantCommand(() -> hopper.stop(), hopper));
                NamedCommands.registerCommand("Shooter",
                                new FlyWheelAutoDistanceCommand(flywheel, drivetrain, shotCalculator));
                NamedCommands.registerCommand("Shoot",
                                new InstantCommand(() -> multiTransferSubsystem.reverseAuto()));

                NamedCommands.registerCommand("HopperOut", new HopperCmd(hopper, 0.6).withTimeout(3));
                NamedCommands.registerCommand("HopperShoot", new HopperCmdshoot(hopper));

                // NamedCommands.registerCommand("HopperShoot", new HopperCmdshoot(hopper,
                // -0.3));
                NamedCommands.registerCommand("Shoot_Stop", new ParallelCommandGroup(
                                new InstantCommand(() -> flywheel.stop()),
                                new InstantCommand(() -> multiTransferSubsystem.stop())));
                SmartDashboard.putNumber("rpmInput", 0);
                SmartDashboard.putData("Field", field);

                autoChooser = AutoBuilder.buildAutoChooser("None");
                // ledsub.runPattern(display);

                SmartDashboard.putData("Auto Mode", autoChooser);
                configureBindings();

        }

        private void configureBindings() {

                drivetrain.setDefaultCommand(
                                drivetrain.applyRequest(() -> drive.withVelocityX(-Drive.getLeftY() * MaxSpeed * speed)
                                                .withVelocityY(-Drive.getLeftX() * MaxSpeed * speed)
                                                .withRotationalRate((-Drive.getRightX() * MaxSpeed * speed))));

                driver1();
                driver2();

        }

        public void driver1() {
                // Default Drive --------------------------

                // Reset Robot Heading ---------------------
                Drive.pov(0).onTrue(drivetrain.runOnce(drivetrain::seedFieldCentric));

                // Break Mode ------------------------------
                Drive.cross().whileTrue(drivetrain.applyRequest(() -> brake));

                final var idle = new SwerveRequest.Idle();
                RobotModeTriggers.disabled().whileTrue(
                                drivetrain.applyRequest(() -> idle).ignoringDisable(true));

                // Align to Hub -----------------------------
                Drive.L1().whileTrue(
                                new ParallelCommandGroup(
                                                Commands.run(() -> drivetrain.autoAlignToHub()),

                                                drivetrain.applyRequest(() -> drive
                                                                .withVelocityX(-Drive.getLeftY() * MaxSpeed
                                                                                * AlignmentSpeed)
                                                                .withVelocityY(-Drive.getLeftX() * MaxSpeed
                                                                                * AlignmentSpeed)
                                                                .withRotationalRate(drivetrain.rot * MaxAngularRate))));
                Drive.R1().whileTrue(
                                drivetrain.applyRequest(() -> drive
                                                .withVelocityX(-Drive.getLeftY() * MaxSpeed
                                                                * AlignmentSpeed)
                                                .withVelocityY(-Drive.getLeftX() * MaxSpeed
                                                                * AlignmentSpeed)
                                                .withRotationalRate(shotCalculator.rot * MaxAngularRate)));
                // Drive Slow --------------------------------
                Drive.R2().whileTrue(
                                drivetrain.applyRequest(() -> drive
                                                .withVelocityX(-Drive.getLeftY() * MaxSpeed * 0.4)
                                                .withVelocityY(-Drive.getLeftX() * MaxSpeed * 0.4)
                                                .withRotationalRate(drivetrain.rotDrive * MaxSpeed * speed)));

                drivetrain.registerTelemetry((state) -> {
                        field.setRobotPose(state.Pose);
                });
                drivetrain.registerTelemetry(logger::telemeterize);

        }

        public void driver2() {

                // Manual RPM By the Elastic -----------------
                Mechanism.rightTrigger(0.7).whileTrue(
                                new ParallelCommandGroup(new FlywheelPower(flywheel),
                                                new MultiTransferCmd(multiTransferSubsystem, 0.75, 0.75, 0.75)));

                hopperstop.whileTrue(new InstantCommand(() -> hopper.stop()));
                hopperup.whileTrue(new InstantCommand(() -> hopper.run(-0.5)));
                hopperdown.whileTrue(new InstantCommand(() -> hopper.run(0.5)));

                // Ball Transger ------------------------------
                triggerstop.whileTrue(new InstantCommand(() -> multiTransferSubsystem.stop()));
                triggerDown.whileTrue(new InstantCommand(() -> multiTransferSubsystem.forward()));
                triggerUp.whileTrue(new ParallelCommandGroup(
                                new InstantCommand(() -> multiTransferSubsystem.reverse())));
                // Shooter ------------------------------------
                Mechanism.leftTrigger(0.5)
                                .whileTrue(new FlyWheelAutoDistanceCommandTele2(flywheel, drivetrain, shotCalculator));

                // Intake -------------------------------------
                Mechanism.leftBumper().onTrue(
                                new IntakCmd(intake, 0.6));

                // Outtake ------------------------------------
                Mechanism.rightBumper().onTrue(
                                new IntakCmd(intake, 0));

                // Intake Stop ---------------------------------
                Mechanism.povDown().onTrue(
                                new IntakCmd(intake, -0.6));
                Mechanism.x().onTrue(
                                new ParallelCommandGroup(
                                                new IntakCmd(intake, 0),
                                                new InstantCommand(() -> flywheel.stop()),
                                                new InstantCommand(() -> multiTransferSubsystem.stop()))

                );

                // All Mechanisms Stop -------------------------
                Mechanism.b().onTrue(new InstantCommand(() -> {
                        flywheel.clearFaults();
                        hopper.clearFaults();
                        intake.clearFaults();
                        multiTransferSubsystem.clearFaults();
                }));
        }

        private void test() {

                test.square().whileTrue(new InstantCommand(() -> flywheel.setFlyWheelRpm(2000)))
                                .onFalse(new InstantCommand(() -> flywheel.stop()));

                // agitator test
                test.triangle().whileTrue(new InstantCommand(() -> multiTransferSubsystem.run(0.5, 0, 0)))
                                .onFalse(new InstantCommand(() -> multiTransferSubsystem.stop()));

                // transfer test
                test.cross().whileTrue(new InstantCommand(() -> multiTransferSubsystem.run(0, 0.5, 0)))
                                .onFalse(new InstantCommand(() -> multiTransferSubsystem.stop()));

                // feeder test
                test.circle().whileTrue(new InstantCommand(() -> multiTransferSubsystem.run(0, 0, 0.5)))
                                .onFalse(new InstantCommand(() -> multiTransferSubsystem.stop()));

                // intake test
                test.povUp().whileTrue(new InstantCommand(() -> intake.runIntake(0.5)))
                                .onFalse(new InstantCommand(() -> intake.StopIntake()));
        }

        // public void clearFaults() {

        // flywheel.clearFaults();
        // hopper.clearFaults();
        // intake.clearFaults();

        // }

        public Command getAutonomousCommand() {
                return autoChooser.getSelected();
        }
}
