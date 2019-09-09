package gui.dashboard;

import StateSaving.StateSaving;
import database.MySqlConnect;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.nio.file.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import static gui.Main.OS;


class DirectoryDetails extends TitledPane {
    Connection conn = null;
    PreparedStatement preparedStatement = null;
    ResultSet resultSet = null;
    private String dir_path;
    private int create_count;
    private int modify_count;
    private int delete_count;
    Label createCount;
    Label modifyCount;
    Label deleteCount;
    public DirectoryDetails(String dir_path, int create_count, int modify_count, int delete_count) {
        this.dir_path = dir_path;
        this.create_count = create_count;
        this.modify_count = modify_count;
        this.delete_count = delete_count;
        this.setExpanded(false);
        createComponent(dir_path, create_count, modify_count, delete_count);
        watchFile(dir_path, createCount, modifyCount, deleteCount);
    }

    private void createComponent(String dir_path, int create_count, int modify_count, int delete_count) {
        VBox pane = new VBox();
        createCount = new Label("New Files/Folders Created : " + create_count);
        modifyCount = new Label("Files/Folders Modified : " + modify_count);
        deleteCount = new Label("Files/Folders Deleted : " + delete_count);
        HBox hb = new HBox();
        hb.setSpacing(20);
        Button openInExplorer = new Button("Open in explorer");
        openInExplorer.setOnAction(e -> {
            try {
                if (OS.contains("win")) {
                    Runtime.getRuntime().exec("start " + dir_path);
                } else if(OS.contains("nix") || OS.contains("nux") || OS.contains("aix")) {
                    Runtime.getRuntime().exec("nautilus " + dir_path);
                }
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });
        Button setState = new Button("Save State");
        setState.setOnAction(e -> {
            StateSaving.saveState(dir_path,"/home/tirth211/Desktop/ToolsAndPrograms/JAVA/FileIntegrityMonitor/SavedStates");
            try {
                String sql = "UPDATE entries SET entry_create_count = 0, entry_delete_count = 0, entry_modify_count = 0 WHERE entry_absolute_path = ?";
                preparedStatement = conn.prepareStatement(sql);
                preparedStatement.setString(1, dir_path);
                preparedStatement.executeUpdate();
                Platform.runLater(() -> {
                    createCount.setText("New Files/Folders Created : 0");
                    deleteCount.setText("Files/Folders Deleted : 0");
                    modifyCount.setText("Files/Folders Modified: 0");
                });
            } catch (SQLException e1) {
                e1.printStackTrace();
            }
        });
        Button revertState = new Button("Revert to previous state");
        revertState.setOnAction(e -> {
            StateSaving.revertState(dir_path);
        });
        Button delete = new Button("Delete  Entry");
        delete.setOnAction(e -> {

        });

        hb.getChildren().addAll(openInExplorer, setState, revertState, delete);
        pane.setSpacing(25);
        pane.getChildren().addAll(createCount, modifyCount, deleteCount, hb);
        this.setText(dir_path);
        this.setContent(pane);
    }

    void watchFile(String dir_path, Label createCount, Label modifyCount, Label deleteCount) {
        Thread t = new Thread(() -> {
            try {
                WatchService watchService = FileSystems.getDefault().newWatchService();
                Path dir = Paths.get(dir_path);
                System.out.println(dir.toAbsolutePath().toString());
                WatchKey watchKey = dir.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);
                conn = MySqlConnect.connectDB();
                while(true) {
                    for (WatchEvent<?> event : watchKey.pollEvents()) {
                        System.out.println("Event Kind : " + event.kind().toString());
                        switch(event.kind().toString()) {
                            case "ENTRY_CREATE" :
                            {
                                try {
                                String sql = "UPDATE entries SET entry_create_count = entry_create_count + 1 WHERE entry_absolute_path = ?";
                                preparedStatement = conn.prepareStatement(sql);
                                preparedStatement.setString(1, dir_path);
                                preparedStatement.executeUpdate();

                                sql = "SELECT entry_create_count FROM entries WHERE entry_absolute_path = ?";
                                preparedStatement = conn.prepareStatement(sql);
                                preparedStatement.setString(1, dir_path);
                                resultSet = preparedStatement.executeQuery();
                                if(resultSet.next()) {
                                    final int count = resultSet.getInt("entry_create_count");
                                    Platform.runLater(() -> {
                                        createCount.setText("New Files/Folders Created : " + count);
                                    });
                                }

                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                                break;

                            case "ENTRY_DELETE" :
                            {
                                try {
                                    String sql = "UPDATE entries SET entry_delete_count = entry_delete_count + 1 WHERE entry_absolute_path = ?";
                                    preparedStatement = conn.prepareStatement(sql);
                                    preparedStatement.setString(1, dir_path);
                                    preparedStatement.executeUpdate();

                                    sql = "SELECT entry_delete_count FROM entries WHERE entry_absolute_path = ?";
                                    preparedStatement = conn.prepareStatement(sql);
                                    preparedStatement.setString(1, dir_path);
                                    resultSet = preparedStatement.executeQuery();
                                    if(resultSet.next()) {
                                        final int count = resultSet.getInt("entry_delete_count");
                                        Platform.runLater(() -> {
                                            deleteCount.setText("Files/Folders Deleted : " + count);
                                        });
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                                break;

                            case "ENTRY_MODIFY" :
                            {
                                try {
                                    String sql = "UPDATE entries SET entry_modify_count = entry_modify_count + 1 WHERE entry_absolute_path = ?";
                                    preparedStatement = conn.prepareStatement(sql);
                                    preparedStatement.setString(1, dir_path);
                                    preparedStatement.executeUpdate();

                                    sql = "SELECT entry_modify_count FROM entries WHERE entry_absolute_path = ?";
                                    preparedStatement = conn.prepareStatement(sql);
                                    preparedStatement.setString(1, dir_path);
                                    resultSet = preparedStatement.executeQuery();
                                    if(resultSet.next()) {
                                        final int count = resultSet.getInt("entry_modify_count");
                                        Platform.runLater(() -> {
                                            modifyCount.setText("Files/Folders Modified : " + count);
                                        });
                                    }
                                } catch (SQLException e) {
                                    e.printStackTrace();
                                }
                            }
                                break;
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        t.start();
    }
}
