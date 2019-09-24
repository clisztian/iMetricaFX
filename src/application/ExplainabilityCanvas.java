package application;


import java.util.ArrayList;
import java.util.HashMap;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

import ch.imetrica.mdfa.series.MultivariateFXSeries;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ValueAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Shape;
import javafx.scene.text.Font;



public class ExplainabilityCanvas {

	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static XYChart<NumberAxis, CategoryAxis> createAreaChart(ArrayList<String> names, MultivariateFXSeries series, int selectedSeries, double targetFreq) {
		
		
		  CategoryAxis yAxis = new CategoryAxis();

		  NumberAxis xAxis = new NumberAxis(0, 1.0, .1);
	      xAxis.setLabel("Feature Importance");
	      xAxis.setTickLabelFill(Color.AQUA);
	     

	      BarChart ac = new BarChart<>(xAxis, yAxis);
	     
	      
//	      ac.applyCss();
	      
	      if(series != null ) {
	    	  

	    	  xAxis = new NumberAxis(0, 1.0, .1);
		      xAxis.setLabel("Feature Importance");
		
		      
		      ac = new BarChart<>(xAxis, buildCategoriesAxis(names, selectedSeries));
	    	  
	    	  ac.setData(getChartData(names, series, selectedSeries, targetFreq));  
	    	  
	    	  ac.setTitle("Current Market-Impact Drivers for Swiss Economy Last " + series.getMDFAFactory(0).getFilterLength() + " Days");


	    	  ac.setBarGap(1);
	    	  ac.setLegendVisible(false);
	    	 

	          for(int num = 0; num < names.size(); num++) {
			  		
		    	    double val = series.getSeries(num).getCoefficientSet(0)[0];
	              
		    	    if(val >= 0) {
		    	    	Node n = ac.lookup(".default-color" + num + ".chart-bar"); 
		    	    	if(n != null) {
		    	    		n.setStyle("-fx-bar-fill: rgb(0,200,0); ");
		    	    		
		    	    	}
		    	    }
		    	    else {
		    	    	Node n = ac.lookup(".default-color" + num + ".chart-bar");
		    	    	if(n != null) {
		    	    		n.setStyle("-fx-bar-fill: rgb(200,20,0); ");
		    	    	}
		    	    	
		    	    }
		      }	  
	      }
	      	      
	      return ac;

	}
	
	
	public static ObservableList<XYChart.Series<Double, String>> getChartData(ArrayList<String> names, MultivariateFXSeries series, int selectedSeries, double targetFreq) {
 
		ObservableList<XYChart.Series<Double, String>> data = FXCollections.observableArrayList();
		
		double freq; 
		int K = 100/2;
		
		  //computeCorrelationMatrix(double[][] data)
		  double[] target_coeffs = null;
		  HashMap<String, Double> myvals = new HashMap<String, Double>();
		  ArrayList<double[]> coeffs = new ArrayList<double[]>();
		  for(int i = 0; i < series.getNumberSeries(); i++) {
			  coeffs.add(series.getSeries(i).getOriginalCoefficients(0));
		  }
		  
	
			  double delta = Math.PI/K;
			
			  for(int num = 0; num < names.size(); num++) {
	
				double[] b = coeffs.get(num);		  		
				
				if(num == selectedSeries) {
					target_coeffs = b;
				}
				
				double fullIntegral = 0;
				double integrandSum = 0;
				Complex sum;
				for(int k = 0; k <= K; k++) {
					
					sum = new Complex(0,0);
					freq = Math.PI*k/K;
					for(int l = 0; l < b.length; l++) {
						
						Complex z = (new Complex(0, -l*freq)).exp();				 
						sum = sum.add(z.multiply(b[l])); 
					}
				
					if(freq <= targetFreq) { 					
						integrandSum = integrandSum + sum.abs()*delta;	
						fullIntegral = fullIntegral + delta;
					}	  				
				}
				
				double value = integrandSum/fullIntegral;		  					
				value = Math.min(1.0, value);
						  			
				myvals.put(names.get(num), value);		
				
				
			}
		
		double[] corells = new double[names.size()];
		
		
		
		for(int num = 0; num < names.size(); num++) {
		
			corells[num] = getCorrelation(target_coeffs, coeffs.get(num));
			
			System.out.println(names.get(selectedSeries) + " with " + names.get(num) + " " + corells[num]);
			
			Series<Double, String> frf = new Series<>();
			String key = names.get(num);
			if(key != names.get(selectedSeries)) {
		 	    frf.getData().add(new XYChart.Data<>(myvals.get(key), key));
				frf.setName(names.get(num));
				data.add(frf);
			}

		}
			
		return data;
    }
	
	
	public static CategoryAxis buildCategoriesAxis(ArrayList<String> names, int selectedSeries) {
	      final ObservableList<String> catNames = FXCollections.observableArrayList();
	   
	      for(String name : names) {
	    	  if(name != names.get(selectedSeries)) 
	    	  catNames.add(name);
	      }

	      final CategoryAxis categoryAxis = new CategoryAxis(catNames);
	      //categoryAxis.setLabel("Series");
	   
	      return categoryAxis;
	   }
	
	
	
	
	private static double getCorrelation(double[] a, double[] b) {
	    double correlation;
	    if (a.length >= 2) {
	        correlation = new PearsonsCorrelation().correlation(a, b);
	    } else {
	        correlation = 1;
	    }

	    if (Double.isNaN(correlation)) {
	        // No correlation defined mathematically means variables have zero std. For discussion about implications:
	        // http://stats.stackexchange.com/questions/18333/what-is-the-correlation-if-the-standard-deviation-of-one-variable-is-0
	        correlation = 0;
	    }
	    return correlation;
	}
	

	
}

class LevelLegend extends GridPane {
    LevelLegend() {
      setHgap(10);
      setVgap(10);
      addColumn(0, createSymbol("#57b757"),     new Label("Positive Impact"));
      addColumn(1, createSymbol("#f3622d"),     new Label("Negative Impact"));
      

    }
    
   /** Create a custom symbol for a custom chart legend with the given fillStyle style string. */
   private Node createSymbol(String fillStyle) {
      Shape symbol = new Ellipse(10, 5, 10, 5);
      symbol.setStyle("-fx-fill: " + fillStyle);
      symbol.setStroke(Color.BLACK);
      symbol.setStrokeWidth(2);
      
      return symbol;
    }
  }


//public class ExplainabilityCanvas {
//
//	
//
//	final BarChart<NumberAxis, CategoryAxis> barChart;
//	
//	public ExplainabilityCanvas() {
//	
//	final CategoryAxis xAxis = new CategoryAxis();
//	final ValueAxis<Number> yAxis = new NumberAxis(0, 1.0, .1);
//    
//    barChart = new BarChart(yAxis,xAxis);
//    
////    xAxis.setLabel("Series");
////    yAxis.setLabel("Feature Importance");
//
////    XYChart.Series series = new XYChart.Series();
////    series.getData().add(new XYChart.Data(.3, project));
////    series.getData().add(new XYChart.Data(.10, quiz));
////    series.getData().add(new XYChart.Data(.36, midterm));
////    series.getData().add(new XYChart.Data(.44, finalexam));
////    barChart.getData().add(series);
////    
////    Node n = barChart.lookup(".data0.chart-bar");
////    n.setStyle("-fx-bar-fill: rgb(200,30,30)");
////    n = barChart.lookup(".data1.chart-bar");
////    n.setStyle("-fx-bar-fill: blue");
////    n = barChart.lookup(".data2.chart-bar");
////    n.setStyle("-fx-bar-fill: blue");
////    n = barChart.lookup(".data3.chart-bar");
////    n.setStyle("-fx-bar-fill: orange");
//    
//    barChart.setLegendVisible(true);
//	
//	}
//	
//	
//	public void createAreaChart(ArrayList<String> names, MultivariateFXSeries series, int selectedSeries, double targetFreq) {
//		
//		
//	  barChart.getData().clear();	
//	  
//	  barChart.setTitle("Market Drivers of " + names.get(selectedSeries));
//	  HashMap<String, Double> myvals = new HashMap<String, Double>();
//  	  ArrayList<double[]> coeffs = new ArrayList<double[]>();
//  	  for(int i = 0; i < series.getNumberSeries(); i++) {
//  		  coeffs.add(series.getSeries(i).getCoefficientSet(selectedSeries));
//  	  }
//
//  	  double freq; 
//		  int K = 100;
//		  double delta = Math.PI/K;
//		
//		  for(int num = 0; num < names.size(); num++) {
//
//			double[] b = coeffs.get(num);		  			
//			double fullIntegral = 0;
//			double integrandSum = 0;
//			Complex sum;
//			for(int k = 0; k <= K; k++) {
//				
//				sum = new Complex(0,0);
//				freq = Math.PI*k/K;
//				for(int l = 0; l < b.length; l++) {
//					
//					Complex z = (new Complex(0, -l*freq)).exp();				 
//					sum = sum.add(z.multiply(b[l])); 
//				}
//			
//				if(freq <= targetFreq) { 					
//					integrandSum = integrandSum + sum.abs()*delta;	
//					fullIntegral = fullIntegral + delta;
//				}	  				
//			}
//			
//			double value = integrandSum/fullIntegral;		  					
//			value = Math.min(1.0, value);
//					  			
//			myvals.put(names.get(num), value);		  			
//		}
//		  
//		  
//		  XYChart.Series series1 = new XYChart.Series();  
//		  for(int num = 0; num < names.size(); num++) {
//		  		
//	 	    String key = names.get(num);
//	 	    series1.getData().add(new XYChart.Data<Double,String>(myvals.get(key), key));	
//	      }
//		  series1.setName("Feature Importance");
//		  
//		  barChart.getData().add(series1);
//		  barChart.applyCss();
//
//		  
//		  for(int num = 0; num < names.size(); num++) {
//		  		
//	    	    double val = coeffs.get(num)[0];
//              
//	    	    if(val >= 0) {
//	    	    	Node n = barChart.lookup(".data" + num + ".chart-bar");
//	    	    	n.setStyle("-fx-bar-fill: rgb(10, 199, 40)");
//	    	    }
//	    	    else {
//	    	    	Node n = barChart.lookup(".data" + num + ".chart-bar");
//	    	    	n.setStyle("-fx-bar-fill: rgb(200, 2, 4)");
//	    	    }
//	      }
//		
//	}
//	
//	
//	public BarChart getBarChart() {
//		return barChart;
//	}
	
	
//	private static boolean hasNames = false;
//	
//	@SuppressWarnings("rawtypes")
//	BarChart<Double,String> populationBarChart;
//	VBox chartWithLegend;
//	
//	@SuppressWarnings({ "unchecked", "rawtypes" })
//	public ExplainabilityCanvas() {
//		
//		final ValueAxis explainabilityAxis = new NumberAxis(0, 1.0, .1);
//	    ArrayList<String> emptyArray = new ArrayList<String>();
//	    emptyArray.add("Enter data");
//	    populationBarChart = new BarChart(explainabilityAxis, buildCategoriesAxis(emptyArray));		   
//	    	    
//	}
//	
//	   /**
//	    * Build horizontal bar chart comparing populations of sample states.
//	    * 
//	    * @return Horizontal bar chart comparing populations of sample states.
//	    */
//	   @SuppressWarnings({ "rawtypes", "unchecked" })
//	   public XYChart<Double,String> buildHorizontalExplainabilityBarChart(ArrayList<String> names, MultivariateFXSeries series, int selectedSeries, double targetFreq) {
//	      
//		   final ValueAxis explainabilityAxis = new NumberAxis(0, 1.0, .1);
//	      
//		   if(series == null) {
//			   ArrayList<String> emptyArray = new ArrayList<String>();
//			   emptyArray.add("Enter data");
//			   return new BarChart(explainabilityAxis, buildCategoriesAxis(emptyArray));
//		   }
//		   hasNames = true;
//		   
//	      populationBarChart = new BarChart(explainabilityAxis, buildCategoriesAxis(names)); //, buildExplainableOutputs(names, series, selectedSeries, targetFreq));
//	     
//	      populationBarChart.setTitle("Market Driver Features of " + names.get(selectedSeries));
//	      return populationBarChart;
//	   }
//	   
//	   
//	   
//	   @SuppressWarnings("unchecked")
//	   public void updateValues(ArrayList<String> names, MultivariateFXSeries series, int selectedSeries, double targetFreq) {
//		   
//		   
//
//		   populationBarChart.getData().clear();
//		   
//		      HashMap<String, Double> myvals = new HashMap<String, Double>();
//	    	  ArrayList<double[]> coeffs = new ArrayList<double[]>();
//	    	  for(int i = 0; i < series.getNumberSeries(); i++) {
//	    		  coeffs.add(series.getSeries(i).getCoefficientSet(selectedSeries));
//	    	  }
//
//	    	  double freq; 
//	  		  int K = 100;
//	  		  double delta = Math.PI/K;
//	  		
//	  		  for(int num = 0; num < names.size(); num++) {
//
//	  			double[] b = coeffs.get(num);		  			
//	  			double fullIntegral = 0;
//	  			double integrandSum = 0;
//	  			Complex sum;
//	  			for(int k = 0; k <= K; k++) {
//	  				
//	  				sum = new Complex(0,0);
//	  				freq = Math.PI*k/K;
//	  				for(int l = 0; l < b.length; l++) {
//	  					
//	  					Complex z = (new Complex(0, -l*freq)).exp();				 
//	  					sum = sum.add(z.multiply(b[l])); 
//	  				}
//	  			
//	  				if(freq <= targetFreq) { 					
//	  					integrandSum = integrandSum + sum.abs()*delta;	
//	  					fullIntegral = fullIntegral + delta;
//	  				}	  				
//	  			}
//	  			
//	  			double value = integrandSum/fullIntegral;		  					
//	  			value = Math.min(1.0, value);
//	  					  			
//	  			myvals.put(names.get(num), value);		  			
//	  		}
//	    	  
//
//		   final XYChart.Series<Double,String> positiveImpact = new XYChart.Series<Double,String>();
//		
//		
//		   
//		   for(int num = 0; num < names.size(); num++) {
//			  		
//		 	    String key = names.get(num);
//		 	    
//		 	    final XYChart.Data<Double,String> data = new XYChart.Data<Double,String>(myvals.get(key), key); 
//			    	positiveImpact.getData().add(data);
//		 	    
//		
//		   }
//		   
//		   
//		   populationBarChart.getData().add(positiveImpact);
//		   for(int num = 0; num < names.size(); num++) {
//			  		
//		 	    double val = coeffs.get(num)[0];
//		         
//		 	    if(val >= 0) {
//		 	    	Node n = populationBarChart.lookup(".data" + num + ".chart-bar");
//		 	    	n.setStyle("-fx-bar-fill: rgb(10, 199, 40)");
//		 	    }
//		 	    else {
//		 	    	Node n = populationBarChart.lookup(".data" + num + ".chart-bar");
//		 	    	n.setStyle("-fx-bar-fill: rgb(200, 2, 4)");
//		 	    }
//		   }
//		   
//		   System.out.println("Is empyt" + populationBarChart.getData().isEmpty());
//	   }
//	   
//	   public XYChart<Double,String> getBarChart() {
//		   
//		   return populationBarChart;
//	   }
//	   
//	   
//	   /**
//	    * Build one or more series of XYChart Data representing population as 'x'
//	    * portion and state names as 'y' portion. This method is likely to be used
//	    * in horizontal presentations where state names are desired on the y-axis
//	    * and population numbers are desired on the x-axis.
//	    * 
//	    * @return Series of XYChar Data representing population as 'x' portion and
//	    *    state names as 'y' portion.
//	    */
//	   @SuppressWarnings("unchecked")
//	public void buildExplainableOutputs(ArrayList<String> names, 
//			                                    MultivariateFXSeries series, int selectedSeries, double targetFreq)
//	   {
//		   
//		   HashMap<String, Double> myvals = new HashMap<String, Double>();
//
//		    	  
//		    	  ArrayList<double[]> coeffs = new ArrayList<double[]>();
//		    	  for(int i = 0; i < series.getNumberSeries(); i++) {
//		    		  coeffs.add(series.getSeries(i).getCoefficientSet(selectedSeries));
//		    	  }
//
//		    	  double freq; 
//		  		  int K = 100;
//		  		  double delta = Math.PI/K;
//		  		
//		  		  for(int num = 0; num < names.size(); num++) {
//
//		  			double[] b = coeffs.get(num);		  			
//		  			double fullIntegral = 0;
//		  			double integrandSum = 0;
//		  			Complex sum;
//		  			for(int k = 0; k <= K; k++) {
//		  				
//		  				sum = new Complex(0,0);
//		  				freq = Math.PI*k/K;
//		  				for(int l = 0; l < b.length; l++) {
//		  					
//		  					Complex z = (new Complex(0, -l*freq)).exp();				 
//		  					sum = sum.add(z.multiply(b[l])); 
//		  				}
//		  			
//		  				if(freq <= targetFreq) { 					
//		  					integrandSum = integrandSum + sum.abs()*delta;	
//		  					fullIntegral = fullIntegral + delta;
//		  				}	  				
//		  			}
//		  			
//		  			double value = integrandSum/fullIntegral;		  					
//		  			value = Math.min(1.0, value);
//		  					  			
//		  			myvals.put(names.get(num), value);		  			
//		  		}
//		    	  
//		    	 
//		   
//		  	
//		  		  
//		  		  
//		   
//	      final XYChart.Series<Double,String> positiveImpact = new XYChart.Series<Double,String>();
//
//
//	      
//	      for(int num = 0; num < names.size(); num++) {
//		  		
//	    	    String key = names.get(num);
//	    	    
//	    	    final XYChart.Data<Double,String> data = new XYChart.Data<Double,String>(myvals.get(key), key); 
//    	    	positiveImpact.getData().add(data);
//	    	    
//
//	      }
//	      
//	      
//	      populationBarChart.getData().add(positiveImpact);
//	      for(int num = 0; num < names.size(); num++) {
//		  		
//	    	    double val = coeffs.get(num)[0];
//                
//	    	    if(val >= 0) {
//	    	    	Node n = populationBarChart.lookup(".data" + num + ".chart-bar");
//	    	    	n.setStyle("-fx-bar-fill: rgb(10, 199, 40)");
//	    	    }
//	    	    else {
//	    	    	Node n = populationBarChart.lookup(".data" + num + ".chart-bar");
//	    	    	n.setStyle("-fx-bar-fill: rgb(200, 2, 4)");
//	    	    }
//	      }
//	      
//	      
//	   }
//	   
//	   /**
//	    * Provides a CategoryAxis instantiated with sample states' names.
//	    *
//	    * @return CategoryAxis with sample states' names.
//	    */
//	   public CategoryAxis buildCategoriesAxis(ArrayList<String> names) {
//	      final ObservableList<String> catNames = FXCollections.observableArrayList();
//	   
//	      for(String name : names) {
//	    	  catNames.add(name);
//	      }
//	   
//	      hasNames = true;
//	      final CategoryAxis categoryAxis = new CategoryAxis(catNames);
//	      //categoryAxis.setLabel("Series");
//	   
//	      return categoryAxis;
//	   }
//
//	   public boolean hasNames() {
//			return hasNames;
//	    }


	
/*
HashMap<String, Double> myvals = new HashMap<String, Double>();
	    	  ArrayList<double[]> coeffs = new ArrayList<double[]>();
	    	  for(int i = 0; i < series.getNumberSeries(); i++) {
	    		  coeffs.add(series.getSeries(i).getCoefficientSet(selectedSeries));
	    	  }

	    	  double freq; 
	  		  int K = 100;
	  		  double delta = Math.PI/K;
	  		
	  		  for(int num = 0; num < names.size(); num++) {

	  			double[] b = coeffs.get(num);		  			
	  			double fullIntegral = 0;
	  			double integrandSum = 0;
	  			Complex sum;
	  			for(int k = 0; k <= K; k++) {
	  				
	  				sum = new Complex(0,0);
	  				freq = Math.PI*k/K;
	  				for(int l = 0; l < b.length; l++) {
	  					
	  					Complex z = (new Complex(0, -l*freq)).exp();				 
	  					sum = sum.add(z.multiply(b[l])); 
	  				}
	  			
	  				if(freq <= targetFreq) { 					
	  					integrandSum = integrandSum + sum.abs()*delta;	
	  					fullIntegral = fullIntegral + delta;
	  				}	  				
	  			}
	  			
	  			double value = integrandSum/fullIntegral;		  					
	  			value = Math.min(1.0, value);
	  					  			
	  			myvals.put(names.get(num), value);		  			
	  		}
	    	  

		   final XYChart.Series<Double,String> positiveImpact = new XYChart.Series<Double,String>();
		
		
		   
		   for(int num = 0; num < names.size(); num++) {
			  		
		 	    String key = names.get(num);
		 	    
		 	    final XYChart.Data<Double,String> data = new XYChart.Data<Double,String>(myvals.get(key), key); 
			    	positiveImpact.getData().add(data);
		 	    
		
		   }
		   
		   
		   populationBarChart.getData().add(positiveImpact);
		   for(int num = 0; num < names.size(); num++) {
			  		
		 	    double val = coeffs.get(num)[0];
		         
		 	    if(val >= 0) {
		 	    	Node n = populationBarChart.lookup(".data" + num + ".chart-bar");
		 	    	n.setStyle("-fx-bar-fill: rgb(10, 199, 40)");
		 	    }
		 	    else {
		 	    	Node n = populationBarChart.lookup(".data" + num + ".chart-bar");
		 	    	n.setStyle("-fx-bar-fill: rgb(200, 2, 4)");
		 	    }
		   }
		   
		   */
	

	

