package flightLogApp;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Locale;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.SplitPane;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import com.sun.javafx.css.StyleManager;

import flightLogApp.controller.FlightController;
import flightLogApp.controller.Log;
import flightLogApp.controller.MainViewController;
import flightLogApp.controller.SettingsWinController;
import flightLogApp.controller.Updater;
import flightLogApp.model.Flight;
import flightLogApp.model.FlightBook;
import flightLogApp.utils.PrefLoader;
import flightLogApp.utils.SQLiteParser;

/**
 * main method and startup.
 *
 * @author Juri Bieler
 */

public class FlightLog extends Application {

	private String aktVersion = "0.20";

	private Boolean debug = true;
	private Boolean errorLog = true;
	private String theme = "Default";
	private String language = "de";

	private String languages[] = { "en", "de" };

	private Scene scene;
	private Stage primaryStage;
	private BorderPane rootLayout;
	

	private MainViewController mainViewController;
	private Log log;

	private String dbFileName = "flug";
	SQLiteParser sql;

	private FlightController flightControll;

	public FlightLog() {
		this.log = new Log();
		sql = new SQLiteParser(dbFileName, log);
	}

	@Override
	public void start(Stage primaryStage) {
		this.primaryStage = primaryStage;
		this.primaryStage.setTitle("FlightLog");

		PrefLoader pref = new PrefLoader();
		language = pref.loadString("language", language);
		log.setDebug(pref.loadBoolean("debug", debug));
		log.setErrorLog(pref.loadBoolean("errorLog", errorLog));

		sql.connectToDB();

		initRootLayout();
		showMainView();

		log.setTextArea(mainViewController.getLogTextArea());

		flightControll = new FlightController(mainViewController, log, sql);
		flightControll.updateView();

		primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {
			public void handle(WindowEvent we) {
				closeStage();
				System.out.println("Stage is closing");
			}
		});

		log.stat("Version: " + aktVersion);
		loadSettings(pref);
		flightControll.loadBookState();

		Updater.checkIfUpdated(getParameters(), aktVersion);
	}

	public void closeStage() {
		flightControll.saveBookState();
		sql.disconnectToDB();
		saveSettings();
		System.out.println("Stage is closing");
		//primaryStage.close();
	}

	/**
	 * Initializes the root layout.
	 */
	public void initRootLayout() {
		try {
			// Load root layout from fxml file.
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(FlightLog.class.getResource("view/RootLayout.fxml"));
			rootLayout = (BorderPane) loader.load();

			// Show the scene containing the root layout.
			scene = new Scene(rootLayout);
			primaryStage.setScene(scene);
			primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("view/image/icon.png")));
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
			loader.setLocation(FlightLog.class.getResource("view/MainView.fxml"));
			loader.setResources(ResourceBundle.getBundle("bundles.general", new Locale(language)));

			AnchorPane mainView = (AnchorPane) loader.load();

			// Set mainView into the center of root layout.
			rootLayout.setCenter(mainView);

			// get SplitPane to save divider position
			//splitPane = (SplitPane) (mainView.getChildren().get(0));

			rootLayout.widthProperty().addListener(new ChangeListener<Number>() {
				@Override
				public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
					if (newSceneWidth.doubleValue() > rootLayout.getMinWidth()) {
						// System.out.println("resize Win");
					}
				}
			});

			// Give the controller access to the main app.
			mainViewController = loader.getController();
			mainViewController.setMainApp(this);
			mainViewController.setLog(log);

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
		Double dividerPos = mainViewController.getSplitPane(0).getDividerPositions()[0];
		Double dividerPos1 = mainViewController.getSplitPane(1).getDividerPositions()[0];
		Double dividerPos2 = mainViewController.getSplitPane(2).getDividerPositions()[0];

		PrefLoader pref = new PrefLoader();
		pref.save("debug", log.getDebug().toString());
		pref.save("errorLog", log.getErrorLog().toString());

		pref.save("theme", theme);
		pref.save("language", language);

		pref.save("winWidth", winWidth.toString());
		pref.save("winHeight", winHeight.toString());
		pref.save("winX", winX.toString());
		pref.save("winY", winY.toString());
		pref.save("dividerPos", dividerPos.toString());
		pref.save("dividerPos1", dividerPos1.toString());
		pref.save("dividerPos2", dividerPos2.toString());
	}

	private void loadSettings(PrefLoader pref) {
		// load theme
		changeTheme(pref.loadString("theme", theme));

		primaryStage.setWidth(pref.loadDouble("winWidth", 600.0));
		primaryStage.setHeight(pref.loadDouble("winHeight", 400.0));
		primaryStage.setX(pref.loadDouble("winX", 0.0));
		primaryStage.setY(pref.loadDouble("winY", 0.0));

		mainViewController.getSplitPane(0).setDividerPositions(pref.loadDouble("dividerPos", 0.3));
		mainViewController.getSplitPane(1).setDividerPositions(pref.loadDouble("dividerPos1", 0.4));
		mainViewController.getSplitPane(2).setDividerPositions(pref.loadDouble("dividerPos2", 0.7));
	}

	public void changeTheme(String themeName) {
		changeTheme(themeName, this.scene);
	}

	private void changeTheme(String themeName, Scene scene) {
		try {
			String themePath = "themes/" + themeName + ".css";
			InputStream inputStream;
			if (themeName.equals("Default")) {
				themePath = "view/Default.css";
				inputStream = FlightLog.class.getResourceAsStream(themePath);
			} else {
				File f = new File(themePath);
				if (!f.exists() || f.isDirectory()) {
					log.error("Theme does not exist.");
					return;
				}
				inputStream = new FileInputStream(themePath);
			}

			// InputStream inputStream =
			// MainApp.class.getResourceAsStream(themePath);

			File tempStyleSheetDest = File.createTempFile("javafx_stylesheet", "");
			tempStyleSheetDest.deleteOnExit();
			Files.copy(inputStream, tempStyleSheetDest.toPath(), StandardCopyOption.REPLACE_EXISTING);
			// scene.getStylesheets().add(tempStyleSheetDest.toURI().toString());

			Application.setUserAgentStylesheet(null);
			StyleManager.getInstance().addUserAgentStylesheet(tempStyleSheetDest.toURI().toString());

			theme = themeName;
			// scene.getStylesheets().add("view/DarkTheme.css");
		} catch (Exception e) {
			log.error("could not load Theme", e);
		}
	}

	/**
	 * Returns the main stage.
	 * 
	 * @return
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	public String[] getLanguages() {
		return languages;
	}

	public String getLanguage() {
		return language;
	}
	
	public void setLanguage(String language){
		this.language = language;
	}

	public String getTheme() {
		return theme;
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

	public void addFlightButton(FlightBook flightBook) {
		flightControll.openNewFlightDialog(primaryStage, flightBook);
	}

	public void editFlightButton(Flight flight) {
		flightControll.openEditFlightDialog(primaryStage, flight);
	}

	public void deleteFlightButton(ObservableList<Flight> flights) {
		flightControll.openDeleteFlightDialog(primaryStage, flights);
	}

	public void exportButton(FlightBook flightBook) {
		flightControll.openExportDialog(primaryStage, flightBook);
	}

	public void importButton(FlightBook flightBook) {
		flightControll.openImportDialog(primaryStage, flightBook);
	}

	public void updateButton() {
		try {
			Updater update = new Updater(log, this);
			update.runUpdater(aktVersion);
		} catch (Exception e) {
			log.error("could not load Updater.", e);
		}
	}

	public void settingsButton() {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(FlightLog.class.getResource("view/SettingsWin.fxml"));
			Stage stage = new Stage();
			SettingsWinController settingsWin = new SettingsWinController(log, this, stage, sql);
			loader.setController(settingsWin);
			Parent root = (Parent) loader.load();

			stage.setScene(new Scene(root));
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.show();

			settingsWin.prepare();
		} catch (Exception e) {
			e.printStackTrace();
			log.error("failed loading settingsWin", e);
		}
	}

	public static void main(String[] args) {
		launch(args);
	}
}