// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import java.util.Optional;

import com.ctre.phoenix6.HootAutoReplay;

import edu.wpi.first.wpilibj.AddressableLED;
import edu.wpi.first.wpilibj.AddressableLEDBuffer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.LEDPattern;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.util.Color;
import edu.wpi.first.wpilibj2.command.Command;
import edu.wpi.first.wpilibj2.command.CommandScheduler;
import edu.wpi.first.wpilibj2.command.RunCommand;
import frc.robot.subsystems.LimelightHelpers;
import frc.robot.util.RobotHealthManager;

public class Robot extends TimedRobot {

    public Optional<Alliance> alliance;
    boolean Allianceshift;
    double Matchtime;
    boolean AutoResult = false;
    private Command m_autonomousCommand;
    private final RobotContainer m_robotContainer;
    Color testColor = new Color(255, 255, 255);

    public Robot() {
     


        m_robotContainer = new RobotContainer();
    }

    @Override
    public void robotPeriodic() {


         
        m_robotContainer.field.setRobotPose(m_robotContainer.drivetrain.GetPose());
        Matchtime = DriverStation.getMatchTime();
        SmartDashboard.putNumber("MatchTime", DriverStation.getMatchTime());
        SmartDashboard.putBoolean("Active Zone or No ", Allianceshift);

        RobotHealthManager.updateGlobalStatus();
        CommandScheduler.getInstance().run();
    }
   

    @Override
    public void disabledInit() {
    }

    @Override
    public void disabledPeriodic() {
        m_robotContainer.drivetrain.checkcase = 0;
        CommandScheduler.getInstance().cancelAll();
        
    }

    @Override
    public void disabledExit() {
    }

    @Override
    public void autonomousInit() {
        LimelightHelpers.setPipelineIndex("limelight-l", 1);
        m_autonomousCommand = m_robotContainer.getAutonomousCommand();
        if (m_autonomousCommand != null) {
            // Timer.delay(0.2);
            CommandScheduler.getInstance().schedule(m_autonomousCommand);
        }
    }

    @Override
    public void autonomousPeriodic() {
        SmartDashboard.putNumber("LimelightPipelineIndex", LimelightHelpers.getCurrentPipelineIndex("limelight-l"));
    }

    @Override
    public void autonomousExit() {
    }

    @Override
    public void teleopInit() {
        LimelightHelpers.setPipelineIndex("limelight-l", 1);
        if (m_autonomousCommand != null) {
            CommandScheduler.getInstance().cancel(m_autonomousCommand);

        }
    }

    @Override
    public void teleopPeriodic() {
        SmartDashboard.putNumber("LimelightPipelineIndex", LimelightHelpers.getCurrentPipelineIndex("limelight-l"));
        SmartDashboard.putString("Alliance Shift", testColor.toString());

    }

    @Override
    public void teleopExit() {
    }

    @Override
    public void testInit() {
        CommandScheduler.getInstance().cancelAll();
    }

    @Override
    public void testPeriodic() {
    }

    @Override
    public void testExit() {
    }

    @Override
    public void simulationPeriodic() {
    }
}
