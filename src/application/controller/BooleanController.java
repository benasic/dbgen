package application.controller;


import application.generator.BooleanGenerator;
import application.generator.BooleanGeneratorType;
import application.generator.Generator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;

public class BooleanController {

    @FXML
    private ToggleGroup booleanToggleGroup;

    @FXML
    private RadioButton randomRadioButton;

    @FXML
    private RadioButton onlyTrueRadioButton;

    @FXML
    private RadioButton onlyFalseRadioButton;

    @FXML
    private RadioButton percentageRadioButton;

    @FXML
    private Slider percentageSlider;

    private BooleanGenerator booleanGenerator;

    private ChangeListener<Toggle> toggleChangeListener = new ChangeListener<Toggle>() {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            switch ((BooleanGeneratorType) newValue.getUserData()) {
                case RANDOM:
                    booleanGenerator.setBooleanGeneratorType(BooleanGeneratorType.RANDOM);
                    break;
                case ONLY_TRUE:
                    booleanGenerator.setBooleanGeneratorType(BooleanGeneratorType.ONLY_TRUE);
                    break;
                case ONLY_FALSE:
                    booleanGenerator.setBooleanGeneratorType(BooleanGeneratorType.ONLY_FALSE);
                    break;
                case PERCENTAGE:
                    booleanGenerator.setBooleanGeneratorType(BooleanGeneratorType.PERCENTAGE);
                    break;
            }
        }
    };

    @FXML
    private void initialize(){
        randomRadioButton.setUserData(BooleanGeneratorType.RANDOM);
        onlyTrueRadioButton.setUserData(BooleanGeneratorType.ONLY_TRUE);
        onlyFalseRadioButton.setUserData(BooleanGeneratorType.ONLY_FALSE);
        percentageRadioButton.setUserData(BooleanGeneratorType.PERCENTAGE);
    }

    public void bindValues(Generator generator){
        booleanGenerator = (BooleanGenerator)generator;
        booleanToggleGroup.selectedToggleProperty().addListener(toggleChangeListener);
        percentageSlider.valueProperty().bindBidirectional(booleanGenerator.percentageProperty());
        switch (booleanGenerator.getBooleanGeneratorType()){
            case RANDOM:
                booleanToggleGroup.selectToggle(randomRadioButton);
                break;
            case ONLY_TRUE:
                booleanToggleGroup.selectToggle(onlyTrueRadioButton);
                break;
            case ONLY_FALSE:
                booleanToggleGroup.selectToggle(onlyFalseRadioButton);
                break;
            case PERCENTAGE:
                booleanToggleGroup.selectToggle(percentageRadioButton);
                break;
        }

    }

    public void unbindValues(Generator generator){
        booleanGenerator = (BooleanGenerator)generator;
        booleanToggleGroup.selectedToggleProperty().removeListener(toggleChangeListener);
        percentageSlider.valueProperty().unbindBidirectional(booleanGenerator.percentageProperty());
    }
}
