package application;

import java.util.ArrayList;

import ch.imetrica.mdfa.series.MultivariateFXSeries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class CoefficientCanvas {

	
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static XYChart<CategoryAxis, NumberAxis> createAreaChart(ArrayList<String> names, MultivariateFXSeries series, int selectedSeries) {
		
		  CategoryAxis xAxis = new CategoryAxis();
	      NumberAxis yAxis = new NumberAxis();
	      LineChart lc = new LineChart<>(xAxis, yAxis);
	      
	      lc.setTitle("MDFA Coefficients");
	      
	      lc.setHorizontalGridLinesVisible(false);
	      lc.setVerticalGridLinesVisible(false);
	      lc.setAlternativeColumnFillVisible(false);
	      lc.setAlternativeRowFillVisible(false);
	      
	      if(series != null) {
	    	  
	    	  ArrayList<double[]> coeffs = new ArrayList<double[]>();
	    	  for(int i = 0; i < series.getNumberSeries(); i++) {
	    		  coeffs.add(series.getSeries(i).getCoefficientSet(selectedSeries));
	    	  }	    	  
	    	  lc.setData(getChartData(names, coeffs));    
	      }

	      return lc;

	}
	
	
	public static ObservableList<XYChart.Series<String, Double>> getChartData(ArrayList<String> names, ArrayList<double[]> coeffs) {

		ObservableList<XYChart.Series<String, Double>> data = FXCollections.observableArrayList();
		
		for(int num = 0; num < names.size(); num++) {
			
			Series<String, Double> frf = new Series<>();
			double[] b = coeffs.get(num);
			
			for(int l = 0; l < b.length; l++) {
				
				frf.getData().add(new XYChart.Data<>(Integer.toString(l), b[l]));	
				
			}
			frf.setName(names.get(num));
			data.add(frf);
		}
				
		return data;
		
  }		
	
	
	
}
