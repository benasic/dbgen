package application.controller;

import application.generator.TableGenerationSettings;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class TableGenerationSettingsController {

    @FXML
    private CheckBox allowGenerationCheckBox;

    @FXML
    private TextField numberOfDataToGenerateTextField;

    public void bindValues(TableGenerationSettings tableGenerationSettings){
        allowGenerationCheckBox.selectedProperty().bindBidirectional(tableGenerationSettings.allowGenerationProperty());
        numberOfDataToGenerateTextField.textProperty().bindBidirectional(tableGenerationSettings.numberOfDataToGenerateStringProperty());
    }

    public void unbindValues(TableGenerationSettings tableGenerationSettings){
        allowGenerationCheckBox.selectedProperty().unbindBidirectional(tableGenerationSettings.allowGenerationProperty());
        numberOfDataToGenerateTextField.textProperty().unbindBidirectional(tableGenerationSettings.numberOfDataToGenerateStringProperty());
    }
}
