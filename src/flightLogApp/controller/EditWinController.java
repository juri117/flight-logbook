package flightLogApp.controller;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.geometry.Bounds;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import flightLogApp.model.Flight;
import flightLogApp.model.FlightBook;
import flightLogApp.utils.ConverterUtil;
import flightLogApp.utils.SQLiteParser;

public class EditWinController {

	@FXML
	private ChoiceBox<String> bookChoice;

	@FXML
	private TextField nrTextField;

	@FXML
	private DatePicker datePick;

	@FXML
	private Pane timeSliderBox;

	@FXML
	private TextField fromTimeTextField;
	@FXML
	private Slider hourSlide;
	@FXML
	private Slider minuteSlide;
	private TimeController timeControll;

	// private TextField activeTimeField;

	@FXML
	private TextField toTimeTextField;

	@FXML
	private TextField flightTimeTextField;

	public static final int FROM = 0;
	public static final int TO = 1;
	public static final int DIFF = 2;

	Boolean[] computedTime = { true, true, true };

	@FXML
	private TextField callSignTextField;
	@FXML
	private ToggleGroup callSignTogleGroup;
	@FXML
	private RadioButton callSignRadio1;
	@FXML
	private RadioButton callSignRadio2;
	@FXML
	private RadioButton callSignRadio3;
	@FXML
	private RadioButton callSignRadio4;
	@FXML
	private RadioButton callSignRadio5;
	@FXML
	private RadioButton callSignRadio6;
	@FXML
	private TextField planeTextField;

	@FXML
	private TextField fromTextField;
	@FXML
	private ToggleGroup fromTogleGroup;
	@FXML
	private RadioButton fromRadio1;
	@FXML
	private RadioButton fromRadio2;
	@FXML
	private RadioButton fromRadio3;
	@FXML
	private RadioButton fromRadio4;
	@FXML
	private RadioButton fromRadio5;
	@FXML
	private RadioButton fromRadio6;

	@FXML
	private TextField toTextField;
	@FXML
	private ToggleGroup toTogleGroup;
	@FXML
	private RadioButton toRadio1;
	@FXML
	private RadioButton toRadio2;
	@FXML
	private RadioButton toRadio3;
	@FXML
	private RadioButton toRadio4;
	@FXML
	private RadioButton toRadio5;
	@FXML
	private RadioButton toRadio6;

	@FXML
	private TextField pilotTextField;
	@FXML
	private TextField copilotTextField;
	@FXML
	private TextField passengerTextField;

	@FXML
	private TextField launchMethodTextField;
	@FXML
	private ToggleGroup launchTogleGroup;
	@FXML
	private RadioButton launchMethodRadio1;
	@FXML
	private RadioButton launchMethodRadio2;
	@FXML
	private RadioButton launchMethodRadio3;
	@FXML
	private RadioButton launchMethodRadio4;
	@FXML
	private TextField landingsTextField;

	@FXML
	private TextField noteTextField;
	@FXML
	private TextField costTextField;
	@FXML
	private TextField distanceTextField;

	@FXML
	private Button saveNextBut;
	@FXML
	private Button saveFinishBut;
	@FXML
	private Label errorLabel;

	@SuppressWarnings("unused")
	private Log log;

	private FlightController flightControl;
	private Stage stage;
	private SQLiteParser sql;
	private String deFlightBook;
	private HashMap<String, String> planes;
	private Flight flight;

	public EditWinController(Log log, FlightController flightControl, Stage stage, SQLiteParser sql, FlightBook flightBook, Flight flight) {
		this.log = log;
		this.flightControl = flightControl;
		this.stage = stage;
		this.sql = sql;
		this.deFlightBook = flightBook.toString();

		this.flight = flight;
	}

	public void prepare() {
		// ------GENERALS-----

		// load Books
		ObservableList<String> bookList = FXCollections.observableArrayList();
		for (int i = 0; i < flightControl.getFlightBooks().size(); i++) {
			bookList.add(flightControl.getFlightBooks().get(i).toString());
		}

		bookChoice.setItems(bookList);
		bookChoice.setValue(deFlightBook);

		bookChoice.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> list, String oldVal, String newVal) {
				// int bookId =
				// flightControl.getFlightBooks().get(flightControl.getBookIndex(newVal)).getBookId().get();
				prepareForBook(flightControl.getBook(newVal));
			};
		});
		
		//initialize TimeSlider
		timeControll = new TimeController(hourSlide, minuteSlide);

		fromTimeTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					showTimeSliders(fromTimeTextField);
				} else {
					hideTimeSliders();
				}
			}
		});

		fromTimeTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				calculateFlightTime(FROM, fromTimeTextField.isFocused() || fromTimeTextField.equals(timeControll.getTextField()));
			}
		});

		toTimeTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					showTimeSliders(toTimeTextField);
				} else {
					hideTimeSliders();
				}
			}
		});
		
		toTimeTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				calculateFlightTime(TO, toTimeTextField.isFocused() || toTimeTextField.equals(timeControll.getTextField()));
			}
		});

		flightTimeTextField.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (newPropertyValue) {
					showTimeSliders(flightTimeTextField);
				} else {
					hideTimeSliders();
				}
			}
		});
		
		flightTimeTextField.textProperty().addListener(new ChangeListener<String>() {
			@Override
			public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
				calculateFlightTime(DIFF, flightTimeTextField.isFocused() || flightTimeTextField.equals(timeControll.getTextField()));
			}
		});

		hourSlide.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (!newPropertyValue) {
					hideTimeSliders();
				}
			}
		});

		minuteSlide.focusedProperty().addListener(new ChangeListener<Boolean>() {
			@Override
			public void changed(ObservableValue<? extends Boolean> arg0, Boolean oldPropertyValue, Boolean newPropertyValue) {
				if (!newPropertyValue) {
					hideTimeSliders();
				}
			}
		});

		
		// toControll = new TimeController(toTimeTextField, hourToSlide,
		// minuteToSlide);
		// difControll = new TimeController(flightTimeTextField, hourDifSlide,
		// minuteDifSlide);

		prepareForBook(getSelectedBookId());

		// TODO: set some inteligent Time?
		// TODO: toolTip for Copilote?
	}

	private FlightBook getSelectedBookId() {
		return flightControl.getBook(bookChoice.getSelectionModel().getSelectedItem());

		// return
		// flightControl.getFlightBooks().get(flightControl.getBookIndex(bookChoice.getSelectionModel().getSelectedItem())).getBookId().get();
	}

	private void prepareForBook(FlightBook book) {
		int bookId = book.getBookId().get();
		// load planes in HashMap
		planes = sql.getPlanes(bookId);

		// set guess data for Toggles
		initializeToggleGroupPlane(callSignTogleGroup, sql.getTopFive(bookId, "planeCallSign"));
		initializeToggleGroup(fromTogleGroup, sql.getTopFive(bookId, "fromAf"));
		initializeToggleGroup(toTogleGroup, sql.getTopFive(bookId, "toAf"));
		initializeToggleGroup(launchTogleGroup, sql.getTopFive(bookId, "launchMethod"));

		// add Listener for toggleChange of callSign
		callSignTogleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
			public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle) {
				if (callSignTogleGroup.getSelectedToggle() != null) {
					callSignChangeSelection();
				}
			}
		});

		// set defaults for new Flight
		if (flight == null) {
			// set next flightNr
			nrTextField.setText(sql.getNextFlightNr(bookId) + "");

			// datePick -> today
			datePick.setValue(LocalDate.now());

			// set last used Plane to planeTextField by calling callSignToggle
			// update
			callSignChangeSelection();

			// set last flown pilot in pilotTextField
			pilotTextField.setText(sql.getPilot(bookId));

			setStaticDefaults();
		}
		// set value for edit
		else {
			nrTextField.setText(flight.getNr().get() + "");

			// TODO:Editable mit check ob Nr frei
			bookChoice.setDisable(true);
			nrTextField.setEditable(false);
			nrTextField.setDisable(true);

			datePick.setValue(flight.getStart().get().toLocalDate());
			fromTimeTextField.setText(flight.getStartTime().get());
			toTimeTextField.setText(flight.getStopTime().get());
			flightTimeTextField.setText(flight.getTimeStr().get());

			callSignTextField.setText(flight.getPlaneCallSign().get());
			callSignRadio1.setSelected(true);
			planeTextField.setText(flight.getPlane().get());

			fromTextField.setText(flight.getFrom().get());
			fromRadio1.setSelected(true);
			toTextField.setText(flight.getTo().get());
			toRadio1.setSelected(true);

			pilotTextField.setText(flight.getPilot().get());
			copilotTextField.setText(flight.getCopilot().get());
			passengerTextField.setText(flight.getPassanger().get());

			launchMethodTextField.setText(flight.getLaunchMethod().get());
			launchMethodRadio1.setSelected(true);
			landingsTextField.setText(flight.getLandings().get() + "");

			noteTextField.setText(flight.getNote().get());
			costTextField.setText(flight.getCost().get() + "");
			distanceTextField.setText(flight.getDistance().get() + "");

			saveNextBut.setVisible(false);

			computedTime[FROM] = false;
			computedTime[TO] = false;
		}
	}

	private void setStaticDefaults() {
		errorLabel.setVisible(false);

		fromTimeTextField.setText("00:00");
		toTimeTextField.setText("00:00");
		flightTimeTextField.setText("0:00");
		callSignTextField.setText("D-");
		landingsTextField.setText("1");
		costTextField.setText("0.00");
		distanceTextField.setText("0.0");
	}

	public void initializeToggleGroup(ToggleGroup toggleGroup, ArrayList<String> top) {
		// ObservableList<Toggle> toggle = callSignTogleGroup.getToggles();
		int i = -1;
		for (Toggle toggle : toggleGroup.getToggles()) {
			if (toggle instanceof RadioButton && i >= 0) {
				if (i < top.size()) {
					((RadioButton) toggle).setText(top.get(i));
					((RadioButton) toggle).setUserData(top.get(i));
					((RadioButton) toggle).setVisible(true);
					if (i == 0) {
						((RadioButton) toggle).setSelected(true);
					}
				} else {
					((RadioButton) toggle).setVisible(false);
				}
			}
			i++;
		}
	}

	public void initializeToggleGroupPlane(ToggleGroup toggleGroup, ArrayList<String> top) {
		// ObservableList<Toggle> toggle = callSignTogleGroup.getToggles();
		int i = -1;
		for (Toggle toggle : toggleGroup.getToggles()) {
			if (toggle instanceof RadioButton && i >= 0) {
				if (i < top.size()) {
					((RadioButton) toggle).setText(top.get(i) + "   " + planes.getOrDefault(top.get(i), "") + "");
					((RadioButton) toggle).setUserData(top.get(i));
					((RadioButton) toggle).setVisible(true);
					if (i == 0) {
						((RadioButton) toggle).setSelected(true);
					}
				} else {
					((RadioButton) toggle).setVisible(false);
				}
			}
			i++;
		}
	}

	private void calculateFlightTime(int enter, Boolean focused) {

		if(focused){
			computedTime[enter] = false;
		}

		if (ConverterUtil.timeStrToMins(fromTimeTextField.getText()) <= 0) {
			computedTime[FROM] = true;
		}
		if (ConverterUtil.timeStrToMins(toTimeTextField.getText()) <= 0) {
			computedTime[TO] = true;
		}
		if (ConverterUtil.timeStrToMins(flightTimeTextField.getText()) <= 0) {
			computedTime[DIFF] = true;
		}

		if ((enter == FROM || enter == TO) && computedTime[DIFF] && !computedTime[TO]) {
			try {
				SimpleDateFormat format = new SimpleDateFormat("HH:mm");
				Date from = format.parse(fromTimeTextField.getText());
				Date to = format.parse(toTimeTextField.getText());
				Date diff = new Date(to.getTime() - from.getTime() - (60000 * 60));

				flightTimeTextField.setText(format.format(diff));
			} catch (Exception e) {
				// e.printStackTrace();
			}
		}
		if (enter == DIFF && computedTime[TO]) {
			try {

				SimpleDateFormat format = new SimpleDateFormat("HH:mm");
				Date from = format.parse(fromTimeTextField.getText());
				int diff = ConverterUtil.timeStrToMins(flightTimeTextField.getText());
				Date to = new Date(from.getTime() + (diff * 60000));

				toTimeTextField.setText(format.format(to));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		timeControll.textChanged();
	}

	@FXML
	private void flightTimeFromChanged(Event event) {
		Bounds bound = fromTimeTextField.getBoundsInParent();

		timeSliderBox.setLayoutY(fromTimeTextField.layoutYProperty().get() + bound.getHeight());

		// String txt = fromTimeTextField.getText();
		// if (txt.matches("[0-9][0-9]")) {
		// fromTimeTextField.setText(txt + ":");
		// fromTimeTextField.positionCaret(3);
		// }
		//calculateFlightTime(FROM);
	}

	@FXML
	private void flightTimeToChanged(Event event) {

		// String txt = toTimeTextField.getText();
		// if (txt.matches("[0-9][0-9]")) {
		// toTimeTextField.setText(txt + ":");
		// toTimeTextField.positionCaret(3);
		// }
		//calculateFlightTime(TO);
	}

	@FXML
	private void flightTimeDiffChanged(Event event) {

		// String txt = flightTimeTextField.getText();
		// if (txt.matches("[0-9][0-9]")) {
		// flightTimeTextField.setText(txt + ":");
		// flightTimeTextField.positionCaret(3);
		// }
		//calculateFlightTime(DIFF);
	}

	private void showTimeSliders(TextField txt) {
		// activeTimeField = txt;
		timeControll.updateTextField(txt);

		// Bounds bound = txt.getParent().getLayoutBounds();
		AnchorPane parent = (AnchorPane) txt.getParent().getParent();
		// double xPos = 50;
		timeSliderBox.setLayoutX(parent.getLayoutX() + parent.getWidth() - timeSliderBox.getWidth() + parent.getParent().getLayoutX() - 10);
		timeSliderBox.setVisible(true);

		System.out.println(parent.getLayoutX());
	}

	private void hideTimeSliders() {
		if (!hourSlide.isFocused() && !minuteSlide.isFocused()) {
			timeSliderBox.setVisible(false);
		}
	}

	@FXML
	private void callSignTextEntered(Event event) {
		callSignRadio1.setSelected(true);
		callSignChangeSelection();
	}

	private void callSignChangeSelection() {
		String callSign = callSignTextField.getText();
		if (callSignTogleGroup.getSelectedToggle().getUserData() != null) {
			callSign = callSignTogleGroup.getSelectedToggle().getUserData().toString();
		}
		String plane = planes.get(callSign);
		if (plane != null) {
			planeTextField.setText(plane);
		} else {
			if (planes.values().contains(planeTextField.getText())) {
				planeTextField.setText("");
			}
		}
	}

	private Boolean safeFlight() {
		// -----collect info + check-----
		// nr
		int flightNr;
		int bookId = getSelectedBookId().getBookId().get();
		try {
			flightNr = Integer.parseInt(nrTextField.getText());
			if (flightNr < 0) {
				displayError("The Flight-Nr. is smaller than 0");
				return false;
			}
		} catch (Exception e) {
			displayError("The Flight-Nr. is not numeric");
			return false;
		}

		if (flight == null && sql.flightNrExists(bookId, flightNr)) {
			displayError("The Flight-Nr. is in use");
			return false;
		}

		// date and Time
		LocalDateTime start;
		LocalDateTime stop;
		try {
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy;kk:mm");
			start = LocalDateTime.parse(datePick.getEditor().getText() + ";" + fromTimeTextField.getText(), formatter);
			stop = LocalDateTime.parse(datePick.getEditor().getText() + ";" + toTimeTextField.getText(), formatter);
		} catch (Exception e) {
			displayError("The Flight-Nr. is not numeric");
			return false;
		}

		// flightTime
		int flightTimeMin = ConverterUtil.timeStrToMins(flightTimeTextField.getText());
		if (flightTimeMin < 0) {
			displayError("Wrong FlightTime format.");
			return false;
		}

		// callSign
		String callSign = callSignTextField.getText();
		if (callSignTogleGroup.getSelectedToggle().getUserData() != null) {
			callSign = callSignTogleGroup.getSelectedToggle().getUserData().toString();
		}

		// plane
		String plane = planeTextField.getText();

		// fromAf
		String fromAf = fromTextField.getText();
		if (fromTogleGroup.getSelectedToggle().getUserData() != null) {
			fromAf = fromTogleGroup.getSelectedToggle().getUserData().toString();
		}

		// fromAf
		String toAf = toTextField.getText();
		if (toTogleGroup.getSelectedToggle().getUserData() != null) {
			toAf = toTogleGroup.getSelectedToggle().getUserData().toString();
		}

		// pilot
		String pilot = pilotTextField.getText();

		// copilot
		String copilot = copilotTextField.getText();

		// passenger
		String passenger = passengerTextField.getText();

		// launchMethod
		String launchMethod = launchMethodTextField.getText();
		if (launchTogleGroup.getSelectedToggle().getUserData() != null) {
			launchMethod = launchTogleGroup.getSelectedToggle().getUserData().toString();
		}

		// landings
		int landings = 1;
		try {
			landings = Integer.parseInt(landingsTextField.getText());
		} catch (Exception e) {
			displayError("Wrong Landings format");
			return false;
		}

		// note
		String note = noteTextField.getText();

		// cost
		double cost = 0.0;
		try {
			String costStr = costTextField.getText();
			costStr = costStr.replace(",", ".");
			cost = Double.parseDouble(costStr);
		} catch (Exception e) {
			displayError("Wrong Cost format");
			return false;
		}

		// distance
		double distance = 0.0;
		try {
			String distanceStr = costTextField.getText();
			distanceStr = distanceStr.replace(",", ".");
			distance = Double.parseDouble(distanceStr);
		} catch (Exception e) {
			displayError("Wrong Distance format");
			return false;
		}

		// new Flight
		if (flight == null) {
			Flight newFlight = new Flight(flightNr, pilot, copilot, passenger, plane, callSign, launchMethod, start, stop, flightTimeMin, landings, fromAf, toAf, distance, cost, note);
			flightControl.newFlight(bookId, newFlight);
		}
		// update Flight
		else {
			Flight newFlight = new Flight(flightNr, pilot, copilot, passenger, plane, callSign, launchMethod, start, stop, flightTimeMin, landings, fromAf, toAf, distance, cost, note);
			flightControl.updateFlight(bookId, flight, newFlight);
		}
		flightControl.updateView();
		return true;
	}

	private void displayError(String error) {
		errorLabel.setText(error);
		errorLabel.setVisible(true);
	}

	// prepare form for next flight (same day, plane, pilot ect.)
	private void setUpForNext() {
		setStaticDefaults();
		nrTextField.setText(sql.getNextFlightNr(getSelectedBookId().getBookId().get()) + "");
	}

	@FXML
	private void selectedFlightTimeFrom(Event event) {
		System.out.println("SELECTED");
	}

	@FXML
	private void fromTextEntered(Event event) {
		fromRadio1.setSelected(true);
		// DOTO: search for Plane
	}

	@FXML
	private void toTextEntered(Event event) {
		toRadio1.setSelected(true);
	}

	@FXML
	private void launchMethodTextEntered(Event event) {
		launchMethodRadio1.setSelected(true);
	}

	@FXML
	private void saveNextButton(ActionEvent event) {
		if (safeFlight()) {
			setUpForNext();
		}
	}

	@FXML
	private void saveFinishButton(ActionEvent event) {
		if (safeFlight()) {
			stage.close();
		}
	}

	@FXML
	private void cancelFinishButton(ActionEvent event) {
		stage.close();
	}
}
