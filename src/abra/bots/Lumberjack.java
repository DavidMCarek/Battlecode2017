package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Lumberjack {
    public static void run(RobotController rc) {

        boolean moved = false;
        Direction preferredDir = Utils.randomDirection();
        Direction tempDir = null;

        while (true) {
            try {

                TreeInfo[] nearbyTrees = rc.senseNearbyTrees();
                for (TreeInfo tree : nearbyTrees) {
                    if (tree.getTeam() != rc.getTeam() && rc.canChop(tree.getLocation())) {
                        rc.chop(tree.getLocation());
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

                    }
                }

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
