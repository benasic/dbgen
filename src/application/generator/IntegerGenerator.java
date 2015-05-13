package application.generator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.uncommons.maths.random.DefaultSeedGenerator;
import org.uncommons.maths.random.SeedGenerator;

import java.util.Random;

public class IntegerGenerator implements Generator {

    private StringProperty minNumberDiscreteUniform;
    private int minNumberDiscreteUniformInt;
    private StringProperty maxNumberDiscreteUniform;
    private int maxNumberDiscreteUniformInt;
    private StringProperty seedString;


    SeedGenerator seedGenerator = DefaultSeedGenerator.getInstance();
    private Random random = new Random();

    public IntegerGenerator(){
        minNumberDiscreteUniform = new SimpleStringProperty();
        maxNumberDiscreteUniform = new SimpleStringProperty();
        seedString = new SimpleStringProperty();
    }

    public void initiateGenerator(){
        minNumberDiscreteUniformInt = Integer.parseInt(minNumberDiscreteUniform.get());
        maxNumberDiscreteUniformInt = Integer.parseInt(maxNumberDiscreteUniform.get());
    }

    public Integer generate(){
        return random.nextInt();
    }

    // Getters and setters

    // min Number Discrete Uniform

    public String getMinNumberDiscreteUniform() {
        return minNumberDiscreteUniform.get();
    }

    public StringProperty minNumberDiscreteUniformProperty() {
        return minNumberDiscreteUniform;
    }

    public void setMinNumberDiscreteUniform(String minNumberDiscreteUniform) {
        this.minNumberDiscreteUniform.set(minNumberDiscreteUniform);
    }

    // max Number Discrete Uniform

    public String getMaxNumberDiscreteUniform() {
        return maxNumberDiscreteUniform.get();
    }

    public StringProperty maxNumberDiscreteUniformProperty() {
        return maxNumberDiscreteUniform;
    }

    public void setMaxNumberDiscreteUniform(String maxNumberDiscreteUniform) {
        this.maxNumberDiscreteUniform.set(maxNumberDiscreteUniform);
    }



}
