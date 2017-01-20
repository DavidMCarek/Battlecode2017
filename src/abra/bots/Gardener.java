package abra.bots;

import abra.Utils;
import battlecode.common.*;

public strictfp class Gardener {

    private static int lumberjacks = 0;
    private static int scouts = 0;
    private static int soldiers = 0;
    private static int tanks = 0;

    public static void run(RobotController rc) {

        int turnCount = 1;
        int cooldown = 0;
        Direction preferredDir = null;

        RobotType buildType = RobotType.SCOUT;

        while (true) {
            try {

                if (turnCount > 5) {
                    TreeInfo[] trees = rc.senseNearbyTrees(3f);


                    if (trees.length > 3) {

                        if (cooldown < 1 && Utils.tryBuild(Direction.getNorth(), unitToBuild(), rc))
                            cooldown = 10;

                        rc.water(0);


                        if (rc.hasTreeBuildRequirements()) {
                            tryBuild(rc);
                        }

                    } else {
                        if (rc.hasTreeBuildRequirements()) {
                            tryBuild(rc);
                        }
                    }
                }

                Direction dir = Utils.randomDirection();

                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                }

                Utils.tryMove(Utils.randomDirection(), rc);

                turnCount++;
                cooldown--;

                Clock.yield();
            }catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    private static boolean tryWater() {

        return false;
    }

    private static RobotType unitToBuild() {

        if (scouts + lumberjacks < 10) {
            if (scouts < lumberjacks)
                return RobotType.SCOUT;
            else
                return RobotType.LUMBERJACK;
        } else {
            if (scouts + lumberjacks > 5 * soldiers) {
                return RobotType.SOLDIER;
            } else {
                if (scouts < lumberjacks)
                    return RobotType.SCOUT;
                else
                    return RobotType.LUMBERJACK;
            }
        }
    }

    private static boolean tryBuild(RobotController rc) throws GameActionException {

        Direction dir = Direction.getNorth();

        if (rc.canPlantTree(dir.rotateRightDegrees(60))) {
            rc.plantTree(dir.rotateRightDegrees(60));
            return true;
        } else if (rc.canPlantTree(dir.rotateRightDegrees(120))) {
            rc.plantTree(dir.rotateRightDegrees(180));
            return true;
        } else if (rc.canPlantTree((dir.opposite()))) {
            rc.plantTree(dir.opposite());
            return true;
        } else if (rc.canPlantTree(dir.rotateLeftDegrees(120))) {
            rc.plantTree(dir.rotateLeftDegrees(120));
            return true;
        } else if (rc.canPlantTree(dir.rotateLeftDegrees(60))) {
            rc.plantTree(dir.rotateLeftDegrees(60));
            return true;
        }

        return false;
    }
}
