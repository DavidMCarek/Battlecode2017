package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Gardener {

    private static int lumberjacks = 0;
    private static int scouts = 0;
    private static int soldiers = 0;
    private static int tanks = 0;

    public static void run(RobotController rc) {

        int cooldown = 0;
        int treeCooldown = 0;
        Direction buildDir = Utils.randomDirection();
        Direction preferredDir = Utils.randomDirection();
        Direction tempDir = null;
        int wateringTree = 0;
        boolean settled = false;

        RobotType buildType = RobotType.LUMBERJACK;

        while (true) {
            try {


                if (settled) {
                    TreeInfo[] trees = rc.senseNearbyTrees(3f);

                    if (wateringTree < trees.length && trees.length > 0 && rc.canWater(trees[wateringTree].getLocation()))
                        rc.water(trees[wateringTree].getLocation());

                    if ((treeCooldown < 1) && (trees.length < 5) && tryBuildTree(buildDir, rc)) {
                        treeCooldown = 15 + (3 * trees.length);

                    }

                    if (cooldown < 1 && rc.hasRobotBuildRequirements(buildType)) {
                        if (Utils.tryBuild(buildDir, buildType, rc)) {

                            if (buildType.equals(RobotType.LUMBERJACK))
                                lumberjacks++;
                            else if (buildType.equals(RobotType.SCOUT))
                                scouts++;
                            else if (buildType.equals(RobotType.SOLDIER))
                                soldiers++;
                            else if (buildType.equals(RobotType.TANK))
                                tanks++;

                            cooldown = 15;
                            buildType = unitToBuild();
                        }
                    }
                } else {
                    MapLocation currentLoc = rc.getLocation();

                    tempDir = Utils.trySafeMove(preferredDir, rc);
                    if (tempDir != null) {
                        preferredDir = tempDir;
                    } else {
                        tempDir = Utils.tryMove(preferredDir, rc);
                        if (tempDir != null)
                            preferredDir = tempDir;
                    }

                    RobotInfo[] nearbyBots = rc.senseNearbyRobots();

                    boolean nearbyGardener = false;

                    for (RobotInfo bot : nearbyBots) {
                        if (bot.getTeam() == rc.getTeam() && bot.getType() == RobotType.GARDENER)
                            nearbyGardener = true;
                    }

                    if (!nearbyGardener && rc.onTheMap(currentLoc, 3.5f) &&
                            ((!rc.isCircleOccupiedExceptByThisRobot(currentLoc, 4f))
                            || cooldown < -20))
                        settled = true;
                }

                cooldown--;
                treeCooldown--;
                wateringTree = (wateringTree + 1) % 5;

                Clock.yield();
            }catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    private static RobotType unitToBuild() {

        if (soldiers + lumberjacks < 10) {
            if (soldiers < lumberjacks)
                return RobotType.SOLDIER;
            else
                return RobotType.LUMBERJACK;
        } else {
            if (soldiers + lumberjacks > 3 * scouts) {
                return RobotType.SCOUT;
            } else {
                if (soldiers < lumberjacks)
                    return RobotType.SOLDIER;
                else
                    return RobotType.LUMBERJACK;
            }
        }
    }

    private static boolean tryBuildTree(Direction dir, RobotController rc) throws GameActionException {

        if (rc.canPlantTree(dir.rotateRightDegrees(60))) {
            rc.plantTree(dir.rotateRightDegrees(60));
            return true;
        } else if (rc.canPlantTree(dir.rotateRightDegrees(120))) {
            rc.plantTree(dir.rotateRightDegrees(120));
            return true;
        } else if (rc.canPlantTree((dir.rotateRightDegrees(180)))) {
            rc.plantTree(dir.rotateRightDegrees(180));
            return true;
        } else if (rc.canPlantTree(dir.rotateRightDegrees(240))) {
            rc.plantTree(dir.rotateRightDegrees(240));
            return true;
        } else if (rc.canPlantTree(dir.rotateRightDegrees(300))) {
            rc.plantTree(dir.rotateRightDegrees(300));
            return true;
        }

        return false;
    }
}
