package art;
import battlecode.common.*;
import java.util.*;

public class RobotPlayer {
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {
        RobotType type = rc.getType();
        Globals.init(rc);
        Movement.init(rc);

        try {
            if (type == RobotType.ARCHON) {
                Archon.runForever(rc);
            }
            else if (type == RobotType.GARDENER) {
                Gardener.runForever(rc);
            }
            else if (type == RobotType.SCOUT) {
                Scout.runForever(rc);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
