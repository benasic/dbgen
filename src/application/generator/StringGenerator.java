package application.generator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StringGenerator implements Generator {

    private StringProperty generatorStringProperty;

    public StringGenerator(){
        generatorStringProperty = new SimpleStringProperty("Filip");
    }

    public String generate(){

        return generatorStringProperty.getValue();
    }

    public StringProperty getGeneratorStringProperty(){
        return generatorStringProperty;
    }
}
