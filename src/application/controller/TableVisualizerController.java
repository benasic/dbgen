package application.controller;

import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.Map;

public class TableVisualizerController {

    @FXML
    private VBox VBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane anchorPane;

    private BarChart<Number,String> barChart = null;



    @FXML
    private void initialize(){
        scrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                    anchorPane.setMinWidth(newValue.doubleValue() - 30);
                }
        );
    }

    public void init(Map<String, Integer> rowCount){
        CategoryAxis yAxis = new CategoryAxis();
        yAxis.setLabel("Tables");
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Rows");
        barChart = new BarChart<>(xAxis, yAxis);
        barChart.setLegendVisible(false);
        XYChart.Series<Number, String> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : rowCount.entrySet()){
            XYChart.Data<Number, String> data = new XYChart.Data<>(stringIntegerEntry.getValue(), stringIntegerEntry.getKey());
            data.nodeProperty().addListener((ov, oldNode, node) -> {
                if (node != null) {
                    displayLabelForData(data);
                }
            });
            series.getData().add(data);

        }
        barChart.setPrefHeight(rowCount.size() * 50);
        barChart.getData().add(series);
        VBox.getChildren().add(barChart);

    }

    private void displayLabelForData(XYChart.Data<Number, String> data) {
        final Node node = data.getNode();
        final Text dataText = new Text(data.getXValue().toString());
        node.parentProperty().addListener((ov, oldParent, parent) -> {
            Group parentGroup = (Group) parent;
            parentGroup.getChildren().add(dataText);
        });

        node.boundsInParentProperty().addListener((ov, oldBounds, bounds) -> {
            if(dataText.prefHeight(-1) < bounds.getWidth()){
                dataText.setLayoutX(
                        Math.round(
                                bounds.getMinX() + bounds.getWidth() - dataText.prefWidth(-1)
                        )
                );
            }
            else{
                dataText.setLayoutX(
                        Math.round(
                                bounds.getMinX() + bounds.getWidth() + dataText.prefWidth(-1)
                        )
                );
            }

            dataText.setLayoutY(
                    Math.round(
                            bounds.getMinY() + dataText.prefHeight(-1)
                    )
            );
        });
    }
}
