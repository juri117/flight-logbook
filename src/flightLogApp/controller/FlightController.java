package flightLogApp.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.Optional;

import flightLogApp.FlightLog;
import flightLogApp.model.Flight;
import flightLogApp.model.FlightBook;
import flightLogApp.utils.CsvParser;
import flightLogApp.utils.SQLiteParser;
import javafx.beans.InvalidationListener;
import javafx.beans.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * ACHTUNG: flightBookIndex: index in ArrayList bookId: id in DB
 * 
 * @author Juri Bieler
 *
 */
public class FlightController {

	MainViewController mainViewController;
	private ObservableList<FlightBook> flightBooks;
	Log log;

	SQLiteParser sql;

	public FlightController(MainViewController mainViewController, Log log, SQLiteParser sql) {
		this.mainViewController = mainViewController;
		this.log = log;
		this.sql = sql;

		flightBooks = FXCollections.observableArrayList();

		loadBooksFromSQL();
	}

	public void updateView() {
		Task<Object> task = new Task<Object>() {
			@Override
			public Object call() throws Exception {
				updateBookList();
				updateFlightsList();

				return true;
			}
		};
		Thread th = new Thread(task);
		th.setDaemon(true);
		th.start();
	}

	public void parseCSVFlightBook(int bookId, String path, String mapping) {
		CsvParser csv = new CsvParser(log);
		ObservableList<Flight> flights;
		// log.stat("found Flights: "+flights.size());

		if (!bookExists(getBook(bookId).toString())) {
			flights = csv.importData(path, mapping, 1);
			newFlightBook(getBook(bookId).toString(), flights);
		} else {
			int nrOffset = sql.getNextFlightNr(bookId);
			flights = csv.importData(path, mapping, nrOffset);
			newFlights(bookId, flights);
		}

		log.stat("found Flights: " + flights.size());
	}

	public void loadBooksFromSQL() {
		ObservableList<FlightBook> flightBooks = sql.loadFlightBooks();
		addFlightBooks(flightBooks);
	}

	public void updateFlightsList() {
		mainViewController.updateFlightsList(getFlights());
		mainViewController.updateTableColumns(getTableColumns());
	}

	public void updateBookList() {
		mainViewController.updateBookList(flightBooks);
	}

	public void openNewFlightBookDialog(Stage primaryStage) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(FlightLog.class.getResource("view/NewBookWin.fxml"));
			Stage stage = new Stage();
			NewBookWinController newBookWin = new NewBookWinController(log, this, stage);
			loader.setController(newBookWin);
			Parent root = (Parent) loader.load();

			stage.setScene(new Scene(root));
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openNewFlightDialog(Stage primaryStage, FlightBook flightBook) {
		openEditFlightDialog(primaryStage, flightBook, null);
	}

	public void openEditFlightDialog(Stage primaryStage, Flight flight) {
		openEditFlightDialog(primaryStage, getBook(flight), flight);
	}

	public void openEditFlightDialog(Stage primaryStage, FlightBook flightBook, Flight flight) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(FlightLog.class.getResource("view/EditWin.fxml"));
			Stage stage = new Stage();
			EditWinController editWin = new EditWinController(log, this, stage, sql, flightBook, flight);
			loader.setController(editWin);
			Parent root = (Parent) loader.load();

			stage.setScene(new Scene(root));

			// stage.getScene()
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.show();

			editWin.prepare();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openDeleteFlightDialog(Stage primaryStage, ObservableList<Flight> flights) {
		try {
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Warning");
			alert.setHeaderText("Are you shure?");
			alert.setContentText("Delete the flight(s)?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				// TODO:delete all flights
				for (int i = 0; i < flights.size(); i++) {
					// TODO: work with flight id
					int flightBookIndex = findFlightBookByFlight(flights.get(i));
					sql.deleteFlight(flightBooks.get(flightBookIndex).getBookId().get(), flights.get(i).getNr().get());
					flightBooks.get(flightBookIndex).getFlights().remove(flights.get(i));
				}
				// refresh gui
				updateView();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void openExportDialog(Stage primaryStage, FlightBook flightBook) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Export " + flightBook);
		FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
		chooser.getExtensionFilters().add(extFilter);
		File file = chooser.showSaveDialog(primaryStage);
		if (file != null) {
			log.stat("File exists. Overwrite...");
		}

		CsvParser csv = new CsvParser(log);
		csv.exportData(file, flightBook);
	}

	public void openImportDialog(Stage primaryStage, FlightBook flightBook) {
		try {
			FXMLLoader loader = new FXMLLoader();
			loader.setLocation(FlightLog.class.getResource("view/ImportWin.fxml"));
			Stage stage = new Stage();
			ImportWinController importWin = new ImportWinController(log, this, stage, flightBook.getBookId().get());
			loader.setController(importWin);
			Parent root = (Parent) loader.load();

			stage.setScene(new Scene(root));
			stage.initModality(Modality.WINDOW_MODAL);
			stage.initOwner(primaryStage);
			stage.show();

			importWin.prepare();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void newFlightBook(String name, ObservableList<Flight> flights) {
		// save it
		FlightBook book = sql.createNewFlightBook(name, "", "");
		for (int i = 0; i < flights.size(); i++) {
			sql.insertFlight(book.getBookId().get(), flights.get(i));
		}
		// update model
		book.insertFlights(flights);
		addFlightBook(book);
		// refresh gui
		updateView();
	}

	public void newFlight(int bookId, Flight flight) {
		// save
		sql.insertFlight(bookId, flight);
		// add to modell
		// flightBooks.get(getBookIndex(bookId)).insertFlight(flight);
		getBook(bookId).insertFlight(flight);
	}

	public void newFlights(int bookId, ObservableList<Flight> flights) {
		for (int i = 0; i < flights.size(); i++) {
			newFlight(bookId, flights.get(i));
		}
		// refresh gui
		updateView();
	}

	public void updateFlight(int bookId, Flight oldFlight, Flight newFlight) {
		// update DB
		sql.updateFlight(bookId, oldFlight.getNr().get(), newFlight);
		// update view
		int flightIndex = getFlightIndex(getBookIndex(bookId), oldFlight);
		// flightBooks.get(getBookIndex(bookId)).getFlights().set(flightIndex,
		// newFlight);
		getBook(bookId).getFlights().set(flightIndex, newFlight);
		updateView();
	}

	public void addFlightBooks(ObservableList<FlightBook> books) {
		for (int i = 0; i < books.size(); i++) {
			addFlightBook(books.get(i));
		}
	}

	public void addFlightBook(FlightBook book) {
		book.selected.addListener(new InvalidationListener() {
			@Override
			public void invalidated(Observable o) {
				updateFlightsList();
				if (book.selected.get()) {
					mainViewController.setSelectedBook(getBookIndex(book));
				}
			}
		});
		flightBooks.add(book);
	}

	public ObservableList<FlightBook> getFlightBooks() {
		return flightBooks;
	}

	public ObservableList<Flight> getFlights() {
		ObservableList<Flight> flightData = FXCollections.observableArrayList();
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).getSelected()) {
				flightData.addAll(flightBooks.get(i).getFlights());
			}
		}
		return flightData;
	}

	public ArrayList<String[]> getTableColumns() {
		ArrayList<String[]> tableCols = new ArrayList<String[]>();
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).getSelected()) {
				ArrayList<String[]> tmpCols = flightBooks.get(i).getTableColumns();
				for (int j = 0; j < tmpCols.size(); j++) {
					if (!keyExists(tableCols, tmpCols.get(j)[FlightBook.COL_KEY])) {
						tableCols.add(tmpCols.get(j));
					}
				}
			}
		}
		return tableCols;
	}

	private boolean keyExists(ArrayList<String[]> cols, String key) {
		for (int i = 0; i < cols.size(); i++) {
			if (cols.get(i)[FlightBook.COL_KEY].equals(key)) {
				return true;
			}
		}
		return false;
	}

	public Boolean bookExists(String name) {
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).toString().equals(name)) {
				return true;
			}
		}
		return false;
	}

	public void saveBookState() {
		String active = "";
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).getSelected()) {
				active = active + flightBooks.get(i).getId() + ",";
			}
		}
		sql.setOption("activeBooks", active);
	}

	public void loadBookState() {
		String activeStr = sql.getOption("activeBooks");
		String[] activeAry = activeStr.split(",");
		int[] active = new int[activeAry.length];
		for (int i = 0; i < activeAry.length; i++) {
			if (!activeAry[i].equals("")) {
				try {
					active[i] = Integer.parseInt(activeAry[i]);
				} catch (NumberFormatException e) {
					log.error("in activeBooks where others than integer", e);
				}
			}
		}

		for (int i = 0; i < flightBooks.size(); i++) {
			flightBooks.get(i).setSelected(false);
			int id = flightBooks.get(i).getId();
			for (int j = 0; j < active.length; j++) {
				if (id == active[j]) {
					flightBooks.get(i).setSelected(true);
					break;
				}
			}
		}
	}

	@Deprecated
	public int findFlightBookByFlight(Flight flight) {
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).getFlights().contains(flight)) {
				return i;
			}
		}
		return -1;
	}

	@Deprecated
	private int getBookIndex(FlightBook book) {
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).equals(book)) {
				return i;
			}
		}
		return -1;
	}

	@Deprecated
	public int getBookIndex(String book) {
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).toString().equals(book)) {
				return i;
			}
		}
		return -1;
	}

	@Deprecated
	public int getBookIndex(int bookId) {
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).getBookId().get() == bookId) {
				return i;
			}
		}
		return -1;
	}

	// durch id in flügen lösen
	@Deprecated
	public int getFlightIndex(int bookIndex, Flight flight) {
		for (int i = 0; i < flightBooks.get(bookIndex).getFlights().size(); i++) {
			if (flightBooks.get(bookIndex).getFlights().get(i).equals(flight)) {
				return i;
			}
		}
		return -1;
	}

	public FlightBook getBook(int bookId) {
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).getBookId().get() == bookId) {
				return flightBooks.get(i);
			}
		}
		return null;
	}

	public FlightBook getBook(String name) {
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).toString().equals(name)) {
				return flightBooks.get(i);
			}
		}
		return null;
	}

	public FlightBook getBook(Flight flight) {
		for (int i = 0; i < flightBooks.size(); i++) {
			if (flightBooks.get(i).getFlights().contains(flight)) {
				return flightBooks.get(i);
			}
		}
		return null;
	}
}
