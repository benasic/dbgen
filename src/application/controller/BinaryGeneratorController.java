package application.controller;

import application.generator.BinaryGenerator;
import application.generator.Generator;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class BinaryGeneratorController {

    @FXML
    private Label typeLabel;

    @FXML
    private TextField numberOfRandomBytesTextField;

    private BinaryGenerator binaryGenerator;

    @FXML
    private void initialize(){

    }

    public void bindValues(Generator generator, String type){
        binaryGenerator = (BinaryGenerator)generator;
        typeLabel.setText(type.toLowerCase());
        numberOfRandomBytesTextField.textProperty().bindBidirectional(binaryGenerator.randomSizeProperty());

    }

    public void unbindValues(Generator generator){
        binaryGenerator = (BinaryGenerator)generator;
        numberOfRandomBytesTextField.textProperty().unbindBidirectional(binaryGenerator.randomSizeProperty());
    }
}
