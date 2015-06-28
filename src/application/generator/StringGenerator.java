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
    private int maxLength;

    private StringGenerationType stringGenerationType = StringGenerationType.CUSTOM;

    public StringGenerator(){
        generatorString = new SimpleStringProperty("[a-z0-9]{10,15}");
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
                String value = generex.random();
                if(value.length() > maxLength){
                    value = value.substring(0, maxLength);
                }
                return value;
            case CATALOG:
                return catalogNames.get(randomDataGenerator.nextInt(0, catalogSize));
            default:
                return "";
        }
    }

    public void setAppropriateGeneratorContext(String contextName, String maxLength){
        this.maxLength = Integer.parseInt(maxLength);
        if(contextName.toLowerCase().contains("guid")){
            setStringGenerationType(StringGenerationType.REGEX);
            setGeneratorString("[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}");
            return;
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

    // max length

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

}
