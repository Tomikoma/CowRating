package hu.szt.CowRating;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

public class Main extends Application {

    private int count = 0;
    private Thread thread;
    static String lastEnar = "";
    static Map<String, Integer> enarRates;
    static ArrayList<String> enarNumbers;


    @Override
    public void start(Stage primaryStage) throws Exception{
        enarRates = new HashMap<>();
        enarNumbers = new ArrayList<>();
        String row;
        try(BufferedReader bufferedReader = new BufferedReader(new FileReader("Fülszámok.csv"))){
            while((row = bufferedReader.readLine()) != null){
                String[] data = row.split(",");
                //if(data[2].equals("13")){
                    enarNumbers.add(data[1].substring(5));
                //}
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        Label lbl = (Label)root.lookup("#enarLbl");
        Button btn = (Button)root.lookup("#btn");
        MediaView mediaView1 = (MediaView)root.lookup("#mediaView1");
        MediaView mediaView2 = (MediaView)root.lookup("#mediaView2");
        Button rateBtn = (Button)root.lookup("#rateBtn");
        rateBtn.setDisable(true);
        btn.setOnAction(e -> {

            if(mediaView1.getMediaPlayer() != null && mediaView2.getMediaPlayer() != null && Controller.enarNumbers.size() > 0) {
                if(rateBtn.isDisable())
                    rateBtn.setDisable(false);
                if (!mediaView1.getMediaPlayer().getStatus().equals(MediaPlayer.Status.PLAYING)) {
                    mediaView1.getMediaPlayer().play();
                    mediaView2.getMediaPlayer().play();
                    btn.setText("Szünet");
                    if (thread == null) {
                        thread = new Thread(() -> {
                            Runnable updater = () -> {
                                count++;
                                int sec = (int) mediaView1.getMediaPlayer().getCurrentTime().toSeconds();
                                System.out.println(sec);
                                System.out.println("COUNT: " + count);
                                if (sec < Controller.enarNumbers.size()) {
                                    String recognizedEnar = Controller.enarNumbers.get(sec);
                                    String enar = null;
                                    if(enarNumbers.contains(recognizedEnar))
                                        enar = recognizedEnar;
                                    else {
                                        for (String en:enarNumbers) {
                                            if (en.substring(0, 4).equals(recognizedEnar.substring(0, 4))) {
                                                enar = en;
                                                break;
                                            }
                                        }
                                        if(enar == null){
                                            enar = recognizedEnar;
                                        }
                                    }
                                    lbl.setText("A detektált fülszám: " + enar);
                                    if (!enar.equals("Semmi"))
                                        lastEnar = enar;
                                }
                            };

                            while (true) {
                                try {
                                    Thread.sleep(1100);
                                } catch (Exception ex){
                                    ex.printStackTrace();
                                }

                                // UI update is run on the Application thread
                                Platform.runLater(updater);
                            }
                        });
                        thread.setDaemon(true);
                        thread.start();
                    } else {
                        thread.resume();
                    }

                } else {
                    mediaView1.getMediaPlayer().pause();
                    mediaView2.getMediaPlayer().pause();
                    btn.setText("Indítás");
                    thread.suspend();
                }
            } else {
                System.out.println("Kell még fájl");
                Controller.createAlert(Alert.AlertType.WARNING,"Hiba", "Valamelyik (szükséges) fájl nincs megnyitva!");
            }
        });
        primaryStage.setTitle("CowRating");
        primaryStage.setScene(new Scene(root, 500,400));
        primaryStage.show();
    }

    @Override
    public void stop(){
        if(enarRates.size()>0){

            try(FileWriter fw = new FileWriter("enarRates.csv")){
                fw.write("Enar,Pont\n");
                enarRates.forEach((k,v) -> {
                    try{
                        fw.write(k + "," + v + "\n");
                    } catch (IOException e) {
                        Controller.createAlert(Alert.AlertType.WARNING, "Hiba", "Hiba történt a fájl írása közben.");
                        e.printStackTrace();
                    }
                });

            } catch (IOException e) {
                Controller.createAlert(Alert.AlertType.WARNING, "Hiba", "Hiba történt a fájl megnyitásakor.");
                e.printStackTrace();
            }
        }
    }



    public static void main(String[] args) {
        launch(args);
    }
}
