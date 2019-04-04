package application;

import ch.imetrica.mdfa.series.MultivariateFXSeries;
import ch.imetrica.mdfa.series.TimeSeriesEntry;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.AreaChart;
import javafx.scene.chart.Axis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import javafx.scene.chart.XYChart.Data;
import javafx.scene.chart.XYChart.Series;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class TurningPointCanvas {

	
	static double minYValue = Double.MAX_VALUE;
	static double maxYValue = Double.MIN_VALUE;
	static DropShadow shadow = new DropShadow(10, 0, 2, Color.GREY);
	static String[] linestyle;


	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static XYChart<DateAxis, NumberAxis> createAreaChart(MultivariateFXSeries series, String format, int selectSeries) {
		
		minYValue = Double.MAX_VALUE;
		maxYValue = Double.MIN_VALUE;
		
		NumberAxis yAxis;
    	DateAxis xAxis = new DateAxis();
    	if(series == null) {
    		yAxis = new NumberAxis();
    	}

        
    	AreaChartWithMarkers<DateAxis, NumberAxis> lc = new AreaChartWithMarkers<DateAxis, NumberAxis>(xAxis, new NumberAxis());
  
	      if(series != null ) {
	    	  
	    	  ObservableList<XYChart.Series<Date, Double>> myData = getChartData(series, format, selectSeries);
	    	  ObservableList<Data<Date, Double>> verticalMarkers = getMarkers(series, format, selectSeries);
	    	  
	    	  maxYValue = maxYValue + (int)(maxYValue*.05);
	    	  double lines = (maxYValue-minYValue)/10.0;
	    	  
	    	  yAxis = new NumberAxis(minYValue, maxYValue, lines);
	    	
	    	  lc = new AreaChartWithMarkers<DateAxis, NumberAxis>(xAxis, yAxis);

		      lc.setTitle("Real-Time Turning-Point Detection");	      
		      lc.setCreateSymbols(false); 
	    	  lc.setData(myData);  
	    	  lc.applyCss();
	    	  
	    	  for(int i = 0; i < verticalMarkers.size(); i++) {
	    		  lc.addVerticalValueMarker(verticalMarkers.get(i));
	    	  }
	    	  
	    	  XYChart.Series<Date, Double> signal = (XYChart.Series<Date, Double>) lc.getData().get(0);
	  
	              for (XYChart.Data<Date, Double> d : signal.getData()) {
	                  Tooltip.install(d.getNode(), new Tooltip(
	                          d.getXValue().toString() + "\n" +
	                                  "Number Of Events : " + d.getYValue()));

//	                  //Adding class on hover
//	                  d.getNode().setOnMouseEntered(event -> d.getNode().getStyleClass().add("onHover"));
//
//	                  //Removing class on exit
//	                  d.getNode().setOnMouseExited(event -> d.getNode().getStyleClass().remove("onHover"));
	              }
	          
	    	  
	    	  
	      }
	      
	      return lc;

	}
	
	
	public static ObservableList<XYChart.Series<Date, Double>> getChartData(MultivariateFXSeries series, String format, int selectSeries) {

		ObservableList<XYChart.Series<Date, Double>> data = FXCollections.observableArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
		
		int n = selectSeries;
		maxYValue = 0;
		linestyle = new String[series.getNumberSeries()];
		
		ArrayList<Series<Date, Double>> anySignals = new ArrayList<Series<Date, Double>>();
		
		Series<Date, Double> signal = new Series<>();
		anySignals.add(signal);
		
        int start = Math.max(0, series.size() - 300);
    	
        try {
        	
	        for (int i = start; i < series.size(); i++) {

		        	String datetarget = series.getSeries(n).getTargetDate(i);
		        	double val = series.getSeries(n).getOriginalValue(i);

	            	(anySignals.get(0)).getData().add(new XYChart.Data<>(dateFormat.parse(datetarget), val)); 
	            		            
	            	if(val > maxYValue) {
	            		maxYValue = val;
	            	}
	            	else if(val < minYValue) {
	            		minYValue = val;
	            	}
	        }
	                
	   }
       catch (Exception e) {
			e.printStackTrace();
	   } 
                   
       
         
        
        
 	   anySignals.get(0).setName(series.getSeries(n).getName());
 	   data.add(anySignals.get(0));			
	   
 
 	   
 	   return data;
	}		
	
	
	public static ObservableList<Data<Date, Double>> getMarkers(MultivariateFXSeries series, String format, int selectSeries) {
		
		ObservableList<Data<Date, Double>> verticalMarkers = FXCollections.observableArrayList();
		SimpleDateFormat dateFormat = new SimpleDateFormat(format);
	
		int start = Math.max(0, series.size() - 300);
    	
        try {        	
	        for (int i = start+1; i < series.size(); i++) {
		
	           String datetarget = series.getSeries(selectSeries).getTargetDate(i);
 	    	   double val = series.getSeries(selectSeries).getOriginalValue(i);
 	    	     	   
 	    	    	    	   
    	       if(series.getSignal(i).getValue()[0]*series.getSignal(i-1).getValue()[0] < 0) {
    	    	   verticalMarkers.add(new Data<>(dateFormat.parse(datetarget), val)); 
    	       }
	        }	        
        }
        catch (Exception e) {
			e.printStackTrace();
	    } 

		return verticalMarkers;
	}
	
	
	
	
	static private class AreaChartWithMarkers<X,Y> extends AreaChart {

        private ObservableList<Data<Date, Double>> horizontalMarkers;
        private ObservableList<Data<Date, Double>> verticalMarkers;

        public AreaChartWithMarkers(DateAxis xAxis, NumberAxis yAxis) {
            super(xAxis, yAxis);
            horizontalMarkers = FXCollections.observableArrayList(data -> new Observable[] {data.YValueProperty()});
            horizontalMarkers.addListener((InvalidationListener)observable -> layoutPlotChildren());
            verticalMarkers = FXCollections.observableArrayList(data -> new Observable[] {data.XValueProperty()});
            verticalMarkers.addListener((InvalidationListener)observable -> layoutPlotChildren());
        }

        public void addHorizontalValueMarker(Data<Date, Double> marker) {
            Objects.requireNonNull(marker, "the marker must not be null");
            if (horizontalMarkers.contains(marker)) return;
            Line line = new Line();
            marker.setNode(line );
            getPlotChildren().add(line);
            horizontalMarkers.add(marker);
        }

        public void removeHorizontalValueMarker(Data<X, Y> marker) {
            Objects.requireNonNull(marker, "the marker must not be null");
            if (marker.getNode() != null) {
                getPlotChildren().remove(marker.getNode());
                marker.setNode(null);
            }
            horizontalMarkers.remove(marker);
        }

        public void addVerticalValueMarker(Data<Date, Double> data) {
            Objects.requireNonNull(data, "the marker must not be null");
            if (verticalMarkers.contains(data)) return;
            Line line = new Line();
            data.setNode(line );
            getPlotChildren().add(line);
            verticalMarkers.add(data);
        }

        public void removeVerticalValueMarker(Data<X, Y> marker) {
            Objects.requireNonNull(marker, "the marker must not be null");
            if (marker.getNode() != null) {
                getPlotChildren().remove(marker.getNode());
                marker.setNode(null);
            }
            verticalMarkers.remove(marker);
        }


        @Override
        protected void layoutPlotChildren() {
            super.layoutPlotChildren();
            for (Data<Date, Double> horizontalMarker : horizontalMarkers) {
                Line line = (Line) horizontalMarker.getNode();
                line.setStartX(0);
                line.setEndX(getBoundsInLocal().getWidth());
                line.setStartY(getYAxis().getDisplayPosition(horizontalMarker.getYValue()) + 0.5); // 0.5 for crispness
                line.setEndY(line.getStartY());
                line.toFront();
            }
            for (Data<Date, Double> verticalMarker : verticalMarkers) {
                Line line = (Line) verticalMarker.getNode();
                line.setStartX(getXAxis().getDisplayPosition(verticalMarker.getXValue()) + 0.5);  // 0.5 for crispness
                line.setEndX(line.getStartX());
                line.setStartY(0d);
                line.setEndY(getBoundsInLocal().getHeight());
                line.toFront();
            }      
        }

    }
	
/*
 * 
 * 
 * private ObservableList<Data<X, X>> verticalRangeMarkers;

public LineChartWithMarkers(Axis<X> xAxis, Axis<Y> yAxis) {
    ...            
    verticalRangeMarkers = FXCollections.observableArrayList(data -> new Observable[] {data.XValueProperty()});
    verticalRangeMarkers = FXCollections.observableArrayList(data -> new Observable[] {data.YValueProperty()}); // 2nd type of the range is X type as well
    verticalRangeMarkers.addListener((InvalidationListener)observable -> layoutPlotChildren());
}        


public void addVerticalRangeMarker(Data<X, X> marker) {
    Objects.requireNonNull(marker, "the marker must not be null");
    if (verticalRangeMarkers.contains(marker)) return;

    Rectangle rectangle = new Rectangle(0,0,0,0);
    rectangle.setStroke(Color.TRANSPARENT);
    rectangle.setFill(Color.BLUE.deriveColor(1, 1, 1, 0.2));

    marker.setNode( rectangle);

    getPlotChildren().add(rectangle);
    verticalRangeMarkers.add(marker);
}

public void removeVerticalRangeMarker(Data<X, X> marker) {
    Objects.requireNonNull(marker, "the marker must not be null");
    if (marker.getNode() != null) {
        getPlotChildren().remove(marker.getNode());
        marker.setNode(null);
    }
    verticalRangeMarkers.remove(marker);
}

protected void layoutPlotChildren() {

    ...

    for (Data<X, X> verticalRangeMarker : verticalRangeMarkers) {

        Rectangle rectangle = (Rectangle) verticalRangeMarker.getNode();
        rectangle.setX( getXAxis().getDisplayPosition(verticalRangeMarker.getXValue()) + 0.5);  // 0.5 for crispness
        rectangle.setWidth( getXAxis().getDisplayPosition(verticalRangeMarker.getYValue()) - getXAxis().getDisplayPosition(verticalRangeMarker.getXValue()));
        rectangle.setY(0d);
        rectangle.setHeight(getBoundsInLocal().getHeight());
        rectangle.toBack();

    }
} 
*/	
 
	
	
}

