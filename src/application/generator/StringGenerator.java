package application.generator;

import application.DbGen;
import application.model.Catalog;
import com.mifmif.common.regex.Generex;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.math3.random.RandomDataGenerator;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class StringGenerator implements Generator {

    private StringProperty generatorString;

    private Generex generex = null;

    private Catalog catalog;
    private List<String> catalogNames = new ArrayList<>();
    private RandomDataGenerator randomDataGenerator = new RandomDataGenerator();
    private int catalogSize;

    private StringGenerationType stringGenerationType = StringGenerationType.CUSTOM;

    public StringGenerator(){
        generatorString = new SimpleStringProperty("[0-7]-([a-k]|[l-z]{5,8})");
    }

    public void initiateGenerator(){
        switch (stringGenerationType){
            case CATALOG:
                try {
                    Stream<String> lines = Files.lines(Paths.get(DbGen.class.getClassLoader().getResource("application/resources/catalogs/" + catalog.getFileName()).toURI()));
                    catalogNames = lines.collect(Collectors.toList());
                    lines.close();
                    catalogSize = catalogNames.size() - 1;
                } catch (IOException | NullPointerException | URISyntaxException e) {
                    e.printStackTrace();
                }
                break;
            case REGEX:
            case CUSTOM:
                generex = new Generex(generatorString.get());
                break;
        }
    }

    public String generate(){
        switch (stringGenerationType){
            case REGEX:
            case CUSTOM:
                return generex.random();
            case CATALOG:
                return catalogNames.get(randomDataGenerator.nextInt(0,catalogSize));
            default:
                return "";
        }
    }

    public String getGeneratorString() {
        return generatorString.get();
    }

    public StringProperty generatorStringProperty() {
        return generatorString;
    }

    //@JsonProperty("generatorString")
    public void setGeneratorString(String generatorString) {
        this.generatorString.set(generatorString);
    }

    // String Generation Type

    public StringGenerationType getStringGenerationType() {
        return stringGenerationType;
    }

    public void setStringGenerationType(StringGenerationType stringGenerationType) {
        this.stringGenerationType = stringGenerationType;
    }

    // Catalog

    public Catalog getCatalog() {
        return catalog;
    }

    public void setCatalog(Catalog catalog) {
        this.catalog = catalog;
    }


}
