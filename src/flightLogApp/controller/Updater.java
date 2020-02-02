package flightLogApp.controller;

import java.io.File;
import java.util.List;
import java.util.Optional;

import javafx.application.Application.Parameters;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import flightLogApp.FlightLog;
import flightLogApp.utils.FileUtils;
import flightLogApp.utils.PrefLoader;

public class Updater {
	public static long TIME_OUT = 10000;

	private Log log;
	private FlightLog mainApp;

	public Updater(Log log, FlightLog mainApp) {
		this.log = log;
		this.mainApp = mainApp;
	}

	// public void performCommand(String command) {
	// switch (command) {
	// case "close":
	// mainApp.closeStage();
	// return;
	// default:
	// return;
	// }
	// }

	public void runUpdater(String oldVersion) {
		try {
			String newVersion = getNewsestVersionName();
			if (newVersion.equals(oldVersion)) {
				Alert alert = new Alert(AlertType.INFORMATION);
				alert.setTitle("Updates?");
				alert.setHeaderText("This is the newest version!");
				alert.setContentText("this Version: " + oldVersion);
				alert.showAndWait();
				return;
			}
			if (newVersion.equals("")) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Updates?");
				alert.setHeaderText("Could not contact Server!");
				alert.setContentText("this Version: " + oldVersion);
				alert.showAndWait();
				return;
			}

			// new Version available
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("new Version");
			alert.setHeaderText("A new Version is available?");
			alert.setContentText("Update now?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK) {
				updateIt(newVersion);
			}

			// final Process process =

			// final Process process = Runtime.getRuntime().exec(new String[] {
			// "java", "-jar", "FlightLogUpdater.jar", aktVersion, "debug"});
			// if (process != null) {
			//
			// try {
			// DataInputStream in = new
			// DataInputStream(process.getInputStream());
			// BufferedReader br = new BufferedReader(new
			// InputStreamReader(in));
			// String line;
			// while ((line = br.readLine()) != null) {
			// if (line.startsWith("ECHO:")) {
			// log.stat(line.replaceFirst("ECHO:", ""));
			// }
			// if (line.startsWith("CMD:")) {
			// log.debug("cmd: " + line.replaceFirst("CMD:", ""));
			// performCommand(line.replaceFirst("CMD:", ""));
			// }
			// }
			// in.close();
			// } catch (Exception e) {
			// // handle exception here ...
			// }
			// }
			//
			// process.waitFor();
			// if (process.exitValue() == 0) {
			// // process exited ...
			// } else {
			// // process failed ...
			// }
		} catch (Exception ex) {
			// handle exception
		}
	}

	public String getNewsestVersionName() {
		try {
			FileUtils.loadFile("version.ini", "https://dl.dropboxusercontent.com/u/1338162/FlightLog/version.ini", log);
			log.stat("loaded!");
			PrefLoader pref = new PrefLoader("version.ini");
			pref.readIni();
			String newVersion = pref.load("newVersion");
			if (newVersion != "") {
				log.stat("new version: " + newVersion);
				System.out.println("ECHO:new version: " + newVersion);
				// if (newVersion.equals(oldVersion)) {
				// log.stat("no Updates available");
				// System.out.println("no Updates available");
				// return "";
				// }
				return newVersion;
			} else {
				log.error("empty version.ini");
			}
		} catch (Exception e) {
			log.error("error on Update", e);
		}
		return "";
	}

	private boolean updateIt(String newVersion) {
		try {
			log.stat("loading new Versoin...");
			FileUtils.loadFile("FlightLog.zip", "https://dl.dropboxusercontent.com/u/1338162/FlightLog/FlightLog.zip", log);

			File f = new File("FlightLog.zip");
			if (!f.exists() || f.isDirectory()) {
				Alert alert = new Alert(AlertType.WARNING);
				alert.setTitle("Warning");
				alert.setHeaderText("Download of new Version failed!");
				alert.setContentText("Please try manually.");
				alert.showAndWait();
				return false;
			}

			// final Process process =
			// Runtime.getRuntime().exec(new String[] { "java", "-jar",
			// "FlightLog.jar", newVersion, "updated" });
			Runtime.getRuntime().exec(new String[] { "java", "-jar", "FlightLogUpdater.jar", "FlightLog.zip", "FlightLog.jar", newVersion });

			mainApp.closeStage();
			return true;
		} catch (Exception e) {
			log.error("Error on update.", e);
		}
		return false;
	}

	static public boolean checkIfUpdated(Parameters params, String aktVersion) {
		//final Parameters params = getParameters();
		final List<String> parameters = params.getRaw();
		if (!parameters.isEmpty()) {
			if (parameters.size() > 0) {
				if (parameters.get(0).equals("updated")) {
					if (parameters.size() > 1) {
						if (parameters.get(1).equals(aktVersion)) {
							cleanUp();
							Alert alert = new Alert(AlertType.INFORMATION);
							alert.setTitle("Updated");
							alert.setHeaderText("Update was successfull!");
							alert.setContentText("Version: " + aktVersion);
							alert.showAndWait();
							return true;
						} else {
							Alert alert = new Alert(AlertType.WARNING);
							alert.setTitle("Warning");
							alert.setHeaderText("The Update failed.");
							alert.setContentText("Please try to load the new Software-Version manually.");
							alert.showAndWait();
							return false;
						}
					}
				}
			}
		}
		return false;
	}
	
	static private void cleanUp(){
		try{
			File f = new File("FlightLog.zip");
			if (f.exists()) {
				f.delete();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
	}
}