package application.generator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.uncommons.maths.random.*;

public class IntegerGenerator implements Generator {

    private StringProperty minNumberDiscreteUniform;
    private int minNumberDiscreteUniformInt;
    private StringProperty maxNumberDiscreteUniform;
    private int maxNumberDiscreteUniformInt;

    private StringProperty numberOfTrailsBinomial;
    private int numberOfTrailsBinominalInt;


    private StringProperty probabilityBinomial;
    private double probabilityBinomialDouble;

    private StringProperty seedString;

    private DistributionType distributionType;


    SeedGenerator seedGenerator = DefaultSeedGenerator.getInstance();
    private DiscreteUniformGenerator discreteUniformGenerator = null;
    private BinomialGenerator binomialGenerator = null;

    public IntegerGenerator(){
        minNumberDiscreteUniform = new SimpleStringProperty("0");
        maxNumberDiscreteUniform = new SimpleStringProperty("10000");
        numberOfTrailsBinomial = new SimpleStringProperty("10");
        probabilityBinomial = new SimpleStringProperty("0.248");
        seedString = new SimpleStringProperty();
    }

    public void initiateGenerator(){

        MersenneTwisterRNG mersenneTwisterRNG = null;
        try {
            mersenneTwisterRNG = new MersenneTwisterRNG(seedGenerator);
        } catch (SeedException e) {
            e.printStackTrace();
        }

        if(distributionType == DistributionType.UNIFORM){
            minNumberDiscreteUniformInt = Integer.parseInt(minNumberDiscreteUniform.get());
            maxNumberDiscreteUniformInt = Integer.parseInt(maxNumberDiscreteUniform.get());
            discreteUniformGenerator = new DiscreteUniformGenerator(minNumberDiscreteUniformInt, maxNumberDiscreteUniformInt, mersenneTwisterRNG);

        }
        else if(distributionType == DistributionType.BINOMIAL){
            numberOfTrailsBinominalInt = Integer.parseInt(numberOfTrailsBinomial.get());
            probabilityBinomialDouble = Double.parseDouble(probabilityBinomial.get());
            binomialGenerator = new BinomialGenerator(numberOfTrailsBinominalInt, probabilityBinomialDouble, mersenneTwisterRNG);
        }
    }

    public Integer generate(){
        if(distributionType == DistributionType.UNIFORM){
            return discreteUniformGenerator.nextValue();
        }
        else if(distributionType == DistributionType.BINOMIAL){
            return binomialGenerator.nextValue();
        }
        return null;
    }

    // Getters and setters

    //Distribution Type

    public DistributionType getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(DistributionType distributionType) {
        this.distributionType = distributionType;
    }

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

    // Number Of Trails Binomial Property

    public String getNumberOfTrailsBinomial() {
        return numberOfTrailsBinomial.get();
    }

    public StringProperty numberOfTrailsBinomialProperty() {
        return numberOfTrailsBinomial;
    }

    public void setNumberOfTrailsBinomial(String numberOfTrailsBinomial) {
        this.numberOfTrailsBinomial.set(numberOfTrailsBinomial);
    }

    // Probability Binomial Property

    public String getProbabilityBinomial() {
        return probabilityBinomial.get();
    }

    public StringProperty probabilityBinomialProperty() {
        return probabilityBinomial;
    }

    public void setProbabilityBinomial(String probabilityBinomial) {
        this.probabilityBinomial.set(probabilityBinomial);
    }



}
