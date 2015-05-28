package application.utils;

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


    public static void createJSONforColumnInfo(List<ColumnInfo> columnInfos, String name){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File("d:\\" + name + "_project.json"), columnInfos);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void createJSONforConnectionInfo(ConnectionInfo connectionInfo, String name){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            objectMapper.writeValue(new File("d:\\" + name + "_connection.json"), connectionInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static ObservableList<ColumnInfo> createJavaObjectsforColumnInfo(String name){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        List<ColumnInfo> columnInfos = null;
        try {
            columnInfos = objectMapper.readValue(new File("d:\\" + name + "_project.json"), new TypeReference<ArrayList<ColumnInfo>>() {});
        } catch (IOException e) {
            e.printStackTrace();
        }
        return FXCollections.observableArrayList(columnInfos);
    }

    public static ConnectionInfo createJavaObjectsforConnectionInfo(String name){
        objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        ConnectionInfo connectionInfo = null;
        try {
            connectionInfo = objectMapper.readValue(new File("d:\\" + name + "_connection.json"), ConnectionInfo.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return connectionInfo;
    }
}
