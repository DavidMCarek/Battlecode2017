package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Archon {
    public static void run(RobotController rc) {

        Direction preferredDir = null;
        boolean moved = false;

        while (true) {
            try {

                // preferredDir gets set in the avoid bullet method if robot moves
                if (Utils.avoidBullets(preferredDir != null ? preferredDir : Utils.randomDirection(), rc))
                    moved = true;

                if (!moved) {
                    if (Utils.microAway(preferredDir, rc))
                        moved = true;

                    else if (Utils.tryMove(preferredDir != null ? preferredDir : Utils.randomDirection(), rc)) {
                        moved = true;
                    }
                }

                if (rc.hasRobotBuildRequirements(RobotType.GARDENER) && rc.getRobotCount() < 20) {
                    if (moved)
                        Utils.tryBuild(preferredDir != null ? preferredDir.opposite() : Utils.randomDirection(), RobotType.GARDENER, rc);
                    else
                        Utils.tryBuild(preferredDir != null ? preferredDir.opposite() : Utils.randomDirection(), 9, 20, RobotType.GARDENER, rc);
                }

                moved = false;
                Clock.yield();


            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
}
