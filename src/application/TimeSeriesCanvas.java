package application;

import ch.imetrica.mdfa.series.MultivariateSeries;
import ch.imetrica.mdfa.series.TimeSeriesEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.Date;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;

public class TimeSeriesCanvas {

	

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static XYChart<DateAxis, NumberAxis> createAreaChart(MultivariateSeries series, int samples, String format) {
		
    	DateAxis xAxis = new DateAxis();
        NumberAxis yAxis = new NumberAxis();
        AreaChart lc = new AreaChart<>(xAxis, yAxis);

	      lc.setTitle("iMetricaFX Signal Extraction");	      
	      lc.setCreateSymbols(false);


	      
	      if(series != null ) {
	    	  lc.setData(getChartData(series, samples, format));
	      }
	    
	      return lc;

	}
	
	
	public static ObservableList<XYChart.Series<Date, Double>> getChartData(MultivariateSeries series, int samples, String format) {

		ObservableList<XYChart.Series<Date, Double>> data = FXCollections.observableArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		
		Series<Date, Double> anySignal = new Series<>();
		Series<Date, Double> anyTarget = new Series<>();
		
        int start = Math.max(0, series.getSeries(0).size() - 300);
    	
        try {
	        for (int i = start; i < series.getSeries(0).size(); i++) {
	        	
	        	TimeSeriesEntry<double[]> ts = series.getSignalTargetPair(i);
				 
	            double value = ts.getValue()[0];
	            double target = ts.getValue()[1];
	            String datetime = ts.getDateTime();
	        	
	        	anySignal.getData().add(new XYChart.Data<>(dateFormat.parse(datetime), value));        	
	        	anyTarget.getData().add(new XYChart.Data<>(dateFormat.parse(datetime), target));
	        }
	        
	        for (XYChart.Data<Date, Double> d : anySignal.getData()) {
	            Tooltip.install(d.getNode(), new Tooltip(
	                    d.getXValue().toString() + "\n" +
	                            "Signal: " + d.getYValue()));
	            
//	            d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//	            d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
	        }
        
	   }
       catch (Exception e) {
			e.printStackTrace();
	   } 
        
        anySignal.setName("MDFA signal");
        anyTarget.setName(series.getSeries(0).getName());
		
		data.add(anySignal);
		data.add(anyTarget);

						
		return data;
	}		
	
	
	
	
	
	
	
}
