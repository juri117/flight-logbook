package flightLogApp.controller;

import java.io.File;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import flightLogApp.FlightLog;
import flightLogApp.utils.SQLiteParser;

public class SettingsWinController {

	@FXML
	private ChoiceBox<String> languageChoice;
	@FXML
	private ChoiceBox<String> themeChoice;

	@FXML
	private Button saveBut;
	@FXML
	private Button cancelBut;
	@FXML
	private Label errorLabel;

	@SuppressWarnings("unused")
	private Log log;
	private FlightLog mainApp;
	private Stage stage;
	@SuppressWarnings("unused")
	private SQLiteParser sql;
	
	private String actTheme;

	public SettingsWinController(Log log, FlightLog mainApp, Stage stage, SQLiteParser sql) {
		this.log = log;
		this.mainApp = mainApp;
		this.stage = stage;
		this.sql = sql;
		this.actTheme = mainApp.getTheme();
	}

	public void prepare() {
		// ------GENERALS-----
		// load languages
		ObservableList<String> languages = FXCollections.observableArrayList();
		languages.addAll(mainApp.getLanguages());

		languageChoice.setItems(languages);
		languageChoice.setValue(mainApp.getLanguage());

		// load themes
		ObservableList<String> settings = FXCollections.observableArrayList();
		settings.add("Default");

		File themeFolder = new File("themes/");
		File[] themeFiles = themeFolder.listFiles();
		for (int i = 0; i < themeFiles.length; i++) {
			if (!themeFiles[i].isDirectory()) {
				String fileName = themeFiles[i].getName();
				if (fileName.length() > 4) {
					if (fileName.substring(fileName.length() - 4).equals(".css")) {
						settings.add(fileName.substring(0, fileName.length() - 4));
					}
				}
			}
		}

		themeChoice.setItems(settings);
		themeChoice.setValue(mainApp.getTheme());

		themeChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> list, String oldVal, String newVal) {
				// prepareForBook(flightControl.getBook(newVal));
				changeTheme(newVal);
			};
		});
	}

	private void changeTheme(String theme) {
		mainApp.changeTheme(theme);
	}

	private Boolean saveSettings() {
		try {
			mainApp.setLanguage(languageChoice.getSelectionModel().getSelectedItem());
			return true;
		} catch (Exception e) {
			displayError("save failed");
		}

		return false;
	}

	private void displayError(String error) {
		errorLabel.setText(error);
		errorLabel.setVisible(true);
	}

	@FXML
	private void saveButton(ActionEvent event) {
		if (saveSettings()) {
			stage.close();
		}
	}

	@FXML
	private void cancelButton(ActionEvent event) {
		mainApp.changeTheme(actTheme);
		stage.close();
	}
}
