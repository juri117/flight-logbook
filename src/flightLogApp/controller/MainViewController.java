package flightLogApp.controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableView;
import javafx.scene.control.TextArea;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import flightLogApp.FlightLog;
import flightLogApp.model.Flight;
import flightLogApp.model.FlightBook;
import flightLogApp.utils.ConverterUtil;

/**
 * controller for main view
 *
 * @author Juri Bieler
 */

public class MainViewController implements Initializable {
	@FXML
	private SplitPane splitPane;
	@FXML
	private SplitPane splitPane1;
	@FXML
	private SplitPane splitPane2;
	@FXML
	private TableView<Flight> flightTable;
	@FXML
	private TextArea infoText;
	@FXML
	private VBox bookList;
	private ListView<FlightBook> listView;

	@FXML
	private Label flightsLabel;
	@FXML
	private Label timeLabel;

	private ResourceBundle res;

	private int selectedBook = 0;

	private int flightsSum;
	private int minsSum;

	// Reference to the main application.
	private FlightLog mainApp;

	private Log log;

	public MainViewController() {
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {
		this.res = resources;
		// log = new Log(infoText);
	}

	public void init() {
		flightTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
		flightTable.getSelectionModel().getSelectedIndices().addListener(new ListChangeListener<Integer>() {
			@Override
			public void onChanged(Change<? extends Integer> change) {
				updateSum();
			}
		});
	}

	public void updateTableColumns(ArrayList<String[]> tableCols) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				flightTable.getColumns().clear();

				for (int i = 0; i < tableCols.size(); i++) {
					String key = tableCols.get(i)[FlightBook.COL_KEY];
					String type = tableCols.get(i)[FlightBook.COL_TYPE];

					switch (type) {
					case "str":
						TableColumn<Flight, String> strTabCol = new TableColumn<>(getRes(key));
						strTabCol.setCellValueFactory(cellData -> cellData.getValue().getStrField(key));
						flightTable.getColumns().add(strTabCol);
						break;
					case "int":
						TableColumn<Flight, Number> intTabCol = new TableColumn<>(key);
						intTabCol.setCellValueFactory(cellData -> cellData.getValue().getIntField(key));
						flightTable.getColumns().add(intTabCol);
						break;
					case "dbl":
						TableColumn<Flight, Number> dblTabCol = new TableColumn<>(key);
						dblTabCol.setCellValueFactory(cellData -> cellData.getValue().getDblField(key));
						flightTable.getColumns().add(dblTabCol);
						break;
					}
				}
			}
		});
	}

	private String getRes(String key) {
		if (res.containsKey(key)) {
			return res.getString(key);
		}
		return key;
	}

	/**
	 * Is called by the main application to give a reference back to itself.
	 * 
	 * @param mainApp
	 */
	public void setMainApp(FlightLog mainApp) {
		this.mainApp = mainApp;
	}

	public void updateFlightsList(ObservableList<Flight> flights) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				flightTable.setItems(flights);
				// automatically adjust width of columns depending on their
				// content
				flightTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
				flightTable.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
				flightTable.requestLayout();

				flightTable.setRowFactory(tv -> {
					TableRow<Flight> row = new TableRow<>();
					row.setOnMouseClicked(event -> {
						if (event.getClickCount() == 2 && (!row.isEmpty())) {
							editFlight(row.getItem());
						}
					});
					return row;
				});
			}
		});

		updateSum();
	}

	public void updateBookList(ObservableList<FlightBook> books) {
		if (listView != null) {
			selectedBook = Math.max(listView.getSelectionModel().getSelectedIndex(), 0);
		}
		listView = new ListView<FlightBook>();
		listView.setPrefSize(200, 250);
		listView.setEditable(true);
		listView.setItems(books);

		Callback<FlightBook, ObservableValue<Boolean>> getProperty = new Callback<FlightBook, ObservableValue<Boolean>>() {
			@Override
			public BooleanProperty call(FlightBook layer) {
				return layer.selectedProperty();
			}
		};
		Callback<ListView<FlightBook>, ListCell<FlightBook>> forListView = CheckBoxListCell.forListView(getProperty);
		listView.setCellFactory(forListView);

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				bookList.getChildren().clear();
				bookList.getChildren().addAll(listView);
				if (bookList.getChildren().size() > 0) {
					listView.getSelectionModel().select(selectedBook);
				}
			}
		});
	}

	public void setSelectedBook(int index) {
		listView.getSelectionModel().select(index);
	}

	public void updateSum() {
		ObservableList<Flight> flights = flightTable.getSelectionModel().getSelectedItems();
		if (flights.size() <= 1) {
			flights = mainApp.getFlightControll().getFlights();// flightTable.getItems();
		}

		this.flightsSum = flights.size();
		int mins = 0;

		for (int i = 0; i < flights.size(); i++) {
			mins = mins + flights.get(i).getTime().get();
		}

		this.minsSum = mins;

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				flightsLabel.setText(getRes("flights") + ": " + flightsSum);
				timeLabel.setText(getRes("flightTime") + ": " + ConverterUtil.minsToTimeStr(minsSum));
			}
		});
	}

	private FlightBook getSelectedFlightBook() {
		FlightBook flightBook = listView.getSelectionModel().getSelectedItem();
		if (flightBook == null) {
			Alert alert = new Alert(AlertType.WARNING);
			alert.setTitle("Warning");
			alert.setHeaderText("No FlightBook slected!");
			alert.setContentText("Select a FlightBook on the left side and try again.");

			alert.showAndWait();
		}
		return flightBook;
	}

	private Flight getSelectedFlight() {
		ObservableList<Flight> flightData = flightTable.getSelectionModel().getSelectedItems();
		if (flightData.size() == 1) {
			return flightData.get(0);
		}
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText("No or more than one Flights are slected!");
		alert.setContentText("Select one Flight in the table and try again.");
		alert.showAndWait();
		return null;
	}

	private ObservableList<Flight> getSelectedFlights() {
		ObservableList<Flight> flightData = flightTable.getSelectionModel().getSelectedItems();
		if (flightData.size() > 0) {
			return flightData;
		}
		Alert alert = new Alert(AlertType.WARNING);
		alert.setTitle("Warning");
		alert.setHeaderText("No Flights are slected!");
		alert.setContentText("Select at least one Flight in the table and try again.");
		alert.showAndWait();
		return null;
	}

	private void editFlight(Flight flight) {
		if (flight != null) {
			mainApp.editFlightButton(flight);
		}
	}

	public void setLog(Log log) {
		this.log = log;
	}

	public TextArea getLogTextArea() {
		return infoText;
	}

	@FXML
	private void editFlight(ActionEvent event) {
		editFlight(getSelectedFlight());
	}

	@FXML
	private void deleteFlights(ActionEvent event) {
		mainApp.deleteFlightButton(getSelectedFlights());
	}

	@FXML
	private void tmpButton(ActionEvent event) {
		mainApp.tmpButton();
	}

	@FXML
	private void addButton(ActionEvent event) {
		FlightBook flightBook = getSelectedFlightBook();
		if (flightBook != null) {
			mainApp.addFlightButton(flightBook);
		}
	}

	@FXML
	private void newBookButton(ActionEvent event) {
		mainApp.newBookButton();
	}

	@FXML
	private void importButton(ActionEvent event) {
		FlightBook flightBook = getSelectedFlightBook();
		if (flightBook != null) {
			mainApp.importButton(flightBook);
		}
	}

	@FXML
	private void exportButton(ActionEvent event) {
		FlightBook flightBook = getSelectedFlightBook();
		if (flightBook != null) {
			mainApp.exportButton(flightBook);
		}
	}

	@FXML
	private void optionsButton(ActionEvent event) {
		log.debug("Options Button");
		mainApp.settingsButton();
		// ... TODO:
	}

	@FXML
	private void updateButton(ActionEvent event) {
		log.debug("Update Button");
		// ... TODO:
		mainApp.updateButton();
	}

	@FXML
	private void contactButton(ActionEvent event) {
		log.debug("Contact Button");
		// ... TODO:
	}

	@FXML
	private void helpButton(ActionEvent event) {
		log.debug("Help Button");
		// ... TODO:
	}

	public SplitPane getSplitPane(int index) {
		switch (index) {
		case 1:
			return splitPane1;
		case 2:
			return splitPane2;
		default:
			return splitPane;
		}
	}
}
