package application.controller;

import application.generator.Generator;
import application.generator.StringGenerator;
import application.model.RegexTemplate;
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



    @FXML
    private void initialize(){
        templatesChoiceBox.getSelectionModel().selectedItemProperty()
                .addListener((observable, oldValue, newValue) -> {
                    generatorTextField.textProperty().setValue(newValue.getRegex());
                });
    }

    public void unbindValues(Generator generator){
        stringGenerator = (StringGenerator)generator;
        generatorTextField.textProperty().unbindBidirectional(stringGenerator.getGeneratorStringProperty());
    }

    public void setGenerator(Generator generator){
        stringGenerator = (StringGenerator)generator;
        generatorTextField.textProperty().bindBidirectional(stringGenerator.getGeneratorStringProperty());

        ObservableList<RegexTemplate> regexTemplates = FXCollections.observableArrayList();
        regexTemplates.add(new RegexTemplate("email","[a-z]{5,8}\\.[a-z]{5,8}\\@[a-z]{3,4}\\.com"));
        regexTemplates.add(new RegexTemplate("number","[0-9]{5,8}"));

        templatesChoiceBox.setItems(regexTemplates);
        templatesChoiceBox.getSelectionModel().selectFirst();
    }
}
