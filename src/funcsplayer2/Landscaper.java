package funcsplayer2;

import battlecode.common.GameActionException;

public class Landscaper extends RobotPlayer {
    public Landscaper() {
    }
    static void run() throws GameActionException {
        tryMove(randomDirection());
    }
}
