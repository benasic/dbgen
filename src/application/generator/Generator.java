package application.generator;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, include = JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = NumberGenerator.class, name = "numberGenerator"),
        @JsonSubTypes.Type(value = StringGenerator.class, name = "stringGenerator"),
        @JsonSubTypes.Type(value = DateGenerator.class, name = "dateGenerator"),
        @JsonSubTypes.Type(value = BooleanGenerator.class, name = "booleanGenerator")
})
public interface Generator {

    Object generate();
    void initiateGenerator();
}
