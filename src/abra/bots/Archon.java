package examplefuncsplayer.bots;

import battlecode.common.BulletInfo;
import battlecode.common.Direction;
import battlecode.common.RobotController;
import battlecode.common.RobotType;

public class Archon {
    public static void run(RobotController rc) {
        while (true) {
            try {

                BulletInfo[] nearbyBullets = rc.senseNearbyBullets();
                
                int bullets = (int)rc.getTeamBullets();

                rc.hasRobotBuildRequirements(RobotType.GARDENER);

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

    private void tryBuild(RobotController rc) {
        if (rc.canBuildRobot(RobotType.GARDENER, Direction.getEast()));
    }
}
