package boardgame.view;

import boardgame.model.BoardGameModel;
import boardgame.model.PlayerDataHandler;
import boardgame.model.Position;
import boardgame.model.Square;
import common.util.TwoPhaseMoveSelector;
import game.State;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import jfxutils.JFXTwoPhaseMoveSelector;
import org.tinylog.Logger;

import java.io.IOException;

/**
 * This is the controller class of the BoardGame part.
 */

public class BoardGameController {
    @FXML
    private GridPane board;

    public Label player1Label;
    public Label player2Label;

    private final BoardGameModel model = new BoardGameModel();

    private final JFXTwoPhaseMoveSelector<Position> selector = new JFXTwoPhaseMoveSelector<>(model);

    @FXML
    private void initialize() {
        for (var i = 0; i < board.getRowCount(); i++) {
            for (var j = 0; j < board.getColumnCount(); j++) {
                var square = createSquare(i, j);
                board.add(square, j, i);
            }
        }
        selector.phaseProperty().addListener(this::showSelectionPhaseChange);

        model.nextPlayerProperty().addListener((obs, oldPlayer, newPlayer) -> updateLabelStyles(newPlayer));
        updateLabelStyles(model.getNextPlayer());

    }

    private void updateLabelStyles(State.Player nextPlayer) {
        if (nextPlayer == State.Player.PLAYER_1) {
            player1Label.setStyle("-fx-font-weight: bold");
            player2Label.setStyle("-fx-font-weight: normal");
        } else {
            player1Label.setStyle("-fx-font-weight: normal");
            player2Label.setStyle("-fx-font-weight: bold");
        }
    }

    private StackPane createSquare(int row, int col)  {
        var square = new StackPane();
        square.getStyleClass().add("square");
        Node shape;
        if(model.squareProperty(row, col).get() == Square.BLACK) {
            square.setBackground(new Background(new BackgroundFill(Color.BLACK, CornerRadii.EMPTY, Insets.EMPTY)));
        }
        else {
            var circle = new Circle(50);
            circle.fillProperty().bind(createSquareBinding(model.squareProperty(row, col)));
            square.getChildren().add(circle);
            square.setOnMouseClicked(this::handleMouseClick);
        }
        return square;
    }

    @FXML
    private void handleMouseClick(MouseEvent event) {
        var square = (StackPane) event.getSource();

        Integer rowIndex = GridPane.getRowIndex(square);
        Integer colIndex = GridPane.getColumnIndex(square);

        int row = rowIndex != null ? rowIndex : 0;
        int col = colIndex != null ? colIndex : 0;

        Logger.info("Click on square ({},{})", row, col);
        var pos = new Position(row, col);

        if (selector.getPhase() == TwoPhaseMoveSelector.Phase.SELECT_TO
                && selector.getFrom() != null
                && selector.getFrom().equals(pos)) {
            Logger.info("Deselection of square ({},{})", row, col);
            hideSelection(pos);
            selector.reset();
            return;
        }

        selector.select(pos);

        if (selector.isReadyToMove()) {
            selector.makeMove();

            if (model.isGameOver()) {
                Logger.info("Game over situation");
                showGameOverScreen();
            }
        }
    }


    private ObjectBinding<Paint> createSquareBinding(ReadOnlyObjectProperty<Square> squareProperty) {
        return new ObjectBinding<Paint>() {
            {
                super.bind(squareProperty);
            }
            @Override
            protected Paint computeValue() {
                return switch (squareProperty.get()) {
                    case NONE -> Color.TRANSPARENT;
                    case RED -> Color.RED;
                    case BLUE -> Color.BLUE;
                    case BLACK -> Color.BLACK;
                };
            }
        };
    }

    private void showSelectionPhaseChange(ObservableValue<? extends TwoPhaseMoveSelector.Phase> value, TwoPhaseMoveSelector.Phase oldPhase, TwoPhaseMoveSelector.Phase newPhase) {
        switch (newPhase) {
            case SELECT_FROM -> {}
            case SELECT_TO -> showSelection(selector.getFrom());
            case READY_TO_MOVE -> hideSelection(selector.getFrom());
        }
    }

    private void showSelection(Position position) {
        var square = getSquare(position);
        square.getStyleClass().add("selected");
    }

    private void hideSelection(Position position) {
        var square = getSquare(position);
        square.getStyleClass().remove("selected");
    }

    private void showGameOverScreen() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/gameOver.fxml"));
            Parent root = loader.load();
            GameOver controller = loader.getController();

            /* File handling */
            String winnerName = model.getWinnerName();
            Logger.info("The winner is :"+ winnerName);
            String loserName = model.getLoserName();
            Logger.info("The loser is: "+loserName);

            controller.setWinner(winnerName);


            PlayerDataHandler handler = new PlayerDataHandler();
            handler.updatePoints(winnerName, loserName);
            handler.savePlayersIfNotExists(winnerName, loserName);

            Scene scene = new Scene(root);
            Stage stage = (Stage) board.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            Logger.error(e, "Error with buffering gameOver.fxml");
        }
    }

    private StackPane getSquare(Position position) {
        for (var child : board.getChildren()) {
            if (GridPane.getRowIndex(child) == position.row() && GridPane.getColumnIndex(child) == position.col()) {
                return (StackPane) child;
            }
        }
        throw new AssertionError();
    }

    public void setPlayers(String player1, String player2) {
        player1Label.setText(player1);
        player2Label.setText(player2);
        model.setPlayers(player1, player2);
    }
}
