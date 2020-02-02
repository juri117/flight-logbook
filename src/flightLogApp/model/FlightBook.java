package flightLogApp.model;

import java.util.ArrayList;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

/**
 * Model class for a Flights.
 *
 * @author Juri Bieler
 */

public class FlightBook {

	private IntegerProperty bookId;
	private StringProperty name;
	private StringProperty note;
	public final SimpleBooleanProperty selected;

	private ArrayList<String[]> tableCols;
	public static final int COL_KEY = 0;
	public static final int COL_TYPE = 1;

	private ObservableList<Flight> flightData = FXCollections.observableArrayList();

	public FlightBook(String name, int bookId) {
		this.name = new SimpleStringProperty(name);
		this.bookId = new SimpleIntegerProperty(bookId);
		this.selected = new SimpleBooleanProperty(true);

		this.tableCols = new ArrayList<String[]>();
		this.tableCols.add(new String[] { "nr", "int" });
		this.tableCols.add(new String[] { "pilot", "str" });
		this.tableCols.add(new String[] { "copilot", "str" });
		this.tableCols.add(new String[] { "plane", "str" });
		this.tableCols.add(new String[] { "planeCallSign", "str" });
		this.tableCols.add(new String[] { "startDate", "str" });
		this.tableCols.add(new String[] { "startTime", "str" });
		this.tableCols.add(new String[] { "stopTime", "str" });
		this.tableCols.add(new String[] { "flightTime", "str" });
		this.tableCols.add(new String[] { "fromAf", "str" });
		this.tableCols.add(new String[] { "toAf", "str" });
		this.tableCols.add(new String[] { "launchMethod", "str" });
		this.tableCols.add(new String[] { "note", "str" });
	}

	public void setNote(String note) {
		this.note = new SimpleStringProperty(note);
	}

	public StringProperty getNote() {
		return note;
	}

	public ArrayList<String[]> getTableColumns() {
		return tableCols;
	}

	public void insertFlight(Flight flight) {
		flightData.add(flight);
	}

	public void insertFlights(ObservableList<Flight> flights) {
		flightData.addAll(flights);
	}

	public ObservableList<Flight> getFlights() {
		return flightData;
	}

	public boolean getSelected() {
		return selected.get();
	}

	public void setSelected(boolean selected) {
		this.selected.set(selected);
	}

	public SimpleBooleanProperty selectedProperty() {
		return selected;
	}

	public IntegerProperty getBookId() {
		return bookId;
	}
	
	public int getId(){
		return bookId.get();
	}

	@Override
	public String toString() {
		return name.get();
	}
}
