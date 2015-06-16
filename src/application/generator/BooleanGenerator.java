package application.generator;


import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class BooleanGenerator implements Generator{

    private BooleanGeneratorType booleanGeneratorType = BooleanGeneratorType.RANDOM;
    private DoubleProperty percentage = null;

    private List<Boolean> booleanList = new ArrayList<>();
    private int dataNumber;
    private int currentData;

    Random random = new Random(System.currentTimeMillis());

    public BooleanGenerator(){
        percentage = new SimpleDoubleProperty(60.00);
    }

    public void setDataNumber(int dataNumber){
        this.dataNumber = dataNumber;
        switch (booleanGeneratorType){
            case PERCENTAGE:
                // add true values
                for(int i = 0; i < dataNumber * percentage.get() / 100; i++){
                    booleanList.add(true);
                }
                // add false values
                while(booleanList.size() < dataNumber){
                    booleanList.add(false);
                }
                Collections.shuffle(booleanList);
                return;
            case RANDOM:
                for(int i = 0; i < dataNumber; i++){
                    booleanList.add(random.nextBoolean());
                }
        }
    }

    @Override
    public Object generate() {
        switch (booleanGeneratorType){
            case ONLY_TRUE:
                return true;
            case ONLY_FALSE:
                return false;
            case RANDOM:
            case PERCENTAGE:
                return booleanList.get(currentData++);
            default:
                return null;
        }
    }

    @Override
    public void initiateGenerator() {
        booleanList.clear();
        currentData = 0;
    }

    // Percentage

    public double getPercentage() {
        return percentage.get();
    }

    public DoubleProperty percentageProperty() {
        return percentage;
    }

    public void setPercentage(int percentage) {
        this.percentage.set(percentage);
    }

    // Boolean Generator Type

    public BooleanGeneratorType getBooleanGeneratorType() {
        return booleanGeneratorType;
    }

    public void setBooleanGeneratorType(BooleanGeneratorType booleanGeneratorType) {
        this.booleanGeneratorType = booleanGeneratorType;
    }
}
