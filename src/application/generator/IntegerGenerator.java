package application.generator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Random;

public class IntegerGenerator implements Generator {

    private IntegerProperty generatorIntegerProperty;
    private Random random = new Random();

    public IntegerGenerator(){
        generatorIntegerProperty = new SimpleIntegerProperty(65);
        random.setSeed(generatorIntegerProperty.getValue());
    }

    public Integer generate(){
        return random.nextInt();
    }

    public IntegerProperty getGeneratorIntegerProperty(){
        return generatorIntegerProperty;
    }
}
