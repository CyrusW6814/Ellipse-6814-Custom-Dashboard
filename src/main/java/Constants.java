import edu.wpi.first.math.util.Units;

/* List of almost literally anything that this could need to know/display
- Team Number
- Field Dimensions as pixels scaling with resolution
- Window size (Field Dimensions + 50% of each side so 1.5 x field height up and same down and left and right)
- Phase timers
- Total time
- Status of subsystems and (really small) status of connection to them
    - Status of autoalign
    - Status of intake(s)
    - Status of elevator
    - Status of shooter
    - Status of outtake
- Battery status
- Position
- Indicator when next phase start (flashing light)
 */

public class Constants {
    public static int teamNumber = 6814;
    public static int windowWidth = 900;
    public static int windowHeight = 550;

    public static int mapWidth = 800;
    public static double mapPadding = 100;

    // from constants.java in robot code
    public static final double fieldWidth = 8.069326;
    public static final double fieldLength = 16.540988;
    public static final double robotImgWidth = (mapWidth - mapPadding*2) / fieldLength * Units.inchesToMeters(28);

    // Phase timers 
    // Edit the mainPhaseTimes and phaseTimers per game type
    public static final int totalGameTime = 160;
    public static final int phaseWidths = 90;
    public static final int phaseHeights = 60;
    public static final int[] mainPhaseTimes = {20, 10, 25, 25, 25, 25, 30};
    public static int[] phaseTimeRemaining = {20, 140, 130, 105, 80, 55, 30};
    // These are indexes aka phase number - 1
    public static final int startCopyPhase = 2;
    public static final int endCopyPhase = 5;
}
