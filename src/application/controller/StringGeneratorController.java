package application.controller;

import application.generator.Generator;
import application.generator.StringGenerator;
import application.model.RegexTemplate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;

public class StringGeneratorController {

    @FXML
    private TextField generatorTextField;

    @FXML
    private ChoiceBox<RegexTemplate> templatesChoiceBox;

    private StringGenerator stringGenerator;

    private ChangeListener<RegexTemplate> templatesChoiceBoxListener = new ChangeListener<RegexTemplate>() {

        @Override
        public void changed(ObservableValue<? extends RegexTemplate> observable, RegexTemplate oldValue, RegexTemplate newValue) {
            if(newValue != null){
                generatorTextField.textProperty().setValue(newValue.getRegex());
            }

        }
    };

    @FXML
    private void initialize(){

    }

    public void unbindValues(Generator generator){
        stringGenerator = (StringGenerator)generator;
        templatesChoiceBox.getSelectionModel().selectedItemProperty().removeListener(templatesChoiceBoxListener);
        generatorTextField.textProperty().unbindBidirectional(stringGenerator.getGeneratorStringProperty());
    }

    public void setGenerator(Generator generator){
        stringGenerator = (StringGenerator)generator;
        templatesChoiceBox.getSelectionModel().selectedItemProperty().addListener(templatesChoiceBoxListener);
        generatorTextField.textProperty().bindBidirectional(stringGenerator.getGeneratorStringProperty());


        ObservableList<RegexTemplate> regexTemplates = FXCollections.observableArrayList();
        regexTemplates.add(new RegexTemplate("email","[a-z]{5,8}\\.[a-z]{5,8}\\@[a-z]{3,4}\\.com"));
        regexTemplates.add(new RegexTemplate("number","[0-9]{5,8}"));

        templatesChoiceBox.setItems(regexTemplates);
        templatesChoiceBox.getSelectionModel().selectFirst();
    }
}
