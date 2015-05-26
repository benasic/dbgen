package application.utils;

import application.model.ColumnInfo;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class JSON {

    public static void createJSON(List<ColumnInfo> columnInfos, String name){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        //objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        //ColumnInfo columnInfo1 = null;
        //List<ColumnInfo> columnInfos = new ArrayList<>();
        try {
            objectMapper.writeValue(new File("d:\\" + name + ".json"), columnInfos);
            //columnInfos = objectMapper.readValue(new File("d:\\user.json"), new TypeReference<ArrayList<ColumnInfo>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        //ObservableList<ColumnInfo> columnInfos1 = FXCollections.observableArrayList(columnInfos);
    }

    public static ObservableList<ColumnInfo> createJavaObjects(String name){
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<ColumnInfo> columnInfos = null;
        try {
            columnInfos = objectMapper.readValue(new File("d:\\" + name + ".json"), new TypeReference<ArrayList<ColumnInfo>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }

        return FXCollections.observableArrayList(columnInfos);
    }
}
