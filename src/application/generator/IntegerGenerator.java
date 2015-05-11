package application.generator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.uncommons.maths.random.DefaultSeedGenerator;
import org.uncommons.maths.random.SeedGenerator;

import java.util.Random;

public class IntegerGenerator implements Generator {

    private IntegerProperty minNumberProperty;
    private StringProperty seedString;

    SeedGenerator seedGenerator = DefaultSeedGenerator.getInstance();
    private Random random = new Random();

    public IntegerGenerator(){
        minNumberProperty = new SimpleIntegerProperty();
        seedString = new SimpleStringProperty();
    }

    public Integer generate(){
        return random.nextInt();
    }

    // Geters and setters

    public int getMinNumberProperty() {
        return minNumberProperty.get();
    }

    public IntegerProperty minNumberPropertyProperty() {
        return minNumberProperty;
    }

    public void setMinNumberProperty(int minNumberProperty) {
        this.minNumberProperty.set(minNumberProperty);
    }


}
