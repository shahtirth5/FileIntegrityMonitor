package gui.login;

import StateSaving.CryptoUtils;
import com.jfoenix.controls.*;
import database.MySqlConnect;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;


public class LoginPageController implements Initializable{
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    public static int id;
    public static String username;

    @FXML
    JFXButton btnLogin;

    @FXML
     JFXTextField tfName;

    @FXML
    JFXPasswordField tfPassword;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        conn = MySqlConnect.connectDB();
    }

    private boolean processLogin(String username, String password) {
        try {
            String sql = "SELECT * FROM users WHERE username = ? AND password = ?";
            preparedStatement = conn.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
            if(resultSet.next()) {
                id = resultSet.getInt("id");
                username = resultSet.getString("username");
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    private void clearFields(){
        tfName.setText("");
        tfPassword.setText("");
    }

    @FXML
    private void btnLoginClicked(ActionEvent ae) throws IOException {
        String username = tfName.getText();
        String password = tfPassword.getText();
        if(username.equals("") || password.equals("")) {
            new Alert(Alert.AlertType.INFORMATION , "Name/Password Incorrect" , ButtonType.OK).show();
        }
        String hashedPassword = CryptoUtils.hashSHA256(password);
        Stage mainStage = (Stage) ((Node) ae.getSource()).getScene().getWindow();
        if(processLogin(username, hashedPassword)) {
            Parent p = FXMLLoader.load(getClass().getResource("../dashboard/dashboard.fxml"));
            Scene sc = new Scene(p, 800, 600);
            mainStage.setScene(sc);
        } else {
            new Alert(Alert.AlertType.INFORMATION , "Name/Password Incorrect" , ButtonType.OK).show();
            clearFields();
        }
    }

}

