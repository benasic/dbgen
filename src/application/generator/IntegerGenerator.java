package application.generator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.uncommons.maths.random.*;

public class IntegerGenerator implements Generator {

    private StringProperty minNumberDiscreteUniform;
    private StringProperty maxNumberDiscreteUniform;

    private StringProperty numberOfTrailsBinomial;
    private StringProperty probabilityBinomial;

    private StringProperty meanPoisson;

    // default value
    private DistributionType distributionType = DistributionType.UNIFORM;

    private NumberType numberType;

    SeedGenerator seedGenerator = DefaultSeedGenerator.getInstance();

    private DiscreteUniformGenerator discreteUniformGenerator = null;
    private BinomialGenerator binomialGenerator = null;
    private PoissonGenerator poissonGenerator = null;

    public IntegerGenerator(){
        minNumberDiscreteUniform = new SimpleStringProperty("0");
        maxNumberDiscreteUniform = new SimpleStringProperty("10000");
        numberOfTrailsBinomial = new SimpleStringProperty("10");
        probabilityBinomial = new SimpleStringProperty("0.248");
        meanPoisson = new SimpleStringProperty("56.8");
    }

    public void initiateGenerator(){

        MersenneTwisterRNG mersenneTwisterRNG = null;
        try {
            mersenneTwisterRNG = new MersenneTwisterRNG(seedGenerator);
        } catch (SeedException e) {
            e.printStackTrace();
        }

        if(distributionType == DistributionType.UNIFORM){
            int minNumberDiscreteUniformInt = Integer.parseInt(minNumberDiscreteUniform.get());
            int  maxNumberDiscreteUniformInt = Integer.parseInt(maxNumberDiscreteUniform.get());
            discreteUniformGenerator = new DiscreteUniformGenerator(minNumberDiscreteUniformInt, maxNumberDiscreteUniformInt, mersenneTwisterRNG);
        }
        else if(distributionType == DistributionType.BINOMIAL){
            int numberOfTrailsBinomialInt = Integer.parseInt(numberOfTrailsBinomial.get());
            double probabilityBinomialDouble = Double.parseDouble(probabilityBinomial.get());
            binomialGenerator = new BinomialGenerator(numberOfTrailsBinomialInt, probabilityBinomialDouble, mersenneTwisterRNG);
        }
        else if(distributionType == DistributionType.POISSON){
            double meanPoissonDouble = Double.parseDouble(meanPoisson.get());
            poissonGenerator = new PoissonGenerator(meanPoissonDouble, mersenneTwisterRNG);
        }
    }

    public Integer generate(){
        if(distributionType == DistributionType.UNIFORM){
            return discreteUniformGenerator.nextValue();
        }
        else if(distributionType == DistributionType.BINOMIAL){
            return binomialGenerator.nextValue();
        }
        else if(distributionType == DistributionType.POISSON){
            return poissonGenerator.nextValue();
        }
        return 1;
    }

    // Getters and setters

    //Distribution Type

    public DistributionType getDistributionType() {
        return distributionType;
    }

    public void setDistributionType(DistributionType distributionType) {
        this.distributionType = distributionType;
    }

    // Number type

    public NumberType getNumberType() {
        return numberType;
    }

    public void setNumberType(NumberType numberType) {
        this.numberType = numberType;
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

    // Mean Poisson

    public String getMeanPoisson() {
        return meanPoisson.get();
    }

    public StringProperty meanPoissonProperty() {
        return meanPoisson;
    }

    public void setMeanPoisson(String meanPoisson) {
        this.meanPoisson.set(meanPoisson);
    }




}
