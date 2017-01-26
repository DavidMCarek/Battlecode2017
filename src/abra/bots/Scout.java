package abra.bots;

import abra.MoveUtils;
import abra.Utils;
import battlecode.common.*;

public strictfp class Scout {

    public static void run(RobotController rc) {

        Direction preferredDir = Utils.randomDirection();
        Direction tempDir = null;
        boolean fired = false;
        boolean moved = false;
        Direction previousDir;
        int lineCount = 0;
        int stuckCount = 0;

        while (true) {
            try {

                previousDir = preferredDir;

                if (stuckCount > 5) {
                    tempDir = MoveUtils.tryMove(preferredDir, 5, 36, rc);
                    if (tempDir != null) {
                        preferredDir = tempDir;
                        moved = true;
                        stuckCount = 0;
                    }
                }

                TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
                for (TreeInfo tree : nearbyTrees) {
                    if (tree.getTeam().equals(Team.NEUTRAL) && rc.canShake(tree.getLocation()))
                        rc.shake(tree.getLocation());
                }

                RobotInfo[] nearbyBots = rc.senseNearbyRobots();
                boolean[] enemiesCanSee = Utils.setCanSeeNearbyRobots(nearbyBots, rc);
                tempDir = Utils.trySafeShot(nearbyBots, enemiesCanSee, 1, rc);
                if (tempDir != null) {
                    preferredDir = tempDir;
                    fired = true;
                }

                if (!moved && fired) {
                    tempDir = MoveUtils.trySafeMove(preferredDir.opposite(), rc);
                    if (tempDir != null) {
                        preferredDir = tempDir;
                        moved = true;
                    }
                }

                if (!moved && !fired) {
                    tempDir = MoveUtils.trySafeMove(preferredDir, rc);
                    if (tempDir != null) {
                        preferredDir = tempDir;
                        moved = true;
                    }
                }

                if (preferredDir.equals(previousDir))
                    lineCount++;
                else
                    lineCount = 0;

                if (lineCount > 10) {
                    lineCount = 0;
                    preferredDir = Utils.randomDirection();
                }

                if (!moved)
                    stuckCount++;
                else
                    stuckCount = 0;

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
