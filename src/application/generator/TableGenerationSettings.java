package application.generator;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class TableGenerationSettings {

    private StringProperty numberOfDataToGenerate;
    private BooleanProperty allowGeneration;

    public TableGenerationSettings(){
        numberOfDataToGenerate = new SimpleStringProperty("1000");
        allowGeneration = new SimpleBooleanProperty(true);
    }

    // Number Of Data To Generate

    public String getNumberOfDataToGenerate() {
        return numberOfDataToGenerate.get();
    }

    public StringProperty numberOfDataToGenerateProperty() {
        return numberOfDataToGenerate;
    }

    public void setNumberOfDataToGenerate(String numberOfDataToGenerate) {
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
