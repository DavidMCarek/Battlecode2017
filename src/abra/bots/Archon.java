package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Archon {
    public static void run(RobotController rc) {

        while (true) {
            try {

                boolean randomMove = true;

                BulletInfo[] bulletInfos = rc.senseNearbyBullets();

                System.out.println(bulletInfos.length);
                if (bulletInfos.length != 0) {
                    randomMove = false;

                    for (int i = 0; i < bulletInfos.length; i++) {
                        if (Utils.willCollideWithMe(bulletInfos[i], rc)) {
                            if (rc.canMove(bulletInfos[i].dir.rotateLeftDegrees(90)))
                                rc.move(bulletInfos[i].dir.rotateLeftDegrees(90));

                            else if (rc.canMove(bulletInfos[i].dir.rotateRightDegrees(90)))
                                rc.move(bulletInfos[i].dir.rotateRightDegrees(90));

                            else if (rc.canMove(bulletInfos[i].dir)){
                                rc.move(bulletInfos[i].dir);
                            }

                            else {
                                rc.move(bulletInfos[i].dir.opposite());
                            }
                        }
                    }

                }


                BulletInfo[] nearbyBullets = rc.senseNearbyBullets();
                
                int bullets = (int)rc.getTeamBullets();

                if (rc.hasRobotBuildRequirements(RobotType.GARDENER))
                    rc.buildRobot(RobotType.GARDENER, Utils.randomDirection());

                if (randomMove)
                    rc.move(Utils.randomDirection());

                Clock.yield();

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
