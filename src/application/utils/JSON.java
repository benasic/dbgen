package application.utils;

import application.Constants;
import application.model.ColumnInfo;
import application.model.ConnectionInfo;
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

    private final static ObjectMapper objectMapper = new ObjectMapper();

    public static void createJSONforColumnInfo(List<ColumnInfo> columnInfos, String name, boolean root){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            String extension = root ? "_tables.json" : "_columns.json";
            objectMapper.writeValue(new File(Constants.SaveLocation + name + extension), columnInfos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createJSONforConnectionInfo(ConnectionInfo connectionInfo, String name){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File(Constants.SaveLocation + name + "_connection.json"), connectionInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<ColumnInfo> createJavaObjectsforColumnInfo(String name, boolean root){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<ColumnInfo> columnInfos = null;
        try {
            String extension = root ? "_tables.json" : "_columns.json";
            columnInfos = objectMapper.readValue(new File(Constants.SaveLocation + name + extension), new TypeReference<ArrayList<ColumnInfo>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(columnInfos);
    }

    public static ConnectionInfo createJavaObjectsforConnectionInfo(String name){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        ConnectionInfo connectionInfo = null;
        try {
            connectionInfo = objectMapper.readValue(new File(Constants.SaveLocation + name + "_connection.json"), ConnectionInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connectionInfo;
    }
}
