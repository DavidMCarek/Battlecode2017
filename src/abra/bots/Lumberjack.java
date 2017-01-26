package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Lumberjack {
    public static void run(RobotController rc) {

        boolean moved = false;
        boolean chopped = false;
        Direction preferredDir = Utils.randomDirection();
        Direction tempDir = null;
        Direction previousDir;
        int lineCount = 0;
        int stuckCount = 0;

        while (true) {
            try {

                previousDir = preferredDir;

                if (stuckCount > 5) {
                    tempDir = Utils.tryMove(preferredDir, 5, 36, rc);
                    if (tempDir != null) {
                        preferredDir = tempDir;
                        moved = true;
                        stuckCount = 0;
                    }
                }

                RobotInfo[] nearbyBots = rc.senseNearbyRobots();
                for (RobotInfo bot : nearbyBots) {
                    if (bot.getTeam() != rc.getTeam() && rc.canStrike()) {
                        if (rc.getLocation().distanceTo(bot.getLocation()) < (2.5 + bot.getRadius()))
                            rc.strike();
                        else if (!moved) {
                            tempDir = Utils.tryMove(new Direction(rc.getLocation(), bot.getLocation()), rc);
                            if (tempDir != null) {
                                preferredDir = tempDir;
                                moved = true;
                            }
                        }
                    }
                }

                TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
                for (TreeInfo tree : nearbyTrees) {
                    if (tree.getTeam().equals(Team.NEUTRAL) && rc.canShake(tree.getLocation()))
                        rc.shake(tree.getLocation());

                    if (tree.getTeam() != rc.getTeam() && rc.canChop(tree.getLocation())) {
                        rc.chop(tree.getLocation());
                        chopped = true;
                        break;
                    } else if (tree.getTeam() != rc.getTeam() && !moved) {
                        tempDir = Utils.tryMove(new Direction(rc.getLocation(), tree.getLocation()), rc);
                        if (tempDir != null) {
                            preferredDir = tempDir;
                            moved = true;
                            break;
                        }
                    }
                }


                if (!chopped && !moved) {
                    tempDir = Utils.tryMove(preferredDir, rc);
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

                if (!moved && !chopped)
                    stuckCount++;
                else if (moved)
                    stuckCount = 0;

                moved = false;
                chopped = false;
                Clock.yield();
            }catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }

    private boolean safeToStrike(RobotController rc) {

        return false;
    }
}
