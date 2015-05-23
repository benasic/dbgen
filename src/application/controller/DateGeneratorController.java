package application.controller;

import application.generator.DateGenerator;
import application.generator.Generator;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;

import java.sql.Date;
import java.sql.Time;

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

    @FXML
    private HBox daysHBox;

    @FXML
    private CheckBox monCheckBox;

    @FXML
    private CheckBox tueCheckBox;

    @FXML
    private CheckBox wenCheckBox;

    @FXML
    private CheckBox thuCheckBox;

    @FXML
    private CheckBox friCheckBox;

    @FXML
    private CheckBox satCheckBox;

    @FXML
    private CheckBox sunCheckBox;



    private DateGenerator dateGenerator;

    private String activeGeneratorType;

    @FXML
    private void initialize(){
        startDatePicker.setOnAction(t -> {
            dateGenerator.setStartDate(Date.valueOf(startDatePicker.getValue().toString()));
        });
        endDatePicker.setOnAction(t -> {
            dateGenerator.setEndDate(Date.valueOf(endDatePicker.getValue().toString()));
        });
        startTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                Time t = Time.valueOf(newValue);
                dateGenerator.setStartTime(t);
            } catch (IllegalArgumentException e){
                System.err.println(newValue);
            }
        });
        endTimeTextField.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                Time t = Time.valueOf(newValue);
                dateGenerator.setEndTime(t);
            } catch (IllegalArgumentException e){
                System.err.println(newValue);
            }
        });
    }

    public void unbindValues(Generator generator){
        dateGenerator = (DateGenerator)generator;
        monCheckBox.selectedProperty().unbindBidirectional(dateGenerator.monBooleanPropertyProperty());
        tueCheckBox.selectedProperty().unbindBidirectional(dateGenerator.tueBooleanPropertyProperty());
        wenCheckBox.selectedProperty().unbindBidirectional(dateGenerator.wenBooleanPropertyProperty());
        thuCheckBox.selectedProperty().unbindBidirectional(dateGenerator.thuBooleanPropertyProperty());
        friCheckBox.selectedProperty().unbindBidirectional(dateGenerator.friBooleanPropertyProperty());
        satCheckBox.selectedProperty().unbindBidirectional(dateGenerator.satBooleanPropertyProperty());
        sunCheckBox.selectedProperty().unbindBidirectional(dateGenerator.sunBooleanPropertyProperty());
    }

    public void setGenerator(Generator generator, String type) {
        activeGeneratorType = type;
        enableFields(type);
        dateTypeLabel.setText(type.toLowerCase());
        dateGenerator = (DateGenerator) generator;
        startDatePicker.setValue(dateGenerator.getStartDate().toLocalDate());
        endDatePicker.setValue(dateGenerator.getEndDate().toLocalDate());
        startTimeTextField.textProperty().setValue(dateGenerator.getStartTime().toString());
        endTimeTextField.textProperty().setValue(dateGenerator.getEndTime().toString());
        monCheckBox.selectedProperty().bindBidirectional(dateGenerator.monBooleanPropertyProperty());
        tueCheckBox.selectedProperty().bindBidirectional(dateGenerator.tueBooleanPropertyProperty());
        wenCheckBox.selectedProperty().bindBidirectional(dateGenerator.wenBooleanPropertyProperty());
        thuCheckBox.selectedProperty().bindBidirectional(dateGenerator.thuBooleanPropertyProperty());
        friCheckBox.selectedProperty().bindBidirectional(dateGenerator.friBooleanPropertyProperty());
        satCheckBox.selectedProperty().bindBidirectional(dateGenerator.satBooleanPropertyProperty());
        sunCheckBox.selectedProperty().bindBidirectional(dateGenerator.sunBooleanPropertyProperty());
    }

    private void enableFields(String type){
        switch(type){
            case "DATE":
                startDatePicker.disableProperty().set(false);
                endDatePicker.disableProperty().set(false);
                startTimeTextField.disableProperty().set(true);
                endTimeTextField.disableProperty().set(true);
                daysHBox.disableProperty().set(false);
                break;
            case "TIME":
                startDatePicker.disableProperty().set(true);
                endDatePicker.disableProperty().set(true);
                startTimeTextField.disableProperty().set(false);
                endTimeTextField.disableProperty().set(false);
                daysHBox.disableProperty().set(true);
                break;
            case "TIMESTAMP":
                startDatePicker.disableProperty().set(false);
                endDatePicker.disableProperty().set(false);
                startTimeTextField.disableProperty().set(false);
                endTimeTextField.disableProperty().set(false);
                daysHBox.disableProperty().set(false);
                break;
        }
    }
}
