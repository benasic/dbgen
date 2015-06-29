package application.controller;

import application.Constants;
import application.generator.Generator;
import application.generator.StringGenerationType;
import application.generator.StringGenerator;
import application.model.Catalog;
import application.model.RegexTemplate;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class StringGeneratorController {

    @FXML
    private Label stringTypeLabel;

    @FXML
    private ToggleGroup stringToggleGroup;

    @FXML
    private RadioButton catalogRadioButton;

    @FXML
    private RadioButton templateRadioButton;

    @FXML
    private RadioButton customizedRadioButton;

    @FXML
    private ChoiceBox<RegexTemplate> templatesChoiceBox;

    @FXML
    private ChoiceBox<Catalog> catalogChoiceBox;

    @FXML
    private TextField generatorTextField;

    private StringGenerator stringGenerator;

    private ChangeListener<RegexTemplate> templatesChoiceBoxListener = new ChangeListener<RegexTemplate>() {

        @Override
        public void changed(ObservableValue<? extends RegexTemplate> observable, RegexTemplate oldValue, RegexTemplate newValue) {
            if(newValue != null){
                generatorTextField.textProperty().setValue(newValue.getRegex());
            }
        }
    };

    private ChangeListener<Catalog> catalogChoiceBoxListener = new ChangeListener<Catalog>() {

        @Override
        public void changed(ObservableValue<? extends Catalog> observable, Catalog oldValue, Catalog newValue) {
            if(newValue != null){
                stringGenerator.setCatalog(newValue.getFileName());
            }
        }
    };

    private ChangeListener<Toggle> toggleChangeListener = new ChangeListener<Toggle>() {
        @Override
        public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
            switch ((StringGenerationType) newValue.getUserData()) {
                case REGEX:
                    catalogChoiceBox.disableProperty().set(true);
                    templatesChoiceBox.disableProperty().set(false);
                    generatorTextField.disableProperty().set(true);
                    stringGenerator.setStringGenerationType(StringGenerationType.REGEX);
                    break;
                case CATALOG:
                    catalogChoiceBox.disableProperty().set(false);
                    templatesChoiceBox.disableProperty().set(true);
                    generatorTextField.disableProperty().set(true);
                    stringGenerator.setStringGenerationType(StringGenerationType.CATALOG);
                    break;
                case CUSTOM:
                    catalogChoiceBox.disableProperty().set(true);
                    templatesChoiceBox.disableProperty().set(true);
                    generatorTextField.disableProperty().set(false);
                    stringGenerator.setStringGenerationType(StringGenerationType.CUSTOM);
                    break;
            }
        }
    };

    @FXML
    private void initialize(){
        catalogRadioButton.setUserData(StringGenerationType.CATALOG);
        templateRadioButton.setUserData(StringGenerationType.REGEX);
        customizedRadioButton.setUserData(StringGenerationType.CUSTOM);
    }

    public void unbindValues(Generator generator){
        stringGenerator = (StringGenerator)generator;
        catalogChoiceBox.getSelectionModel().selectedItemProperty().removeListener(catalogChoiceBoxListener);
        templatesChoiceBox.getSelectionModel().selectedItemProperty().removeListener(templatesChoiceBoxListener);
        stringToggleGroup.selectedToggleProperty().removeListener(toggleChangeListener);
        generatorTextField.textProperty().unbindBidirectional(stringGenerator.generatorStringProperty());
    }

    public void setGenerator(Generator generator, String type){
        stringGenerator = (StringGenerator)generator;
        stringTypeLabel.setText(type.toLowerCase());
        catalogChoiceBox.getSelectionModel().selectedItemProperty().addListener(catalogChoiceBoxListener);
        templatesChoiceBox.getSelectionModel().selectedItemProperty().addListener(templatesChoiceBoxListener);
        stringToggleGroup.selectedToggleProperty().addListener(toggleChangeListener);

        ObservableList<RegexTemplate> regexTemplates = FXCollections.observableArrayList();
        regexTemplates.add(new RegexTemplate("email", "[a-z]{5,8}\\.[a-z]{5,8}\\@[a-z]{3,4}\\.com"));
        regexTemplates.add(new RegexTemplate("number", "[0-9]{5,8}"));
        regexTemplates.add(new RegexTemplate("guid", "[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}"));
        templatesChoiceBox.setItems(regexTemplates);


        ObservableList<Catalog> catalogs = FXCollections.observableArrayList();
        catalogs.add(new Catalog("Male names", "maleNames.txt"));
        catalogs.add(new Catalog("Female names", "femaleNames.txt"));
        catalogs.add(new Catalog("Names", "names.txt"));
        catalogs.add(new Catalog("Countries", "country.txt"));
        catalogs.add(new Catalog("Cities", "city.txt"));
        catalogs.add(new Catalog("Ženska imena", "zenskaImena.txt"));
        catalogs.add(new Catalog("Muška imena", "muskaImena.txt"));
        catalogs.add(new Catalog("Imena", "imena.txt"));
        catalogs.add(new Catalog("Gradovi", "gradovi.txt"));
        catalogs.add(new Catalog("Države", "drzave.txt"));
        catalogChoiceBox.setItems(catalogs);
        catalogChoiceBox.getItems().clear();
        File f = new File(Constants.CatalogLocation);
        Set<String> customCatalogs = new HashSet<>(Arrays.asList(f.list()));
        customCatalogs.stream().forEach(s ->
            catalogChoiceBox.getItems().add(new Catalog(s.replace(".txt",""), s)));


        generatorTextField.textProperty().bindBidirectional(stringGenerator.generatorStringProperty());

        //customizedRadioButton.selectedProperty().set(true);
        StringGenerationType stringGenerationType = stringGenerator.getStringGenerationType();
        switch (stringGenerationType){
            case REGEX:
                stringToggleGroup.selectToggle(templateRadioButton);
                RegexTemplate regex = templatesChoiceBox.getItems().stream()
                        .filter(e -> e.getRegex().equals(stringGenerator.getGeneratorString()))
                        .findFirst().get();
                templatesChoiceBox.getSelectionModel().select(regex);
                break;
            case CATALOG:
                stringToggleGroup.selectToggle(catalogRadioButton);
                Catalog catalog = catalogChoiceBox.getItems().stream()
                        .filter(e -> e.getFileName().equals(stringGenerator.getCatalogName()))
                        .findFirst().get();
                catalogChoiceBox.getSelectionModel().select(catalog);
                break;
            case CUSTOM:
                stringToggleGroup.selectToggle(customizedRadioButton);
                break;
        }

    }
}
