package hu.szt.CowRating;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.util.*;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Class.forName("com.mysql.jdbc.Driver");
        Parent root = FXMLLoader.load(getClass().getResource("gui.fxml"));
        primaryStage.setTitle("CowRating");
        primaryStage.setOnCloseRequest(getOnCloseRequestEventHandler());
        primaryStage.setScene(new Scene(root, 500,400));
        primaryStage.show();
    }


    private EventHandler<WindowEvent> getOnCloseRequestEventHandler(){
        return event -> {
            if (Controller.isSaved || Controller.enarRates.isEmpty())
                return;
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("Információ");
            alert.setHeaderText(null);
            alert.setContentText("Az értékelés nem lett elmentve. Biztos, hogy kilépsz?");

            Optional<ButtonType> result = alert.showAndWait();
            if (result.get() != ButtonType.OK)
                event.consume();
        };
    }

    public static void main(String[] args) {
        launch(args);
    }
}
