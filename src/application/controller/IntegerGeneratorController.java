package application.controller;

import application.generator.Generator;
import application.generator.IntegerGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IntegerGeneratorController {

    @FXML
    private TextField generatorTextField;

    private IntegerGenerator integerGenerator;

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
        integerGenerator = (IntegerGenerator)generator;
        generatorTextField.textProperty().unbindBidirectional(integerGenerator.getGeneratorIntegerProperty());
    }

    public void setGenerator(Generator generator){
        integerGenerator = (IntegerGenerator)generator;
        NumberStringConverter numberStringConverter = new NumberStringConverter(){
            @Override
            protected NumberFormat getNumberFormat() {
                return NumberFormat.getIntegerInstance();
            }
        };
        generatorTextField.textProperty().bindBidirectional(integerGenerator.getGeneratorIntegerProperty(), numberStringConverter);
    }
}
