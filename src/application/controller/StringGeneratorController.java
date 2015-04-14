package application.controller;

import application.generator.Generator;
import application.generator.StringGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

public class StringGeneratorController {

    @FXML
    private TextField generatorTextField;

    private StringGenerator stringGenerator;

    @FXML
    private void initialize(){
        //generatorTextField.setText("neki teks");
    }

    public void setGenerator(Generator generator){
        stringGenerator = (StringGenerator)generator;
        generatorTextField.textProperty().bindBidirectional(this.stringGenerator.getGeneratorStringProperty());
    }
}
