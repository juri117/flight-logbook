package flightLogApp.utils;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import flightLogApp.controller.Log;
import flightLogApp.model.Flight;
import flightLogApp.model.FlightBook;

/**
 * 
 * @author Juri Bieler
 *
 */
public class SQLiteParser {

	private int aktVersion = 1;

	private String dbName;
	private Connection con;
	private Statement stm;
	private Log log;

	public SQLiteParser(String dbName, Log log) {
		this.dbName = dbName;
		this.log = log;
	}

	public void connectToDB() {
		try {
			Boolean dbExists = true;
			File f = new File(dbName + ".db");
			if (!f.exists() || f.isDirectory()) {
				log.debug("lege DB an...");
				dbExists = false;
			}
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:" + dbName + ".db");
			stm = con.createStatement();
			if (!dbExists) {
				createStructure();
			}
			checkVersion();
		} catch (Exception e) {
			e.printStackTrace();

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Failed to load Database!");
			alert.setContentText(e.toString());
			alert.showAndWait();
		}
	}

	public void disconnectToDB() {
		try {
			con.close();
		} catch (Exception e) {
			log.error("erroro on disconnecting DB", e);
			e.printStackTrace();
		}
	}

	private void createStructure() {
		try {
			stm.executeUpdate("CREATE TABLE flights (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "bookId INTEGER, " + "nr INTEGER, " + "pilot TEXT, " + "copilot TEXT, " + "passenger TEXT, "
					+ "plane TEXT, " + "planeCallSign TEXT, " + "launchMethod TEXT, " + "start TEXT, " + "stop TEXT, " + "flightTime TEXT, " + "landings INTEGER, " + "fromAf TEXT, " + "toAf TEXT, "
					+ "distance REAL, " + "cost REAL, " + "note TEXT, " + "deleted INTEGER DEFAULT 0);");
			stm.executeUpdate("CREATE TABLE flightBooks (" + "id INTEGER PRIMARY KEY AUTOINCREMENT, " + "type INTEGER, " + "name TEXT, " + "note TEXT, " + "deleted INTEGER DEFAULT 0);");
			stm.executeUpdate("CREATE TABLE options (" + "key TEXT PRIMARY KEY, " + "value TEXT);");
			// set default options
			setOption("version", aktVersion + "");
		} catch (Exception e) {
			log.error("error on creating db strucktute", e);
			e.printStackTrace();
		}
	}

	private void checkVersion() {
		try {
			String dbVersionStr = getOption("version");
			if (!dbVersionStr.equals("")) {
				log.debug("dbVersion: " + dbVersionStr);
				int dbVersion = Integer.parseInt(dbVersionStr);
				if (dbVersion != aktVersion) {
					updateDb(dbVersion);
				}
			} else{
				log.error("could not read db version");
			}
		} catch (Exception e) {
			log.error("error on checking db version", e);
			e.printStackTrace();
		}
	}

	private void updateDb(int dbVersion) {
		// TODO:update DB to newest version
		if (dbVersion == 1) {
			// ...
		}
	}

	public ObservableList<FlightBook> loadFlightBooks() {
		ObservableList<FlightBook> flightBooks = FXCollections.observableArrayList();
		try {
			// load book table names
			ResultSet rs = stm.executeQuery("SELECT id, name, type, note FROM flightBooks WHERE deleted = 0;");
			while (rs.next()) {
				FlightBook newBook = new FlightBook(rs.getString("name"), rs.getInt("id"));
				newBook.setNote(rs.getString("note"));
				flightBooks.add(newBook);
			}
			rs.close();

			// load flights from each table, create flightbooks
			for (int i = 0; i < flightBooks.size(); i++) {
				int bookId = flightBooks.get(i).getBookId().get();
				ObservableList<Flight> flights = loadFlights(bookId);
				flightBooks.get(i).insertFlights(flights);
			}
		} catch (Exception e) {
			log.error("error on loading flights", e);
			e.printStackTrace();
		}
		return flightBooks;
	}

	public FlightBook createNewFlightBook(String name, String type, String note) {
		int id = -1;
		try {
			String sql = "INSERT INTO flightBooks" + " (name, type, note) VALUES (";
			sql = sql + "'" + name + "',";
			sql = sql + "'" + type + "',";
			sql = sql + "'" + note + "');";

			stm.executeUpdate(sql);
			stm.getGeneratedKeys();

			ResultSet resultSet = stm.getGeneratedKeys();

			if (resultSet != null && resultSet.next()) {
				id = resultSet.getInt(1);
			}

		} catch (Exception e) {
			log.error("error on creating new flight book", e);
			e.printStackTrace();
		}
		FlightBook book = new FlightBook(name, id);
		book.setNote(note);
		return book;
	}

	public int insertFlight(int bookId, Flight flight) {
		int id = -1;
		try {
			String sql = "INSERT INTO flights"
					+ " (bookId, nr, pilot, copilot, passenger, plane, planeCallSign, launchMethod, start, stop, flightTime, landings, fromAf, toAf, distance, cost, note) VALUES (";
			sql = sql + bookId + ",";
			sql = sql + flight.getNr().get() + ",";
			sql = sql + "'" + flight.getPilot().get() + "',";
			sql = sql + "'" + flight.getCopilot().get() + "',";
			sql = sql + "'" + flight.getPassanger().get() + "',";
			sql = sql + "'" + flight.getPlane().get() + "',";
			sql = sql + "'" + flight.getPlaneCallSign().get() + "',";
			sql = sql + "'" + flight.getLaunchMethod().get() + "',";
			sql = sql + "'" + flight.getStart().get().toString() + "',";
			sql = sql + "'" + flight.getStop().get() + "',";
			sql = sql + flight.getTime().get() + ",";
			sql = sql + flight.getLandings().get() + ",";
			sql = sql + "'" + flight.getFrom().get() + "',";
			sql = sql + "'" + flight.getTo().get() + "',";
			sql = sql + flight.getDistance().get() + ",";
			sql = sql + flight.getCost().get() + ",";
			sql = sql + "'" + flight.getNote().get() + "');";

			stm.executeUpdate(sql);

			ResultSet resultSet = stm.getGeneratedKeys();
			if (resultSet != null && resultSet.next()) {
				id = resultSet.getInt(1);
			}
		} catch (Exception e) {
			log.error("error on inserting flight", e);
			e.printStackTrace();
		}
		return id;
	}

	public void deleteFlight(int bookId, int nr) {
		try {
			String sql = "UPDATE flights SET ";
			sql = sql + " deleted = 1";
			sql = sql + " WHERE bookId = " + bookId + " AND nr = " + nr + ";";

			//System.out.println(sql);

			stm.executeUpdate(sql);
		} catch (Exception e) {
			log.error("error on deleting flight", e);
			e.printStackTrace();
		}
	}

	public void updateFlight(int bookId, int nr, Flight flight) {
		try {
			String sql = "UPDATE flights SET ";
			sql = sql + " nr = " + flight.getNr().get() + ",";
			sql = sql + " pilot = '" + flight.getPilot().get() + "',";
			sql = sql + " copilot = '" + flight.getCopilot().get() + "',";
			sql = sql + " passenger = '" + flight.getPassanger().get() + "',";
			sql = sql + " plane = '" + flight.getPlane().get() + "',";
			sql = sql + " planeCallSign = '" + flight.getPlaneCallSign().get() + "',";
			sql = sql + " launchMethod = '" + flight.getLaunchMethod().get() + "',";
			sql = sql + " start = '" + flight.getStart().get().toString() + "',";
			sql = sql + " stop = '" + flight.getStop().get() + "',";
			sql = sql + " flightTime = " + flight.getTime().get() + ",";
			sql = sql + " landings = " + flight.getLandings().get() + ",";
			sql = sql + " fromAf = '" + flight.getFrom().get() + "',";
			sql = sql + " toAf = '" + flight.getTo().get() + "',";
			sql = sql + " distance = " + flight.getDistance().get() + ",";
			sql = sql + " cost = " + flight.getCost().get() + ",";
			sql = sql + " note = '" + flight.getNote().get() + "'";
			sql = sql + " WHERE bookId = " + bookId + " AND nr = " + nr + ";";

			//System.out.println(sql);

			stm.executeUpdate(sql);
		} catch (Exception e) {
			log.error("error on updating flight", e);
			e.printStackTrace();
		}
	}

	private ObservableList<Flight> loadFlights(int bookId) {
		ObservableList<Flight> flightData = FXCollections.observableArrayList();
		try {
			ResultSet rs = stm.executeQuery("SELECT * FROM flights WHERE bookId = " + bookId + " AND deleted = 0;");
			while (rs.next()) {

				LocalDateTime start = LocalDateTime.parse(rs.getString("start"));
				LocalDateTime stop = LocalDateTime.parse(rs.getString("stop"));

				Flight flight = new Flight(rs.getInt("nr"), rs.getString("pilot"), rs.getString("copilot"), rs.getString("passenger"), rs.getString("plane"), rs.getString("planeCallSign"),
						rs.getString("launchMethod"), start, stop, rs.getInt("flightTime"), rs.getInt("landings"), rs.getString("fromAf"), rs.getString("toAf"), rs.getDouble("distance"),
						rs.getDouble("cost"), rs.getString("note"));
				flightData.add(flight);
			}
			rs.close();
		} catch (Exception e) {
			log.error("error on loading flights", e);
			e.printStackTrace();
		}
		return flightData;
	}

	public ArrayList<String> getTopFive(int bookId, String value) {
		ArrayList<String> top = new ArrayList<String>();
		try {
			ResultSet rs = stm.executeQuery("SELECT DISTINCT " + value + " FROM flights WHERE bookId = " + bookId + " AND deleted = 0 ORDER BY id DESC LIMIT 2;");
			while (rs.next()) {
				top.add(rs.getString(value));
			}
			rs.close();

			rs = stm.executeQuery("SELECT DISTINCT " + value + " FROM flights WHERE bookId = " + bookId + " AND deleted = 0 GROUP BY " + value + " ORDER BY count(" + value + ") DESC LIMIT 5;");
			while (rs.next() && top.size() < 5) {
				String val = rs.getString(value);
				if (!top.contains(val)) {
					top.add(val);
				}
			}
			rs.close();
		} catch (Exception e) {
			log.error("error get from db", e);
			e.printStackTrace();
		}
		return top;
	}

	public HashMap<String, String> getPlanes(int bookId) {
		HashMap<String, String> planes = new HashMap<String, String>();
		try {
			ResultSet rs = stm.executeQuery("SELECT DISTINCT planeCallSign, plane FROM flights WHERE bookId = " + bookId + " AND deleted = 0;");
			while (rs.next()) {
				planes.put(rs.getString("planeCallSign"), rs.getString("plane"));
			}
			rs.close();
		} catch (Exception e) {
			log.error("error get from db", e);
			e.printStackTrace();
		}

		return planes;
	}

	public String getPilot(int bookId) {
		String pilot = "";
		try {
			ResultSet rs = stm.executeQuery("SELECT DISTINCT pilot FROM flights WHERE bookId = " + bookId + " AND deleted = 0 ORDER BY id DESC LIMIT 1;");
			if (rs.next()) {
				pilot = rs.getString("pilot");
			}
			rs.close();
		} catch (Exception e) {
			log.error("error get from db", e);
			e.printStackTrace();
		}

		return pilot;
	}

	public int getNextFlightNr(int bookId) {
		int nr = 0;
		try {
			ResultSet rs = stm.executeQuery("SELECT nr FROM flights WHERE bookId = " + bookId + " AND deleted = 0 ORDER BY nr DESC LIMIT 1;");
			if (rs.next()) {
				nr = rs.getInt("nr");
			}
			rs.close();
			nr++;
		} catch (Exception e) {
			log.error("error get from db", e);
			e.printStackTrace();
		}
		return nr;
	}

	public Boolean flightNrExists(int bookId, int nr) {
		try {
			ResultSet rs = stm.executeQuery("SELECT id FROM flights WHERE bookId = " + bookId + " AND nr = " + nr + " AND deleted = 0 LIMIT 1;");
			if (rs.next()) {
				return true;
			}
			rs.close();
		} catch (Exception e) {
			log.error("error get from db", e);
			e.printStackTrace();
		}
		return false;
	}

	public String getOption(String key) {
		String value = "";
		try {
			ResultSet rs = stm.executeQuery("SELECT value FROM options WHERE key = '" + key + "';");
			if (rs.next()) {
				value = rs.getString("value");
			}
			rs.close();
		} catch (Exception e) {
			log.error("error get Option from db", e);
			e.printStackTrace();
		}
		return value;
	}

	public boolean setOption(String key, String value) {
		try {
			ResultSet rs = stm.executeQuery("SELECT value FROM options WHERE key = '" + key + "';");
			if (rs.next()) {
				// update
				String sql = "UPDATE options SET value = '" + value + "' WHERE key = '" + key + "';";
				stm.executeUpdate(sql);
			} else {
				// insert
				String sql = "INSERT INTO options (key, value) VALUES ('" + key + "', '" + value + "');";
				stm.executeUpdate(sql);
			}
			return true;
		} catch (Exception e) {
			log.error("error set Option in db", e);
			e.printStackTrace();
		}
		return false;
	}
}
