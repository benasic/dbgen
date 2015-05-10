package application.generator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.Random;

public class NumberGenerator implements Generator {

    private IntegerProperty generatorNumberProperty;
    private Random random = new Random();

    public NumberGenerator(){
        generatorNumberProperty = new SimpleIntegerProperty(65);
        random.setSeed(generatorNumberProperty.getValue());
    }

    public Integer generate(){
        return random.nextInt();
    }

    public IntegerProperty getGeneratorNumberProperty(){
        return generatorNumberProperty;
    }
}
