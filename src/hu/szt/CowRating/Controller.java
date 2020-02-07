package hu.szt.CowRating;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import java.util.concurrent.TimeUnit;

public class Controller implements Initializable {

    @FXML
    private BorderPane borderPane;

    @FXML
    private MediaView mediaView1;

    @FXML
    private MediaView mediaView2;

    @FXML
    private MenuItem video1;

    @FXML
    private MenuItem video2;

    @FXML
    private Button resetButton;

    static List<String> enarNumbers;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        enarNumbers = new ArrayList<>();
        //mediaView1.setFitWidth(400);
        //mediaView2.setFitWidth(400);

    }

    public void chooseVideo(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Videó megnyitása");
        Stage stage = (Stage)borderPane.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("MP4","*.mp4", "*.MP4"));
        File file = fileChooser.showOpenDialog(stage);
        MenuItem menuItem = (MenuItem) event.getSource();
        System.out.println(menuItem.getId());

        if (file != null) {
            if(menuItem.equals(video1)) {
                System.out.println(file.toURI().toString());
                Media media = new Media(file.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                System.out.println(mediaPlayer.getCurrentTime());
                System.out.println(mediaPlayer.getStartTime());
                System.out.println(mediaPlayer.getTotalDuration());
                System.out.println(mediaPlayer.getStopTime());
                mediaPlayer.setStartTime(new Duration(1000));
                mediaPlayer.setVolume(0);
                mediaView1.setMediaPlayer(mediaPlayer);
                resetButton.setDisable(false);

                DoubleProperty width = mediaView1.fitWidthProperty();
                DoubleProperty height = mediaView1.fitHeightProperty();
            } else if (menuItem.equals(video2)) {
                System.out.println(file.toURI().toString());
                Media media = new Media(file.toURI().toString());
                MediaPlayer mediaPlayer = new MediaPlayer(media);
                mediaPlayer.setVolume(0);
                mediaView2.setMediaPlayer(mediaPlayer);
                resetButton.setDisable(false);
            }
        }
    }

    public void chooseFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Videó megnyitása");
        Stage stage = (Stage)borderPane.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("TXT","*.txt", "*.TXT"));
        File file = fileChooser.showOpenDialog(stage);
        try {
            BufferedReader br = new BufferedReader(new FileReader(file));
            String st;
            while ((st = br.readLine()) != null){
                String[] splittedStr = st.split(" ");
                if (splittedStr.length == 1){
                    enarNumbers.add("Semmi");
                } else {
                    enarNumbers.add(splittedStr[1]);
                }
            }
            resetButton.setDisable(false);
            System.out.println(enarNumbers.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void rate(){
        Stage stage = new RateStage();
        stage.show();
    }

    public void reset(){
        mediaView1.setMediaPlayer(null);
        mediaView2.setMediaPlayer(null);
        enarNumbers = new ArrayList<>();
        resetButton.setDisable(true);

    }

    public void about(){
        createAlert(Alert.AlertType.INFORMATION,"Információ","Ez az alkalmazás a tehenek osztályozásának folyamatát próbálja gyorsítani.");
    }

    static void createAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
