package hu.szt.CowRating;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;

import java.io.IOException;

class RateStage extends Stage {

    RateStage(){
        this.setTitle("Értékelés");
        try {
            Parent root = FXMLLoader.load(getClass().getResource("rate.fxml"));
            this.setScene(new Scene(root));
        } catch (IOException e) {
            Controller.createAlert(Alert.AlertType.WARNING, "Hiba", "Probléma adódott az ablak betöltése közben.");
            e.printStackTrace();
        }
    }
}
