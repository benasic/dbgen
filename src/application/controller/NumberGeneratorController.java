package application.controller;

import application.generator.DistributionType;
import application.generator.Generator;
import application.generator.IntegerGenerator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;

import java.util.*;

public class NumberGeneratorController {

    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label numberType;

    @FXML
    private ToggleGroup toggleGroup1;

    @FXML
    private RadioButton integer_DUD_RadioButton;

    @FXML
    private RadioButton integer_BD_RadioButton;

    @FXML
    private RadioButton poissonRadioButton;

    @FXML
    private RadioButton normallyRadioButton;

    @FXML
    private RadioButton exponentialRadioButton;

    // 1.1 integer discrete uniform part

    @FXML
    private Pane paneIntegerTabDiscreteUniform;

    @FXML
    private TextField integerMinNumberDiscreteUniformTextField;
    private Tooltip minIntegerDiscreteUniformTooltip;

    @FXML
    private TextField integerMaxNumberDiscreteUniformTextField;
    private Tooltip maxIntegerDiscreteUniformTooltip;

    // 1.2 integer binomial part

    @FXML
    private Pane paneIntegerTabBinomial;

    @FXML
    private TextField integerTrailsBinomialTextField;

    @FXML
    private TextField integerProbabilityBinomialTextField;

    // Poisson Distribution part

    @FXML
    private Pane poissonPane;

    @FXML
    private TextField meanPoissonTextBox;

    // Normally Distribution part

    @FXML
    private Pane normallyPane;

    @FXML
    private TextField meanNormallyTextField;

    @FXML
    private TextField standardDeviationTextField;

    // exponential Distribution part

    @FXML
    private Pane exponentialPane;

    @FXML
    private TextField rateExponentialTextField;



    private String activeGeneratorType;

    private IntegerGenerator integerGenerator;

    private MainAppController mainAppController;


    private Set<String> blockedSet = new HashSet<>();

    private ChangeListener<String> integerMinNumberListener = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(newValue != null){
                boolean valid = false;
                try{
                    Integer.parseInt(newValue);
                    valid = true;
                } catch (NumberFormatException e){
                    System.err.println(e.getMessage());
                    blockedSet.add(integerMinNumberDiscreteUniformTextField.getId());
                    mainAppController.blockAll = true;
                    Point2D point2D = integerMinNumberDiscreteUniformTextField.localToScreen(integerMinNumberDiscreteUniformTextField.getLayoutBounds().getMaxX(), integerMinNumberDiscreteUniformTextField.getLayoutBounds().getMinY());
                    minIntegerDiscreteUniformTooltip.show(integerMinNumberDiscreteUniformTextField, point2D.getX() + 5, point2D.getY());
                }
                if(valid){
                    minIntegerDiscreteUniformTooltip.hide();
                    blockedSet.remove(integerMinNumberDiscreteUniformTextField.getId());
                    if(blockedSet.isEmpty()){
                        mainAppController.blockAll = false;
                    }
                }
            }
        }
    };

    private ChangeListener<String> integerMaxNumberListener = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(newValue != null){
                boolean valid = false;
                try{
                    Integer.parseInt(newValue);
                    valid = true;
                } catch (NumberFormatException e){
                    System.err.println(e.getMessage());
                    blockedSet.add(integerMaxNumberDiscreteUniformTextField.getId());
                    mainAppController.blockAll = true;
                    Point2D point2D = integerMaxNumberDiscreteUniformTextField.localToScreen(integerMaxNumberDiscreteUniformTextField.getLayoutBounds().getMaxX(), integerMaxNumberDiscreteUniformTextField.getLayoutBounds().getMinY());
                    maxIntegerDiscreteUniformTooltip.show(integerMaxNumberDiscreteUniformTextField, point2D.getX() + 5, point2D.getY());
                }
                if(valid){
                    maxIntegerDiscreteUniformTooltip.hide();
                    blockedSet.remove(integerMaxNumberDiscreteUniformTextField.getId());
                    if(blockedSet.isEmpty()){
                        mainAppController.blockAll = false;
                    }
                }
            }
        }
    };

    private ChangeListener<Toggle> toggleChangeListener = new ChangeListener<Toggle>() {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            for(Toggle toggle : toggleGroup1.getToggles()){
                if(toggle.isSelected() && toggle.getUserData() == DistributionType.UNIFORM){
                    integerGenerator.setDistributionType(DistributionType.UNIFORM);
                    paneIntegerTabDiscreteUniform.visibleProperty().setValue(true);
                    paneIntegerTabBinomial.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.BINOMIAL){
                    integerGenerator.setDistributionType(DistributionType.BINOMIAL);
                    paneIntegerTabDiscreteUniform.visibleProperty().setValue(false);
                    paneIntegerTabBinomial.visibleProperty().set(true);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.POISSON){
                    integerGenerator.setDistributionType(DistributionType.POISSON);
                    paneIntegerTabDiscreteUniform.visibleProperty().setValue(false);
                    paneIntegerTabBinomial.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(true);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.NORMALLY){
                    integerGenerator.setDistributionType(DistributionType.NORMALLY);
                    paneIntegerTabDiscreteUniform.visibleProperty().setValue(false);
                    paneIntegerTabBinomial.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(true);
                    exponentialPane.visibleProperty().set(false);
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.EXPONENTIAL){
                    integerGenerator.setDistributionType(DistributionType.EXPONENTIAL);
                    paneIntegerTabDiscreteUniform.visibleProperty().setValue(false);
                    paneIntegerTabBinomial.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(true);
                }
            }
        }
    };

    @FXML
    private void initialize(){
        minIntegerDiscreteUniformTooltip = new Tooltip("Invalid integer value");
        minIntegerDiscreteUniformTooltip.setAutoHide(false);
        minIntegerDiscreteUniformTooltip.getStyleClass().add("tooltip");
        maxIntegerDiscreteUniformTooltip = new Tooltip("Invalid integer value");
        maxIntegerDiscreteUniformTooltip.setAutoHide(false);
        maxIntegerDiscreteUniformTooltip.getStyleClass().add("tooltip");

        integer_BD_RadioButton.setUserData(DistributionType.BINOMIAL);
        integer_DUD_RadioButton.setUserData(DistributionType.UNIFORM);
        poissonRadioButton.setUserData(DistributionType.POISSON);
        normallyRadioButton.setUserData(DistributionType.NORMALLY);
        exponentialRadioButton.setUserData(DistributionType.EXPONENTIAL);
    }

    public void setMainController(MainAppController mainController){
        mainAppController = mainController;
    }

    public void unbindValues(Generator generator, String type){
        unbindFields(generator, type);
    }

    public void setGenerator(Generator generator, String type){
        setupActiveGenerator(type);
        bindFields(generator, type);
    }

    private void setupActiveGenerator(String type){
        activeGeneratorType = type;

    }

    private void bindFields(Generator generator, String type){
        numberType.setText(type.toLowerCase());
        switch (type){
            case "INTEGER":
                integerGenerator = (IntegerGenerator)generator;

                integerMinNumberDiscreteUniformTextField.textProperty().addListener(integerMinNumberListener);
                integerMaxNumberDiscreteUniformTextField.textProperty().addListener(integerMaxNumberListener);
                toggleGroup1.selectedToggleProperty().addListener(toggleChangeListener);

                for(Toggle radioButton : toggleGroup1.getToggles()){
                    if(radioButton.getUserData() == integerGenerator.getDistributionType()){
                        radioButton.selectedProperty().setValue(true);
                        break;
                    }
                }

                integerMinNumberDiscreteUniformTextField.textProperty().bindBidirectional(integerGenerator.minNumberDiscreteUniformProperty());
                integerMaxNumberDiscreteUniformTextField.textProperty().bindBidirectional(integerGenerator.maxNumberDiscreteUniformProperty());
                integerTrailsBinomialTextField.textProperty().bindBidirectional(integerGenerator.numberOfTrailsBinomialProperty());
                integerProbabilityBinomialTextField.textProperty().bindBidirectional(integerGenerator.probabilityBinomialProperty());
                meanPoissonTextBox.textProperty().bindBidirectional(integerGenerator.meanPoissonProperty());
                meanNormallyTextField.textProperty().bindBidirectional(integerGenerator.meanNormallyProperty());
                standardDeviationTextField.textProperty().bindBidirectional(integerGenerator.standardDeviationNormallyProperty());
                rateExponentialTextField.textProperty().bindBidirectional(integerGenerator.rateExponentialProperty());
                break;
            case "SMALLINT":

                break;
            case "TINYINT":

                break;
        }
    }

    private void unbindFields(Generator generator, String type){
        switch (type){
            case "NUMBER":
                integerMinNumberDiscreteUniformTextField.textProperty().removeListener(integerMinNumberListener);
                integerMaxNumberDiscreteUniformTextField.textProperty().removeListener(integerMaxNumberListener);
                toggleGroup1.selectedToggleProperty().removeListener(toggleChangeListener);

                integerGenerator = (IntegerGenerator)generator;
                integerMinNumberDiscreteUniformTextField.textProperty().unbindBidirectional(integerGenerator.minNumberDiscreteUniformProperty());
                integerMaxNumberDiscreteUniformTextField.textProperty().unbindBidirectional(integerGenerator.maxNumberDiscreteUniformProperty());
                integerTrailsBinomialTextField.textProperty().unbindBidirectional(integerGenerator.numberOfTrailsBinomialProperty());
                integerProbabilityBinomialTextField.textProperty().unbindBidirectional(integerGenerator.probabilityBinomialProperty());
                meanPoissonTextBox.textProperty().unbindBidirectional(integerGenerator.meanPoissonProperty());
                meanNormallyTextField.textProperty().unbindBidirectional(integerGenerator.meanNormallyProperty());
                standardDeviationTextField.textProperty().unbindBidirectional(integerGenerator.standardDeviationNormallyProperty());
                rateExponentialTextField.textProperty().unbindBidirectional(integerGenerator.rateExponentialProperty());
                integerGenerator = null;
                break;
            case "SMALLINT":

                break;
            case "TINYINT":

                break;
        }
    }
}
