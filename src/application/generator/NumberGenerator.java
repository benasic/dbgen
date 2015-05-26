package application.generator;

import com.fasterxml.jackson.annotation.JsonProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.math3.random.RandomDataGenerator;
import org.uncommons.maths.random.*;

import java.math.BigDecimal;

public class NumberGenerator implements Generator {

    private StringProperty minNumberUniform;
    private StringProperty maxNumberUniform;

    private long minNumberDiscreteUniformLong;
    private long maxNumberDiscreteUniformLong;

    private StringProperty numberOfTrailsBinomial;
    private StringProperty probabilityBinomial;

    private StringProperty meanPoisson;

    private StringProperty meanNormally;
    private StringProperty standardDeviationNormally;

    private StringProperty rateExponential;

    // default value
    private DistributionType distributionType = DistributionType.UNIFORM;

    private NumberType numberType;

    SeedGenerator seedGenerator = DefaultSeedGenerator.getInstance();

    private DiscreteUniformGenerator discreteUniformGenerator = null;
    private ContinuousUniformGenerator continuousUniformGenerator = null;
    private BinomialGenerator binomialGenerator = null;
    private PoissonGenerator poissonGenerator = null;
    private GaussianGenerator gaussianGenerator = null;
    private ExponentialGenerator exponentialGenerator = null;

    private RandomDataGenerator randomDataGenerator = null;

    public NumberGenerator(){
        minNumberUniform = new SimpleStringProperty();
        maxNumberUniform = new SimpleStringProperty();

        numberOfTrailsBinomial = new SimpleStringProperty();
        probabilityBinomial = new SimpleStringProperty();

        meanPoisson = new SimpleStringProperty();

        meanNormally = new SimpleStringProperty();
        standardDeviationNormally = new SimpleStringProperty();

        rateExponential = new SimpleStringProperty();
    }

    public NumberGenerator(NumberType numberType){
        this.numberType = numberType;

        minNumberUniform = new SimpleStringProperty("0");
        maxNumberUniform = new SimpleStringProperty("10000");

        numberOfTrailsBinomial = new SimpleStringProperty("10");
        probabilityBinomial = new SimpleStringProperty("0.248");

        meanPoisson = new SimpleStringProperty("56.8");

        meanNormally = new SimpleStringProperty("25.7");
        standardDeviationNormally = new SimpleStringProperty("15.58");

        rateExponential = new SimpleStringProperty("0.5");
    }

    public void initiateGenerator(){

        MersenneTwisterRNG mersenneTwisterRNG = null;
        try {
            mersenneTwisterRNG = new MersenneTwisterRNG(seedGenerator);
        } catch (SeedException e) {
            e.printStackTrace();
        }

        if(distributionType == DistributionType.UNIFORM){
            switch (numberType){
                case INTEGER:
                case SMALLINT:
                case TINYINT:
                    int minNumberDiscreteUniformInt = Integer.parseInt(minNumberUniform.get());
                    int  maxNumberDiscreteUniformInt = Integer.parseInt(maxNumberUniform.get());
                    discreteUniformGenerator = new DiscreteUniformGenerator(minNumberDiscreteUniformInt, maxNumberDiscreteUniformInt, mersenneTwisterRNG);
                    break;
                case BIGINT:
                    minNumberDiscreteUniformLong = Long.parseLong(minNumberUniform.get());
                    maxNumberDiscreteUniformLong = Long.parseLong(maxNumberUniform.get());
                    randomDataGenerator = new RandomDataGenerator();
                    break;
                case REAL:
                    float minNumberDiscreteUniformFloat = Float.parseFloat(minNumberUniform.get());
                    float maxNumberDiscreteUniformFloat = Float.parseFloat(maxNumberUniform.get());
                    continuousUniformGenerator = new ContinuousUniformGenerator(minNumberDiscreteUniformFloat, maxNumberDiscreteUniformFloat, mersenneTwisterRNG);
                    break;
                case FLOAT:
                case DOUBLE:
                    double minNumberDiscreteUniformDouble = Double.parseDouble(minNumberUniform.get());
                    double maxNumberDiscreteUniformDouble = Double.parseDouble(maxNumberUniform.get());
                    continuousUniformGenerator = new ContinuousUniformGenerator(minNumberDiscreteUniformDouble, maxNumberDiscreteUniformDouble, mersenneTwisterRNG);
                    break;
                case DECIMAL:
                case NUMERIC:
                    double minNumberDiscreteUniformDecimal = Double.parseDouble(minNumberUniform.get());
                    double maxNumberDiscreteUniformDecimal = Double.parseDouble(maxNumberUniform.get());
                    continuousUniformGenerator = new ContinuousUniformGenerator(minNumberDiscreteUniformDecimal, maxNumberDiscreteUniformDecimal, mersenneTwisterRNG);
                    break;
            }
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
        else if(distributionType == DistributionType.NORMALLY){
            double meanNormallyDouble = Double.parseDouble(meanNormally.get());
            double standardDeviationNormallyDouble = Double.parseDouble(standardDeviationNormally.get());
            gaussianGenerator = new GaussianGenerator(meanNormallyDouble, standardDeviationNormallyDouble, mersenneTwisterRNG);
        }
        else if(distributionType == DistributionType.EXPONENTIAL){
            double rateExponentialDouble = Double.parseDouble(rateExponential.get());
            exponentialGenerator = new ExponentialGenerator(rateExponentialDouble, mersenneTwisterRNG);
        }
    }

    public Number generate(){
        if(distributionType == DistributionType.UNIFORM){
            switch (numberType){
                case INTEGER:
                case SMALLINT:
                case TINYINT:
                    return discreteUniformGenerator.nextValue();
                case BIGINT:
                    return randomDataGenerator.nextLong(minNumberDiscreteUniformLong, maxNumberDiscreteUniformLong);
                case REAL:
                    return new Float(continuousUniformGenerator.nextValue());
                case FLOAT:
                case DOUBLE:
                    return  continuousUniformGenerator.nextValue();
                case DECIMAL:
                case NUMERIC:
                    return BigDecimal.valueOf(continuousUniformGenerator.nextValue());
            }
        }
        else if(distributionType == DistributionType.BINOMIAL){
            return binomialGenerator.nextValue();
        }
        else if(distributionType == DistributionType.POISSON){
            return poissonGenerator.nextValue();
        }
        else if(distributionType == DistributionType.NORMALLY){
            return gaussianGenerator.nextValue();
        }
        else if(distributionType == DistributionType.EXPONENTIAL){
            return exponentialGenerator.nextValue();
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

    public String getMinNumberUniform() {
        return minNumberUniform.get();
    }

    public StringProperty minNumberUniformProperty() {
        return minNumberUniform;
    }

    @JsonProperty("minNumberUniform")
    public void setMinNumberUniform(String minNumberUniform) {
        this.minNumberUniform.set(minNumberUniform);
    }

    // max Number Discrete Uniform

    public String getMaxNumberUniform() {
        return maxNumberUniform.get();
    }

    public StringProperty maxNumberUniformProperty() {
        return maxNumberUniform;
    }

    @JsonProperty("maxNumberUniform")
    public void setMaxNumberUniform(String maxNumberUniform) {
        this.maxNumberUniform.set(maxNumberUniform);
    }

    // Number Of Trails Binomial Property

    public String getNumberOfTrailsBinomial() {
        return numberOfTrailsBinomial.get();
    }

    public StringProperty numberOfTrailsBinomialProperty() {
        return numberOfTrailsBinomial;
    }

    @JsonProperty("numberOfTrailsBinomial")
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

    @JsonProperty("probabilityBinomial")
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

    @JsonProperty("meanPoisson")
    public void setMeanPoisson(String meanPoisson) {
        this.meanPoisson.set(meanPoisson);
    }

    // Mean Normally

    public String getMeanNormally() {
        return meanNormally.get();
    }

    public StringProperty meanNormallyProperty() {
        return meanNormally;
    }

    @JsonProperty("meanNormally")
    public void setMeanNormally(String meanNormally) {
        this.meanNormally.set(meanNormally);
    }

    // Standard Deviation Normally

    public String getStandardDeviationNormally() {
        return standardDeviationNormally.get();
    }

    public StringProperty standardDeviationNormallyProperty() {
        return standardDeviationNormally;
    }

    @JsonProperty("standardDeviationNormally")
    public void setStandardDeviationNormally(String standardDeviationNormally) {
        this.standardDeviationNormally.set(standardDeviationNormally);
    }

    // Rate Exponential

    public String getRateExponential() {
        return rateExponential.get();
    }

    public StringProperty rateExponentialProperty() {
        return rateExponential;
    }

    @JsonProperty("rateExponential")
    public void setRateExponential(String rateExponential) {
        this.rateExponential.set(rateExponential);
    }


}
