import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.IOException;

import edu.wpi.first.math.geometry.Pose2d;
import edu.wpi.first.networktables.NetworkTableEvent;

public class Program extends Application
{
    private long lastNTUpdate = 0;
    private double lastPublishedX = 0.5;
    private double lastPublishedY = 0.5;
    private Label[] phaseLabels = new Label[7];
    private int[] phaseTimes = Constants.mainPhaseTimes.clone();
    private int currentPhase = 0;
    private long lastTimerUpdate = 0;
    HBox[] phaseSelect = new HBox[7];
    Pane[] boxes = new Pane[7];

    private enum TargetMode
    {
        DEFAULT, DRIVER_CHOSEN
    }

    public static void main(String[] args) throws IOException{
        NTManager.init();
        launch(args);
    }
    
    private Label connectedToNTLabel = new Label("Not connected");

    @Override
    public void start(Stage stage) {
        NTManager.addConnectionListener(this::updateDisconnection);

        Button robot = new Button("Connect to Robot (" + Constants.teamNumber + ")");
        robot.setOnAction(e -> {
            NTManager.connectNT(true);
            connectedToNTLabel.setText("Connecting to " + Constants.teamNumber + "...");
        });

        Button simulation = new Button("Connect to Simulation");
        simulation.setOnAction(e -> {
            NTManager.connectNT(false);
            connectedToNTLabel.setText("Connecting to simulation...");
        });

        HBox whiteBoxes = new HBox(5);
        whiteBoxes.setPadding(new Insets(-5));
        whiteBoxes.setAlignment(Pos.BOTTOM_CENTER);

        for(int i = 0; i < 7; i++)
        {
            Pane backPart = new Pane();
            backPart.setMaxHeight(Constants.phaseHeights+8);
            backPart.setMaxWidth(Constants.phaseWidths+8);
            backPart.setPrefSize(Constants.phaseWidths+8, Constants.phaseHeights+8);

            backPart.setBackground(new Background(new BackgroundFill(Color.YELLOW, new CornerRadii(1), Insets.EMPTY)));

            HBox phaseBack = new HBox();
            double moveRight = 0;
            double centerSpan = 6 * (Constants.phaseWidths + 5);
            double temp = centerSpan/(i+1);
            double temp2 = centerSpan/(-i+7);
            if(i < 3 && i % 2 == 0) moveRight = temp;
            else if(i < 3 && i % 2 == 1) moveRight = temp + (centerSpan/6);
            else if(i > 3 && i % 2 == 1) moveRight = -1*temp2 - (centerSpan/6);
            else if(i > 3 && i % 2 == 0) moveRight = -1*temp2;
            else moveRight = 0;
            phaseBack.setPadding(new Insets(0, moveRight, 1.75, 0));
            phaseBack.setAlignment(Pos.BOTTOM_CENTER);

            phaseBack.getChildren().add(backPart);
            phaseBack.setMouseTransparent(true);

            phaseBack.setVisible(false);

            phaseSelect[i]=phaseBack;

            Color firstColor = Color.BLUE;
            Color secondColor = Color.RED;

            Pane boxe = new Pane();
            boxe.setMaxHeight(Constants.phaseHeights);
            boxe.setMaxWidth(Constants.phaseWidths);
            boxe.setPrefSize(Constants.phaseWidths, Constants.phaseHeights);

            boxe.setAccessibleText(String.valueOf(Constants.mainPhaseTimes[i]));
            boxe.setFocusTraversable(true);
            boxes[i] = boxe;

            Label boxIn = new Label(String.valueOf(phaseTimes[i]));
            boxIn.setFont(Font.font(30));
            boxIn.setPadding(new Insets(10, 0, 0, Constants.phaseWidths/2.0 - 15));
            phaseLabels[i] = boxIn;
            boxIn.setTextFill(Color.WHITE);
            boxe.getChildren().add(boxIn);
            boxIn.autosize();
            
            whiteBoxes.getChildren().add(boxe);
        }
        whiteBoxes.setMouseTransparent(true);

        HBox threeSecToSwitchFront = new HBox(5);
        HBox threeSecToSwitchBack = new HBox(5);
        Pane light = new Pane();
        light.setMaxHeight(231);
        light.setMaxWidth(31);
        light.setPrefSize(31, 231);
        light.setBackground(new Background(
                    new BackgroundFill(Color.GREY, new CornerRadii(5), Insets.EMPTY)
                ));

        Pane lightBack = new Pane();
        lightBack.setMaxHeight(241);
        lightBack.setMaxWidth(41);
        lightBack.setPrefSize(41, 241);
        lightBack.setBackground(new Background(
                    new BackgroundFill(Color.BLACK, new CornerRadii(5), Insets.EMPTY)
                ));

        threeSecToSwitchBack.getChildren().add(lightBack);
        threeSecToSwitchFront.getChildren().add(light);

        threeSecToSwitchFront.setAlignment(Pos.CENTER_LEFT);
        threeSecToSwitchBack.setAlignment(Pos.CENTER_LEFT);
        threeSecToSwitchFront.setPadding(new Insets(0, 0, 0, 17));
        threeSecToSwitchBack.setPadding(new Insets(0, 0, 0, 12));
        threeSecToSwitchBack.setMouseTransparent(true);
        threeSecToSwitchFront.setMouseTransparent(true);

        Button modeSwitcher = new Button("");

        robot.setMaxHeight(20);
        simulation.setMaxHeight(20);
        connectedToNTLabel.setTextFill(Color.WHITE);
        connectedToNTLabel.setAlignment(Pos.CENTER);
        HBox connectbuttons = new HBox(10);
        connectbuttons.setPadding(new Insets(10, 0, 0, 10));
        connectbuttons.setAlignment(Pos.TOP_CENTER);
        connectbuttons.getChildren().add(robot);
        connectbuttons.getChildren().add(simulation);
        connectbuttons.getChildren().add(connectedToNTLabel);

        Image map = new Image(getClass().getResource("/field.png").toExternalForm());
        ImageView mapView = new ImageView(map);
        
        mapView.setFitWidth(Constants.mapWidth);
        mapView.setPreserveRatio(true);
        mapView.setTranslateY(10);
        mapView.setMouseTransparent(true);
        
        ImageView targetView = new ImageView(new Image(getClass().getResource("/target.png").toExternalForm()));
        targetView.setFitWidth(50);
        targetView.setLayoutX(200);
        targetView.setLayoutY(200);
        targetView.setPickOnBounds(true);
        targetView.setPreserveRatio(true);

        final double[] startMouseX = new double[1];
        final double[] startMouseY = new double[1];
        final double[] startTranslateX = new double[1];
        final double[] startTranslateY = new double[1];

        targetView.setOnMousePressed(e -> {
            startMouseX[0] = e.getSceneX();
            startMouseY[0] = e.getSceneY();

            startTranslateX[0] = targetView.getTranslateX();
            startTranslateY[0] = targetView.getTranslateY();
        });

        targetView.setOnMouseDragged(e -> {
            targetView.setTranslateX(startTranslateX[0] + e.getSceneX() - startMouseX[0]);
            targetView.setTranslateY(startTranslateY[0] + e.getSceneY() - startMouseY[0]);
        });

        ImageView robotView = new ImageView(new Image(getClass().getResource("/robot.png").toExternalForm()));
        robotView.setFitWidth(Constants.robotImgWidth);
        robotView.setPreserveRatio(true);
        
        // publisher loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // only update if it's been at least 20ms since last update
                if (now - lastNTUpdate < 20000000) {
                    return;
                }
                lastNTUpdate = now;

                // NTManager.retryGettingRobotInfo();

                // get target center
                var targetScene = targetView.localToScene(
                    targetView.getBoundsInLocal().getWidth() / 2.0,
                    targetView.getBoundsInLocal().getHeight() / 2.0
                );

                // convert to map coordinates
                var targetOnMap = mapView.sceneToLocal(targetScene);

                // i dont know man chatgpt wrote this lmaooo
                double sourceImageWidth = map.getWidth();
                double sourceImageHeight = map.getHeight();

                double displayedWidth = mapView.getBoundsInLocal().getWidth();
                double displayedHeight = mapView.getBoundsInLocal().getHeight();

                double scaleX = displayedWidth / sourceImageWidth;
                double scaleY = displayedHeight / sourceImageHeight;

                double paddingX = Constants.mapPadding * scaleX;
                double paddingY = Constants.mapPadding * scaleY;

                double usableWidth = displayedWidth - 2.0 * paddingX;
                double usableHeight = displayedHeight - 2.0 * paddingY;

                double normalizedX = (targetOnMap.getX() - paddingX) / usableWidth;
                double normalizedY = (targetOnMap.getY() - paddingY) / usableHeight;

                normalizedX = Math.max(0.0, Math.min(1.0, normalizedX));
                normalizedY = Math.max(0.0, Math.min(1.0, normalizedY));

                

                if(lastPublishedX != normalizedX || lastPublishedY != normalizedY)
                {
                    lastPublishedX = normalizedX;
                    lastPublishedY = normalizedY;
                    NTManager.publishTarget(normalizedX * Constants.fieldLength, (1.0 - normalizedY) * Constants.fieldWidth);
                }

                // robot pose
                Pose2d pose = NTManager.getRobotPose();
                robotView.setTranslateX(pose.getX() / Constants.fieldLength * usableWidth - usableWidth / 2.0);
                robotView.setTranslateY(-(pose.getY() / Constants.fieldWidth * usableHeight - usableHeight / 2.0));

                Color autoColor = Color.rgb(94, 11, 47);
                Color neutralPhaseColor = Color.rgb(78, 30, 107);
                // }
                boxes[0].setBackground(new Background(new BackgroundFill(autoColor, new CornerRadii(5), Insets.EMPTY)));
                boxes[1].setBackground(new Background(new BackgroundFill(neutralPhaseColor, new CornerRadii(5), Insets.EMPTY)));
                boxes[6].setBackground(new Background(new BackgroundFill(neutralPhaseColor, new CornerRadii(5), Insets.EMPTY)));

                NTManager.Alliance autoWinner = NTManager.getAutoWinner();
                boolean isAutonomous = NTManager.isAutonomous();    

                if(NTManager.isEnabled())
                {
                    for(int i = 2; i < 6; i++)
                    {
                        if(autoWinner == NTManager.Alliance.UNKNOWN || isAutonomous)
                        {
                            boxes[i].setBackground(new Background(new BackgroundFill(Color.GREY, new CornerRadii(5), Insets.EMPTY)));
                        }
                        else
                        {
                            Color firstShift = (autoWinner == NTManager.Alliance.RED) ? Color.BLUE : Color.RED;
                            Color secondShift = firstShift.equals(Color.RED) ? Color.BLUE : Color.RED;
                            boxes[i].setBackground(new Background(new BackgroundFill((i % 2 == 0) ? firstShift : secondShift, new CornerRadii(5), Insets.EMPTY)));
                        }
                    }

                    int matchTime = (int)NTManager.getCurrentMatchTime(); 
                    if(isAutonomous)
                    {
                        for(int i = 1; i < 7; i++)
                        {
                            phaseSelect[i].setVisible(false);
                        }

                        phaseLabels[0].setText(Integer.toString(matchTime));
                        phaseLabels[1].setText("10");
                        for(int i = 2; i < 6; i++)
                        {
                            phaseLabels[i].setText("25");
                        }
                        phaseLabels[6].setText("30");

                        phaseSelect[0].setVisible(true);
                    }
                    else
                    {
                        for(int i = 1; i < 7; i++)
                        {
                            phaseSelect[i].setVisible(false);
                        }
                        
                        phaseSelect[0].setVisible(false);
                        phaseLabels[0].setText("0");
                        
                        for(int i = 1; i < 7; i++)
                        {
                            phaseSelect[i].setVisible(false);
                            phaseLabels[i].setText(Integer.toString(clamp(Constants.mainPhaseTimes[i] - (Constants.phaseTimeRemaining[i] - matchTime), 0, Constants.mainPhaseTimes[i])));
                        }

                        int currentphase = 1;
                        for(int phase = 1; phase < 7; phase++)
                        {
                            if(Constants.phaseTimeRemaining[phase] <= matchTime)
                            {
                                currentphase = phase - 1;
                                break;
                            }                            
                        }

                        phaseSelect[currentphase].setVisible(true);
                    }
                }
                else
                {
                    for(int i = 2; i < 6; i++)
                    {
                        boxes[i].setBackground(new Background(new BackgroundFill(Color.GREY, new CornerRadii(5), Insets.EMPTY)));
                    }
                }
            }
        };

        timer.start();
      
        StackPane root = new StackPane(mapView, robotView, connectbuttons, whiteBoxes, targetView, threeSecToSwitchBack, threeSecToSwitchFront, 
            phaseSelect[0], phaseSelect[1], phaseSelect[2], phaseSelect[3], phaseSelect[4], phaseSelect[5], phaseSelect[6]);

        StackPane.setAlignment(connectbuttons, Pos.TOP_LEFT);
        StackPane.setAlignment(whiteBoxes, Pos.BOTTOM_CENTER);
        for(int j = 0; j < 6; j++) phaseSelect[j].toFront();
        whiteBoxes.toFront();
        threeSecToSwitchBack.toFront();
        threeSecToSwitchFront.toFront();

        StackPane.setMargin(whiteBoxes, new Insets(0, 0, 10, 0));
        root.setBackground(new Background(new BackgroundFill(Color.rgb(41, 41, 48), CornerRadii.EMPTY, Insets.EMPTY)));

        stage.setTitle("Ellashboard 2026");  
        stage.setScene(new Scene(root, Constants.windowWidth, Constants.windowHeight));
        stage.show();
    }

    public void updateDisconnection(NetworkTableEvent event)
    {
        if(event.getInstance().isConnected())
        {
            Platform.runLater(()->{
                connectedToNTLabel.setText("Connected to " + (NTManager.connectToRealRobot ? "robot (" + Constants.teamNumber + ")" : "simulation"));
            });
        }
    }

    private int clamp(int value, int min, int max)
    {
        return Math.max(min, Math.min(max, value));
    }
}