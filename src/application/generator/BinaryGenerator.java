package application.generator;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import org.apache.commons.math3.random.MersenneTwister;
import org.apache.commons.math3.random.RandomGenerator;
import org.uncommons.maths.random.MersenneTwisterRNG;

import javax.sql.rowset.serial.SerialBlob;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class BinaryGenerator implements Generator {

    private StringProperty randomSize;

    private int randomSizeInt;
    private List<File> imageList = new ArrayList<>();
    private List<Blob> imageBlobList = new ArrayList<>();

    private RandomGenerator randomGenerator;
    private BinaryGenerationType binaryGenerationType = BinaryGenerationType.BINARY;
    private Random random = new MersenneTwisterRNG();

    public BinaryGenerator(){
        randomGenerator = new MersenneTwister();
        randomSize = new SimpleStringProperty("5");
    }

    @Override
    public Object generate() {
        switch (binaryGenerationType){
            case BINARY:
                byte[] bytes = new byte[randomSizeInt];
                randomGenerator.nextBytes(bytes);
                return bytes;
            case BLOB:
                return imageBlobList.get(random.nextInt(imageBlobList.size()));
            default:
                return null;
        }
    }

    @Override
    public void initiateGenerator() {

        switch (binaryGenerationType){
            case BINARY:
                randomSizeInt = Integer.parseInt(randomSize.get());
                break;
            case BLOB:
                imageBlobList.clear();
                for(File file : imageList){
                    if(file.exists()){
                        try {
                            byte[] bytes = Files.readAllBytes(file.toPath());
                            imageBlobList.add(new SerialBlob(bytes));
                        } catch (IOException | SQLException e) {
                            e.printStackTrace();
                        }
                    }
                }
                if(imageBlobList.isEmpty()){
                    try {
                        imageBlobList.add(new SerialBlob(new byte[10]));
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
                break;
            default:
                break;
        }

    }

    // Random Size

    public String getRandomSize() {
        return randomSize.get();
    }

    public StringProperty randomSizeProperty() {
        return randomSize;
    }

    public void setRandomSize(String randomSize) {
        this.randomSize.set(randomSize);
    }

    // Binary Generation Type

    public BinaryGenerationType getBinaryGenerationType() {
        return binaryGenerationType;
    }

    public void setBinaryGenerationType(BinaryGenerationType binaryGenerationType) {
        this.binaryGenerationType = binaryGenerationType;
    }

    // Image List

    public List<File> getImageList() {
        return imageList;
    }

    public void setImageList(List<File> imageList) {
        this.imageList = imageList;
    }

}
