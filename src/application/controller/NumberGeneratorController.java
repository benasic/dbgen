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

import java.util.HashSet;
import java.util.Set;

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
    private Tooltip probabilityBinomialTooltip;

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

    private NumberGenerator numberGenerator;

    private MainAppController mainAppController;


    private Set<String> blockedSet = new HashSet<>();

    private ChangeListener<String> uniformMinNumberListener = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(newValue != null){
                boolean valid = false;
                try{
                    switch(activeGeneratorType){
                        case "INTEGER":
                            Integer.parseInt(newValue);
                            valid = true;
                            break;
                        case "SMALLINT":
                            Short.parseShort(newValue);
                            valid = true;
                            break;
                        case "TINYINT":
                            short number = Short.parseShort(newValue);
                            // TODO for now: range depends on database implementation, this is safe for all databases
                            if(number < 0 || number > 127){
                                throw new NumberFormatException("Invalid tinyint value");
                            }
                            valid = true;
                            break;
                        case "BIGINT":
                            Long.parseLong(newValue);
                            valid = true;
                            break;
                        case "REAL":
                            Float.parseFloat(newValue);
                            valid = true;
                            break;
                        case "FLOAT":
                        case "DOUBLE":
                            Double.parseDouble(newValue);
                            valid = true;
                            break;
                        case "DECIMAL":
                        case "NUMERIC":
                            Double.parseDouble(newValue);
                            valid = true;
                            break;
                    }
                } catch (NumberFormatException e){
                    System.err.println(e.getMessage());
                    blockedSet.add(minNumberUniformTextField.getId());
                    mainAppController.blockAll = true;
                    Point2D point2D = minNumberUniformTextField.localToScreen(minNumberUniformTextField.getLayoutBounds().getMaxX(), minNumberUniformTextField.getLayoutBounds().getMinY());
                    switch(activeGeneratorType){
                        case "INTEGER":
                            minNumberUniformTooltip.setText("Invalid integer value");
                            break;
                        case "SMALLINT":
                            minNumberUniformTooltip.setText("Invalid smallint value");
                            break;
                        case "TINYINT":
                            minNumberUniformTooltip.setText("Invalid tinyint value");
                            break;
                        case "BIGINT":
                            minNumberUniformTooltip.setText("Invalid bigint value");
                            break;
                        case "REAL":
                            minNumberUniformTooltip.setText("Invalid real value");
                            break;
                        case "FLOAT":
                            minNumberUniformTooltip.setText("Invalid float value");
                            break;
                        case "DOUBLE":
                            minNumberUniformTooltip.setText("Invalid double value");
                            break;
                        case "DECIMAL":
                            minNumberUniformTooltip.setText("Invalid decimal value");
                            break;
                        case "NUMERIC":
                            minNumberUniformTooltip.setText("Invalid numeric value");
                            break;
                    }
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

    private ChangeListener<String> uniformMaxNumberListener = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(newValue != null){
                boolean valid = false;
                try{
                    switch(activeGeneratorType){
                        case "INTEGER":
                            Integer.parseInt(newValue);
                            valid = true;
                            break;
                        case "SMALLINT":
                            Short.parseShort(newValue);
                            valid = true;
                            break;
                        case "TINYINT":
                            short number = Short.parseShort(newValue);
                            if(number < -128 || number > 127){
                                throw new NumberFormatException("Invalid tinyint value");
                            }
                            valid = true;
                            break;
                        case "BIGINT":
                            Long.parseLong(newValue);
                            valid = true;
                            break;
                        case "REAL":
                            Float.parseFloat(newValue);
                            valid = true;
                            break;
                        case "FLOAT":
                        case "DOUBLE":
                            Double.parseDouble(newValue);
                            valid = true;
                            break;
                        case "DECIMAL":
                        case "NUMERIC":
                            Double.parseDouble(newValue);
                            valid = true;
                            break;
                    }

                } catch (NumberFormatException e){
                    System.err.println(e.getMessage());
                    blockedSet.add(maxNumberUniformTextField.getId());
                    mainAppController.blockAll = true;
                    Point2D point2D = maxNumberUniformTextField.localToScreen(maxNumberUniformTextField.getLayoutBounds().getMaxX(), maxNumberUniformTextField.getLayoutBounds().getMinY());
                    switch(activeGeneratorType){
                        case "INTEGER":
                            maxNumberUniformTooltip.setText("Invalid integer value");
                            break;
                        case "SMALLINT":
                            maxNumberUniformTooltip.setText("Invalid smallint value");
                            break;
                        case "TINYINT":
                            maxNumberUniformTooltip.setText("Invalid tinyint value");
                            break;
                        case "BIGINT":
                            maxNumberUniformTooltip.setText("Invalid bigint value");
                            break;
                        case "REAL":
                            maxNumberUniformTooltip.setText("Invalid real value");
                            break;
                        case "FLOAT":
                            maxNumberUniformTooltip.setText("Invalid float value");
                            break;
                        case "DOUBLE":
                            maxNumberUniformTooltip.setText("Invalid double value");
                            break;
                        case "DECIMAL":
                            minNumberUniformTooltip.setText("Invalid decimal value");
                            break;
                        case "NUMERIC":
                            minNumberUniformTooltip.setText("Invalid numeric value");
                            break;
                    }

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

    private ChangeListener<String> probabilityBinomialListener = new ChangeListener<String>() {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            if(newValue != null){
                boolean valid = false;
                try{
                    switch(activeGeneratorType){
                        case "INTEGER":
                        case "SMALLINT":
                        case "TINYINT":
                            double number = Double.parseDouble(newValue);
                            if(number < 0 || number > 1){
                                throw new NumberFormatException("Invalid value for probability");
                            }
                            valid = true;
                            break;
                    }
                } catch (NumberFormatException e){
                    blockedSet.add(probabilityBinomialTextField.getId());
                    mainAppController.blockAll = true;
                    Point2D point2D = probabilityBinomialTextField.localToScreen(probabilityBinomialTextField.getLayoutBounds().getMaxX(), probabilityBinomialTextField.getLayoutBounds().getMinY());
                    switch(activeGeneratorType){
                        case "INTEGER":
                        case "SMALLINT":
                        case "TINYINT":
                            probabilityBinomialTooltip.setText("Invalid value for probability");
                            break;
                    }
                    probabilityBinomialTooltip.show(probabilityBinomialTextField, point2D.getX() + 5, point2D.getY());
                }
                if(valid){
                    probabilityBinomialTooltip.hide();
                    blockedSet.remove(probabilityBinomialTextField.getId());
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
                    numberGenerator.setDistributionType(DistributionType.UNIFORM);
                    uniformPane.visibleProperty().setValue(true);
                    binomialPane.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.BINOMIAL){
                    numberGenerator.setDistributionType(DistributionType.BINOMIAL);
                    uniformPane.visibleProperty().setValue(false);
                    binomialPane.visibleProperty().set(true);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.POISSON){
                    numberGenerator.setDistributionType(DistributionType.POISSON);
                    uniformPane.visibleProperty().setValue(false);
                    binomialPane.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(true);
                    normallyPane.visibleProperty().set(false);
                    exponentialPane.visibleProperty().set(false);
                    break;
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.NORMALLY){
                    numberGenerator.setDistributionType(DistributionType.NORMALLY);
                    uniformPane.visibleProperty().setValue(false);
                    binomialPane.visibleProperty().set(false);
                    poissonPane.visibleProperty().set(false);
                    normallyPane.visibleProperty().set(true);
                    exponentialPane.visibleProperty().set(false);
                }
                else if(toggle.isSelected() && toggle.getUserData() == DistributionType.EXPONENTIAL){
                    numberGenerator.setDistributionType(DistributionType.EXPONENTIAL);
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
        minNumberUniformTooltip = new Tooltip();
        minNumberUniformTooltip.setAutoHide(false);
        minNumberUniformTooltip.getStyleClass().add("tooltip");
        maxNumberUniformTooltip = new Tooltip();
        maxNumberUniformTooltip.setAutoHide(false);
        maxNumberUniformTooltip.getStyleClass().add("tooltip");
        probabilityBinomialTooltip = new Tooltip();
        probabilityBinomialTooltip.setAutoHide(false);
        probabilityBinomialTooltip.getStyleClass().add("tooltip");

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
        activeGeneratorType = type;
        bindFields(generator, type);
        enableGenerators(type);
    }

    private void bindFields(Generator generator, String type){
        numberType.setText(type.toLowerCase());

        numberGenerator = (NumberGenerator)generator;

        minNumberUniformTextField.textProperty().addListener(uniformMinNumberListener);
        maxNumberUniformTextField.textProperty().addListener(uniformMaxNumberListener);
        probabilityBinomialTextField.textProperty().addListener(probabilityBinomialListener);

        distributionToggleGroup.selectedToggleProperty().addListener(toggleChangeListener);

        for(Toggle radioButton : distributionToggleGroup.getToggles()){
            if(radioButton.getUserData() == numberGenerator.getDistributionType()){
                radioButton.selectedProperty().setValue(true);
                break;
            }
        }

        minNumberUniformTextField.textProperty().bindBidirectional(numberGenerator.minNumberUniformProperty());
        maxNumberUniformTextField.textProperty().bindBidirectional(numberGenerator.maxNumberUniformProperty());
        trailsBinomialTextField.textProperty().bindBidirectional(numberGenerator.numberOfTrailsBinomialProperty());
        probabilityBinomialTextField.textProperty().bindBidirectional(numberGenerator.probabilityBinomialProperty());
        meanPoissonTextField.textProperty().bindBidirectional(numberGenerator.meanPoissonProperty());
        meanNormallyTextField.textProperty().bindBidirectional(numberGenerator.meanNormallyProperty());
        standardDeviationTextField.textProperty().bindBidirectional(numberGenerator.standardDeviationNormallyProperty());
        rateExponentialTextField.textProperty().bindBidirectional(numberGenerator.rateExponentialProperty());
    }

    private void unbindFields(Generator generator, String type){
        minNumberUniformTextField.textProperty().removeListener(uniformMinNumberListener);
        maxNumberUniformTextField.textProperty().removeListener(uniformMaxNumberListener);
        probabilityBinomialTextField.textProperty().removeListener(probabilityBinomialListener);
        distributionToggleGroup.selectedToggleProperty().removeListener(toggleChangeListener);

        numberGenerator = (NumberGenerator)generator;
        minNumberUniformTextField.textProperty().unbindBidirectional(numberGenerator.minNumberUniformProperty());
        maxNumberUniformTextField.textProperty().unbindBidirectional(numberGenerator.maxNumberUniformProperty());
        trailsBinomialTextField.textProperty().unbindBidirectional(numberGenerator.numberOfTrailsBinomialProperty());
        probabilityBinomialTextField.textProperty().unbindBidirectional(numberGenerator.probabilityBinomialProperty());
        meanPoissonTextField.textProperty().unbindBidirectional(numberGenerator.meanPoissonProperty());
        meanNormallyTextField.textProperty().unbindBidirectional(numberGenerator.meanNormallyProperty());
        standardDeviationTextField.textProperty().unbindBidirectional(numberGenerator.standardDeviationNormallyProperty());
        rateExponentialTextField.textProperty().unbindBidirectional(numberGenerator.rateExponentialProperty());
        numberGenerator = null;
    }

    private void enableGenerators(String type){
        switch (type){
            case "INTEGER":
            case "SMALLINT":
            case "TINYINT":
                uniformRadioButton.disableProperty().set(false);
                binomialRadioButton.disableProperty().set(false);
                poissonRadioButton.disableProperty().set(false);
                normallyRadioButton.disableProperty().set(true);
                exponentialRadioButton.disableProperty().set(true);
                break;
            case "BIGINT":
                uniformRadioButton.disableProperty().set(false);
                binomialRadioButton.disableProperty().set(true);
                poissonRadioButton.disableProperty().set(true);
                normallyRadioButton.disableProperty().set(true);
                exponentialRadioButton.disableProperty().set(true);
                break;
            case "REAL":
            case "FLOAT":
            case "DOUBLE":
            case "DECIMAL":
            case "NUMERIC":
                uniformRadioButton.disableProperty().set(false);
                binomialRadioButton.disableProperty().set(true);
                poissonRadioButton.disableProperty().set(true);
                normallyRadioButton.disableProperty().set(false);
                exponentialRadioButton.disableProperty().set(false);
                break;
        }
    }
}
