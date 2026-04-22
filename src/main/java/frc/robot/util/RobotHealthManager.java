package frc.robot.util;

import com.revrobotics.spark.SparkBase.Faults;

import edu.wpi.first.util.struct.Struct;
import edu.wpi.first.wpilibj.Alert;
import edu.wpi.first.wpilibj.Alert.AlertType;

public class RobotHealthManager {

        // ================= ALERTS =================

        private static final Alert flywheelLeftFaultAlert = new Alert("Flywheel Left Motor Fault!", AlertType.kError);
        private static final Alert flywheelRightFaultAlert = new Alert("Flywheel Right Fault!", AlertType.kError);
        private static final Alert flywheelCurrentAlert = new Alert("Flywheel High Current!", AlertType.kWarning);

        private static final Alert feederCurrentAlert = new Alert("Feeder High Current!", AlertType.kWarning);
        private static final Alert feederLeftFaultAlert = new Alert("Feeder Left Motor Fault!", AlertType.kError);
        private static final Alert feederRightFaultAlert = new Alert("Feeder Right Motor Fault!", AlertType.kError);

        private static final Alert hopperFaultAlert = new Alert("Hopper Fault", AlertType.kError);
        private static final Alert hopperCurrentAlert = new Alert("Hopper High Current!", AlertType.kWarning);

        private static final Alert intakeCurrentAlert = new Alert("Intake High Current!", AlertType.kWarning);
        private static final Alert intakeLeftFaultAlert = new Alert("Intake Left Motor Fault!", AlertType.kError);
        private static final Alert intakeRightFaultAlert = new Alert("Intake Right Motor Fault!", AlertType.kError);

        private static final Alert transferCurrentAlert = new Alert("Transfer High Current!", AlertType.kWarning);
        private static final Alert transferFaultAlert = new Alert("Transfer Motor Fault!", AlertType.kError);

        private static final Alert agitatorCurrentAlert = new Alert("Agitator High Current!", AlertType.kWarning);
        private static final Alert agitatorFaultAlert = new Alert("Agitator Motor Fault!", AlertType.kWarning);

        private static final Alert robotReady = new Alert("Robot Ready", AlertType.kInfo);

        // ================= STATE =================

        private static boolean flywheelLeftFault = false;
        private static boolean flywheelRightFault = false;
        private static boolean flywheelCurrent = false;
        private static boolean feederCurrent = false;
        private static boolean feederLeftFault = false;
        private static boolean feederRightFault = false;
        private static boolean hopperFault = false;
        private static boolean hopperCurrent = false;
        private static boolean intakeCurrent = false;
        private static boolean intakeLeftFault = false;
        private static boolean intakeRightFault = false;
        private static boolean transferCurrent = false;
        private static boolean transferFault = false;
        private static boolean agitatorCurrent = false;
        private static boolean agitatorFault = false;

        // ================= NOTIFICATION FLAGS =================

        private static boolean flywheelLeftFaultSent = false;
        private static boolean flywheelRightFaultSent = false;
        private static boolean flywheelCurrentSent = false;
        private static boolean feederCurrentSent = false;
        private static boolean feederRightFaultSent = false;
        private static boolean feederLeftFaultSent = false;
        private static boolean hopperFaultSent = false;
        private static boolean hopperCurrentSent = false;
        private static boolean intakeCurrentSent = false;
        private static boolean intakeLeftFaultSent = false;
        private static boolean intakeRightFaultSent = false;
        private static boolean transferCurrentSent = false;
        private static boolean transferFaultSent = false;
        private static boolean agitatorCurrentSent = false;
        private static boolean agitatorFaultSent = false;

        private boolean intakeLeftCanFault = false;
        private boolean intakeRightCanFault = false;
        private boolean intakeRightGateDriverFault = false;
        private boolean intakeLeftGateDriverFault = false;
        private boolean intakeLeftSensorFault = false;
        private boolean intakeRightSensorFault = false;


        // ================= UPDATE METHODS =================

        public static void updateLeftFlywheel(boolean fault, boolean highCurrent, String errorText) {

                flywheelLeftFault = fault;
                flywheelCurrent = highCurrent;

                String text = errorText;

                flywheelLeftFaultAlert.setText(text);
                flywheelLeftFaultAlert.set(fault);
                flywheelCurrentAlert.set(highCurrent);

                flywheelLeftFaultSent = NotificationUtil.sendOnce(
                                fault,
                                flywheelLeftFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Flywheel Left Motor Fault",
                                                "Check flywheel Left motor"));

                flywheelCurrentSent = NotificationUtil.sendOnce(
                                highCurrent,
                                flywheelCurrentSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.WARNING,
                                                "Flywheel Current",
                                                "High current detected"));
        }

        public static void updateRightFlywheel(boolean fault, boolean highCurrent, String errorText) {

                flywheelRightFault = fault;
                flywheelCurrent = highCurrent;
                String text = errorText;
                flywheelRightFaultAlert.setText(text);
                flywheelRightFaultAlert.set(fault);
                flywheelCurrentAlert.set(highCurrent);

                flywheelRightFaultSent = NotificationUtil.sendOnce(
                                fault,
                                flywheelRightFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Flywheel Right Motor Fault",
                                                "Check flywheel Right motor"));

                flywheelCurrentSent = NotificationUtil.sendOnce(
                                highCurrent,
                                flywheelCurrentSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.WARNING,
                                                "Flywheel Current",
                                                "High current detected"));
        }

        public static void updateLeftFeeder(boolean fault, boolean highCurrent, String errorText) {

                feederLeftFault = fault;
                feederCurrent = highCurrent;
                feederCurrentAlert.set(highCurrent);
                String text = errorText;
                feederLeftFaultAlert.setText(text);
                feederLeftFaultAlert.set(fault);

                feederCurrentSent = NotificationUtil.sendOnce(
                                highCurrent,
                                feederCurrentSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.WARNING,
                                                "Feeder Current",
                                                "High current detected"));

                feederLeftFaultSent = NotificationUtil.sendOnce(
                                feederLeftFault,
                                feederLeftFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Feeder Left Motor Fault",
                                                "Check Feeder Left Motor"));
        }

        public static void updateRightFeeder(boolean fault, boolean highCurrent, String errorText) {

                feederRightFault = fault;
                feederCurrent = highCurrent;
                feederCurrentAlert.set(highCurrent);
                String text = errorText;
                feederRightFaultAlert.setText(text);
                feederRightFaultAlert.set(fault);

                feederCurrentSent = NotificationUtil.sendOnce(
                                highCurrent,
                                feederCurrentSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.WARNING,
                                                "Feeder Current",
                                                "High current detected"));

                feederRightFaultSent = NotificationUtil.sendOnce(
                                feederRightFault,
                                feederRightFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Feeder Right Motor Fault",
                                                "Check Feeder Right Motor"));
        }

        public static void updateHopper(boolean fault, boolean highCurrent, String errorText) {

                hopperFault = fault;
                hopperCurrent = highCurrent;
                String text = errorText;

                hopperFaultAlert.setText(text);
                hopperFaultAlert.set(fault);
                hopperCurrentAlert.set(hopperCurrent);

                hopperFaultSent = NotificationUtil.sendOnce(
                                hopperFault,
                                hopperFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Hopper Fault",
                                                "Check hopper system"));

                hopperCurrentSent = NotificationUtil.sendOnce(
                                highCurrent,
                                hopperCurrentSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.WARNING,
                                                "Hopper Current",
                                                "High current detected"));
        }

        public static void updateLeftIntake(boolean fault, boolean highCurrent, String errorText) {

                intakeLeftFault = fault;
                intakeCurrent = highCurrent;
                String text = errorText;

                intakeLeftFaultAlert.setText(text);
                intakeLeftFaultAlert.set(fault);
                intakeCurrentAlert.set(intakeCurrent);

                intakeLeftFaultSent = NotificationUtil.sendOnce(
                                intakeLeftFault,
                                intakeLeftFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Intake Left Motor Fault",
                                                "Check Intake Left Motor"));

                intakeCurrentSent = NotificationUtil.sendOnce(
                                intakeCurrent,
                                intakeCurrentSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.WARNING,
                                                "Intake Current",
                                                "High current detected"));
        }

        public static void updateRightIntake(boolean fault, boolean highCurrent, String errorText) {

                intakeRightFault = fault;
                intakeCurrent = highCurrent;
                String text = errorText;
                intakeLeftFaultAlert.setText(text);
                intakeRightFaultAlert.set(intakeRightFault);

                intakeCurrentAlert.set(intakeCurrent);

                intakeRightFaultSent = NotificationUtil.sendOnce(
                                intakeRightFault,
                                intakeRightFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Intake Right Motor Fault",
                                                "Check Intake Right Motor"));

                intakeCurrentSent = NotificationUtil.sendOnce(
                                intakeCurrent,
                                intakeCurrentSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.WARNING,
                                                "Intake Current",
                                                "High current detected"));
        }

        public static void updateTransfer(boolean fault, String errorText) {

                transferFault = fault;
                String text = errorText;
                transferFaultAlert.setText(text);
                transferFaultAlert.set(transferFault);

                transferFaultSent = NotificationUtil.sendOnce(
                                transferFault,
                                transferFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Transfer Fault",
                                                "Transfer Motor Has Faults"));
        }

        public static void updateAgitator(boolean fault, String errorText) {

                agitatorFault = fault;
                agitatorFaultAlert.setText(errorText);
                agitatorFaultAlert.set(agitatorFault);

                agitatorFaultSent = NotificationUtil.sendOnce(
                                agitatorFault,
                                agitatorFaultSent,
                                new Elastic.Notification(
                                                Elastic.NotificationLevel.ERROR,
                                                "Agitator Fault",
                                                "Agitator Motor Has Faults"));
        }

        // ================= GLOBAL STATUS =================

        public static void updateGlobalStatus() {

                boolean everythingOK = !flywheelLeftFault && !flywheelRightFault && !transferFault && !hopperFault
                                && !intakeLeftFault && !intakeRightFault && !feederLeftFault && !feederRightFault
                                && !agitatorFault;

                robotReady.set(everythingOK);

                if (everythingOK) {
                        robotReady.setText("All Systems OK! Good To Go ...");
                }
        }

        public static boolean hasActiveFault() {

                boolean everythingOK = !flywheelLeftFault && !flywheelRightFault && !transferFault && !hopperFault
                                && !intakeLeftFault && !intakeRightFault && !feederLeftFault && !feederRightFault
                                && !agitatorFault;

                return !everythingOK;
        }

        public static String getFaultString(Faults faults) {

                StringBuilder msg = new StringBuilder();

                if (faults.can)
                        msg.append("CAN ");
                if (faults.temperature)
                        msg.append("TEMP ");
                if (faults.sensor)
                        msg.append("SENSOR ");
                if (faults.gateDriver)
                        msg.append("DRIVER ");
                if (faults.escEeprom)
                        msg.append("EEPROM ");
                if (faults.firmware)
                        msg.append("FIRMWARE ");
                if (faults.motorType)
                        msg.append("TYPE ");
                if (faults.other)
                        msg.append("OTHER ");

                if (msg.length() == 0)
                        return "";

                return msg.toString();
        }
}