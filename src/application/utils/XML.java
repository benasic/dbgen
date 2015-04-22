package application.utils;

import application.model.ColumnInfo;

import java.util.List;

public class XML {

    public static void createXML(List<ColumnInfo> columnInfos){
        for(ColumnInfo columnInfo : columnInfos){
            System.out.println(columnInfo);
        }
    }
}
