package database;

import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class MySqlConnect {
    static Connection conn;
    public static Connection connectDB(){
        try {
            conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/file-integrity-monitor" , "root" , "");
        } catch (SQLException e) {
            new Alert(Alert.AlertType.ERROR , "Connection Unsuccessful" , ButtonType.OK);
            return null ;
        } catch (Exception e){
            new Alert(Alert.AlertType.ERROR , "Something else went wrong" , ButtonType.OK);
            return null ;
        }
        return conn;
    }
}
