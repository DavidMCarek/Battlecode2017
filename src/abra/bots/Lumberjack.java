package abra.bots;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public strictfp class Lumberjack {
    public static void run(RobotController rc) {
        while (true) {
            try {

                Clock.yield();
            }catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }
}
