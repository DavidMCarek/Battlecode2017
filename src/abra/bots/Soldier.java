package abra.bots;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public strictfp class Soldier {
    public static void run(RobotController rc) {
        while (true) {
            try {

                Clock.yield();
            }catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }
}
