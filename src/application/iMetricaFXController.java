package application;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

import ch.imetrica.mdfa.datafeeds.CsvFeed;
import ch.imetrica.mdfa.mdfa.MDFABase;
import ch.imetrica.mdfa.mdfa.MDFASolver;
import ch.imetrica.mdfa.series.MultivariateSeries;
import ch.imetrica.mdfa.series.SignalSeries;
import ch.imetrica.mdfa.series.TargetSeries;
import ch.imetrica.mdfa.series.TimeSeriesEntry;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckMenuItem;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.RadioMenuItem;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class iMetricaFXController {

	DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	final FileChooser fileChooser = new FileChooser();
	private MDFABase anyMDFA;
	private Stage primaryStage; 
	private CsvFeed marketFeed; 
	private MultivariateSeries multiSeries;	
	private ArrayList<String> csvFileStreams = new ArrayList<String>();
	private ArrayList<String> assetNames = new ArrayList<String>();
    private String datetimeFormat = new String("yyyy-MM-dd");
	private ToggleGroup dateFormatSelect;


	Stage coeffWindow = new Stage();
	Stage frfWindow = new Stage();
	Stage phaseWindow = new Stage();
	
	StackPane coeffPane = new StackPane();
	StackPane frfPane = new StackPane();
	StackPane phasePane = new StackPane();
	
	Scene coeffScene;
	Scene frfScene;
	Scene phaseScene;
	

	public iMetricaFXController() { }
	
	@FXML
	private TextField smoothnessText;
	
	@FXML
	private TextField decayStartText;
	
	@FXML
	private TextField decayStrengthText;
	
	@FXML
	private TextField crossCorrelationText;
	
	@FXML
	private TextField alphaText;
	
	@FXML
	private TextField lambdaText;
	
	@FXML
	private TextField freqCutoff0Text;
	
	@FXML
	private TextField freqCutoff1Text;
	
	@FXML
	private TextField sampleSizeText;
	
	@FXML
	private TextField fractionalDText;
	
	@FXML
	private TextField filterLengthText;
	
	@FXML 
	private TextField lagText;
	
	@FXML 
	private TextField phaseText;
	
	@FXML
	private StackPane plotCanvas;
	
	@FXML 
	private Button newObservationButton;
	
	@FXML 
	private Button compileData;
	
	@FXML
	private CheckBox i1Checkbox;
	
	@FXML
	private CheckBox i2Checkbox;
	
	@FXML 
	private Slider smoothness;
	
	@FXML 
	private Slider decayStart;
	
	@FXML 
	private Slider decayStrength;
	
	@FXML 
	private Slider crossCorrelation;
	
	@FXML 
	private Slider alphaSlider;
	
	@FXML 
	private Slider lambdaSlider;
	
	@FXML 
	private Slider freqCutoff0;
	
	@FXML 
	private Slider filterLength;
	
	@FXML 
	private Slider sampleSize;
	
	@FXML 
	private Slider freqCutoff1;
	
	@FXML 
	private Slider phaseShift;
	
	@FXML 
	private Slider fractionalD;
	
	@FXML 
	private Slider lagSlider;
	
	@FXML 
	private Menu FileMenu;
	
	@FXML
	private MenuItem openDataFile;
	
	@FXML
	private RadioMenuItem yyyyMMdd;
	
	@FXML
	private RadioMenuItem yyyyMMddHHmmss;	
	
	@FXML
	private CheckMenuItem frfCheckbox;

	@FXML
	private CheckMenuItem coeffCheckbox;
	
	@FXML
	private CheckMenuItem phaseCheckbox;

	public void setDateFormatToggleGroup() {
		
		dateFormatSelect = new ToggleGroup();
		yyyyMMdd.setToggleGroup(dateFormatSelect);
		yyyyMMddHHmmss.setToggleGroup(dateFormatSelect);
		yyyyMMdd.setSelected(true);		
	}


    @FXML 
    protected void handleDateFormatSelectToggle(ActionEvent event) {
    	
    	if(dateFormatSelect.getSelectedToggle().equals(yyyyMMdd)) {
    		System.out.println("Set Dateformat to 'yyyy-MM-dd'");
    		datetimeFormat = "yyyy-MM-dd";
    	}
    	else if(dateFormatSelect.getSelectedToggle().equals(yyyyMMdd)) {
    		System.out.println("Set Dateformat to 'yyyy-MM-dd HH:mm:ss'");
    		datetimeFormat = "yyyy-MM-dd HH:mm:ss";
    	}
    }
	
	
	@FXML
	protected void handleOpenDataFile(ActionEvent event) {
				
		
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		fileChooser.getExtensionFilters().add(extFilter);
		fileChooser.setTitle("Open CSV File");
		List<File> filelist = fileChooser.showOpenMultipleDialog(primaryStage);
		
		if(filelist != null) {
			
			for(File file : filelist) {
				
				csvFileStreams.add(file.getAbsolutePath());
				assetNames.add(file.getName().split("[.]+")[0]);
				System.out.println(file.getAbsolutePath());
			}			
		}
				
	}
	
	@FXML
	protected void handleSmoothnessChange() {
		
		double val = smoothness.getValue();
		smoothnessText.setText(decimalFormat.format(val));
		anyMDFA.setSmooth(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory().setSmoothRegularization(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
	
	@FXML
	protected void handleDecayStrengthChange() {
		
		double val = decayStrength.getValue();
		decayStrengthText.setText(decimalFormat.format(val));
		anyMDFA.setDecayStrength(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory().setDecayStrengthRegularization(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleDecayStartChange() {
		
		double val = decayStart.getValue();
		decayStartText.setText(decimalFormat.format(val));
		anyMDFA.setDecayStrength(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory().setDecayStartRegularization(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleCrossCorrelationChange() {
		
		double val = crossCorrelation.getValue();
		crossCorrelationText.setText(decimalFormat.format(val));
		anyMDFA.setCrossCorr(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory().setCrossRegularization(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleAlphaChange() {
		
		double val = alphaSlider.getValue();
		alphaText.setText(decimalFormat.format(val));
		anyMDFA.setAlpha(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory().setAlpha(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	protected void handleLambdaChange() {
		
		double val = lambdaSlider.getValue();
		lambdaText.setText(decimalFormat.format(val));
		anyMDFA.setLag(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory().setLambda(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	protected void handleFrequency0Change() {
		
		double val = freqCutoff0.getValue();
		if(val < (freqCutoff1.getValue() - .02)) {
			
			anyMDFA.setBandPassCutoff(val);
			freqCutoff0Text.setText(decimalFormat.format(val));
			if(multiSeries != null) {
				try {
					multiSeries.getMDFAFactory().setBandpassCutoff(val);
					multiSeries.computeFilterCoefficients();
					sketchCanvas();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
		}	
	}
	
	@FXML
	protected void handleFrequency1Change() {
		
		double val = freqCutoff1.getValue();
		if(val > (freqCutoff0.getValue() + .02)) {
			
			anyMDFA.setLowpassCutoff(val);
			freqCutoff1Text.setText(decimalFormat.format(val));
			if(multiSeries != null) {
				try {
					multiSeries.getMDFAFactory().setLowpassCutoff(val);
					multiSeries.computeFilterCoefficients();
					sketchCanvas();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}	
		}		
	}
	
	@FXML
	protected void handleLagChange() {
		
		double val = lagSlider.getValue();
		lagText.setText(decimalFormat.format(val));
		anyMDFA.setLag(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory().setLag(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	protected void handleFractionalDChange() {
		
		double val = fractionalD.getValue();
		fractionalDText.setText(decimalFormat.format(val));
		
		if(multiSeries != null) {
			try {
				multiSeries.adjustFractionalDifferenceData(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleFilterLengthChange() {
		
		int val = (int)filterLength.getValue();
		filterLengthText.setText(""+val);
		anyMDFA.setFilterLength(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory().setFilterLength(val);
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	protected void handleSampleSizeChange() {
		
		int val = (int)sampleSize.getValue();
		
		if(multiSeries != null && val < multiSeries.getSeries(0).size()) {
		
			sampleSizeText.setText(""+val);
			anyMDFA.setSeriesLength(val);
			
			if(multiSeries != null) {
				multiSeries.getMDFAFactory().setSeriesLength(val);
				try {
					multiSeries.computeFilterCoefficients();
					sketchCanvas();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@FXML
	protected void handlePhaseShiftChange() {
		
		double phase = phaseShift.getValue();
		anyMDFA.setShift_constraint(phase);
		
		if(multiSeries != null) {
			
			phaseText.setText(""+phase);
			multiSeries.getMDFAFactory().setShift_constraint(phase);
			try {
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleI1Change() {
		
		int i1 = i1Checkbox.isSelected() ? 1 : 0;
		
		anyMDFA.setI1(i1);
		if(multiSeries != null) {
			multiSeries.getMDFAFactory().setI1(i1);
			try {
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}  
	}
	
	@FXML
	protected void handleI2Change() {
		
		int i2 = i2Checkbox.isSelected() ? 1 : 0;
		
		anyMDFA.setI2(i2);
		if(multiSeries != null) {
			multiSeries.getMDFAFactory().setI2(i2);
			try {
				multiSeries.computeFilterCoefficients();
				sketchCanvas();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}  
	}
	
	private void sketchCanvas() {
			

		if(multiSeries != null) {
			
			try {

		        plotCanvas.getChildren().set(0, TimeSeriesCanvas.createAreaChart(multiSeries, anyMDFA.getSeriesLength(), datetimeFormat));   
								
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(coeffWindow.isShowing()) {
				
				coeffPane.getChildren().set(0, CoefficientCanvas.createAreaChart(assetNames, multiSeries)); 
				coeffScene.setRoot(coeffPane);
				coeffWindow.setScene(coeffScene);
				
			}
			
			if(frfWindow.isShowing()) {
				
			    frfPane.getChildren().set(0,FrequencyResponseCanvas.createAreaChart(assetNames, multiSeries));
			    frfScene.setRoot(frfPane);
			    frfWindow.setScene(frfScene);
			}	
			
			if(phaseWindow.isShowing()) {
				
				phasePane.getChildren().set(0,PhaseCanvas.createAreaChart(assetNames, multiSeries));
				phaseScene.setRoot(phasePane);
				phaseWindow.setScene(phaseScene);
			}
		}
	}
	
	public void initiateCanvas() {
		
		plotCanvas.getChildren().add(TimeSeriesCanvas.createAreaChart(multiSeries, anyMDFA.getSeriesLength(), datetimeFormat));
		
		initFrequencyRFWindow();
		initPhaseWindow();
		initCoefficientWindow();
	}
	
	
	@FXML
	protected void handleButton()  {
		
		if(multiSeries != null) {
			
			try {
				
				TimeSeriesEntry<double[]> observation = marketFeed.getNextMultivariateObservation();
				multiSeries.addValue(observation.getValue(), observation.getDateTime());
			
			} catch (Exception e) {
				
				System.out.println("Insufficient amount of data in csv file");
				e.printStackTrace();
			}
			sketchCanvas();	
		}		
	}
	
	@FXML
	protected void handleCompileData()  {
		
		if(csvFileStreams.size() > 0) {
			
			try {
				marketFeed = new CsvFeed(csvFileStreams, "Index", "Close");
			} catch (IOException e) {
				e.printStackTrace();
			}
			multiSeries = new MultivariateSeries(new MDFASolver(anyMDFA));
			multiSeries.setDateFormat("yyyy-MM-dd");
			
			double d = fractionalD.getValue();
            int nsamples = (int)sampleSize.getValue() + 100;
			
			for(int i = 0; i < csvFileStreams.size(); i++) {
				multiSeries.addSeries(new SignalSeries(new TargetSeries(d, true, assetNames.get(i)), "yyyy-MM-dd"));				
			}
			
			try {		
				
				for(int i = 0; i < nsamples; i++) {
					
					TimeSeriesEntry<double[]> observation = marketFeed.getNextMultivariateObservation();
					multiSeries.addValue(observation.getValue(), observation.getDateTime());
					
				}
				multiSeries.computeFilterCoefficients();
				multiSeries.chopFirstObservations(100);
				
			} catch (Exception e) {
				
				System.out.println("Insufficient amount of data in csv file");
				e.printStackTrace();
			}
			sketchCanvas();		
			
		}	
	}
	
	
	public void initiateMDFABase() {
		
		anyMDFA = new MDFABase()
				      .setAlpha(alphaSlider.getValue())
				      .setLag(lagSlider.getValue())
				      .setLambda(lambdaSlider.getValue())
				      .setBandPassCutoff(freqCutoff0.getValue())
				      .setLowpassCutoff(freqCutoff1.getValue())
				      .setCrossCorr(crossCorrelation.getValue())
				      .setDecayStart(decayStart.getValue())
				      .setDecayStrength(decayStrength.getValue())
				      .setSmooth(smoothness.getValue())
				      .setFilterLength((int)filterLength.getValue())
				      .setI1(i1Checkbox.isSelected() ? 1 : 0)
				      .setI2(i2Checkbox.isSelected() ? 1 : 0)
				      .setShift_constraint(phaseShift.getValue())
				      .setSeriesLength((int)sampleSize.getValue());		
	}
	
	
	private void initPhaseWindow() {
		
		phasePane = new StackPane();
	    phasePane.getChildren().add(PhaseCanvas.createAreaChart(assetNames, multiSeries)); 
	      
	    phaseScene = new Scene(phasePane, 800, 600);
	    phaseScene.getStylesheets().add("css/areachart.css");
	      
	    phaseWindow = new Stage();
	    phaseWindow.setTitle("PhaseDelay Functions");
	    phaseWindow.setScene(phaseScene);
	    phaseWindow.setX(primaryStage.getX() + 800);
	    phaseWindow.setY(primaryStage.getY() + 700);
	    
	    phaseWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                phaseCheckbox.setSelected(false);
            }
        });  
	}
	
	private void initFrequencyRFWindow() {
		
		  frfPane = new StackPane();
	      frfPane.getChildren().add(FrequencyResponseCanvas.createAreaChart(assetNames, multiSeries)); 
	      
	      frfScene = new Scene(frfPane, 800, 600);
	      frfScene.getStylesheets().add("css/areachart.css");
	      
	      frfWindow = new Stage();
          frfWindow.setTitle("Frequency Response Functions");
          frfWindow.setScene(frfScene);
          frfWindow.setX(primaryStage.getX() + 200);
          frfWindow.setY(primaryStage.getY() + 100);
          
          frfWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
              public void handle(WindowEvent we) {
                  frfCheckbox.setSelected(false);
              }
          });   
	}
	

	private void initCoefficientWindow() {
		
		  coeffPane = new StackPane();
	      coeffPane.getChildren().add(CoefficientCanvas.createAreaChart(assetNames, multiSeries)); 
	      
	      coeffScene = new Scene(coeffPane, 800, 600);
	      coeffScene.getStylesheets().add("css/coeffchart.css");
	      
	      coeffWindow = new Stage();
          coeffWindow.setTitle("MDFA Coefficients");
          coeffWindow.setScene(coeffScene);
          coeffWindow.setX(primaryStage.getX() + 500);
          coeffWindow.setY(primaryStage.getY() + 400);
          
  	      coeffWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                coeffCheckbox.setSelected(false);
            }
          });
	}
	
	@FXML
	protected void toggleCoefficientWindow(ActionEvent event) {
		
		if(coeffCheckbox.isSelected()) {
			coeffWindow.show();
		}
		else {
			coeffWindow.close();
		}
	}
		
	@FXML
	protected void toggleFrFWindow(ActionEvent event) {
		
		if(frfCheckbox.isSelected()) {
			frfWindow.show();
		}
		else {
			frfWindow.close();
		}
	}
    
	@FXML
	protected void togglePhaseWindow(ActionEvent event) {
		
		if(phaseCheckbox.isSelected()) {
			phaseWindow.show();
		}
		else {
			phaseWindow.close();
		}
	}
	

	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
    

	
	
	public static DateTime getSignalDateTime(String date, DateTimeFormatter formatter) {
		return formatter.parseDateTime(date);
	}
    
	
}
