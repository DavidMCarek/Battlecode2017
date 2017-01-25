package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Lumberjack {
    public static void run(RobotController rc) {

        boolean moved = false;
        boolean chopped = false;
        Direction preferredDir = Utils.randomDirection();
        Direction tempDir = null;

        while (true) {
            try {

                TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
                for (TreeInfo tree : nearbyTrees) {
                    if (tree.getTeam() != rc.getTeam() && rc.canChop(tree.getLocation())) {
                        rc.chop(tree.getLocation());
                        chopped = true;
                        break;
                    } else if (tree.getTeam() != rc.getTeam()) {
                        tempDir = Utils.tryMove(new Direction(rc.getLocation(), tree.getLocation()), rc);
                        if (tempDir != null) {
                            preferredDir = tempDir;
                            moved = true;
                            break;
                        }

                    }
                }

                RobotInfo[] nearbyBots = rc.senseNearbyRobots();
                for (RobotInfo bot : nearbyBots) {
                    if (bot.getTeam() != rc.getTeam() && rc.canStrike()) {
                        if (rc.getLocation().distanceTo(bot.getLocation()) < (2.5 + bot.getRadius()))
                            rc.strike();
                        else if (!moved && nearbyTrees.length < 1) {
                            tempDir = Utils.tryMove(new Direction(rc.getLocation(), bot.getLocation()), rc);
                            if (tempDir != null) {
                                preferredDir = tempDir;
                                moved = true;
                            }
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
