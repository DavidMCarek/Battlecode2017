package abra.bots;

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

        int cooldown = 0;

        while (true) {
            try {

                tempDir = Utils.avoidBullets(preferredDir, rc);
                if (tempDir != null) {
                    preferredDir = tempDir;
                    moved = true;
                }

                if (!moved) {
                    tempDir = Utils.microAway(rc);
                    if (tempDir != null) {
                        moved = true;
                        preferredDir = tempDir;
                    }
                }

                if (!moved) {
                    tempDir = Utils.tryMove(preferredDir, rc);
                    if (tempDir != null) {
                        moved = true;
                        preferredDir = tempDir;
                    }
                }

                if (!moved && stuckCount > 5) {
                    tempDir = Utils.tryMove(preferredDir, 5, 36, rc);
                    if (tempDir != null) {
                        moved = true;
                        preferredDir = tempDir;
                    }
                }

                if (rc.hasRobotBuildRequirements(RobotType.GARDENER) && cooldown < 1) {
                    if (moved)
                        if (Utils.tryBuild(preferredDir.opposite(), RobotType.GARDENER, rc)) {
                            cooldown = defaultCooldown + (2 * gardenerCount);
                            gardenerCount++;
                        }
                    else
                        if (Utils.tryBuild(preferredDir.opposite(), 9, 20, RobotType.GARDENER, rc)) {
                            cooldown = defaultCooldown + (2 * gardenerCount);
                            gardenerCount++;
                        }
                }

                if (!moved) {
                    stuckCount++;
                } else {
                    stuckCount = 0;
                }

                if (gardenerCount > 30) {
                    defaultCooldown = 3000;
                }

                if (rc.getTeamBullets() > 1000)
                    rc.donate(100);

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
