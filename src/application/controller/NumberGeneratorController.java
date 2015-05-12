package application.controller;

import application.generator.Generator;
import application.generator.IntegerGenerator;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.AnchorPane;
import javafx.util.converter.NumberStringConverter;

import java.text.NumberFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

    private String activeGeneratorType;

    private IntegerGenerator integerGenerator;

    private Tooltip minIntegerTooltip;

    // integer part
    @FXML
    private TextField integerMinNumberTextField;

    private ChangeListener<String> integerMinNumberListener = new ChangeListener<String>() {

        Pattern p = Pattern.compile("\\D");

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            Matcher m = p.matcher(newValue);
            if (m.find()) {
                //integerMinNumberTextField.textProperty().setValue(m.replaceAll(""));
                Point2D point2D = integerMinNumberTextField.localToScreen(integerMinNumberTextField.getLayoutBounds().getMaxX(), integerMinNumberTextField.getLayoutBounds().getMaxY());
                minIntegerTooltip.show(integerMinNumberTextField, point2D.getX(), point2D.getY());
            }
            else{
                minIntegerTooltip.hide();
            }
        }
    };

    @FXML
    private void initialize(){
        minIntegerTooltip = new Tooltip("Invalid integer value");
        minIntegerTooltip.setAutoHide(false);
        minIntegerTooltip.getStyleClass().add("ttip");
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
                integerMinNumberTextField.textProperty().addListener(integerMinNumberListener);
                integerGenerator = (IntegerGenerator)generator;
                NumberStringConverter numberStringConverter = new NumberStringConverter(){
                    @Override
                    protected NumberFormat getNumberFormat() {
                        return NumberFormat.getIntegerInstance();
                    }
                };
                integerMinNumberTextField.textProperty().bindBidirectional(integerGenerator.minNumberPropertyProperty(), numberStringConverter);
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
                integerMinNumberTextField.textProperty().removeListener(integerMinNumberListener);
                integerGenerator = (IntegerGenerator)generator;
                integerMinNumberTextField.textProperty().unbindBidirectional(integerGenerator.minNumberPropertyProperty());
                integerGenerator = null;
                break;
            case "SMALLINT":

                break;
            case "TINYINT":

                break;
        }
    }
}
