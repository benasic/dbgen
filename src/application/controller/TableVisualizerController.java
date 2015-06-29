package application.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Map;

public class TableVisualizerController {

    @FXML
    private VBox VBox;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private AnchorPane anchorPane;

    // for last reference of bar chart
    private BarChart<String,Number> bc = null;
    // for previous reference of bar chart
    private BarChart<String,Number> previousBc = new BarChart<>(new CategoryAxis(), new NumberAxis());


    @FXML
    private void initialize(){
        scrollPane.widthProperty().addListener((observable, oldValue, newValue) -> {
                    anchorPane.setMinWidth(newValue.doubleValue() - 30);

                }
        );

    }

    public void init(Map<String, Integer> rowCount){
        int count = 0;
        final int split = 8;
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Tables");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Rows");
        bc = new BarChart<>(xAxis,yAxis);
        XYChart.Series<String,Number> series = new XYChart.Series<>();
        for (Map.Entry<String, Integer> stringIntegerEntry : rowCount.entrySet()){

            // save existing and add new
            if(count != 0 && count % split == 0){
                bc.setLegendVisible(false);
                bc.getData().add(series);
                VBox.getChildren().add(bc);
                xAxis = new CategoryAxis();
                xAxis.setLabel("Tables");
                yAxis = new NumberAxis();
                yAxis.setLabel("Rows");
                // keep last reference
                previousBc = bc;
                bc = new BarChart<>(xAxis,yAxis);
                series = new XYChart.Series<>();
            }
            series.getData().add(new XYChart.Data<>(stringIntegerEntry.getKey(), stringIntegerEntry.getValue()));
            count++;
        }
        // add last chart
        if(count == split || count % split != 0){
            bc.setLegendVisible(false);
            bc.getData().add(series);
            VBox.getChildren().add(bc);
        }

        previousBc.widthProperty().addListener((observable, oldValue, newValue) -> {
            if(bc.getData().size() !=0){
                Integer columnNumberInPrevious = previousBc.getData().get(0).getData().size();
                Integer columnSize = bc.getData().get(0).getData().size();
                double previousWidth = previousBc.getWidth();
                double additionalWidth = 20 * (columnNumberInPrevious - columnSize) * (1d - columnSize.doubleValue() / columnNumberInPrevious);
                bc.setMaxWidth(columnSize * (previousWidth / columnNumberInPrevious) + additionalWidth);
            }
        });

    }
}
