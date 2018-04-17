package application;

import ch.imetrica.mdfa.series.MultivariateFXSeries;
import ch.imetrica.mdfa.series.TimeSeriesEntry;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;

public class TimeSeriesCanvas {

	
	static double minYValue = Double.MAX_VALUE;
	static DropShadow shadow = new DropShadow(10, 0, 2, Color.GREY);
	static String[] linestyle;
	//.setEffect(new DropShadow(10, 0, 2, Color.GREY))

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static XYChart<DateAxis, NumberAxis> createAreaChart(MultivariateFXSeries series, int samples, int selectedSignal, String format) {
		
    	DateAxis xAxis = new DateAxis();
        NumberAxis yAxis = new NumberAxis();
        AreaChart lc = new AreaChart<>(xAxis, yAxis);

	      lc.setTitle("iMetricaFX Signal Extraction");	      
	      lc.setCreateSymbols(false);
	      lc.applyCss();


	      
	      if(series != null ) {
	    	  
	    	  lc.setData(getChartData(series, samples, selectedSignal, format));  
	    	  lc.applyCss();
	          Node line = lc.lookup(linestyle[selectedSignal]);

	          StringBuilder style = new StringBuilder();
	          style.append("-fx-stroke-width: 2px;");
	          style.append("-fx-effect: dropshadow( gaussian , rgba(0,0,0,1) , 14, 0.2 , 1 , 9 );");
	          
	          if(line != null) {
	        	  line.setStyle(style.toString());
	          }    	  
	      }
	      
	      return lc;

	}
	
	
	public static ObservableList<XYChart.Series<Date, Double>> getChartData(MultivariateFXSeries series, int samples, int selectedSignal, String format) {

		ObservableList<XYChart.Series<Date, Double>> data = FXCollections.observableArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		
		linestyle = new String[series.getNumberSignals()];
		Series<Date, Double> anyTarget = new Series<>();
		
		ArrayList<Series<Date, Double>> anySignals = new ArrayList<Series<Date, Double>>();
		
		for(int i = 0; i < series.getNumberSignals(); i++) {
		 
			Series<Date, Double> signal = new Series<>();
			anySignals.add(signal);
		}
		
        int start = Math.max(0, series.size() - 300);
    	
        try {
        	
	        for (int i = start; i < series.size(); i++) {
	        	
	        	TimeSeriesEntry<double[]> ts = series.getSignal(i);
	        	double target = series.getTargetValue(i);
	        	String datetarget = series.getTargetDate(i);
	            
	        	double[] value = ts.getValue();
	            String datetime = ts.getDateTime();
	        	
	            if(!datetarget.equals(datetime)) {
	            	throw new Exception("Dates don't match: target date is " + datetarget + " " + datetime);
	            }
	            
	            anyTarget.getData().add(new XYChart.Data<>(dateFormat.parse(datetime), target));
	            
	            for(int n = 0; n < series.getNumberSignals(); n++) {
	            	(anySignals.get(n)).getData().add(new XYChart.Data<>(dateFormat.parse(datetime), value[n]));        	
	            } 	
	        }
	                
	   }
       catch (Exception e) {
			e.printStackTrace();
	   } 
       
       
       anyTarget.setName(series.getTargetName()); 
       data.add(anyTarget);
       
       
       for(int n = 0; n < series.getNumberSignals(); n++) {
    		  
    	   anySignals.get(n).setName("Signal " + n);
    	   data.add(anySignals.get(n));
    	   linestyle[n] = ".default-color"+(n+1)+".chart-series-area-line";
    		  
       }
       
       
         
						
	   return data;
	}		
	
	
	
	
	
	
	
}
