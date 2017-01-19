package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Gardener {
    public static void run(RobotController rc) {

        int turnCount = 1;
        Direction preferredDir = null;

        while (true) {
            try {

                if (turnCount > 10) {
                    TreeInfo[] trees = rc.senseNearbyTrees();
                    if (trees.length > 2) {
                        // build robots

                        // water trees
                    } else {

                        // build more trees
                    }
                }

                Direction dir = Utils.randomDirection();

                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                }

                Utils.tryMove(Utils.randomDirection(), rc);

                turnCount++;

                Clock.yield();
            }catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }
}
