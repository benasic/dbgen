package application.controller;

import application.generator.Generator;
import application.generator.NumberGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NumberGeneratorController {

    @FXML
    private TextField generatorTextField;

    private NumberGenerator numberGenerator;

    @FXML
    private void initialize(){
        Pattern p = Pattern.compile("\\D");
        generatorTextField.textProperty().addListener((observable, oldValue, newValue) ->
        {
            Matcher m = p.matcher(newValue);
            if(m.find()){
                generatorTextField.textProperty().setValue(m.replaceAll(""));
            }
        });
    }

    public void unbindValues(Generator generator){
        numberGenerator = (NumberGenerator)generator;
        generatorTextField.textProperty().unbindBidirectional(numberGenerator.getGeneratorNumberProperty());
    }

    public void setGenerator(Generator generator){
        numberGenerator = (NumberGenerator)generator;
        NumberStringConverter numberStringConverter = new NumberStringConverter(){
            @Override
            protected NumberFormat getNumberFormat() {
                return NumberFormat.getIntegerInstance();
            }
        };
        generatorTextField.textProperty().bindBidirectional(numberGenerator.getGeneratorNumberProperty(), numberStringConverter);
    }
}
