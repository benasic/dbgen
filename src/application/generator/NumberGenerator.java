package application.generator;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import org.uncommons.maths.random.*;

import java.util.Arrays;
import java.util.Random;

public class NumberGenerator implements Generator {

    private IntegerProperty generatorNumberProperty;
    private Random random = new Random();

    SeedGenerator seedGenerator = DefaultSeedGenerator.getInstance();

    private org.uncommons.maths.number.NumberGenerator<Integer> a;

    @Override
    public void initiateGenerator() {

    }

    public NumberGenerator(){
        generatorNumberProperty = new SimpleIntegerProperty(65);
        random.setSeed(generatorNumberProperty.getValue());
        try {
            System.out.println(Arrays.toString(seedGenerator.generateSeed(128)));
            Random ad = new MersenneTwisterRNG(DefaultSeedGenerator.getInstance());
            DiscreteUniformGenerator aaa = new DiscreteUniformGenerator(1,30, ad);
            System.out.println("novi");
            for (int i = 0; i < 30; i++)
                System.out.println(aaa.nextValue());
        } catch (SeedException e) {
            e.printStackTrace();
        }
    }

    public Integer generate(){
        return random.nextInt();
    }

    public IntegerProperty getGeneratorNumberProperty(){
        return generatorNumberProperty;
    }
}
