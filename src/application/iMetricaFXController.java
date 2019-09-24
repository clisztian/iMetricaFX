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
import ch.imetrica.mdfa.series.MultivariateFXSeries;
import ch.imetrica.mdfa.series.TargetSeries;
import ch.imetrica.mdfa.series.TimeSeriesEntry;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
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
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class iMetricaFXController {

	DecimalFormat decimalFormat = new DecimalFormat("#.##");
	
	final FileChooser fileChooser = new FileChooser();	
	private ArrayList<MDFABase> anyMDFAs;
	
	private Stage primaryStage; 
	private CsvFeed marketFeed; 
	private MultivariateFXSeries multiSeries;
	private ArrayList<String> csvFileStreams = new ArrayList<String>();
	private ArrayList<String> assetNames = new ArrayList<String>();
    private String datetimeFormat = new String("yyyy-MM-dd");
	private ToggleGroup dateFormatSelect;
	private ToggleGroup targetSeriesSelect;
	private ToggleGroup signalSelect;
	private ArrayList<RadioMenuItem> signalRadioMenuList;
	private ArrayList<RadioMenuItem> targetRadioMenuList;
	private Task<Void> runMarket;
	

	Stage coeffWindow = new Stage();
	Stage frfWindow = new Stage();
	Stage phaseWindow = new Stage();
	Stage explainWindow = new Stage();
	Stage turningPointWindow = new Stage();
	
	StackPane coeffPane = new StackPane();
	StackPane frfPane = new StackPane();
	StackPane phasePane = new StackPane();
	StackPane explainPane = new StackPane();
	StackPane turningPointPane = new StackPane();
	
	Scene coeffScene;
	Scene frfScene;
	Scene phaseScene;
	Scene explainScene;
	Scene turningPointScene;
	

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
	private Button streamDataButton;
	
	@FXML 
	private Button stopDataButton;
	
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
	private RadioMenuItem mmDDyyyy;
	
	@FXML
	private CheckMenuItem frfCheckbox;

	@FXML
	private CheckMenuItem explainCheckbox;
	
	@FXML
	private CheckMenuItem coeffCheckbox;
	
	@FXML
	private CheckMenuItem phaseCheckbox;
	
	@FXML
	private CheckMenuItem prefilterCheck;
	
	@FXML
	private CheckMenuItem useCaseMarketDriversCheckBox;
	
	@FXML
	private CheckMenuItem turningPointCanvas;
	
	@FXML 
	private Menu targetSeriesSelection;
	
	@FXML 
	private Menu signalMenu;
	
	@FXML 
	private MenuItem addNewSignal;

	private int selectedSignal = 0;

	private boolean autocomputeActivated = true;

	private Thread runMarketThread;

	public void setToggleGroups() {
		
		dateFormatSelect = new ToggleGroup();
		yyyyMMdd.setToggleGroup(dateFormatSelect);
		yyyyMMddHHmmss.setToggleGroup(dateFormatSelect);
		mmDDyyyy.setToggleGroup(dateFormatSelect);
		yyyyMMdd.setSelected(true);		
		
		targetSeriesSelect = new ToggleGroup();
		signalSelect = new ToggleGroup();
		
		
		signalRadioMenuList.add((new RadioMenuItem("Signal_1")));
		signalMenu.getItems().add(signalRadioMenuList.get(0));
		signalRadioMenuList.get(0).setSelected(true);
		signalRadioMenuList.get(0).setUserData(""+signalRadioMenuList.size());
		signalRadioMenuList.get(0).setToggleGroup(signalSelect);
		
		
		targetSeriesSelect.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	        public void changed(ObservableValue<? extends Toggle> ov,
	            Toggle old_toggle, Toggle new_toggle) {
	          if (targetSeriesSelect.getSelectedToggle() != null) {
	            
	        	  for(int i = 0; i < targetRadioMenuList.size(); i++) {
	        		  if(targetSeriesSelect.getSelectedToggle().equals(targetRadioMenuList.get(i))) {
	        			try {
	      					
	        				multiSeries.setTargetSeriesIndex(i);	        				
	      					sketchCanvas();
	      				} catch (Exception e) {
	      					e.printStackTrace();
	      				}	
	        		  }
	        	  }
	          }
	        }
	      });
		
		signalSelect.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
	        public void changed(ObservableValue<? extends Toggle> ov,
	            Toggle old_toggle, Toggle new_toggle) {
	          if (signalSelect.getSelectedToggle() != null) {
	            
	        	  for(int i = 0; i < signalRadioMenuList.size(); i++) {
	        		  if(signalSelect.getSelectedToggle().equals(signalRadioMenuList.get(i))) {
	        			  selectedSignal = i;        			  
	        			  setControlsToMDFABase(anyMDFAs.get(i));	        			  
	        			  sketchCanvas();
	        		  }
	        	  }
	          }
	        }
	      });
		
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
    	else if(dateFormatSelect.getSelectedToggle().equals(mmDDyyyy)) {
    		System.out.println("Set Dateformat to 'M/d/yyyy H:mm'");
    		datetimeFormat = "M/d/yyyy H:mm";
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
	protected void handleAddNewSignal() {
		
		System.out.println("Adding new signal...");
		anyMDFAs.add(getNewMDFABase());
		
		try {
			
			multiSeries.addMDFABase(anyMDFAs.get(anyMDFAs.size()-1));
			
			signalRadioMenuList.add((new RadioMenuItem("Signal_" + anyMDFAs.size()))); 
			signalRadioMenuList.get(signalRadioMenuList.size()-1).setUserData(""+signalRadioMenuList.size());
			signalRadioMenuList.get(signalRadioMenuList.size()-1).setToggleGroup(signalSelect);
			signalMenu.getItems().add(signalRadioMenuList.get(signalRadioMenuList.size() - 1));
			signalRadioMenuList.get(signalRadioMenuList.size() - 1).setSelected(true);
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
	
		
	
	@FXML
	protected void handleSmoothnessChange() {
		
		double val = smoothness.getValue();
		smoothnessText.setText(decimalFormat.format(val));
		anyMDFAs.get(selectedSignal).setSmooth(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory(selectedSignal).setSmoothRegularization(val);
				
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}			
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	
	}
	
	@FXML
	protected void handleDecayStrengthChange() {
		
		double val = decayStrength.getValue();
		decayStrengthText.setText(decimalFormat.format(val));
		anyMDFAs.get(selectedSignal).setDecayStrength(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory(selectedSignal).setDecayStrengthRegularization(val);
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleDecayStartChange() {
		
		double val = decayStart.getValue();
		decayStartText.setText(decimalFormat.format(val));
		anyMDFAs.get(selectedSignal).setDecayStrength(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory(selectedSignal).setDecayStartRegularization(val);
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleCrossCorrelationChange() {
		
		double val = crossCorrelation.getValue();
		crossCorrelationText.setText(decimalFormat.format(val));
		anyMDFAs.get(selectedSignal).setCrossCorr(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory(selectedSignal).setCrossRegularization(val);
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleAlphaChange() {
		
		double val = alphaSlider.getValue();
		alphaText.setText(decimalFormat.format(val));
		anyMDFAs.get(selectedSignal).setAlpha(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory(selectedSignal).setAlpha(val);
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	protected void handleLambdaChange() {
		
		double val = lambdaSlider.getValue();
		lambdaText.setText(decimalFormat.format(val));
		anyMDFAs.get(selectedSignal).setLag(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory(selectedSignal).setLambda(val);
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	protected void handleFrequency0Change() {
		
		double val = freqCutoff0.getValue();
		if(val < (freqCutoff1.getValue() - .02)) {
			
			anyMDFAs.get(selectedSignal).setBandPassCutoff(val);
			freqCutoff0Text.setText(decimalFormat.format(val));
			if(multiSeries != null) {
				try {
					multiSeries.getMDFAFactory(selectedSignal).setBandpassCutoff(val);
					if(autocomputeActivated) {
						multiSeries.computeFilterCoefficients(selectedSignal);
						multiSeries.computeAggregateSignal();
						sketchCanvas();
					}
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
			
			anyMDFAs.get(selectedSignal).setLowpassCutoff(val);
			freqCutoff1Text.setText(decimalFormat.format(val));
			if(multiSeries != null) {
				try {
					multiSeries.getMDFAFactory(selectedSignal).setLowpassCutoff(val);
					if(autocomputeActivated) {
						multiSeries.computeFilterCoefficients(selectedSignal);
						multiSeries.computeAggregateSignal();
						sketchCanvas();
					}
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
		anyMDFAs.get(selectedSignal).setLag(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory(selectedSignal).setLag(val);
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
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
				multiSeries.computeAllFilterCoefficients();
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
		anyMDFAs.get(selectedSignal).setFilterLength(val);
		
		if(multiSeries != null) {
			try {
				multiSeries.getMDFAFactory(selectedSignal).setFilterLength(val);
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	@FXML
	protected void handleSampleSizeChange() {
		
		int val = (int)sampleSize.getValue();
		
		if(multiSeries != null && val < multiSeries.size()) {
		
			sampleSizeText.setText(""+val);
			anyMDFAs.get(selectedSignal).setSeriesLength(val);
			
			if(multiSeries != null) {
				multiSeries.getMDFAFactory(selectedSignal).setSeriesLength(val);
				try {
					if(autocomputeActivated) {
						multiSeries.computeFilterCoefficients(selectedSignal);
						multiSeries.computeAggregateSignal();
						sketchCanvas();
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	@FXML
	protected void handlePhaseShiftChange() {
		
		double phase = phaseShift.getValue();
		anyMDFAs.get(selectedSignal).setShift_constraint(phase);
		
		if(multiSeries != null) {
			
			phaseText.setText(""+phase);
			multiSeries.getMDFAFactory(selectedSignal).setShift_constraint(phase);
			try {
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@FXML
	protected void handleI1Change() {
		
		int i1 = i1Checkbox.isSelected() ? 1 : 0;
		
		anyMDFAs.get(selectedSignal).setI1(i1);
		if(multiSeries != null) {
			multiSeries.getMDFAFactory(selectedSignal).setI1(i1);
			try {
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}  
	}
	
	@FXML
	protected void handleI2Change() {
		
		int i2 = i2Checkbox.isSelected() ? 1 : 0;
		
		anyMDFAs.get(selectedSignal).setI2(i2);
		if(multiSeries != null) {
			multiSeries.getMDFAFactory(selectedSignal).setI2(i2);
			try {
				if(autocomputeActivated) {
					multiSeries.computeFilterCoefficients(selectedSignal);
					multiSeries.computeAggregateSignal();
					sketchCanvas();
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}  
	}
	
	
	@FXML
	protected void handleStopOnMarket() {
		
		if(runMarketThread != null && runMarketThread.isAlive()) {		

			runMarketThread.interrupt();
			runMarket.cancel();
		}
		
	}
	
	
	@FXML
	protected void handleRunOnMarket() {
		
		
		
		runMarket = new Task<Void>() {
			
			@Override
            protected Void call() throws Exception {
				
				while(true) {
				
					try { Thread.sleep(5000); // wait time in milliseconds to control duration
			        } catch( InterruptedException e ) { }
					
					if(multiSeries != null) {
						
						TimeSeriesEntry<double[]> observation = marketFeed.getNextMultivariateObservation();
						
						if(observation == null) {
							this.cancel();
							break;
						}
		
						multiSeries.addValue(observation.getDateTime(), observation.getValue());
						multiSeries.computeAllFilterCoefficients();
						multiSeries.computeAggregateSignal();
						
						
						Platform.runLater(new Runnable() {
						    @Override
						    public void run() {
						    	sketchCanvas();
						    }
						});
						
						
					}
					
					if (this.isCancelled()) {
		                break;
		            }				
				}
				return null;
			}
		};

		
		runMarketThread = new Thread(runMarket, "learn-thread");
		runMarketThread.setDaemon(true);
		runMarketThread.start();
		
	}
	
	
	private void sketchCanvas() {
			

		if(multiSeries != null) {
			
			try {

		        plotCanvas.getChildren().set(0, TimeSeriesCanvas.createAreaChart(multiSeries, 
		        		 anyMDFAs.get(selectedSignal).getSeriesLength(), selectedSignal, datetimeFormat));   
								
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(coeffWindow.isShowing()) {
				
				coeffPane.getChildren().set(0, CoefficientCanvas.createAreaChart(assetNames, multiSeries, selectedSignal)); 
				coeffScene.setRoot(coeffPane);
				coeffWindow.setScene(coeffScene);
				
			}
			
			if(frfWindow.isShowing()) {
				
			    frfPane.getChildren().set(0,FrequencyResponseCanvas.createAreaChart(assetNames, multiSeries, selectedSignal));
			    frfScene.setRoot(frfPane);
			    frfWindow.setScene(frfScene);
			}	
			
			if(phaseWindow.isShowing()) {
				
				phasePane.getChildren().set(0,PhaseCanvas.createAreaChart(assetNames, multiSeries, selectedSignal));
				phaseScene.setRoot(phasePane);
				phaseWindow.setScene(phaseScene);
			}
			
			if(explainWindow.isShowing()) {
				

				explainPane.getChildren().set(0, ExplainabilityCanvas.createAreaChart(assetNames, multiSeries, 
						multiSeries.getTargetSeriesIndex(), anyMDFAs.get(selectedSignal).getLowPassCutoff()));
				
				
				
				explainScene.setRoot(explainPane);
				explainWindow.setScene(explainScene);
			}
			
			if(turningPointWindow.isShowing()) {
				
				turningPointPane.getChildren().set(0, TurningPointCanvas.createAreaChart(multiSeries, datetimeFormat, multiSeries.getTargetSeriesIndex())); 
				turningPointScene.setRoot(turningPointPane);
				turningPointWindow.setScene(turningPointScene);
			}
		}
	}
	
	public void initiateCanvas() {
		
		plotCanvas.getChildren().add(TimeSeriesCanvas.createAreaChart(multiSeries, anyMDFAs.get(selectedSignal).getSeriesLength(), 
				                                                        selectedSignal, datetimeFormat));
		
		initFrequencyRFWindow();
		initPhaseWindow();
		initCoefficientWindow();
		initExplainabilityWindow();
		initTurningPointWindow();
	}
	
	
	@FXML
	protected void handleButton()  {
		
		if(multiSeries != null) {
			
			try {
				
				TimeSeriesEntry<double[]> observation = marketFeed.getNextMultivariateObservation();
				multiSeries.addValue(observation.getDateTime(), observation.getValue());
			
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
			multiSeries = new MultivariateFXSeries(anyMDFAs, datetimeFormat);
			
			double d = fractionalD.getValue();
            int nsamples = (int)sampleSize.getValue() + 100;
			
			for(int i = 0; i < csvFileStreams.size(); i++) {
				
				try {
					multiSeries.addSeries(new TargetSeries(d, true, assetNames.get(i)));
				} catch (Exception e) {
					e.printStackTrace();
				}				
			}
			
			try {		
				
				for(int i = 0; i < nsamples; i++) {
					
					TimeSeriesEntry<double[]> observation = marketFeed.getNextMultivariateObservation();
					multiSeries.addValue(observation.getDateTime(), observation.getValue());
					
				}
				multiSeries.computeAllFilterCoefficients();
				multiSeries.chopFirstObservations(100);
				
			} catch (Exception e) {
				
				System.out.println("Insufficient amount of data in csv file");
				e.printStackTrace();
			}
			sketchCanvas();		
			
			//Now add series list to targetSeriesMenu
			targetSeriesSelection.getItems().clear();
			targetRadioMenuList = new ArrayList<RadioMenuItem>();			
			for(int i = 0; i < assetNames.size(); i++) {
				
				targetRadioMenuList.add((new RadioMenuItem(assetNames.get(i))));
				targetRadioMenuList.get(i).setToggleGroup(targetSeriesSelect);
				targetSeriesSelection.getItems().add(targetRadioMenuList.get(i));
			}
	
			
			

		}	
	}
	
	@FXML
	protected void handleApplyPrefilter() {
		
		if(prefilterCheck.isSelected()) {
			
			multiSeries.prefilterActivate(true);
			multiSeries.setWhiteNoisePrefilters(50);
		}
		else {
			multiSeries.prefilterActivate(false);
		}
	   
		try {
			multiSeries.computeAllFilterCoefficients();
			sketchCanvas();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void initiateMDFABase() {
		
		
		anyMDFAs 			= new ArrayList<MDFABase>();
		signalRadioMenuList = new ArrayList<RadioMenuItem>();
		targetRadioMenuList = new ArrayList<RadioMenuItem>();
		
		MDFABase anyMDFA = new MDFABase()
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
		
		anyMDFAs.add(anyMDFA);
		

		
		
	}
	
	public MDFABase getNewMDFABase() {
		
		return new MDFABase()
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
			      .setSeriesLength(anyMDFAs.get(anyMDFAs.size()-1).getSeriesLength());	
	}
	
	private void initPhaseWindow() {
		
		phasePane = new StackPane();
	    phasePane.getChildren().add(PhaseCanvas.createAreaChart(assetNames, multiSeries, selectedSignal)); 
	      
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
	      frfPane.getChildren().add(FrequencyResponseCanvas.createAreaChart(assetNames, multiSeries, selectedSignal)); 
	      
	      frfScene = new Scene(frfPane, 800, 600);
	      frfScene.getStylesheets().add("css/timeserieschart.css");
	      
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
	

	private void initExplainabilityWindow() {
		
		  new ExplainabilityCanvas(); 
		  explainPane = new StackPane();
		  explainPane.getChildren().add(ExplainabilityCanvas.createAreaChart(assetNames, multiSeries, 
				  0, anyMDFAs.get(selectedSignal).getLowPassCutoff())); 
		  

	      
	      explainScene = new Scene(explainPane, 800, 600);
	      explainScene.getStylesheets().add("css/timeserieschart.css");
	      
	      explainWindow = new Stage();
	      explainWindow.setTitle("Global Interpretability");
	      explainWindow.setScene(explainScene);
	      explainWindow.setX(primaryStage.getX() + 200);
	      explainWindow.setY(primaryStage.getY() + 100);
        
	      explainWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
            public void handle(WindowEvent we) {
                explainCheckbox.setSelected(false);
            }
        });   
	}
		
	private void initTurningPointWindow() {
		

		  turningPointPane = new StackPane();
		  turningPointPane.getChildren().add(TurningPointCanvas.createAreaChart(multiSeries, datetimeFormat, 0)); 
		  	      
		  turningPointScene = new Scene(turningPointPane, 980, 700);
		  turningPointScene.getStylesheets().add("css/timeserieschart.css");
	      
		  turningPointWindow = new Stage();
		  turningPointWindow.setTitle("Real-Time Turning-Point Detection");
		  turningPointWindow.setScene(turningPointScene);
		  turningPointWindow.setX(primaryStage.getX() + 200);
		  turningPointWindow.setY(primaryStage.getY() + 100);
      
		  turningPointWindow.setOnCloseRequest(new EventHandler<WindowEvent>() {
          public void handle(WindowEvent we) {
        	  turningPointCanvas.setSelected(false);
          }
      });   
	}
	
	private void initCoefficientWindow() {
		
		  coeffPane = new StackPane();
	      coeffPane.getChildren().add(CoefficientCanvas.createAreaChart(assetNames, multiSeries, selectedSignal)); 
	      
	      coeffScene = new Scene(coeffPane, 800, 600);
	      coeffScene.getStylesheets().add("css/timeserieschart.css");
	      
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
			sketchCanvas();
		}
		else {
			coeffWindow.close();
		}
	}
		
	@FXML
	protected void toggleFrFWindow(ActionEvent event) {
		
		if(frfCheckbox.isSelected()) {
			frfWindow.show();
			sketchCanvas();
		}
		else {
			frfWindow.close();
		}
	}
    
	@FXML
	protected void togglePhaseWindow(ActionEvent event) {
		
		if(phaseCheckbox.isSelected()) {
			phaseWindow.show();
			sketchCanvas();
		}
		else {
			phaseWindow.close();
		}
	}
	
	@FXML
	protected void toggleExplainWindow(ActionEvent event) {
		
		if(explainCheckbox.isSelected()) {
			explainWindow.show();
			sketchCanvas();
		}
		else {
			explainWindow.close();
		}
	}

	
	@FXML
	protected void toggleTurningPointWindow(ActionEvent event) {
		
		if(turningPointCanvas.isSelected()) {
			turningPointWindow.show();
			sketchCanvas();
		}
		else {
			turningPointWindow.close();
		}
	}
	
	
	
	public void setStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}
    

	
	
	public static DateTime getSignalDateTime(String date, DateTimeFormatter formatter) {
		return formatter.parseDateTime(date);
	}
    
	
	private void setControlsToMDFABase(MDFABase thisBase) {
		
		autocomputeActivated  = false;
		smoothness.setValue(thisBase.getSmooth());
		decayStrength.setValue(thisBase.getDecayStrength());
		decayStart.setValue(thisBase.getDecayStart());
		crossCorrelation.setValue(thisBase.getCrossCorr());
		filterLength.setValue(thisBase.getFilterLength());
		lagSlider.setValue(thisBase.getLag());
		alphaSlider.setValue(thisBase.getAlpha());
		lambdaSlider.setValue(thisBase.getLambda());
		freqCutoff0.setValue(thisBase.getBandPassCutoff());
		freqCutoff1.setValue(thisBase.getLowPassCutoff());
		phaseShift.setValue(thisBase.getShift_constraint());
		i2Checkbox.setSelected(thisBase.getI2() == 1);
		i1Checkbox.setSelected(thisBase.getI1() == 1);
		sampleSize.setValue(thisBase.getSeriesLength());
		
		smoothnessText.setText("" + thisBase.getSmooth());
		decayStrengthText.setText("" + thisBase.getDecayStrength());
		decayStartText.setText("" + thisBase.getDecayStart());
		crossCorrelationText.setText("" + thisBase.getCrossCorr());
		filterLengthText.setText("" + thisBase.getFilterLength());
		lagText.setText("" + thisBase.getLag());
		alphaText.setText("" + thisBase.getAlpha());
		lambdaText.setText("" + thisBase.getLambda());
		freqCutoff0Text.setText("" + thisBase.getBandPassCutoff());
		freqCutoff1Text.setText("" + thisBase.getLowPassCutoff());
		phaseText.setText("" + thisBase.getShift_constraint());
		sampleSizeText.setText("" + thisBase.getSeriesLength());
		
		
		autocomputeActivated = true;
		
		try {
			multiSeries.computeFilterCoefficients(selectedSignal);
			multiSeries.computeAggregateSignal();
		} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
		}
	}


	@FXML
	protected void handleApplyUseCase() {
		
		List<File> filelist = new ArrayList<File>();
		filelist.add(new File("data/COFFEE.daily.csv"));
		filelist.add(new File("data/SMI.daily.csv"));
		filelist.add(new File("data/SFR.daily.csv"));
		filelist.add(new File("data/FINANCE.daily.csv"));
		filelist.add(new File("data/CRUDE.daily.csv"));
		filelist.add(new File("data/EURO.daily.csv"));
		filelist.add(new File("data/SUGAR.daily.csv"));
		
		csvFileStreams = new ArrayList<String>();
		assetNames = new ArrayList<String>();
		
		for(File file : filelist) {
			
			csvFileStreams.add(file.getAbsolutePath());
			assetNames.add(file.getName().split("[.]+")[0]);
			System.out.println(file.getAbsolutePath());
		}
		
		MDFABase base = new MDFABase().setAlpha(0)
				                      .setBandPassCutoff(0.0)
				                      .setCrossCorr(0)
				                      .setDecayStart(.20)
				                      .setDecayStrength(.20)
				                      .setFilterLength(20)
				                      .setHybridForecast(0)
				                      .setI1(0)
				                      .setI2(0)
				                      .setLag(-2.0)
				                      .setLambda(0)
				                      .setLowpassCutoff(.28)
				                      .setSmooth(.20);
		
		handleCompileData();		
		setControlsToMDFABase(base);	
		fractionalD.setValue(1.0);
		handleFractionalDChange();
		
//		prefilterCheck.setSelected(true);
//		handleApplyPrefilter();
		handleFilterLengthChange();
		handleFrequency1Change();
		
		if(!turningPointWindow.isShowing()) {
			turningPointWindow.show();
		}
		
		if(!explainWindow.isShowing()) {
			explainWindow.show();
		}
		
		if(!coeffWindow.isShowing()) {
			coeffWindow.show();
		}
		
		
		
	}

	
	
	
}
