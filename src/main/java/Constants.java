import edu.wpi.first.math.util.Units;

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
    public static final int phaseWidths = 90;
    public static final int phaseHeights = 60;
    public static final int[] mainPhaseTimes = {20, 10, 25, 25, 25, 25, 30};
    public static final int[] phaseTimeRemaining = {0, 140, 130, 105, 80, 55, 30};
}
