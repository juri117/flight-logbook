package flightLogApp.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import flightLogApp.model.Flight;

/**
 * 
 * @author Juri Bieler
 *
 */
public class NewBookWinController {

	private Log log;
	private FlightController flightControl;
	private Stage stage;

	@FXML
	private TextField nameText;

	@FXML
	private Label errorLabel;

	public NewBookWinController(Log log, FlightController flightControl, Stage stage) {
		this.log = log;
		this.flightControl = flightControl;
		this.stage = stage;
	}

	@FXML
	private void okButton(ActionEvent event) {
		log.debug("OK Button...");

		String name = nameText.getText();

		// empty String ?
		if (name.length() == 0) {
			errorLabel.setText("enter a name!");
			errorLabel.setVisible(true);
			return;
		}

		// duplicate ?
		if (flightControl.bookExists(name)) {
			errorLabel.setText("this book already exists");
			errorLabel.setVisible(true);
			return;
		}

		ObservableList<Flight> emptyList = FXCollections.observableArrayList();
		flightControl.newFlightBook(name, emptyList);

		log.debug("CREATE...");
		stage.close();
	}

}
