package flightLogApp.model;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import flightLogApp.utils.ConverterUtil;

/**
 * Model class for a Flights.
 *
 * @author Juri Bieler
 */

public class Flight {

	IntegerProperty number;

	StringProperty pilot;
	StringProperty copilot;
	StringProperty passanger;
	StringProperty plane;
	StringProperty planeCallSign;

	// ObjectProperty<LocalDate> startDate; //needed? depends on start, stop
	// format... timestamp?
	ObjectProperty<LocalDateTime> start; // use Time-Format
	ObjectProperty<LocalDateTime> stop;
	IntegerProperty time;
	DoubleProperty distance;

	StringProperty from;
	StringProperty to;

	IntegerProperty landings;

	StringProperty launch_method;

	DoubleProperty cost;
	StringProperty note;

	// private ObservableList<ObjectProperty> atributes =
	// FXCollections.observableArrayList();

	public Flight() {
		this.number = new SimpleIntegerProperty(0);

		this.pilot = new SimpleStringProperty("");
		this.copilot = new SimpleStringProperty("");
		this.passanger = new SimpleStringProperty("");
		this.plane = new SimpleStringProperty("");
		this.planeCallSign = new SimpleStringProperty("");

		this.start = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.parse("0000-00-00T00:00:00"));
		this.stop = new SimpleObjectProperty<LocalDateTime>(LocalDateTime.parse("0000-00-00T00:00:00"));
		this.time = new SimpleIntegerProperty(0);
		this.distance = new SimpleDoubleProperty(0);

		this.launch_method = new SimpleStringProperty("");
		this.landings = new SimpleIntegerProperty(1);
		this.from = new SimpleStringProperty("");
		this.to = new SimpleStringProperty("");

		this.cost = new SimpleDoubleProperty(0.0);
		this.note = new SimpleStringProperty("");
	}

	// deprecated
	public Flight(int number, String pilot, String plane, String planeID, LocalDateTime start, LocalDateTime stop, int time, String note) {
		this.number = new SimpleIntegerProperty(number);
		this.pilot = new SimpleStringProperty(pilot);
		this.plane = new SimpleStringProperty(plane);
		this.planeCallSign = new SimpleStringProperty(plane);
		this.start = new SimpleObjectProperty<LocalDateTime>(start);
		this.stop = new SimpleObjectProperty<LocalDateTime>(stop);
		this.time = new SimpleIntegerProperty(time);
		this.note = new SimpleStringProperty(note);

		// defaults
		this.copilot = new SimpleStringProperty("");
		this.passanger = new SimpleStringProperty("");
		this.distance = new SimpleDoubleProperty(0);
		this.launch_method = new SimpleStringProperty("");
		this.landings = new SimpleIntegerProperty(1);
		this.from = new SimpleStringProperty("");
		this.to = new SimpleStringProperty("");
		this.cost = new SimpleDoubleProperty(0.0);
	}

	public Flight(int number, String pilot, String copilot, String passenger, String plane, String planeCallSign, String launch_method, LocalDateTime start, LocalDateTime stop, int time,
			int landings, String from, String to, double distance, double cost, String note) {
		this.number = new SimpleIntegerProperty(number);
		this.pilot = new SimpleStringProperty(pilot);
		this.copilot = new SimpleStringProperty(copilot);
		this.passanger = new SimpleStringProperty(passenger);
		this.plane = new SimpleStringProperty(plane);
		this.planeCallSign = new SimpleStringProperty(planeCallSign);
		this.launch_method = new SimpleStringProperty(launch_method);
		this.start = new SimpleObjectProperty<LocalDateTime>(start);
		this.stop = new SimpleObjectProperty<LocalDateTime>(stop);
		this.time = new SimpleIntegerProperty(time);
		this.landings = new SimpleIntegerProperty(landings);
		this.from = new SimpleStringProperty(from);
		this.to = new SimpleStringProperty(to);
		this.distance = new SimpleDoubleProperty(distance);
		this.cost = new SimpleDoubleProperty(cost);
		this.note = new SimpleStringProperty(note);
	}

	public IntegerProperty getIntField(String field) {
		switch (field) {
		case "nr":
			return getNr();
		case "time":
			return getTime();
		case "landings":
			return getLandings();
		default:
			return new SimpleIntegerProperty(0);
		}
	}

	public DoubleProperty getDblField(String field) {
		switch (field) {
		case "cost":
			return getCost();
		case "distance":
			return getDistance();
		default:
			return new SimpleDoubleProperty(0.0);
		}
	}

	public StringProperty getStrField(String field) {
		switch (field) {
		case "pilot":
			return getPilot();
		case "copilot":
			return getCopilot();
		case "passenger":
			return getPassanger();
		case "plane":
			return getPlane();
		case "planeCallSign":
			return getPlaneCallSign();
		case "launchMethod":
			return getLaunchMethod();
		case "startTime":
			return getStartTime();
		case "startDate":
			return getStartDate();
		case "stopTime":
			return getStopTime();
		case "stopDate":
			return getStopDate();
		case "flightTime":
			return getTimeStr();
		case "fromAf":
			return getFrom();
		case "toAf":
			return getTo();
		case "note":
			return getNote();
		default:
			return new SimpleStringProperty("");
		}
	}

	public LocalTime calcTime() {
		return LocalTime.parse("T00:00:00");
	}

	public IntegerProperty getNr() {
		return number;
	}

	public StringProperty getPilot() {
		return pilot;
	}

	public StringProperty getCopilot() {
		return copilot;
	}

	public StringProperty getPassanger() {
		return passanger;
	}

	public StringProperty getLaunchMethod() {
		return launch_method;
	}

	public StringProperty getFrom() {
		return from;
	}

	public StringProperty getTo() {
		return to;
	}

	public IntegerProperty getLandings() {
		return landings;
	}

	public StringProperty getPlane() {
		return plane;
	}

	public StringProperty getPlaneCallSign() {
		return planeCallSign;
	}

	public ObjectProperty<LocalDateTime> getStart() {
		return start;
	}

	public StringProperty getStartDate() {
		return new SimpleStringProperty(start.get().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
	}

	public StringProperty getStartTime() {
		return new SimpleStringProperty(start.get().toLocalTime().toString());
	}

	public ObjectProperty<LocalDateTime> getStop() {
		return stop;
	}

	public StringProperty getStopDate() {
		return new SimpleStringProperty(stop.get().toLocalDate().format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
	}

	public StringProperty getStopTime() {
		return new SimpleStringProperty(stop.get().toLocalTime().toString());
	}

	public IntegerProperty getTime() {
		return time;
	}

	public StringProperty getTimeStr() {
		// int hours = time.get() / 60; // since both are ints, you get an int
		// int minutes = time.get() % 60;
		// return new SimpleStringProperty(String.format("%d:%02d", hours,
		// minutes));
		return new SimpleStringProperty(ConverterUtil.minsToTimeStr(time.get()));
	}

	public DoubleProperty getDistance() {
		return distance;
	}

	public DoubleProperty getCost() {
		return cost;
	}

	public StringProperty getNote() {
		return note;
	}
}
