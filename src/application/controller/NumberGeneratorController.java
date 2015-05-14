package application.controller;

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
    private TabPane numberTabPane;

    @FXML
    private Tab integerTab;

    @FXML
    private Tab smallIntTab;

    @FXML
    private Tab tinyIntTab;

    // 1. integer part

    @FXML
    private ToggleGroup toggleGroup1;

    @FXML
    private RadioButton integer_DUD_RadioButton;

    @FXML
    private RadioButton integer_BD_RadioButton;

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
                if(toggle.isSelected() && toggle.getUserData().equals("DUD")){
                    paneIntegerTabDiscreteUniform.visibleProperty().setValue(true);
                    paneIntegerTabBinomial.visibleProperty().set(false);
                }
                else if(toggle.isSelected() && toggle.getUserData().equals("BD")){
                    paneIntegerTabDiscreteUniform.visibleProperty().setValue(false);
                    paneIntegerTabBinomial.visibleProperty().set(true);
                }
            }
        }
    };

    @FXML
    private void initialize() {
        minIntegerDiscreteUniformTooltip = new Tooltip("Invalid integer value");
        minIntegerDiscreteUniformTooltip.setAutoHide(false);
        minIntegerDiscreteUniformTooltip.getStyleClass().add("tooltip");
        maxIntegerDiscreteUniformTooltip = new Tooltip("Invalid integer value");
        maxIntegerDiscreteUniformTooltip.setAutoHide(false);
        maxIntegerDiscreteUniformTooltip.getStyleClass().add("tooltip");

        integer_BD_RadioButton.setUserData("BD");
        integer_DUD_RadioButton.setUserData("DUD");
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
        switch (type){
            case "INTEGER":
                numberTabPane.selectionModelProperty().get().select(integerTab);
                integerTab.disableProperty().setValue(false);
                smallIntTab.disableProperty().setValue(true);
                tinyIntTab.disableProperty().setValue(true);
                break;
            case "SMALLINT":
                numberTabPane.selectionModelProperty().get().select(smallIntTab);
                integerTab.disableProperty().setValue(true);
                smallIntTab.disableProperty().setValue(false);
                tinyIntTab.disableProperty().setValue(true);
                break;
            case "TINYINT":
                numberTabPane.selectionModelProperty().get().select(tinyIntTab);
                integerTab.disableProperty().setValue(true);
                smallIntTab.disableProperty().setValue(true);
                tinyIntTab.disableProperty().setValue(false);
                break;
        }
    }

    private void bindFields(Generator generator, String type){
        switch (type){
            case "INTEGER":
                integerMinNumberDiscreteUniformTextField.textProperty().addListener(integerMinNumberListener);
                integerMaxNumberDiscreteUniformTextField.textProperty().addListener(integerMaxNumberListener);
                toggleGroup1.selectedToggleProperty().addListener(toggleChangeListener);
                integerGenerator = (IntegerGenerator)generator;
                integerMinNumberDiscreteUniformTextField.textProperty().bindBidirectional(integerGenerator.minNumberDiscreteUniformProperty());
                integerMaxNumberDiscreteUniformTextField.textProperty().bindBidirectional(integerGenerator.maxNumberDiscreteUniformProperty());
                break;
            case "SMALLINT":

                break;
            case "TINYINT":

                break;
        }
    }

    private void unbindFields(Generator generator, String type){
        switch (type){
            case "INTEGER":
                integerMinNumberDiscreteUniformTextField.textProperty().removeListener(integerMinNumberListener);
                integerMaxNumberDiscreteUniformTextField.textProperty().removeListener(integerMaxNumberListener);
                toggleGroup1.selectedToggleProperty().removeListener(toggleChangeListener);
                integerGenerator = (IntegerGenerator)generator;
                integerMinNumberDiscreteUniformTextField.textProperty().unbindBidirectional(integerGenerator.minNumberDiscreteUniformProperty());
                integerMaxNumberDiscreteUniformTextField.textProperty().unbindBidirectional(integerGenerator.maxNumberDiscreteUniformProperty());
                integerGenerator = null;
                break;
            case "SMALLINT":

                break;
            case "TINYINT":

                break;
        }
    }
}
