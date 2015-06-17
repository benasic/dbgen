package application.generator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;

public class BinaryGenerator implements Generator {

    private StringProperty randomSize;
    private int randomSizeInt;
    private RandomGenerator randomGenerator;

    public BinaryGenerator(){
        randomGenerator = new MersenneTwister();
        randomSize = new SimpleStringProperty("5");
    }

    @Override
    public Object generate() {
        byte[] bytes = new byte[randomSizeInt];
        randomGenerator.nextBytes(bytes);
        return bytes;
    }

    @Override
    public void initiateGenerator() {
        randomSizeInt = Integer.parseInt(randomSize.get());
    }

    // Random Size

    public String getRandomSize() {
        return randomSize.get();
    }

    public StringProperty randomSizeProperty() {
        return randomSize;
    }

    public void setRandomSize(String randomSize) {
        this.randomSize.set(randomSize);
    }
}
