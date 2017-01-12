package abra.bots;

import battlecode.common.Clock;
import battlecode.common.RobotController;

public strictfp class Gardener {
    public static void run(RobotController rc) {
        while (true) {
            try {

                Clock.yield();
            }catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }
}
