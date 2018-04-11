package application;

import java.util.ArrayList;

import org.apache.commons.math3.complex.Complex;

import ch.imetrica.mdfa.series.MultivariateSeries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;

public class PhaseCanvas {

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static XYChart<NumberAxis, NumberAxis> createAreaChart(ArrayList<String> names, MultivariateSeries series) {
		
	      NumberAxis xAxis = new NumberAxis();
	      NumberAxis yAxis = new NumberAxis();
	      AreaChart ac = new AreaChart<>(xAxis, yAxis);
	     
	      ac.setTitle("TimeShift/PhaseDelay");
	      ac.setCreateSymbols(false);
	      ac.setHorizontalGridLinesVisible(false);
	      ac.setVerticalGridLinesVisible(false);
	      ac.setAlternativeColumnFillVisible(false);
	      ac.setAlternativeRowFillVisible(false);
	      
	      if(series != null) {
	    	  ac.setData(getChartData(names, series.getMDFACoeffs(), series.getSignalSize()));
	      }
	      
	      return ac;

	}
	
	
	public static ObservableList<XYChart.Series<Double, Double>> getChartData(ArrayList<String> names, ArrayList<double[]> coeffs, int resolution) {
 
		ObservableList<XYChart.Series<Double, Double>> data = FXCollections.observableArrayList();
		
		double freq; 
		int K = resolution/2;
		
		for(int num = 0; num < names.size(); num++) {
			
			Series<Double, Double> frf = new Series<>();
			double[] b = coeffs.get(num);
			
			Complex sum;
			for(int k = 0; k <= K; k++) {
				
				sum = new Complex(0,0);
				freq = Math.PI*k/K;
				for(int l = 0; l < b.length; l++) {
					
					Complex z = (new Complex(0, -l*freq)).exp();				 
					sum = sum.add(z.multiply(b[l])); 
				}
				
				double timeShift = (freq == 0) ? sum.getArgument() : sum.getArgument()/freq;
				frf.getData().add(new XYChart.Data<>(freq, timeShift));	
			}
			frf.setName(names.get(num));
			data.add(frf);
		}
				
		return data;
    }
	
}
