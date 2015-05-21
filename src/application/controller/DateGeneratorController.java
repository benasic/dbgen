package application.controller;

import application.generator.DateGenerator;
import application.generator.Generator;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.time.LocalTime;
import java.time.format.DateTimeParseException;

public class DateGeneratorController {

    @FXML
    private Label dateTypeLabel;

    @FXML
    private DatePicker startDatePicker;

    @FXML
    private DatePicker endDatePicker;

    @FXML
    private TextField startTimeTextField;

    @FXML
    private TextField endTimeTextField;

    private DateGenerator dateGenerator;

    private String activeGeneratorType;

    @FXML
    private void initialize(){
        startDatePicker.setOnAction(t -> {
            dateGenerator.setStartDate(startDatePicker.getValue());
        });
        endDatePicker.setOnAction(t -> {
            dateGenerator.setEndDate(endDatePicker.getValue());
        });
        startTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                LocalTime t = LocalTime.parse(newValue);
                dateGenerator.setStartTime(t);
            } catch (DateTimeParseException e){
                System.err.println(newValue);
            }
        });
        endTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                LocalTime t = LocalTime.parse(newValue);
                dateGenerator.setEndTime(t);
            } catch (DateTimeParseException e){
                System.err.println(newValue);
            }
        });
    }

    public void unbindValues(Generator generator){
        dateGenerator = (DateGenerator)generator;
    }

    public void setGenerator(Generator generator, String type) {
        activeGeneratorType = type;
        dateTypeLabel.setText(type.toLowerCase());
        dateGenerator = (DateGenerator) generator;
        startDatePicker.setValue(dateGenerator.getStartDate());
        endDatePicker.setValue(dateGenerator.getEndDate());
        startTimeTextField.textProperty().setValue(dateGenerator.getStartTime().toString());
        endTimeTextField.textProperty().setValue(dateGenerator.getEndTime().toString());

    }
}
