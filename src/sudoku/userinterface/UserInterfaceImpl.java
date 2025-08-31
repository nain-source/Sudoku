package sudoku.userinterface;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.util.Duration;
import sudoku.problemdomain.Coordinates;
import sudoku.problemdomain.SudokuGame;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.util.HashMap;

public class UserInterfaceImpl implements IUserInterfaceContract.View, EventHandler<KeyEvent> {

    private final Stage stage;
    private VBox root;

    private Pane boardPane;
    private StackPane boardPaneWrapper;

    private Label movesLabel;
    private Label timeLabel;
    private final Button resetButton;

    private HashMap<Coordinates, SudokuTextField> textFieldCoordinates;

    private IUserInterfaceContract.EventListener listener;

    private static final double WINDOW_Y = 800;
    private static final double WINDOW_X = 668;
    private static final double BOARD_PADDING = 50;
    private static final double BOARD_X_AND_Y = 576;

    private static final Color WINDOW_BACKGROUND_COLOR = Color.rgb(0, 150, 136);
    private static final Color BOARD_BACKGROUND_COLOR = Color.rgb(224, 242, 241);
    private static final String SUDOKU = "Sudoku";

    public UserInterfaceImpl(Stage stage) {
        this.stage = stage;
        this.root = new VBox(10);
        root.setAlignment(Pos.TOP_CENTER);

        boardPane = new Pane();
        boardPane.setPrefSize(BOARD_X_AND_Y + BOARD_PADDING * 2, BOARD_X_AND_Y + BOARD_PADDING * 2);

        boardPaneWrapper = new StackPane(boardPane);
        boardPaneWrapper.setAlignment(Pos.CENTER);
        boardPaneWrapper.setPrefWidth(Double.MAX_VALUE);

        movesLabel = new Label("Moves: 0");
        timeLabel = new Label("Time: 0s");

        // Style labels for better visibility
        movesLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");
        timeLabel.setStyle("-fx-font-size: 24px; -fx-text-fill: white; -fx-font-weight: bold;");

        this.resetButton = new Button("Reset");
        this.textFieldCoordinates = new HashMap<>();

        initializeUserInterface();
    }

    @Override
    public void setListener(IUserInterfaceContract.EventListener listener) {
        this.listener = listener;
    }

    public void initializeUserInterface() {
        root.getChildren().clear();

        drawTitle(root);

        HBox scoreboard = new HBox(40); // increased spacing
        scoreboard.setAlignment(Pos.CENTER);
        scoreboard.getChildren().addAll(movesLabel, timeLabel);
        root.getChildren().add(scoreboard);

        root.getChildren().add(boardPaneWrapper);

        drawSudokuBoard(boardPane);
        drawTextFields(boardPane);
        drawGridLines(boardPane);
        configureResetButton();

        root.getChildren().add(resetButton);
        root.setStyle("-fx-background-color: rgb(0,150,136);");

        ScrollPane scrollPane = new ScrollPane(root);
        scrollPane.setFitToWidth(true);

        Scene scene = new Scene(scrollPane, WINDOW_X, WINDOW_Y);
        scene.setFill(WINDOW_BACKGROUND_COLOR);

        stage.setScene(scene);
        stage.setTitle("Sudoku");
        stage.setMaximized(false);
        stage.setResizable(false);

        stage.show();
    }

    private void configureResetButton() {
        resetButton.setStyle("-fx-font-size: 16px; -fx-background-color: #FF5722; -fx-text-fill: white;");
        resetButton.setOnAction(e -> {
            if (listener != null) {
                listener.onResetClick();
            }
        });
    }

    private void drawTextFields(Pane boardPane) {
        final int xOrigin = 50;
        final int yOrigin = 50;
        final int xAndYDelta = 64;

        for (int xIndex = 0; xIndex < 9; xIndex++) {
            for (int yIndex = 0; yIndex < 9; yIndex++) {
                int x = xOrigin + xIndex * xAndYDelta;
                int y = yOrigin + yIndex * xAndYDelta;
                SudokuTextField tile = new SudokuTextField(xIndex, yIndex);

                styleSudokuTile(tile, x, y);
                tile.setOnKeyPressed(this);

                textFieldCoordinates.put(new Coordinates(xIndex, yIndex), tile);
                boardPane.getChildren().add(tile);
            }
        }
    }

    private void styleSudokuTile(SudokuTextField tile, double x, double y) {
        Font numberFont = new Font(32);
        tile.setFont(numberFont);
        tile.setAlignment(Pos.CENTER);
        tile.setLayoutX(x);
        tile.setLayoutY(y);
        tile.setPrefHeight(64);
        tile.setPrefWidth(64);
        tile.setBackground(Background.EMPTY);
    }

    private void drawGridLines(Pane boardPane) {
        int xAndY = 114;
        int index = 0;
        while (index < 8) {
            int thickness = (index == 2 || index == 5) ? 3 : 2;

            Rectangle verticalLine = getLine(xAndY + 64 * index, BOARD_PADDING, BOARD_X_AND_Y, thickness);
            Rectangle horizontalLine = getLine(BOARD_PADDING, xAndY + 64 * index, thickness, BOARD_X_AND_Y);

            boardPane.getChildren().addAll(verticalLine, horizontalLine);
            index++;
        }
    }

    public Rectangle getLine(double x, double y, double height, double width) {
        Rectangle line = new Rectangle();
        line.setX(x);
        line.setY(y);
        line.setHeight(height);
        line.setWidth(width);
        line.setFill(Color.BLACK);
        return line;
    }

    private void drawSudokuBoard(Pane boardPane) {
        Rectangle boardBackground = new Rectangle();
        boardBackground.setX(BOARD_PADDING);
        boardBackground.setY(BOARD_PADDING);
        boardBackground.setWidth(BOARD_X_AND_Y);
        boardBackground.setHeight(BOARD_X_AND_Y);
        boardBackground.setFill(BOARD_BACKGROUND_COLOR);
        boardPane.getChildren().add(boardBackground);
    }

    private void drawTitle(VBox root) {
        Text title = new Text(SUDOKU);
        title.setFill(Color.WHITE);
        Font titleFont = new Font(43);
        title.setFont(titleFont);
        root.getChildren().add(0, title);
    }

    private Timeline timer;
    private int secondsElapsed = 0;

    public void startTimer() {
        if (timer != null) timer.stop();
        secondsElapsed = 0;
        timeLabel.setText("Time: 0s");

        timer = new Timeline();
        timer.getKeyFrames().add(
                new KeyFrame(Duration.seconds(1), e -> {
                    secondsElapsed++;
                    timeLabel.setText("Time: " + secondsElapsed + "s");
                })
        );
        timer.setCycleCount(Timeline.INDEFINITE);
        timer.play();
    }

    public void stopTimer() {
        if (timer != null) {
            timer.stop();
        }
    }

    public void updateMoves(int moves) {
        movesLabel.setText("Moves: " + moves);
    }

    @Override
    public void updateSquare(int x, int y, int input) {
        SudokuTextField tile = textFieldCoordinates.get(new Coordinates(x, y));
        String value = (input == 0) ? "" : Integer.toString(input);
        tile.textProperty().setValue(value);
    }

    @Override
    public void updateBoard(SudokuGame game) {
        for (int xIndex = 0; xIndex < 9; xIndex++) {
            for (int yIndex = 0; yIndex < 9; yIndex++) {
                TextField tile = textFieldCoordinates.get(new Coordinates(xIndex, yIndex));
                int cellValue = game.getCopyOfGridState()[xIndex][yIndex];
                String value = (cellValue == 0) ? "" : Integer.toString(cellValue);
                tile.setText(value);

                if (game.isCellFixed(xIndex, yIndex)) {
                    tile.setStyle("-fx-opacity: 0.8;");
                    tile.setDisable(true);
                } else {
                    tile.setStyle("-fx-opacity: 1;");
                    tile.setDisable(false);
                }
            }
        }
    }

    @Override
    public void showDialog(String message) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION, message, ButtonType.OK);
        dialog.showAndWait();
        if (dialog.getResult() == ButtonType.OK)
            listener.onDialogClick();
    }

    @Override
    public void showError(String message) {
        Alert dialog = new Alert(Alert.AlertType.ERROR, message, ButtonType.OK);
        dialog.showAndWait();
    }

    @Override
    public void handle(KeyEvent event) {
        if (event.getEventType() == KeyEvent.KEY_PRESSED) {
            if (event.getText().matches("[0-9]")) {
                int value = Integer.parseInt(event.getText());
                handleInput(value, event.getSource());
            } else if (event.getCode() == KeyCode.BACK_SPACE) {
                handleInput(0, event.getSource());
            } else {
                ((TextField) event.getSource()).setText("");
            }
        }
        event.consume();
    }

    private void handleInput(int value, Object source) {
        listener.onSudokuInput(
                ((SudokuTextField) source).getX(),
                ((SudokuTextField) source).getY(),
                value
        );
    }
}
