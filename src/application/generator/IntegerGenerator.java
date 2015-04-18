package application.generator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class IntegerGenerator implements Generator {

    private IntegerProperty generatorIntegerProperty;

    public IntegerGenerator(){
        generatorIntegerProperty = new SimpleIntegerProperty(65);
    }

    public Integer generate(){

        return generatorIntegerProperty.getValue();
    }

    public IntegerProperty getGeneratorIntegerProperty(){
        return generatorIntegerProperty;
    }
}
