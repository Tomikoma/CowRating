package hu.szt.CowRating;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

class RateStage extends Stage {

    RateStage(){
        this.setTitle("Értékelés");
        VBox root = new VBox();
        VBox containerVBox = new VBox();
        HBox hBox = new HBox();

        Label subtitle = new Label("Tehén értékelése");
        TextField rateField = new TextField(Controller.lastEnar);
        Slider slider = new Slider(1,5,3);
        Label sliderLabel = new Label("3");
        slider.setOnMouseDragged( e -> {
            int value = (int) slider.getValue();
            sliderLabel.setText(Integer.toString(value));
        });
        Button rateButton = new Button("Értékelj");
        rateButton.setOnAction(event -> {
            if(rateField.getText().equals("")){
                Controller.createAlert(Alert.AlertType.WARNING,"Hiba", "Meg kell adni az enart!");
            } else if (rateField.getText().length() != 5){
                Controller.createAlert(Alert.AlertType.WARNING,"Hiba", "Az enar 5 számjegyből áll!");
            } else {
                try {
                    Integer.parseInt(rateField.getText());
                    Controller.enarRates.put(rateField.getText(),(int)slider.getValue());
                    Controller.isSaved = false;
                    this.close();
                } catch (NumberFormatException e){
                    Controller.createAlert(Alert.AlertType.WARNING,"Hiba", "Az enar 5 számjegyből áll!");
                }
            }
        });

        containerVBox.getChildren().addAll(slider,sliderLabel);
        hBox.getChildren().addAll(rateField,containerVBox);
        root.getChildren().addAll(subtitle,hBox,rateButton);

        root.setSpacing(20);
        root.setPadding(new Insets(10));
        root.setAlignment(Pos.TOP_CENTER);
        hBox.setAlignment(Pos.CENTER);
        hBox.setSpacing(20);
        containerVBox.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 300, 150);

        this.setScene(scene);
    }
}
