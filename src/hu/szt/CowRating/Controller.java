package hu.szt.CowRating;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;

import java.io.*;
import java.net.URL;
import java.sql.*;
import java.util.*;

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

    @FXML
    private Label enarLbl;

    @FXML
    private Button startStopButton;

    @FXML
    private Button rateBtn;


    static Map<String, Integer> enarRates;
    static int barnNumber = -1;
    static String lastEnar = "";
    static boolean isSaved = false;

    private ArrayList<String> enarNumbers;
    private UpdaterThread thread;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        enarNumbers = new ArrayList<>();
        enarRates = new HashMap<>();
        rateBtn.setDisable(true);
    }


    public void chooseVideo(ActionEvent event){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Videó megnyitása");
        Stage stage = (Stage)borderPane.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("MP4","*.mp4", "*.MP4"));
        File file = fileChooser.showOpenDialog(stage);
        MenuItem menuItem = (MenuItem) event.getSource();
        System.out.println(menuItem.getId());
        if (file == null)
            return;
        if(menuItem.equals(video1)) {
            Media media = new Media(file.toURI().toString());
            setMediaPlayer(mediaView1,media);
            resetButton.setDisable(false);
        } else if (menuItem.equals(video2)) {
            Media media = new Media(file.toURI().toString());
            setMediaPlayer(mediaView2,media);
            resetButton.setDisable(false);
        }
    }

    public void chooseFile(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Fülszámok megnyitása");
        Stage stage = (Stage)borderPane.getScene().getWindow();
        fileChooser.getExtensionFilters().addAll(new ExtensionFilter("TXT","*.txt", "*.TXT"));
        File file = fileChooser.showOpenDialog(stage);
        if (file == null)
            return;
        enarNumbers = new ArrayList<>();
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
        } catch (Exception e) {
            createAlert(Alert.AlertType.ERROR,"Hiba","Hiba történt a fülszámok beolvasása közben!");
        }

    }

    public void save(){
        if (enarRates.isEmpty()){
            createAlert(Alert.AlertType.INFORMATION,"Információ","Egy érték sem lett még elmentve.");
        } else {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Mentés");
            Stage stage = (Stage)borderPane.getScene().getWindow();
            fileChooser.getExtensionFilters().addAll(new ExtensionFilter("CSV","*.csv", "*.CSV"));
            File file = fileChooser.showSaveDialog(stage);
            if (file != null){
                if (!file.toString().substring(file.toString().length()-4).equals(".csv"))
                    file = new File(file.toString()+ ".csv");
                try(FileWriter fw = new FileWriter(file)) {
                    fw.write("Enar,Pont\n");
                    for (Map.Entry<String, Integer> entry : enarRates.entrySet()) {
                        String k = entry.getKey();
                        Integer v = entry.getValue();
                        fw.write(k + "," + v + "\n");
                    }
                    isSaved = true;
                } catch (IOException e) {
                    createAlert(Alert.AlertType.ERROR,"Hiba","Probléma adódott az értékek elmentése közben!");
                }

            }

        }
    }


    public void playStopVideo(){
        if(mediaView1.getMediaPlayer() != null && mediaView2.getMediaPlayer() != null && enarNumbers.size() > 0) {
            rateBtn.setDisable(false);
            enarLbl.setVisible(true);
            if (!mediaView1.getMediaPlayer().getStatus().equals(MediaPlayer.Status.PLAYING)) {
                mediaView1.getMediaPlayer().play();
                mediaView2.getMediaPlayer().play();
                startStopButton.setText("Szünet");

                if (thread == null || !thread.isAlive()) {
                    thread = new UpdaterThread();
                    thread.setDaemon(true);
                    thread.start();
                }
            } else {
                mediaView1.getMediaPlayer().pause();
                System.out.println("status: " + mediaView1.getMediaPlayer().getStatus());
                mediaView2.getMediaPlayer().pause();
                startStopButton.setText("Indítás");


            }
        } else {
            StringBuilder builder = new StringBuilder();
            if (mediaView1.getMediaPlayer() == null)
                builder.append("\t- Hiányzik az első videó!\n");
            if (mediaView2.getMediaPlayer() == null)
                builder.append("\t- Hiányzik a második videó!\n");
            if (enarNumbers.isEmpty())
                builder.append("\t- Nem lett meg adva megfelelő txt fájl");
            createAlert(Alert.AlertType.WARNING,"Hiba", "Problémák adódtak:\n" + builder.toString());
        }
    }

    private class UpdaterThread extends Thread {
        private volatile boolean isRunning = true;
        @Override
        public void run(){
            Runnable updater = () -> {
                if (!isRunning)
                    return;
                int sec = (int) mediaView1.getMediaPlayer().getCurrentTime().toSeconds();
                if(mediaView1.getMediaPlayer().getTotalDuration().toMillis() <= mediaView1.getMediaPlayer().getCurrentTime().toMillis()) {
                    setMediaPlayer(mediaView1,mediaView1.getMediaPlayer().getMedia());
                    setMediaPlayer(mediaView2,mediaView2.getMediaPlayer().getMedia());
                    startStopButton.setText("Indítás");
                    rateBtn.setDisable(true);
                    enarLbl.setVisible(false);
                    this.stopThread();
                }
                if (sec < enarNumbers.size()) {
                    String recognizedEnar = enarNumbers.get(sec);
                    String enar = getEnarFromDB(recognizedEnar);
                    if (enar == null)
                        enar = recognizedEnar;
                    enarLbl.setText("A detektált fülszám: " + enar);
                    if (!enar.equals("Semmi"))
                        lastEnar = enar;
                    lastEnar = !enar.equals("Semmi") ? enar : "";
                }
            };

            while (isRunning) {
                try {
                    Thread.sleep(1100);
                } catch (Exception ex){
                    createAlert(Alert.AlertType.ERROR,"Hiba","Hiba történt a fülszám frissítése közben!");
                    System.exit(-1);
                }

                // UI update is run on the Application thread
                Platform.runLater(updater);
            }
        }

        void stopThread() {
            isRunning = false;
        }
    }

    private String getEnarFromDB(String enar){
        if (barnNumber == -1)
            return null;
        String enarFromDB = null;
        String connectionString = "jdbc:mysql://localhost:3306/cow";
        try(Connection conn = DriverManager.getConnection(connectionString,"dev","Object==0;")){
            PreparedStatement statement = conn.prepareStatement("SELECT R1SZELETSZ FROM R1" +
                    " WHERE R1SZKSORSZ = ? AND R1SZELETSZ LIKE ? LIMIT 1");
            statement.setInt(1, barnNumber);
            statement.setString(2,"%" + enar);
            ResultSet result = statement.executeQuery();
            if (result.next()){
                enarFromDB = result.getString(1);
            }
            if(enarFromDB == null){
                statement.setString(2,"%" + enar.substring(0,4) + "_");
                result = statement.executeQuery();
                if (result.next()){
                    enarFromDB = result.getString(1);
                }
            }
            if(enarFromDB == null){
                statement = conn.prepareStatement("SELECT R1SZELETSZ FROM R1 WHERE R1SZELETSZ LIKE ? LIMIT 1");
                statement.setString(1,"%" + enar.substring(0,4) + "_");
                result = statement.executeQuery();
                if (result.next()){
                    enarFromDB = result.getString(1);
                }
            }
        } catch (SQLException e) {
            return null;
        }
        return enarFromDB == null ? null : enarFromDB.substring(enarFromDB.length()-5);
    }


    public void rate(){
        Stage stage = new RateStage();//??
        stage.show();
    }

    public void chooseBarn(){
        Stage stage = new BarnStage();
        stage.show();
    }

    public void reset(){
        thread.stopThread();
        mediaView1.setMediaPlayer(null);
        mediaView2.setMediaPlayer(null);
        enarNumbers = new ArrayList<>();
        startStopButton.setText("Indítás");
        resetButton.setDisable(true);
        rateBtn.setDisable(true);
        enarLbl.setVisible(false);

    }

    private static void setMediaPlayer(MediaView mView, Media media ){
        MediaPlayer mediaPlayer = new MediaPlayer(media);
        mediaPlayer.setVolume(0);
        mView.setMediaPlayer(mediaPlayer);
    }

//    public void about(){
//        createAlert(Alert.AlertType.INFORMATION,"Információ","Ez az alkalmazás a tehenek osztályozásának folyamatát próbálja gyorsítani.");
//    }

    static void createAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
