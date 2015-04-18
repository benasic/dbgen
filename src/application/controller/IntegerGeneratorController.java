package application.controller;

import application.generator.Generator;
import application.generator.IntegerGenerator;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.converter.IntegerStringConverter;

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
            System.out.print(oldValue + ' '  + newValue + '\n');
            if(m.find()){
                System.out.println("Promjena");
                generatorTextField.textProperty().setValue(m.replaceAll(""));
            }
        });
    }

    public void setGenerator(Generator generator){
        integerGenerator = (IntegerGenerator)generator;
        generatorTextField.textProperty().bindBidirectional(this.integerGenerator.getGeneratorIntegerProperty(),(StringConverter)new IntegerStringConverter());
    }
}
