import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.concurrent.Worker;
import javafx.scene.Scene;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;

import java.io.IOException;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTableEvent;

public class Program extends Application {

    private WebEngine engine;

    public static void main(String[] args) throws IOException {
        NTManager.init();
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        WebView view = new WebView();
        engine = view.getEngine();

        engine.load(getClass().getResource("/index.html").toExternalForm());

        engine.getLoadWorker().stateProperty().addListener((obs, oldState, newState) -> {
            if (newState == Worker.State.SUCCEEDED) {

                JSObject window = (JSObject) engine.executeScript("window");
                window.setMember("java", this);

                engine.executeScript(
                    "window.CONSTANTS = JSON.parse(window.java.getConstantsJSON());"
                );
            }
        });

        stage.setScene(new Scene(view, Constants.windowWidth, Constants.windowHeight));
        stage.setTitle("Ellashboard 2026");
        stage.show();

        NTManager.addConnectionListener(this::onConnectionUpdate);

        startLoop();
    }

    private void startLoop() {
        AnimationTimer timer = new AnimationTimer() {
            private long last = 0;

            @Override
            public void handle(long now) {
                if (now - last < 20000000) return;
                last = now;

                Pose2d pose = NTManager.getRobotPose();

                double x = pose.getX() / Constants.fieldLength;
                double y = pose.getY() / Constants.fieldWidth;

                engine.executeScript("updateRobot(" + x + "," + y + ")");
            }
        };
        timer.start();
    }

    public void connectRobot() {
        NTManager.connectNT(true);
    }

    public void connectSim() {
        NTManager.connectNT(false);
    }

    private void onConnectionUpdate(NetworkTableEvent e) {
        boolean connected = e.getInstance().isConnected();

        engine.executeScript("updateConnection(" + connected + ")");
    }

    public String getConstantsJSON() {
        return "{"
            + "\"teamNumber\":" + Constants.teamNumber + ","
            + "\"windowWidth\":" + Constants.windowWidth + ","
            + "\"windowHeight\":" + Constants.windowHeight + ","
            + "\"mapWidth\":" + Constants.mapWidth + ","
            + "\"mapPadding\":" + Constants.mapPadding + ","
            + "\"fieldWidth\":" + Constants.fieldWidth + ","
            + "\"fieldLength\":" + Constants.fieldLength + ","
            + "\"robotImgWidth\":" + Constants.robotImgWidth + ","
            + "\"phaseWidths\":" + Constants.phaseWidths + ","
            + "\"phaseHeights\":" + Constants.phaseHeights + ","
            + "\"mainPhaseTimes\":" + arrayToJSON(Constants.mainPhaseTimes) + ","
            + "\"phaseTimeRemaining\":" + arrayToJSON(Constants.phaseTimeRemaining)
            + "}";
    }

    private String arrayToJSON(int[] arr) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < arr.length; i++) {
            sb.append(arr[i]);
            if (i < arr.length - 1) sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }
}