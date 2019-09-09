package gui.dashboard;

import database.MySqlConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;

import static gui.login.LoginPageController.id;

public class DashboardController implements Initializable {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;

    @FXML // fx:id="anchorpane"
    private AnchorPane anchorpane; // Value injected by FXMLLoader

    @FXML // fx:id="textfield_directoryName"
    private TextField textfield_directoryName; // Value injected by FXMLLoader

    @FXML // fx:id="listview_directoryDetails"
    private ListView<DirectoryDetails> listview_directoryDetails; // Value injected by FXMLLoader

    @FXML
    void btnSelectDirectoryClicked(ActionEvent event) {
        DirectoryChooser directoryChooser = new DirectoryChooser();
        Stage stage = (Stage) anchorpane.getScene().getWindow();
        File file = directoryChooser.showDialog(stage);
        if(file != null) {
            textfield_directoryName.setText(file.getAbsolutePath());
        }
    }

    @FXML
    void btnAddToList(ActionEvent event) {
        createEntry();
        refreshListView(listview_directoryDetails);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        conn = MySqlConnect.connectDB();
        refreshListView(listview_directoryDetails);
    }

    private void refreshListView(ListView<DirectoryDetails> listview_directoryDetails) {
        listview_directoryDetails.getItems().clear();
        try {
            String sql = "SELECT * FROM entries WHERE entry_user_id = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();
            while(resultSet.next()) {
                String directory = resultSet.getString("entry_absolute_path");
                int create = resultSet.getInt("entry_create_count");
                int modify = resultSet.getInt("entry_modify_count");
                int delete = resultSet.getInt("entry_delete_count");
                DirectoryDetails d = new DirectoryDetails(directory,create,modify,delete);
                listview_directoryDetails.getItems().add(d);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    void createEntry() {
        try {
            String sql = "INSERT INTO entries(entry_absolute_path, entry_user_id, entry_create_count, entry_modify_count, entry_delete_count) VALUES (?,?,?,?,?)";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, textfield_directoryName.getText());
            preparedStatement.setInt(2, id);
            preparedStatement.setInt(3, 0);
            preparedStatement.setInt(4, 0);
            preparedStatement.setInt(5, 0);
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
