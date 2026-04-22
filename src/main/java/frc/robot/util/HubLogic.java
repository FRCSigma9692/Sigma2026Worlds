package frc.robot.util;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStation.Alliance;
import java.util.Optional;

public class HubLogic {

    int counter = 0;
    boolean on;
    static boolean redInactiveFirst;
    public static boolean isMyHubActive() {

        Optional<Alliance> allianceOpt = DriverStation.getAlliance();
        if (allianceOpt.isEmpty())
            return false;

        Alliance alliance = allianceOpt.get();

        // Auto always active
        if (DriverStation.isAutonomousEnabled())
            return true;

        if (!DriverStation.isTeleopEnabled())
            return false;

        double matchTime = DriverStation.getMatchTime();
        String gameData = DriverStation.getGameSpecificMessage();

        if (gameData.isEmpty())
            return true; // early teleop assume active

         redInactiveFirst = gameData.charAt(0) == 'R';

        boolean shift1Active;

        if (alliance == Alliance.Red)
            shift1Active = !redInactiveFirst;
        else
            shift1Active = redInactiveFirst;

        if (matchTime > 130)
            return true;
        else if (matchTime > 105)
            return shift1Active;
        else if (matchTime > 80)
            return !shift1Active;
        else if (matchTime > 55)
            return shift1Active;
        else if (matchTime > 30)
            return !shift1Active;
        else
            return true;
    }
    public boolean wonAuto(){
        if (DriverStation.getAlliance().get() == Alliance.Red)
            return redInactiveFirst;
        else {
            return !redInactiveFirst;
        }
    }

    public static String getCurrentShift() {

        double time = DriverStation.getMatchTime();

        if (time > 130)
            return "TRANSITION";
        if (time > 105)
            return "SHIFT 1";
        if (time > 80)
            return "SHIFT 2";
        if (time > 55)
            return "SHIFT 3";
        if (time > 30)
            return "SHIFT 4";

        return "ENDGAME";
    }

    public static double getShiftTimeRemaining() {

        double time = DriverStation.getMatchTime();

        if (time > 130)
            return time - 130;
        if (time > 105)
            return time - 105;
        if (time > 80)
            return time - 80;
        if (time > 55)
            return time - 55;
        if (time > 30)
            return time - 30;

        return time;
    }
    // public  String getMessage(){
    //     if (getCurrentShift()=="TRANSITION"){
    //         if (wonAuto()){
    //             return "TRANSITION SHOOT";
    //         }else {
    //             return "TRANSITION PASS";
    //         }
    //     }
    //     if (getCurrentShift()== "SHIFT 1 " || getCurrentShift() == "SHIFT 2")

    // }
    // public boolean isBlinking(){
    //    return getShiftTimeRemaining()<=5;
    // }
    // public boolean blink(){
    //     if (counter%10==0){
    //      on = !on;
    //     }
    //     counter++;
    //     return on;
    // }
}
