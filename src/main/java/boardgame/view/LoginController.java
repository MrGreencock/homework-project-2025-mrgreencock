package boardgame.view;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.tinylog.Logger;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import boardgame.model.PlayerDataHandler;


import java.io.File;
import java.io.IOException;
import java.util.List;


/**
 * This is the controller class of the Login part.
 */
public class LoginController {
    @FXML
    private TextField player1Field;

    @FXML
    private TextField player2Field;

    PlayerDataHandler handler = new PlayerDataHandler();

    @FXML
    private void startGame(ActionEvent event) throws IOException {
        String player1 = player1Field.getText();
        String player2 = player2Field.getText();

        if(!player1.isEmpty() && !player2.isEmpty()) {
            Logger.debug("Player 1: " + player1 + "\nPlayer 2: " + player2);

            handler.savePlayersIfNotExists(player1, player2);

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ui.fxml"));
            Parent root = loader.load();

            BoardGameController controller = loader.getController();
            controller.setPlayers(player1, player2);

            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        }
        else {
            System.out.println("You should give a name to both players");
        }
    }
}
