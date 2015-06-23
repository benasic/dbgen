package application.model;

public class Catalog {


    private String name;
    private String fileName;

    @Override
    public String toString() {
        return name;
    }

    public Catalog(String name, String fileName){
        this.name = name;
        this.fileName = fileName;
    }

    public Catalog(){
    }

    // Name

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // File Name

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

}
