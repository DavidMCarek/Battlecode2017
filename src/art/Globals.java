package art;
import battlecode.common.*;
import java.util.*;

public class Globals {
    static RobotController rc;
    static Team myTeam;
    static Team enemyTeam;
    static Random random;

    static int roundNum;
    static MapLocation myLocation;

    static void init(RobotController rcIN) {
        rc = rcIN;
        myTeam = rc.getTeam();
        enemyTeam = myTeam.opponent();
        roundNum = 0;
        myLocation = rc.getLocation();
        random = new Random(rc.getID());
    }

    static void update() {
        roundNum = rc.getRoundNum();
        myLocation = rc.getLocation();
    }
}
