package boardgame.view;


import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import boardgame.model.PlayerDataHandler.*;

public class Scoreboard {
    @FXML
    public TableView<PlayerData> tableView;

    @FXML
    public TableColumn<PlayerData, String> name;

    @FXML
    public TableColumn<PlayerData, Integer> eloPoint;

    @FXML
    private void initialize() {
        TableColumn<PlayerData, Number> serialNumberColumn = new TableColumn<>("No.");

        serialNumberColumn.setCellFactory(col -> new TableCell<PlayerData, Number>() {
            @Override
            protected void updateItem(Number item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setText(null);
                } else {
                    setText(String.valueOf(getIndex() + 1));
                }
            }
        });
        tableView.getColumns().add(0, serialNumberColumn);
        name.setCellValueFactory(new PropertyValueFactory<>("name"));
        eloPoint.setCellValueFactory(new PropertyValueFactory<>("eloPoint"));
        ObservableList<PlayerData> observableList = FXCollections.observableArrayList();
        observableList.addAll(PlayerData.getAll());
        tableView.setItems(observableList);
    }
}
