package flightLogApp.controller;

import java.io.File;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

/**
 * 
 * @author Juri Bieler
 *
 */
public class ImportWinController {

	private Log log;
	private FlightController flightControl;
	private Stage stage;
	private int bookId;

	private static final String STARTKLADDE = "Startkladde";
	private static final String FLUPP = "FluPP";
	private static final String IGC = "IGC";

	@FXML
	private ChoiceBox<String> typeChoice;

	@FXML
	private TextField pathText;
	@FXML
	private Button searchBut;

	@FXML
	private Label errorLabel;

	public ImportWinController(Log log, FlightController flightControl, Stage stage, int bookId) {
		this.log = log;
		this.flightControl = flightControl;
		this.stage = stage;
		this.bookId = bookId;
	}

	public void prepare() {
		ObservableList<String> typeList = FXCollections.observableArrayList(STARTKLADDE, FLUPP, IGC);
		typeChoice.setItems(typeList);
		typeChoice.setValue(STARTKLADDE);
	}

	private void openFileChooser() {
		// TODO: fileExtention for igc, ect.
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Open CSV File");
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		chooser.getExtensionFilters().add(extFilter);
		File file = chooser.showOpenDialog(stage);
		if (file != null) {
			pathText.setText(file.getAbsolutePath());
		}
	}

	@FXML
	private void searchButton(ActionEvent event) {
		openFileChooser();
	}

	@FXML
	private void okButton(ActionEvent event) {

		String type = typeChoice.getSelectionModel().getSelectedItem();
		log.debug("import " + type);

		String path = pathText.getText();
		File f = new File(path);
		if (!f.exists() || f.isDirectory()) {
			errorLabel.setText("CSV File does not exist");
			errorLabel.setVisible(true);
			return;
		}

		switch (type) {
		case STARTKLADDE:
			flightControl.parseCSVFlightBook(bookId, path, STARTKLADDE);
			break;
		case FLUPP:
			flightControl.parseCSVFlightBook(bookId, path, FLUPP);
			break;
		case IGC:
			errorLabel.setText("IGC-Import is not available yet.");
			errorLabel.setVisible(true);
			return;
		default:
			break;
		}

		stage.close();
	}

}
