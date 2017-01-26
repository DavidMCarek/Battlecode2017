package abra.bots;

import abra.MoveUtils;
import abra.Utils;
import battlecode.common.*;

public strictfp class Archon {
    public static void run(RobotController rc) {

        Direction preferredDir = Utils.randomDirection();
        Direction tempDir = null;
        boolean moved = false;
        int stuckCount = 0;
        int gardenerCount = 0;
        int defaultCooldown = 30;
        Direction previousDir;
        int lineCount = 0;

        int cooldown = 0;

        while (true) {
            try {

                previousDir = preferredDir;

                if (!moved) {
                    tempDir = MoveUtils.microAway(rc);
                    if (tempDir != null) {
                        moved = true;
                        preferredDir = tempDir;
                    }
                }

                if (!moved) {
                    tempDir = MoveUtils.trySafeMove(preferredDir, rc);
                    if (tempDir != null) {
                        moved = true;
                        preferredDir = tempDir;
                    }
                }

                if (!moved) {
                    tempDir = MoveUtils.tryMove(preferredDir, rc);
                    if (tempDir != null) {
                        moved = true;
                        preferredDir = tempDir;
                    }
                }

                if (!moved && stuckCount > 5) {
                    tempDir = MoveUtils.tryMove(preferredDir, 5, 36, rc);
                    if (tempDir != null) {
                        moved = true;
                        preferredDir = tempDir;
                    }
                }

                RobotInfo[] nearbyBots = rc.senseNearbyRobots();
                int nearbyGardenerCount = 0;
                for (RobotInfo bot : nearbyBots) {
                    if (bot.getType().equals(RobotType.GARDENER))
                        nearbyGardenerCount++;
                }

                if (nearbyGardenerCount < 3 && rc.hasRobotBuildRequirements(RobotType.GARDENER) && cooldown < 1) {
                    if (moved)
                        if (Utils.tryBuild(preferredDir.opposite(), RobotType.GARDENER, rc)) {
                            cooldown = defaultCooldown + (3 * gardenerCount);
                            gardenerCount++;
                        }
                    else
                        if (Utils.tryBuild(preferredDir.opposite(), 9, 20, RobotType.GARDENER, rc)) {
                            cooldown = defaultCooldown + (3 * gardenerCount);
                            gardenerCount++;
                        }
                }

                if (!moved) {
                    stuckCount++;
                } else {
                    stuckCount = 0;
                }

                if (gardenerCount > 15) {
                    defaultCooldown = 3000;
                }

                if (preferredDir.equals(previousDir))
                    lineCount++;
                else
                    lineCount = 0;

                if (lineCount > 10) {
                    lineCount = 0;
                    preferredDir = Utils.randomDirection();
                }

                cooldown--;
                moved = false;
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
}
