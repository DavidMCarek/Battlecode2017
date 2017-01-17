package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Archon {
    public static void run(RobotController rc) {

        Direction previousDir = null;
        boolean moved = false;

        while (true) {
            try {

                BulletInfo[] bulletInfos = rc.senseNearbyBullets();

                System.out.println(bulletInfos.length);
                if (bulletInfos.length != 0) {

                    for (int i = 0; i < bulletInfos.length; i++) {
                        if (Utils.willCollideWithMe(bulletInfos[i], rc.getLocation(), rc.getType().bodyRadius)) {

                            previousDir = Utils.trySafeMove(i, bulletInfos, 4, rc);

                            if (previousDir != null) {
                                moved = true;
                                break;
                            }
                        }
                    }

                }

                if (!moved) {

                    if (previousDir == null)
                        if (Utils.microAway(rc))

                    // try previous
                    // try random
                    Utils.tryMove(previousDir != null ? previousDir : Utils.randomDirection(), rc);

                }
                
                int bullets = (int)rc.getTeamBullets();

                if (rc.hasRobotBuildRequirements(RobotType.GARDENER))
                    rc.buildRobot(RobotType.GARDENER, previousDir != null ? previousDir : Utils.randomDirection());


                moved = false;

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
