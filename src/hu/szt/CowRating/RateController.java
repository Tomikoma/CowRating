package hu.szt.CowRating;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;


import java.net.URL;
import java.util.ResourceBundle;

public class RateController implements Initializable {

    @FXML
    private Label rateLbl;

    @FXML
    private Slider rateSld;

    @FXML
    private TextField rateTf;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        rateTf.setText(Main.lastEnar);
    }

    public void rate(){
        //System.out.println(rateTf.getText() + ", " + (int)rateSld.getValue());
        if(rateTf.getText().equals("")){
            Controller.createAlert(Alert.AlertType.WARNING,"Hiba", "Meg kell adni az enart!");
        } else if (rateTf.getText().length() != 5){
            Controller.createAlert(Alert.AlertType.WARNING,"Hiba", "Az enar 5 számjegyből áll!");
        } else {
            try {
                Integer.parseInt(rateTf.getText());
                Main.enarRates.put(rateTf.getText(),(int)rateSld.getValue());
            } catch (NumberFormatException e){
                Controller.createAlert(Alert.AlertType.WARNING,"Hiba", "Az enar 5 számjegyből áll!");
            }
        }
    }

    public void onSliderChanged(){
        int value = (int) rateSld.getValue();
        rateLbl.setText(Integer.toString(value));
    }


}
