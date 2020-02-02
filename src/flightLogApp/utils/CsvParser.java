package flightLogApp.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import flightLogApp.controller.Log;
import flightLogApp.model.Flight;
import flightLogApp.model.FlightBook;

/**
 * parse a csv file
 *
 * @author Juri Bieler
 */

public class CsvParser {

	Log log;

	public CsvParser(Log log) {
		this.log = log;
	}

	// Flupp Synthax
	public void exportData(File path, FlightBook book) {
		ObservableList<Flight> flightData = book.getFlights();

		String splitter = ";";

		try {
			BufferedWriter writer = new BufferedWriter(new FileWriter(path));
			String out = "Nr.;Datum;Muster;Kennz.;Pilot;Copilot;Passagiere;Startart;StZ;LaZ;Flugzeit;Block off;Block on;Blockzeit;Ld.;Von;Bis;Bemerkungen;Strecke;Kategorie;Via;Zeit;Datei;Wettbewerb;;;;";
			writer.write(out);
			writer.newLine();

			for (int i = 0; i < flightData.size(); i++) {
				Flight flight = flightData.get(i);
				out = flight.getNr().get() + splitter + flight.getStartDate().get() + splitter + flight.getPlane().get() + splitter + flight.getPlaneCallSign().get() + splitter
						+ flight.getPilot().get() + splitter + flight.getCopilot().get() + splitter + flight.getPassanger().get() + splitter + flight.getLaunchMethod().get() + splitter
						+ flight.getStartTime().get() + splitter + flight.getStopTime().get() + splitter + ConverterUtil.minsToTimeStr(flight.getTime().get()) + splitter + splitter + splitter
						+ splitter + flight.getLandings().get() + splitter + flight.getFrom().get() + splitter + flight.getTo().get() + splitter + flight.getNote().get() + splitter
						+ flight.getDistance().get() + splitter + splitter + splitter + splitter + splitter + splitter + splitter + splitter + splitter;

				writer.write(out);
				writer.newLine();
			}

			writer.close();
		} catch (Exception e) {
			log.error("File could not be created", e);
		}
	}

	// Flupp Synthax
	public ObservableList<Flight> importData(String path, String mapping, int flightNrOffset) {
		ObservableList<Flight> flightData = FXCollections.observableArrayList();
		// String splitter = ";";

		PrefLoader map = new PrefLoader("mappings/" + mapping + ".map");
		String seperator = map.loadString("seperator", ",");
		String marker = map.loadString("marker", "");
		Boolean fixMarker = map.loadBoolean("fixMarker", false);
		String encoding = map.loadString("encoding", "UTF-8");

		// String mask = "\"";
		try {
			// BufferedReader CSVFile = new BufferedReader(new
			// FileReader(path));
			BufferedReader CSVFile = new BufferedReader(new InputStreamReader(new FileInputStream(path), encoding));
			String dataRow = CSVFile.readLine(); // Read first line.

			dataRow = CSVFile.readLine(); // Read next line of data.

			int i = 0; // lineCount for error log
			int nr = flightNrOffset;
			while (dataRow != null && !dataRow.equals("")) {
				i++;
				log.stat("importiere Flug Nr. " + i);
				if(fixMarker){
					dataRow = fixSynthax(dataRow, marker.charAt(0), seperator.charAt(0));
				}
				String[] row = dataRow.split(seperator);

				try {
					if (map.loadInt("nr") > -1) {
						nr = Integer.parseInt(parseString(row, map.loadInt("nr")));
					}

					Flight flight = new Flight(nr, // nr
							parseString(row, map.loadInt("pilot")), // pilote
							parseString(row, map.loadInt("copilot")), // copilote
							parseString(row, map.loadInt("passenger")), // passenger
							parseString(row, map.loadInt("plane")), // plane
							parseString(row, map.loadInt("planeCallSign")), // planeCallSign
							parseString(row, map.loadInt("launchMethod")), // launch_method
							parseDateTime(row, map.loadInt("start"), map.loadInt("startDate"), map.loadInt("startTime")), // start
							parseDateTime(row, map.loadInt("stop"), map.loadInt("stopDate"), map.loadInt("stopTime")), // stop
							parseFlightTime(row, map.loadInt("flightTime"), map.load("flightTimeFormat")), // flightTime
							Integer.parseInt(parseString(row, map.loadInt("landings"), "1", true)), // landings
							parseString(row, map.loadInt("fromAf")), // fromAf
							parseString(row, map.loadInt("toAf")), // toAf
							Double.parseDouble(parseString(row, map.loadInt("distance"), "0.0", true)), // distance
							Double.parseDouble(parseString(row, map.loadInt("cost"), "0.0", true)), // cost
							parseString(row, map.loadInt("note"))); // note

					flightData.add(flight);
				} catch (Exception e) {
					log.error("Error in Line " + i, e);
				}

				nr++;
				dataRow = CSVFile.readLine(); // Read next line of data.
			}
			// Close the file once all data has been read.
			CSVFile.close();

			return flightData;
		} catch (Exception e) {
			log.error("File could not be opened", e);
		}
		log.error("File is empty", null);

		return flightData;
	}

	private String fixSynthax(String line, char marker, char seperator) {
		Boolean inStr = false;
		String out = "";
		for (int i = 0; i < line.length(); i++) {
			if (line.charAt(i) == marker) {
				inStr = !inStr;
			} else {
				if (inStr) {
					if (line.charAt(i) != seperator) {
						out = out + line.charAt(i);
					}
				} else {
					out = out + line.charAt(i);
				}
			}
		}
		System.out.println(out);
		return out;
	}

	private String fixDate(String dateStr) {
		String[] parts = dateStr.split("[.]");
		String out = "";
		if (parts.length == 3) {
			String day = parts[0];
			String month = parts[1];
			String year = parts[2];

			if (day.length() == 1) {
				day = "0" + day;
			}

			if (month.length() == 1) {
				month = "0" + month;
			}

			out = day + "." + month + "." + year;
		}
		return out;
	}

	private String parseString(String[] row, int index) {
		return parseString(row, index, "");
	}

	private String parseString(String[] row, int index, String def) {
		if (index > -1 && row.length > index) {
			return row[index];
		}
		return def;
	}
	
	private String parseString(String[] row, int index, String def, Boolean defOnEmpty) {
		String out = parseString(row, index, def);
		if(out.equals("")){
			return def;
		}
		return out;
	}

	private int parseFlightTime(String[] row, int index, String format) {
		String timeStr = parseString(row, index, "0");
		if (format.equals("hh:mm")) {
			return ConverterUtil.timeStrToMins(timeStr);
		} else {
			return Integer.parseInt(timeStr);
		}
	}

	private LocalDateTime parseDateTime(String[] row, int dateTime, int date, int time) {
		if (dateTime < -1) {
			if (row.length > dateTime) {
				String dateTimeStr = row[dateTime];
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy;kk:mm");
				return LocalDateTime.parse(dateTimeStr, formatter);
			}
		}
		if (row.length > date && row.length > time) {
			String dateStr = row[date];
			dateStr = fixDate(dateStr);
			System.out.println("fixed Date: " + dateStr);
			String timeStr = row[time];
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy;kk:mm");
			return LocalDateTime.parse(dateStr + ";" + timeStr, formatter);
		}
		return LocalDateTime.now();
	}

}
