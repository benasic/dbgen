package application.controller;

import application.generator.DateGenerator;
import application.generator.Generator;
import javafx.fxml.FXML;

public class DateGeneratorController {

    private DateGenerator dateGenerator;

    @FXML
    private void initialize(){

    }

    public void unbindValues(Generator generator){
        dateGenerator = (DateGenerator)generator;
    }

    public void setGenerator(Generator generator) {
        dateGenerator = (DateGenerator) generator;
    }
}
