package funcsplayer2;
import battlecode.common.*;
import battlecode.world.maps.CentralLake;

import java.util.Random;

import java.awt.*;

public strictfp class RobotPlayer {
    static RobotController rc;

    static Direction[] directions = {
        Direction.NORTH,
        Direction.NORTHEAST,
        Direction.EAST,
        Direction.SOUTHEAST,
        Direction.SOUTH,
        Direction.SOUTHWEST,
        Direction.WEST,
        Direction.NORTHWEST
    };
    static RobotType[] spawnedByMiner = {RobotType.REFINERY, RobotType.VAPORATOR, RobotType.DESIGN_SCHOOL,
            RobotType.FULFILLMENT_CENTER, RobotType.NET_GUN};

    static int turnCount;
    static MapLocation HQLOC = null;
    static int HQELEVATION = Integer.MIN_VALUE;
    static int MINERCOUNT = 0;
    static boolean FOUNDHQ = false;


    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController rc) throws GameActionException {

        // This is the RobotController object. You use it to perform actions from this robot,
        // and to get information on its current status.
        RobotPlayer.rc = rc;

        turnCount = 0;

        //System.out.println("I'm a " + rc.getType() + " and I just got created!");
        while (true) {
            turnCount += 1;
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                // Here, we've separated the controls into a different method for each RobotType.
                // You can add the missing ones or rewrite this into your own control structure.
                System.out.println("I'm a " + rc.getType() + "! Location " + rc.getLocation());
                switch (rc.getType()) {
                    case HQ:                 runHQ();               break;
                    case MINER:              runMiner();            break;
                    case REFINERY:           runRefinery();          break;
                    case VAPORATOR:          runVaporator();         break;
                    case DESIGN_SCHOOL:      runDesignSchool();      break;
                    case FULFILLMENT_CENTER: runFulfillmentCenter(); break;
                    case LANDSCAPER:         runLandscaper();       break;
                    case DELIVERY_DRONE:     runDeliveryDrone();     break;
                    case NET_GUN:            runNetGun();            break;
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                //System.out.println(rc.getType() + " Exception");
                e.printStackTrace();
            }
        }
    }




    static void runRefinery() throws GameActionException {
        // System.out.println("Pollution: " + rc.sensePollution(rc.getLocation()));
    }

    static void runVaporator() throws GameActionException {

    }
    static void runLandscaper() throws GameActionException {
        Random random = new Random();



        if(HQLOC == null) {
            RobotInfo[] robotList = rc.senseNearbyRobots();
            for (RobotInfo robot : robotList) {
                //System.out.println(robot.toString());
                if (robot.type == RobotType.HQ) {
                    HQLOC = robot.getLocation();
                    HQELEVATION = rc.senseElevation(HQLOC);
                    FOUNDHQ = true;
                    System.out.println("i found the hq");
                    //System.out.println("HQLOC: "+HQLOC + " " + HQELEVATION);
                }

            }
        }
        if (!FOUNDHQ) {
            tryMove(randomDirection());
        }
        System.out.println("THe hq position is: " + HQLOC.toString());


        if (HQLOC != null) {
            if (rc.getLocation().distanceSquaredTo(HQLOC) > 13) {
                System.out.println("trying to move closer to HQ");
                //tryMove(randomDirection());
                int xMove = (int) (3* Math.random()+ 1);
                int yMove = (int) (Math.sqrt(13 - xMove*xMove));
                System.out.println(" xmove: " + xMove  + " y move: " + yMove);
                tryMove(HQLOC.translate(xMove, yMove));
                //(int) (3* Math.random()+ 1),(int) (3* Math.random()+ 1));
            }
            Direction hqDir = rc.getLocation().directionTo(HQLOC);
            Direction digDir = Direction.CENTER;
            switch (hqDir) {
                case EAST: digDir = Direction.WEST; break;
                case WEST: digDir = Direction.EAST; break;
                case NORTH: digDir = Direction.SOUTH; break;
                case SOUTH: digDir = Direction.NORTH; break;
                case NORTHEAST: digDir = Direction.SOUTHWEST; break;
                case SOUTHWEST: digDir = Direction.NORTHEAST; break;
                case NORTHWEST: digDir = Direction.SOUTHEAST; break;
                case SOUTHEAST: digDir = Direction.NORTHWEST; break;
            }
            if (rc.isReady() && rc.canDigDirt(digDir)) {
                rc.digDirt(digDir);
                System.out.println("I have " + rc.getDirtCarrying() + " dirt.");
            }
            MapLocation[] nearHQ = new MapLocation[8];
            nearHQ[0] = HQLOC.translate(1,0);
            nearHQ[1] = HQLOC.translate(1,-1);
            nearHQ[2] = HQLOC.translate(0,-1);
            nearHQ[3] = HQLOC.translate(-1,-1);
            nearHQ[4] = HQLOC.translate(-1,0);
            nearHQ[5] = HQLOC.translate(-1,1);
            nearHQ[6] = HQLOC.translate(0,1);
            nearHQ[7] = HQLOC.translate(1,1);

            int movePos = (int) (7*Math.random() + 1);
            if (!rc.isLocationOccupied(nearHQ[movePos]) && rc.isReady()) {
                tryMove(nearHQ[movePos]);
                rc.depositDirt(Direction.CENTER);
            }
//            for (MapLocation loc: nearHQ) {
//                if (!rc.isLocationOccupied(loc) && rc.isReady()) {
//                    tryMove(loc);
//                }
//            }


        }




    }

    static void formFormation() throws GameActionException {
        //"method" for trying to get 3 landscapers to move around in a circle in the 8 blocks around the HQ
        MapLocation[] nearHQ = new MapLocation[8];
        nearHQ[0] = HQLOC.translate(1,0);
        nearHQ[1] = HQLOC.translate(1,-1);
        nearHQ[2] = HQLOC.translate(0,-1);
        nearHQ[3] = HQLOC.translate(-1,-1);
        nearHQ[4] = HQLOC.translate(-1,0);
        nearHQ[5] = HQLOC.translate(-1,1);
        nearHQ[6] = HQLOC.translate(0,1);
        nearHQ[7] = HQLOC.translate(1,1);

        if (rc.getLocation().equals(nearHQ[0])) {
            if (rc.canDigDirt(Direction.EAST) && rc.isReady()) {
                rc.digDirt(Direction.EAST);
                Clock.yield();
            }
            tryMove(Direction.SOUTH);
        } else if (rc.getLocation().equals(nearHQ[1])) {
            if (rc.canDigDirt(Direction.SOUTHEAST) && rc.isReady()) {
                rc.digDirt(Direction.SOUTHEAST);
                Clock.yield();
            }
            tryMove(Direction.WEST);
        } else if (rc.getLocation().equals(nearHQ[2])) {
            if (rc.canDigDirt(Direction.SOUTH) && rc.isReady()) {
                rc.digDirt(Direction.SOUTH);
                Clock.yield();
            }
            tryMove(Direction.WEST);
        } else if (rc.getLocation().equals(nearHQ[3])) {
            if (rc.canDigDirt(Direction.SOUTHWEST) && rc.isReady()) {
                rc.digDirt(Direction.SOUTHWEST);
                Clock.yield();
            }
            tryMove(Direction.NORTH);
        } else if (rc.getLocation().equals(nearHQ[4])) {
            if (rc.canDigDirt(Direction.WEST) && rc.isReady()) {
                rc.digDirt(Direction.WEST);
                Clock.yield();
            }
            tryMove(Direction.NORTH);
        } else if (rc.getLocation().equals(nearHQ[5])) {
            if (rc.canDigDirt(Direction.NORTHWEST) && rc.isReady()) {
                rc.digDirt(Direction.NORTHWEST);
                Clock.yield();
            }
            tryMove(Direction.EAST);
        } else if (rc.getLocation().equals(nearHQ[6])) {
            if (rc.canDigDirt(Direction.NORTH) && rc.isReady()) {
                rc.digDirt(Direction.NORTH);
                Clock.yield();
            }
            tryMove(Direction.EAST);
        } else {
            if (rc.canDigDirt(Direction.NORTHEAST) && rc.isReady()) {
                rc.digDirt(Direction.NORTHEAST);
                Clock.yield();
            }
            tryMove(Direction.SOUTH);
        }
    }

    static void runHQ() throws GameActionException {
        //for (Direction dir : directions)
        if (MINERCOUNT < 6) {
            rc.buildRobot(RobotType.MINER, randomDirection());
            //tryBuild(RobotType.MINER, randomDirection());
            MINERCOUNT++;
        }
    }

    static void runMiner() throws GameActionException {
        //locating the HQ for every miner
        if(HQLOC == null) {
            RobotInfo[] robotList = rc.senseNearbyRobots();
            for (RobotInfo robot : robotList) {
                //System.out.println(robot.toString());
                if (robot.type == RobotType.HQ) {
                    HQLOC = robot.getLocation();
                    HQELEVATION = rc.senseElevation(HQLOC);
                    //System.out.println("HQLOC: "+HQLOC + " " + HQELEVATION);
                }
            }
        }

        //finding where the soup is and then we want to move to the closest one
        MapLocation[] soupLocs = rc.senseNearbySoup();
        MapLocation closestSoup = null;
        int closestSoupDistance = Integer.MAX_VALUE;
        for (MapLocation location: soupLocs) {
            int distance = rc.getLocation().distanceSquaredTo(location);
            if(distance < closestSoupDistance) {
                closestSoup = location;
                closestSoupDistance = distance;
            }
        }
        //System.out.println("closest soup distance" + closestSoupDistance);

        //moving towards soup
//        if (rc.isReady()) {
//            if (closestSoup != null) {
//                tryMove(closestSoup);
//                if (rc.getLocation().distanceSquaredTo(closestSoup) <= 2 && rc.getSoupCarrying() < RobotType.MINER.soupLimit) {
//                    tryMine(rc.getLocation().directionTo(closestSoup));
//                }
//                if (rc.getSoupCarrying() == RobotType.MINER.soupLimit){
//                    System.out.println("Moving towards HQ now and I have " + rc.getSoupCarrying() +  " soup");
//                    System.out.println("This is the HQ LOC: "+ HQLOC.toString());
//                    tryMove(HQLOC);
//                    if (rc.getLocation().distanceSquaredTo(HQLOC) <= 2) {
//                        rc.depositSoup(rc.getLocation().directionTo(HQLOC), RobotType.MINER.soupLimit);
//                    }
//                }
//            } else {
//                tryMove(randomDirection());
//            }
//        }

        if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && rc.getLocation().distanceSquaredTo(HQLOC) <= 2) {
            rc.depositSoup(rc.getLocation().directionTo(HQLOC), RobotType.MINER.soupLimit);
        } else if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && rc.getLocation().distanceSquaredTo(HQLOC) >= 2) {
            tryMove(HQLOC);
        }

        if (closestSoup == null) {
            System.out.println("randomly moving");
            tryMove(randomDirection());
        }

//        } else if (rc.getSoupCarrying() < RobotType.MINER.soupLimit) {
//            tryMove(closestSoup);
//        }
        if (rc.getLocation().distanceSquaredTo(closestSoup) <= 2 && rc.getSoupCarrying() < RobotType.MINER.soupLimit) {
            tryMine(rc.getLocation().directionTo(closestSoup));
        } else if (rc.getLocation().distanceSquaredTo(closestSoup) >= 2 && rc.getSoupCarrying() < RobotType.MINER.soupLimit) {
            tryMove(closestSoup);
        } else if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && rc.getLocation().distanceSquaredTo(HQLOC) <= 2){
            rc.depositSoup(rc.getLocation().directionTo(HQLOC), RobotType.MINER.soupLimit);
        } else if (rc.getSoupCarrying() == RobotType.MINER.soupLimit && rc.getLocation().distanceSquaredTo(HQLOC) >= 2){
            tryMove(HQLOC);
        }

        //building design school

        if (rc.getTeamSoup() > 300) {

            rc.buildRobot(RobotType.DESIGN_SCHOOL, randomDirection());
        }




        //tryBlockchain();
        //tryMove(randomDirection());
    }

    static void runFulfillmentCenter() throws GameActionException {
        for (Direction dir : directions)
            tryBuild(RobotType.DELIVERY_DRONE, dir);
    }


    static void runDesignSchool() throws GameActionException {
        rc.buildRobot(RobotType.LANDSCAPER, randomDirection());
    }


    static void runDeliveryDrone() throws GameActionException {
        Team enemy = rc.getTeam().opponent();
        if (!rc.isCurrentlyHoldingUnit()) {
            // See if there are any enemy robots within striking range (distance 1 from lumberjack's radius)
            RobotInfo[] robots = rc.senseNearbyRobots(GameConstants.DELIVERY_DRONE_PICKUP_RADIUS_SQUARED, enemy);

            if (robots.length > 0) {
                // Pick up a first robot within range
                rc.pickUpUnit(robots[0].getID());
                System.out.println("I picked up " + robots[0].getID() + "!");
            }
        } else {
            // No close robots, so search for robots within sight radius
            tryMove(randomDirection());
        }
    }

    static void runNetGun() throws GameActionException {

    }

    /**
     * Returns a random Direction.
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return directions[(int) (Math.random() * directions.length)];
    }

    /**
     * Returns a random RobotType spawned by miners.
     *
     * @return a random RobotType
     */
    static RobotType randomSpawnedByMiner() {
        return spawnedByMiner[(int) (Math.random() * spawnedByMiner.length)];
    }

    static boolean tryMove() throws GameActionException {
        for (Direction dir : directions)
            if (tryMove(dir))
                return true;
        return false;
//         MapLocation loc = rc.getLocation();
//         if (loc.x < 10 && loc.x < loc.y)
//             return tryMove(Direction.EAST);
//         else if (loc.x < 10)
//             return tryMove(Direction.SOUTH);
//         else if (loc.x > loc.y)
//             return tryMove(Direction.WEST);
//         else
//             return tryMove(Direction.NORTH);
    }

    static void clearHQ() throws GameActionException {

    }

    /**
     * Attempts to move in a given direction.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        // System.out.println("I am trying to move " + dir + "; " + rc.isReady() + " " + rc.getCooldownTurns() + " " + rc.canMove(dir));
        if (rc.isReady() && rc.canMove(dir)) {
            rc.move(dir);
            return true;
        } else {
            //rc.move(randomDirection());
            return false;
        }
    }

    static boolean tryMove(MapLocation destination) throws GameActionException {
        boolean didMove = false;
        //boolean stuck = false;

        MapLocation position = rc.getLocation();



        int deltaX = destination.x - position.x;
        System.out.println("deltax: " + deltaX);
        int deltaY = destination.y - position.y;
        if (deltaX >= 1 && deltaY >= 1 && rc.canMove(Direction.NORTHEAST)) {
            System.out.println("Moving NorthEast");
            tryMove(Direction.NORTHEAST);
            didMove = true;
        }
        if (deltaX >= 1 && deltaY <= -1 && rc.canMove(Direction.SOUTHEAST)) {
            System.out.println("MOving SouthEast");
            tryMove(Direction.SOUTHEAST);
            didMove = true;
        }
        if (deltaX <= -1 && deltaY >= 1 && rc.canMove(Direction.NORTHWEST)) {
            System.out.println("Moving NorthWest");
            tryMove(Direction.NORTHWEST);
            didMove = true;
        }
        if (deltaX <= -1 && deltaY <= -1 && rc.canMove(Direction.SOUTHWEST)) {
            System.out.println("MOving Southwest");
            tryMove(Direction.SOUTHWEST);
            didMove = true;
        }
        if (didMove == false) {
            if (deltaX > 1 && rc.canMove(Direction.EAST)) {
                System.out.println("MOVING EAST");
                rc.move(Direction.EAST);
                didMove = true;

            } else if (deltaX < -1 && rc.canMove(Direction.WEST)) {
                System.out.println("MOVING WEST");
                rc.move(Direction.WEST);
                didMove = true;
            }

            //int deltaY = destination.y - position.y;
            if (deltaY > 1 && rc.canMove(Direction.NORTH)) {
                tryMove(Direction.NORTH);
                didMove = true;
            } else if (deltaY < -1 && rc.canMove(Direction.SOUTH)) {
                tryMove(Direction.SOUTH);
                didMove = true;
            }
        }


        if (!didMove) {
            System.out.println("I DIDNT MOVE");
            tryMove(randomDirection());
        }
        System.out.println("cooldown turns: " + rc.getCooldownTurns());



        return didMove;
    }

    /**
     * Attempts to build a given robot in a given direction.
     *
     * @param type The type of the robot to build
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryBuild(RobotType type, Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canBuildRobot(type, dir)) {
            rc.buildRobot(type, dir);
            return true;
        } else return false;
    }

    /**
     * Attempts to mine soup in a given direction.
     *
     * @param dir The intended direction of mining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMine(Direction dir) throws GameActionException {
        if (rc.isReady()) {
            rc.mineSoup(dir);
            return true;
        } else return false;
    }


    /**
     * Attempts to refine soup in a given direction.
     *
     * @param dir The intended direction of refining
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryRefine(Direction dir) throws GameActionException {
        if (rc.isReady() && rc.canDepositSoup(dir)) {
            rc.depositSoup(dir, rc.getSoupCarrying());
            return true;
        } else return false;
    }


    static void tryBlockchain() throws GameActionException {
        if (turnCount < 3) {
            int[] message = new int[7];
            for (int i = 0; i < 7; i++) {
                message[i] = 123;
            }
            if (rc.canSubmitTransaction(message, 10))
                rc.submitTransaction(message, 10);
        }
        // System.out.println(rc.getRoundMessages(turnCount-1));
    }
}
