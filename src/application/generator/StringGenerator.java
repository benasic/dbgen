package application.generator;

import com.mifmif.common.regex.Generex;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class StringGenerator implements Generator {

    private StringProperty generatorStringProperty;

    private Generex generex = null;

    public StringGenerator(){
        generatorStringProperty = new SimpleStringProperty("[0-7]-([a-k]|[l-z]{5,8})");
    }

    public void initiateGenerator(){
        generex = new Generex(generatorStringProperty.get());
    }

    public String generate(){
        return generex.random();
    }

    public StringProperty getGeneratorStringProperty(){
        return generatorStringProperty;
    }
}
