package application.generator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.lang3.RandomStringUtils;

public class StringGenerator implements Generator {

    private StringProperty generatorStringProperty;

    public StringGenerator(){
        generatorStringProperty = new SimpleStringProperty("Filip");
    }

    public String generate(){

        return RandomStringUtils.randomAlphabetic(10);
    }

    public StringProperty getGeneratorStringProperty(){
        return generatorStringProperty;
    }
}
