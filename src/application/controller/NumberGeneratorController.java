package application.controller;

import application.generator.DistributionType;
import application.generator.Generator;
import application.generator.NumberGenerator;
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
    private ToggleGroup distributionToggleGroup;

    @FXML
    private RadioButton uniformRadioButton;

    @FXML
    private RadioButton binomialRadioButton;

    @FXML
    private RadioButton poissonRadioButton;

    @FXML
    private RadioButton normallyRadioButton;

    @FXML
    private RadioButton exponentialRadioButton;

    // Uniform Distribution part

    @FXML
    private Pane uniformPane;

    @FXML
    private TextField minNumberUniformTextField;
    private Tooltip minNumberUniformTooltip;

    @FXML
    private TextField maxNumberUniformTextField;
    private Tooltip maxNumberUniformTooltip;

    // Binomial Distribution part

    @FXML
    private Pane binomialPane;

    @FXML
    private TextField trailsBinomialTextField;

    @FXML
    private TextField probabilityBinomialTextField;

    // Poisson Distribution part

    @FXML
    private Pane poissonPane;

    @FXML
    private TextField meanPoissonTextField;

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

    private NumberGenerator integerGenerator;

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
                    blockedSet.add(minNumberUniformTextField.getId());
                    mainAppController.blockAll = true;
                    Point2D point2D = minNumberUniformTextField.localToScreen(minNumberUniformTextField.getLayoutBounds().getMaxX(), minNumberUniformTextField.getLayoutBounds().getMinY());
                    minNumberUniformTooltip.show(minNumberUniformTextField, point2D.getX() + 5, point2D.getY());
                }
                if(valid){
                    minNumberUniformTooltip.hide();
                    blockedSet.remove(minNumberUniformTextField.getId());
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
                    blockedSet.add(maxNumberUniformTextField.getId());
                    mainAppController.blockAll = true;
                    Point2D point2D = maxNumberUniformTextField.localToScreen(maxNumberUniformTextField.getLayoutBounds().getMaxX(), maxNumberUniformTextField.getLayoutBounds().getMinY());
                    maxNumberUniformTooltip.show(maxNumberUniformTextField, point2D.getX() + 5, point2D.getY());
                }
                if(valid){
                    maxNumberUniformTooltip.hide();
                    blockedSet.remove(maxNumberUniformTextField.getId());
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
            for(Toggle toggle : distributionToggleGroup.getToggles()){
                if(toggle.isSelected() && toggle.getUserData() == DistributionType.UNIFORM){
                    integerGenerator.setDistributionType(DistributionType.UNIFORM);
                    uniformPane.visibleProperty().setValue(true);
                    binomialPane.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.BINOMIAL){
                    integerGenerator.setDistributionType(DistributionType.BINOMIAL);
                    uniformPane.visibleProperty().setValue(false);
                    binomialPane.visibleProperty().set(true);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.POISSON){
                    integerGenerator.setDistributionType(DistributionType.POISSON);
                    uniformPane.visibleProperty().setValue(false);
                    binomialPane.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(true);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.NORMALLY){
                    integerGenerator.setDistributionType(DistributionType.NORMALLY);
                    uniformPane.visibleProperty().setValue(false);
                    binomialPane.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(true);
                    exponentialPane.visibleProperty().set(false);
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.EXPONENTIAL){
                    integerGenerator.setDistributionType(DistributionType.EXPONENTIAL);
                    uniformPane.visibleProperty().setValue(false);
                    binomialPane.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(true);
                }
            }
        }
    };

    @FXML
    private void initialize(){
        minNumberUniformTooltip = new Tooltip("Invalid integer value");
        minNumberUniformTooltip.setAutoHide(false);
        minNumberUniformTooltip.getStyleClass().add("tooltip");
        maxNumberUniformTooltip = new Tooltip("Invalid integer value");
        maxNumberUniformTooltip.setAutoHide(false);
        maxNumberUniformTooltip.getStyleClass().add("tooltip");

        binomialRadioButton.setUserData(DistributionType.BINOMIAL);
        uniformRadioButton.setUserData(DistributionType.UNIFORM);
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
                integerGenerator = (NumberGenerator)generator;

                minNumberUniformTextField.textProperty().addListener(integerMinNumberListener);
                maxNumberUniformTextField.textProperty().addListener(integerMaxNumberListener);
                distributionToggleGroup.selectedToggleProperty().addListener(toggleChangeListener);

                for(Toggle radioButton : distributionToggleGroup.getToggles()){
                    if(radioButton.getUserData() == integerGenerator.getDistributionType()){
                        radioButton.selectedProperty().setValue(true);
                        break;
                    }
                }

                minNumberUniformTextField.textProperty().bindBidirectional(integerGenerator.minNumberUniformProperty());
                maxNumberUniformTextField.textProperty().bindBidirectional(integerGenerator.maxNumberUniformProperty());
                trailsBinomialTextField.textProperty().bindBidirectional(integerGenerator.numberOfTrailsBinomialProperty());
                probabilityBinomialTextField.textProperty().bindBidirectional(integerGenerator.probabilityBinomialProperty());
                meanPoissonTextField.textProperty().bindBidirectional(integerGenerator.meanPoissonProperty());
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
                minNumberUniformTextField.textProperty().removeListener(integerMinNumberListener);
                maxNumberUniformTextField.textProperty().removeListener(integerMaxNumberListener);
                distributionToggleGroup.selectedToggleProperty().removeListener(toggleChangeListener);

                integerGenerator = (NumberGenerator)generator;
                minNumberUniformTextField.textProperty().unbindBidirectional(integerGenerator.minNumberUniformProperty());
                maxNumberUniformTextField.textProperty().unbindBidirectional(integerGenerator.maxNumberUniformProperty());
                trailsBinomialTextField.textProperty().unbindBidirectional(integerGenerator.numberOfTrailsBinomialProperty());
                probabilityBinomialTextField.textProperty().unbindBidirectional(integerGenerator.probabilityBinomialProperty());
                meanPoissonTextField.textProperty().unbindBidirectional(integerGenerator.meanPoissonProperty());
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
