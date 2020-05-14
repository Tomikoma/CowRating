package hu.szt.CowRating;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


class BarnStage extends Stage {

    BarnStage(){
        Label label = new Label("Istálló száma:");
        Spinner<Integer> spinner = new Spinner<>();
        SpinnerValueFactory<Integer> valueFactory =
                new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 20, 5);
        spinner.setValueFactory(valueFactory);

        VBox root = new VBox();
        HBox hBox = new HBox(label,spinner);
        hBox.setSpacing(10);
        hBox.setAlignment(Pos.CENTER);
        Button okButton = new Button("Ok");
        okButton.setOnAction( e -> {
            Controller.barnNumber = spinner.getValue();
            this.close();
        });
        Button cancelButton = new Button("Mégse");
        cancelButton.setOnAction( e -> {
            this.close();
        });

        HBox actionsHBox = new HBox(okButton,cancelButton);
        actionsHBox.setSpacing(20);
        actionsHBox.setAlignment(Pos.CENTER);

        root.getChildren().addAll(hBox,actionsHBox);
        root.setSpacing(30);
        root.setAlignment(Pos.CENTER);

        Scene scene = new Scene(root, 300, 200);

        this.setTitle("Istálló kiválasztása");
        this.setScene(scene);
    }

}
