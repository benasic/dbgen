package application.controller;

import application.model.ColumnInfo;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import org.uncommons.maths.random.MersenneTwisterRNG;

import java.util.Random;

public class DatabaseSettingsController {

    @FXML
    private VBox vBox;

    @FXML
    private VBox vBoxSlider;

    @FXML
    private VBox vBoxValue;

    @FXML
    private VBox vBoxAllowGeneration;

    @FXML
    private Button randomButton;

    @FXML
    private TextField valueForAllTextField;

    @FXML
    private Button setButton;

    @FXML
    private ToggleGroup toggleGroup;

    @FXML
    private ToggleButton trueToggleButton;

    @FXML
    private ToggleButton falseToggleButton;

    private ObservableList<ColumnInfo> settingsList;
    private Random random = new MersenneTwisterRNG();

    @FXML
    private void initialize(){
        randomButton.setOnAction(event ->
            settingsList.forEach(columnInfo -> {
                columnInfo.getTableGenerationSettings().setNumberOfDataToGenerate(random.nextInt(200000));
            })
        );

        setButton.setOnAction(event ->
                settingsList.forEach(columnInfo ->
                    columnInfo.getTableGenerationSettings().setNumberOfDataToGenerate(Integer.parseInt(valueForAllTextField.getText()))
                )
        );
        trueToggleButton.setUserData(true);
        falseToggleButton.setUserData(false);

        toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null){
                if((boolean) newValue.getUserData()){
                    settingsList.forEach(columnInfo -> {
                        columnInfo.getTableGenerationSettings().setAllowGeneration(true);
                    });
                }
                else {
                    settingsList.forEach(columnInfo -> {
                        columnInfo.getTableGenerationSettings().setAllowGeneration(false);
                    });
                }
            }
        });
    }

    public void init(ObservableList<ColumnInfo> settingsList){
        this.settingsList = settingsList;

        vBox.getChildren().clear();
        vBoxSlider.getChildren().clear();
        vBoxValue.getChildren().clear();
        vBoxAllowGeneration.getChildren().clear();

        this.settingsList.forEach(columnInfo -> {
            Label label = new Label(columnInfo.getTableName());
            HBox hBox = new HBox();
            hBox.setPadding(new Insets(10, 0, 0, 0));
            hBox.setPrefHeight(50);
            hBox.getChildren().add(label);
            vBox.getChildren().add(hBox);

            Slider slider = new Slider(0, 500000, 1000);
            slider.setPrefHeight(250);
            slider.valueProperty().bindBidirectional(columnInfo.getTableGenerationSettings().numberOfDataToGenerateProperty());
            slider.setShowTickLabels(true);
            slider.setMajorTickUnit(500000);
            HBox hBox1 = new HBox();
            hBox1.setPrefHeight(50);
            hBox1.getChildren().add(slider);
            HBox.setHgrow(slider, Priority.ALWAYS);
            vBoxSlider.getChildren().add(hBox1);

            TextField textField = new TextField();
            textField.setDisable(true);
            textField.textProperty().bindBidirectional(columnInfo.getTableGenerationSettings().numberOfDataToGenerateStringProperty());
            HBox hBox2 = new HBox();
            hBox2.setPrefHeight(50);
            hBox2.getChildren().add(textField);
            vBoxValue.getChildren().add(hBox2);

            CheckBox checkBox = new CheckBox("Enable generation");
            checkBox.selectedProperty().bindBidirectional(columnInfo.getTableGenerationSettings().allowGenerationProperty());
            HBox hBox3 = new HBox();
            hBox3.setPrefHeight(50);
            hBox3.getChildren().add(checkBox);
            vBoxAllowGeneration.getChildren().add(hBox3);
        });
    }
}
