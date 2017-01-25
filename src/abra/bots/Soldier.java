package abra.bots;

import abra.Utils;
import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotInfo;

public strictfp class Soldier {
    public static void run(RobotController rc) {
        Direction preferredDir = Utils.randomDirection();
        Direction tempDir = null;
        boolean fired = false;
        boolean moved = false;

        while (true) {
            try {

                RobotInfo[] nearbyBots = rc.senseNearbyRobots();
                boolean[] enemiesCanSee = Utils.setCanSeeNearbyRobots(nearbyBots, rc);
                tempDir = Utils.trySafeShot(nearbyBots, enemiesCanSee, rc);
                if (tempDir != null) {
                    preferredDir = tempDir;
                    fired = true;
                }

                if (!moved && !fired) {
                    tempDir = Utils.trySafeMove(preferredDir, rc);
                    if (tempDir != null) {
                        preferredDir = tempDir;
                        moved = true;
                    }
                }

                moved = false;
                fired = false;
                Clock.yield();
            }catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }
}
