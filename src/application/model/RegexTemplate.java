package application.model;

public class RegexTemplate {

    private String name;
    private String regex;

    public RegexTemplate(String name, String regex){
        this.name = name;
        this.regex = regex;
    }

    public String getRegex(){
        return regex;
    }

    @Override
    public String toString() {
        return name + " - " + regex;
    }
}
