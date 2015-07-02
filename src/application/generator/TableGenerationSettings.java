package application.generator;

import javafx.beans.property.*;

public class TableGenerationSettings {

    private StringProperty numberOfDataToGenerateString;
    private IntegerProperty numberOfDataToGenerate;
    private BooleanProperty allowGeneration;

    public TableGenerationSettings(){
        numberOfDataToGenerateString = new SimpleStringProperty("1000");
        numberOfDataToGenerate = new SimpleIntegerProperty(1000);
        allowGeneration = new SimpleBooleanProperty(true);

        numberOfDataToGenerateString.addListener((observable, oldValue, newValue) -> {
                    if(!oldValue.equals(newValue)){
                        numberOfDataToGenerate.setValue(Integer.parseInt(newValue));
                    }
                }
        );

        numberOfDataToGenerate.addListener((observable, oldValue, newValue) -> {
                    if(!oldValue.equals(newValue)){
                        numberOfDataToGenerateString.setValue(newValue.toString());
                        if(newValue.intValue() == 0){
                            allowGeneration.setValue(false);
                        }
                    }
                }
        );

    }

    // Number Of Data To Generate String

    public String getNumberOfDataToGenerateString() {
        return numberOfDataToGenerateString.get();
    }

    public StringProperty numberOfDataToGenerateStringProperty() {
        return numberOfDataToGenerateString;
    }

    public void setNumberOfDataToGenerateString(String numberOfDataToGenerateString) {
        this.numberOfDataToGenerateString.set(numberOfDataToGenerateString);
    }

    // Number Of Data To Generate

    public int getNumberOfDataToGenerate() {
        return numberOfDataToGenerate.get();
    }

    public IntegerProperty numberOfDataToGenerateProperty() {
        return numberOfDataToGenerate;
    }

    public void setNumberOfDataToGenerate(int numberOfDataToGenerate) {
        this.numberOfDataToGenerate.set(numberOfDataToGenerate);
    }

    // Allow Generation

    public boolean getAllowGeneration() {
        return allowGeneration.get();
    }

    public BooleanProperty allowGenerationProperty() {
        return allowGeneration;
    }

    public void setAllowGeneration(boolean allowGeneration) {
        this.allowGeneration.set(allowGeneration);
    }
}
