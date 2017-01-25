package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Scout {

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
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }
}
