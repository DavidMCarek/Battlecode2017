package abra.bots;

import abra.Utils;
import battlecode.common.*;

public class Archon {
    public static void run(RobotController rc) {

        while (true) {
            try {

                boolean randomMove = true;
                MapLocation botLocation = rc.getLocation();

                BulletInfo[] bulletInfos = rc.senseNearbyBullets();

                if (bulletInfos.length != 0) {
                    randomMove = false;

                    for (int i = 0; i < bulletInfos.length; i++) {
                        if (Utils.willCollideWithMe(bulletInfos[i], rc)) {
                            if (rc.canMove(bulletInfos[i].dir.rotateLeftDegrees(90)))
                                rc.move(bulletInfos[i].dir.rotateLeftDegrees(90));

                            else if (rc.canMove(bulletInfos[i].dir.rotateRightDegrees(90)))
                                rc.move(bulletInfos[i].dir.rotateRightDegrees(90));

                            else if (rc.canMove(bulletInfos[i].dir))
                                rc.move(bulletInfos[i].dir);

                            else rc.move(bulletInfos[i].dir.opposite());
                        }
                        MapLocation bulletLoc = bulletInfos[i].getLocation();
                        Direction bulletDir = bulletInfos[i].dir;
                    }
                }



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
