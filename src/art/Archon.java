package art;
import battlecode.common.*;
import art.*;

class Archon {
    static boolean hasHiredGardener = false;

    static void runForever(RobotController rc) throws GameActionException {
        while (true) {
            Direction dir = Movement.randomDirection();

            if (!hasHiredGardener && rc.canHireGardener(dir)) {
                rc.hireGardener(dir);
                hasHiredGardener = true;
            }

            Clock.yield();
        }
    }
}
