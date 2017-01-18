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

                    else if (Utils.preferredMove(preferredDir != null ? preferredDir : Utils.randomDirection(), 8, rc)) {
                        moved = true;
                    }
                }

                moved = false;

                Utils.tryBuild(preferredDir != null ? preferredDir.opposite() : Utils.randomDirection(), 8, RobotType.GARDENER, moved, rc);

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
