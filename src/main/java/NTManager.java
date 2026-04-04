import java.io.IOException;
import java.util.function.Consumer;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.BooleanSubscriber;
import edu.wpi.first.networktables.DoublePublisher;
import edu.wpi.first.networktables.DoubleSubscriber;
import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEvent;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.networktables.NetworkTablesJNI;
import edu.wpi.first.networktables.StringSubscriber;
import edu.wpi.first.networktables.StructSubscriber;
import edu.wpi.first.util.CombinedRuntimeLoader;
import edu.wpi.first.util.WPIUtilJNI;
import edu.wpi.first.util.struct.Struct;

public class NTManager {
    private static NetworkTableInstance nt;
    private static NetworkTable table;
    private static DoublePublisher targetXPub;
    private static DoublePublisher targetYPub;
    private static BooleanSubscriber isRedAllianceSub;

    private static StructSubscriber<Pose2d> poseSub;
    private static StringSubscriber autoWinnerSub;
    private static DoubleSubscriber matchTimeSub;
    private static BooleanSubscriber isAutonomousSub;
    private static BooleanSubscriber isEnabledSub;

    public static boolean connectToRealRobot = false;

    public static void init() throws IOException
    {
        nt = NetworkTableInstance.getDefault();

        NetworkTablesJNI.Helper.setExtractOnStaticLoad(false);
        WPIUtilJNI.Helper.setExtractOnStaticLoad(false);
        CombinedRuntimeLoader.loadLibraries(NTManager.class, "wpiutiljni", "ntcorejni");
    }

    public static void connectNT(boolean connectToRobot)
    {
        if(nt != null && nt.isConnected())
        {
            nt.stopClient();
            targetXPub.close();
            targetYPub.close();
            isRedAllianceSub.close();
        }
        nt.startClient4("Ellashboard");
        
        if(connectToRobot)
        {
            connectToRealRobot = true;
            nt.setServerTeam(Constants.teamNumber);
            System.out.println("Connecting to robot with team number " + Constants.teamNumber);
        }
        else
        {
            connectToRealRobot = false;
            nt.setServer("127.0.0.1");
            System.out.println("Connecting to simulation on localhost");
        }
        
        table = nt.getTable("Ellashboard");
        
        NetworkTable fmsinfo = nt.getTable("FMSInfo");
        NetworkTable advantageKit = nt.getTable("AdvantageKit");
        targetXPub = table.getDoubleTopic("neutralTargetX").publish();
        targetYPub = table.getDoubleTopic("neutralTargetY").publish();
        isRedAllianceSub = fmsinfo.getBooleanTopic("IsRedAlliance").subscribe(false);

        poseSub = advantageKit.getSubTable("RealOutputs").getSubTable("RobotState").getSubTable("Odometry").getStructTopic("Pose2D", Pose2d.struct).subscribe(Pose2d.kZero);
        autoWinnerSub = fmsinfo.getStringTopic("GameSpecificMessage").subscribe("U");
        matchTimeSub = advantageKit.getSubTable("DriverStation").getDoubleTopic("MatchTime").subscribe(-1);
        isAutonomousSub = advantageKit.getSubTable("DriverStation").getBooleanTopic("Autonomous").subscribe(false);
        isEnabledSub = advantageKit.getSubTable("DriverStation").getBooleanTopic("Enabled").subscribe(false);
    }

    public static boolean getConnection(){
        if(nt.isConnected() == true) return true;
        else return false;
    }

    public static NetworkTableInstance getConVariable(){
        return nt;
    }

    public static void addConnectionListener(Consumer<NetworkTableEvent> listener)
    {
        nt.addConnectionListener(true, event -> { listener.accept(event); });
    }

    public static void publishTarget(double x, double y)
    {
        // the vscode autofill wrote this and its scarily exactly what i wanted
        if(targetXPub != null && targetYPub != null)
        {
            targetXPub.set(x);
            targetYPub.set(y);
        }
    }

    public enum Alliance
    {
        RED, BLUE, UNKNOWN
    }

    public static Alliance getCurrentAlliance()
    {
        if(isRedAllianceSub == null)
        {
            return Alliance.UNKNOWN;
        }
        return isRedAllianceSub.get() ? Alliance.RED : Alliance.BLUE;
    }

    public static Alliance getRedAlliance()
    {
        return Alliance.RED;
    }

    public static Alliance getBlueAlliance()
    {
        return Alliance.BLUE;
    }

    public static Alliance getUnknownAlliance()
    {
        return Alliance.UNKNOWN;
    }

    public static void retryGettingRobotInfo()
    {
        if(isRedAllianceSub != null)
        {
            isRedAllianceSub = nt.getTable("FMSInfo").getBooleanTopic("IsRedAlliance").subscribe(false);
        }
    }

    public static Pose2d getRobotPose()
    {
        if(poseSub != null)
        {
            return poseSub.get();
        }
        return Pose2d.kZero;
    }

    public static Alliance getAutoWinner()
    {
        if(autoWinnerSub != null)
        {
            String winner = autoWinnerSub.get();
            // it says there is an error, ignore dont give a shit
            if(winner.charAt(0) == 'R')
            {
                return Alliance.RED;
            }
            else if(winner.charAt(0) == 'B')
            {
                return Alliance.BLUE;
            }
            else
            {
                return Alliance.UNKNOWN;
            }
        }
        return Alliance.UNKNOWN;
    }

    public static double getCurrentMatchTime()
    {
        if(matchTimeSub != null)
        {
            return matchTimeSub.get();
        }
        return 2000;
    }

    public static boolean isAutonomous()
    {
        if(isAutonomousSub != null)
        {
            return isAutonomousSub.get();
        }
        return false;
    }

    public static boolean isEnabled()
    {
        if(isEnabledSub != null)
        {
            return isEnabledSub.get();
        }
        return false;
    }
}
