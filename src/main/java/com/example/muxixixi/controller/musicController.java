package com.example.muxixixi.controller;

import com.example.muxixixi.koneksi.Database;
import com.example.muxixixi.model.Song;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.util.Duration;

import java.io.File;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

public class musicController implements Initializable {
    @FXML
    private Label songLabel;

    @FXML
    private ComboBox<String> speedComboBox;

    @FXML
    private Slider volumeSlider;

    @FXML
    private ProgressBar songProgressBar;

    @FXML
    private TableView<Song> songTable;
    @FXML
    private TableColumn<Song, String> songNameColumn;

    @FXML
    private ObservableList<Song> songData;

    private ArrayList<File> songs;
    private int songNumber;
    private boolean running;
    private int[] speed = {25,50,75,100,125,150,175,200};
    private Timer timer;
    private TimerTask task;
    private Media media;
    private MediaPlayer mediaPlayer;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        songs = new ArrayList<>();
        songData = FXCollections.observableArrayList();

        try(Connection connection = Database.connect()){
            int userId = AppSession.getCurrentUserId();

            String query = "SELECT nama, path FROM songs WHERE user_id = ?";
            PreparedStatement stmt = connection.prepareStatement(query);
            stmt.setInt(1, userId);
            ResultSet rs = stmt.executeQuery();
            while(rs.next()){
                String name = rs.getString("nama");
                String path = rs.getString("path");
                File file = new File(path);
                if(file.exists()){
                    songs.add(file);
                    songData.add(new Song(name));
                    System.out.println("Loaded from DB: " + name);
                } else {
                    System.out.println("File not found: " + path);
                }

            }
            songTable.setItems(songData);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(!songs.isEmpty()) {
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.setOnError(() -> {
                System.out.println("Media error: " + mediaPlayer.getError().getMessage());
            });
            songLabel.setText(songs.get(songNumber).getName());
        }

        for(int i=0;i<speed.length;i++){
            speedComboBox.getItems().add(Integer.toString(speed[i]) + "%");
        }
        speedComboBox.setOnAction(this::changeSpeed);

        volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1) {
                mediaPlayer.setVolume(volumeSlider.getValue()*0.01);
            }
        });

        songNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        songProgressBar.setStyle("-fx-accent: green;");

        songTable.setOnMouseClicked(event -> {
            Song selectedSong = songTable.getSelectionModel().getSelectedItem();
            if(selectedSong != null) {
                for(int i=0;i<songs.size();i++) {
                    if(songs.get(i).getName().equals(selectedSong.getName())) {
                        songNumber = i;
                        mediaPlayer.stop();
                        if(running) stopTimer();
                        media = new Media(songs.get(songNumber).toURI().toString());
                        mediaPlayer = new MediaPlayer(media);
                        songLabel.setText(songs.get(songNumber).getName());
                        break;
                    }
                }
            }
        });
    }
    @FXML
    public void playMusic() {
        if(mediaPlayer != null) {
            beginTimer();
            changeSpeed(null);
            System.out.println("Playing");
            mediaPlayer.play();
        } else {
            System.out.println("Not Playing");
        }
    }

    @FXML
    public void pauseMusic() {
        if(mediaPlayer != null) {
            stopTimer();
            mediaPlayer.pause();
        } else {
            System.out.println("Not Pause");
        }
    }

    @FXML
    public void resetMusic() {
        songProgressBar.setProgress(0);
        mediaPlayer.seek(Duration.seconds(0));
    }

    @FXML
    public void prevMusic() {
        if(songNumber > 0) {
            songNumber--;
            mediaPlayer.stop();
            if(running) {
                stopTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMusic();
        } else {
            songNumber = songs.size() -1;
            mediaPlayer.stop();
            if(running) {
                stopTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMusic();
        }
    }

    @FXML
    public void nextMusic() {
        if(songNumber < songs.size() -1) {
            songNumber++;
            mediaPlayer.stop();
            if(running) {
                stopTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMusic();
        } else {
            songNumber = 0;
            mediaPlayer.stop();
            if(running) {
                stopTimer();
            }
            media = new Media(songs.get(songNumber).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            songLabel.setText(songs.get(songNumber).getName());
            playMusic();
        }
    }

    @FXML
    public void changeSpeed(ActionEvent e) {
        if(speedComboBox.getValue() == null) {
            mediaPlayer.setRate(1);
        }
        else {
            mediaPlayer.setRate(Integer.parseInt(speedComboBox.getValue().substring(0,speedComboBox.getValue().length()-1))*0.01);
        }

    }

    @FXML
    public void beginTimer() {
        timer = new Timer();
        task = new TimerTask() {
            @Override
            public void run() {
                running = true;
                double current = mediaPlayer.getCurrentTime().toSeconds();
                double end = media.getDuration().toSeconds();
                System.out.println(current/end);
                songProgressBar.setProgress(current/end);
                if(current/end == 1) {
                    stopTimer();
                }
            }
        };
        timer.scheduleAtFixedRate(task, 0, 1000);
    }

    @FXML
    public void stopTimer() {
        running = false;
        timer.cancel();
    }

    @FXML
    public void addSong(ActionEvent e) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Pilih File Lagu");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("MP3", "*.mp3")
        );
        File selectedFile = fileChooser.showOpenDialog(null);
        if(selectedFile != null) {
            boolean exist = songs.stream().anyMatch(song -> song.getName().equals(selectedFile.getName()));
            if(!exist) {
                songs.add(selectedFile);
                songData.add(new Song(selectedFile.getName()));
                try(Connection conn = Database.connect()) {
                   String query = "INSERT INTO songs (nama, path, user_id) VALUES (?,?, ?)";
                   PreparedStatement stmt = conn.prepareStatement(query);
                   stmt.setString(1, selectedFile.getName());
                   stmt.setString(2, selectedFile.getAbsolutePath());
                   stmt.setInt(3, AppSession.getCurrentUserId());
                   stmt.executeUpdate();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                if(mediaPlayer == null) {
                    songNumber = songs.size() - 1;
                    media = new Media(songs.get(songNumber).toURI().toString());
                    mediaPlayer = new MediaPlayer(media);
                    songLabel.setText(songs.get(songNumber).getName());

                }
            } else {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Peringatan");
                alert.setHeaderText("File Sudah Ada");
                alert.setContentText("File dengan nama " + selectedFile.getName() + " sudah ada.");
                alert.showAndWait();
            }
        }
    }

    @FXML
    public void deleteSelectedSong(ActionEvent e) {
        Song selectedSong = songTable.getSelectionModel().getSelectedItem();
        if(selectedSong == null) {
            System.out.println("Tidak ada lagu yang dipilih");
            return;
        }

        try(Connection conn = Database.connect()) {
            String query = "DELETE FROM songs WHERE nama = ? AND user_id = ?";
            PreparedStatement stmt = conn.prepareStatement(query);
            stmt.setString(1, selectedSong.getName());
            stmt.setInt(2, AppSession.getCurrentUserId());
            int rowsDeleted = stmt.executeUpdate();
            if(rowsDeleted > 0) {
                System.out.println("Lagu Berhasil Dihapus");
                for(int i=0;i<songs.size();i++) {
                    if(songs.get(i).getName().equals(selectedSong.getName())) {
                        songs.remove(i);
                        break;
                    }
                }
                songData.remove(selectedSong);
                songTable.refresh();
            } else {
                System.out.println("Lagu Gagal Dihapus");
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
        }
    }
}


