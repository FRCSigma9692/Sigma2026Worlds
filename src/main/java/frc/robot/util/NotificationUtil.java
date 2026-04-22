package frc.robot.util;

public class NotificationUtil {

    public static boolean sendOnce(
        boolean condition,
        boolean sentFlag,
        Elastic.Notification notification
    ) {
        if (condition && !sentFlag) {
            Elastic.sendNotification(notification);
            return true;
        }
        if (!condition) return false;
        return sentFlag;
    }
}