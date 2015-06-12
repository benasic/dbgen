package application.controller;

import application.DatabaseTools;
import application.model.ColumnInfo;
import application.model.helper.ForeignKey;
import com.mxgraph.layout.hierarchical.mxHierarchicalLayout;
import com.mxgraph.layout.mxParallelEdgeLayout;
import com.mxgraph.swing.mxGraphComponent;
import com.mxgraph.util.mxConstants;
import com.mxgraph.view.mxGraph;
import javafx.collections.ObservableList;
import javafx.embed.swing.SwingNode;
import javafx.fxml.FXML;

import java.util.*;

public class DatabaseVisualizerController {

    @FXML
    private SwingNode swingNode;

    @FXML
    private void initialize() {

    }

    private List<ColumnInfo> columnInfoList = null;
    private Set<String> tableNameSet = new HashSet<>();
    private Map<String, Set<String>> referencedTablesMap = new HashMap<>();
    private Map<String, Object> vertexMap = new HashMap<>();

    private String getTableNameByHash(String hash){
        String tableName = null;
        for(ColumnInfo columnInfo : columnInfoList){
            if(columnInfo.getHash().equals(hash)){
                tableName = columnInfo.getTableName();
            }
        }
        return tableName;
    }

    public void init(ObservableList<ColumnInfo> columnInfoList){
        this.columnInfoList = columnInfoList;
        for(ColumnInfo columnInfo : columnInfoList){
            if(!columnInfo.getTableName().equals("")){
                tableNameSet.add(columnInfo.getTableName());
                if(columnInfo.getIsForeignKey()){
                    ForeignKey foreignKey = DatabaseTools.foreignKeyMap.get(columnInfo.getHash());
                    String primaryKeyTableName = foreignKey.getPrimaryKey().getTableName();
                    if(referencedTablesMap.containsKey(columnInfo.getTableName())){
                        referencedTablesMap.get(columnInfo.getTableName()).add(primaryKeyTableName);
                    }
                    else{
                        Set<String> set = new HashSet<>();
                        set.add(primaryKeyTableName);
                        referencedTablesMap.put(columnInfo.getTableName(), set);
                    }
                }
            }
        }


        createAndSetSwingContent();
    }

    private void createAndSetSwingContent(){
        mxGraph graph = new mxGraph();
        Object parent = graph.getDefaultParent();
        graph.getModel().beginUpdate();
        graph.setCellsEditable(false);

        Map<String, Object> edgeStyle = new HashMap<String, Object>();
        edgeStyle.put(mxConstants.STYLE_ROUNDED, true);
        edgeStyle.put(mxConstants.STYLE_ORTHOGONAL, true);
        edgeStyle.put(mxConstants.STYLE_EDGE, mxConstants.EDGESTYLE_ENTITY_RELATION); // <-- This is what you want
        edgeStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_CONNECTOR);
        edgeStyle.put(mxConstants.STYLE_ENDARROW, mxConstants.ARROW_CLASSIC);
        edgeStyle.put(mxConstants.STYLE_VERTICAL_ALIGN, mxConstants.ALIGN_MIDDLE);
        edgeStyle.put(mxConstants.STYLE_ALIGN, mxConstants.ALIGN_CENTER);
        edgeStyle.put(mxConstants.STYLE_STROKECOLOR, "#6482B9");
        edgeStyle.put(mxConstants.STYLE_FONTCOLOR, "#446299");
        Map<String, Object> vertexStyle = new HashMap<String, Object>();
        vertexStyle.put(mxConstants.STYLE_SHAPE, mxConstants.SHAPE_RECTANGLE);

        graph.getStylesheet().setDefaultEdgeStyle(edgeStyle);
        //graph.getStylesheet().setDefaultVertexStyle(vertexStyle);

        try
        {
            int i = 0;
            for(String tableName : tableNameSet){
                Object vertex = graph.insertVertex(parent, null, tableName, i, i, 150,50,"swimlane;rounded=1;");
                vertexMap.put(tableName, vertex);
                i+=100;
            }
            for(String tableName : tableNameSet){
                if(referencedTablesMap.containsKey(tableName)){
                    for(String referencedTable : referencedTablesMap.get(tableName)){
                        graph.insertEdge(parent, null, "", vertexMap.get(tableName), vertexMap.get(referencedTable));
                    }
                }
            }

            //Object v1 = graph.insertVertex(parent, null, "Hello", 0, 0, 100,50);
            //Object v2 = graph.insertVertex(parent, null, "World!", 0, 0, 100, 50);
            //graph.insertEdge(parent, null, "Edge", v1, v2);
            //graph.insertEdge(parent, null, "Edge1", v1, v2);
        }
        finally
        {
            graph.getModel().endUpdate();
        }

        graph.setCellsEditable(false);
        //new mxOrthogonalLayout(graph).execute(graph.getDefaultParent());


        new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
        new mxParallelEdgeLayout(graph).execute(graph.getDefaultParent());


        mxGraphComponent graphComponent = new mxGraphComponent(graph);
        swingNode.setContent(graphComponent);

        //new mxHierarchicalLayout(graph).execute(graph.getDefaultParent());
        //new mxCircleLayout(graph).execute(graph.getDefaultParent());
    }
}
