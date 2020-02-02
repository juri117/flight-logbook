package flightLogApp;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import flightLogApp.controller.FlightController;
import flightLogApp.controller.Log;
import flightLogApp.controller.MainViewController;
import flightLogApp.model.Flight;
import flightLogApp.utils.PrefLoader;
import flightLogApp.utils.SQLiteParser;

/**
 * main method and startup.
 *
 * @author Juri Bieler
 */

public class MainApp extends Application {
	
	private Boolean debug = true;
	private Boolean errorLog = true;

	private Locale local = new Locale("de");

	private Stage primaryStage;
	private BorderPane rootLayout;
	private SplitPane splitPane;

	private MainViewController mainViewController;
	private Log log;

	private String dbFileName = "flug";
	SQLiteParser sql;

	private FlightController flightControll;

	public MainApp() {
		sql = new SQLiteParser(dbFileName);
		sql.connectToDB();
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("FlightLog");

		initRootLayout();
		showMainView();

		log = mainViewController.getLog();

		flightControll = new FlightController(mainViewController, log, sql);
		flightControll.updateView();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				sql.connectToDB();
				System.out.println("Stage is closing");
				// TODO: save? cleanup?
				saveSettings();
			}
		});

		loadSettings();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			Scene scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.show();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Shows the person overview inside the root layout.
	 */
	public void showMainView() {
		try {
			// Load person overview.
			// ResourceBundle bundle =
			// ResourceBundle.getBundle("bundles.general", new Locale("en"));
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(MainApp.class.getResource("view/MainView.fxml"));
			loader.setResources(ResourceBundle.getBundle("bundles.general", local));

			AnchorPane mainView = (AnchorPane) loader.load();

			// Set mainView into the center of root layout.
			rootLayout.setCenter(mainView);

			// get SplitPane to save divider position
			splitPane = (SplitPane) (mainView.getChildren().get(0));

			rootLayout.widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
					if (newSceneWidth.doubleValue() > rootLayout.getMinWidth()) {
						System.out.println("resize Win");
					}
				}
			});

			// Give the controller access to the main app.
			mainViewController = loader.getController();
			mainViewController.setMainApp(this);

			mainViewController.init();

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void saveSettings() {
		Double winWidth = primaryStage.getWidth();
		Double winHeight = primaryStage.getHeight();
		Double winX = primaryStage.getX();
		Double winY = primaryStage.getY();
		Double dividerPos = splitPane.getDividerPositions()[0];

		PrefLoader pref = new PrefLoader();
		pref.save("debug", log.getDebug().toString());
		pref.save("errorLog", log.getErrorLog().toString());
		
		pref.save("winWidth", winWidth.toString());
		pref.save("winHeight", winHeight.toString());
		pref.save("winX", winX.toString());
		pref.save("winY", winY.toString());
		pref.save("dividerPos", dividerPos.toString());
	}

	private void loadSettings() {
		PrefLoader pref = new PrefLoader();
		
		log.setDebug(pref.loadBoolean("debug", debug));
		log.setErrorLog(pref.loadBoolean("errorLog", errorLog));

		primaryStage.setWidth(pref.loadDouble("winWidth", 600.0));
		primaryStage.setHeight(pref.loadDouble("winHeight", 400.0));
		primaryStage.setX(pref.loadDouble("winX", 0.0));
		primaryStage.setY(pref.loadDouble("winY", 0.0));

		splitPane.setDividerPositions(pref.loadDouble("dividerPos", 0.3));
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public FlightController getFlightControll() {
		return flightControll;
	}

	public void tmpButton() {
		flightControll.updateView();
	}

	public void newBookButton() {
		flightControll.openNewFlightBookDialog(primaryStage);
	}

	public void addButton(int flightBookIndex) {
		flightControll.openNewFlightDialog(primaryStage, flightBookIndex);
	}

	public void editButton(Flight flight) {
		flightControll.openEditFlightDialog(primaryStage, flightControll.findFlightBookByFlight(flight), flight);
	}
	
	public void exportButton(int flightBookIndex){
		flightControll.openExportDialog(primaryStage, flightBookIndex);
	}
	
	public void importButton(int flightBookIndex){
		flightControll.openImportDialog(primaryStage, flightBookIndex);
	}

	public static void main(String[] args) {
		launch(args);
	}
}