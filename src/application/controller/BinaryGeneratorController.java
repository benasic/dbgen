package application.controller;

import application.generator.BinaryGenerationType;
import application.generator.BinaryGenerator;
import application.generator.Generator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.List;

public class BinaryGeneratorController {

    @FXML
    private Label typeLabel;

    @FXML
    private ToggleGroup binaryToggleGroup;

    @FXML
    private RadioButton randomBytesRadioButton;

    @FXML
    private RadioButton imageRadioButton;

    @FXML
    private TextField numberOfRandomBytesTextField;

    @FXML
    private Button imagePathButton;


    private BinaryGenerator binaryGenerator;

    private FileChooser imageChooser = new FileChooser();


    private ChangeListener<Toggle> toggleChangeListener = new ChangeListener<Toggle>() {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            switch ((BinaryGenerationType) newValue.getUserData()) {
                case BINARY:
                    numberOfRandomBytesTextField.disableProperty().set(false);
                    imagePathButton.disableProperty().set(true);
                    binaryGenerator.setBinaryGenerationType(BinaryGenerationType.BINARY);
                    break;
                case BLOB:
                    numberOfRandomBytesTextField.disableProperty().set(true);
                    imagePathButton.disableProperty().set(false);
                    binaryGenerator.setBinaryGenerationType(BinaryGenerationType.BLOB);
                    break;
            }
        }
    };



    @FXML
    private void initialize(){
        randomBytesRadioButton.setUserData(BinaryGenerationType.BINARY);
        imageRadioButton.setUserData(BinaryGenerationType.BLOB);
        imageChooser.setTitle("Select images ...");
        imageChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.gif"));

        imagePathButton.setOnAction(
                e -> {
                    List<File> list =
                            imageChooser.showOpenMultipleDialog(imagePathButton.getScene().getWindow());
                    if (list != null) {
                        binaryGenerator.setImageList(list);
                    }
                });
    }

    public void bindValues(Generator generator, String type){
        binaryGenerator = (BinaryGenerator)generator;
        typeLabel.setText(type.toLowerCase());
        numberOfRandomBytesTextField.textProperty().bindBidirectional(binaryGenerator.randomSizeProperty());
        binaryToggleGroup.selectedToggleProperty().addListener(toggleChangeListener);

        BinaryGenerationType binaryGenerationType = binaryGenerator.getBinaryGenerationType();
        switch (binaryGenerationType){
            case BINARY:
                binaryToggleGroup.selectToggle(randomBytesRadioButton);
                break;
            case BLOB:
                binaryToggleGroup.selectToggle(imageRadioButton);
                break;
        }

    }

    public void unbindValues(Generator generator){
        binaryGenerator = (BinaryGenerator)generator;
        numberOfRandomBytesTextField.textProperty().unbindBidirectional(binaryGenerator.randomSizeProperty());
        binaryToggleGroup.selectedToggleProperty().removeListener(toggleChangeListener);
    }
}
