package flightLogApp.utils;

import java.util.*;
import java.io.*;

/**
 *
 * @author Juri Bieler
 */

public class PrefLoader {

	private String path;
	private Properties p;

	public PrefLoader(String path) {
		this.path = path;
		readIni();
	}

	public PrefLoader() {
		this.path = "config.ini";
		readIni();
	}

	public void setPath(String path) {
		this.path = path;
	}

	// post:liest die ini-Datei ein und speichert sie in der Klassenvariable p
	public boolean readIni() {
		try {
			if (!(new File(path)).exists()) {
				File f = new File(path);
				// f.getParentFile().mkdirs();
				f.createNewFile();
			}
			p = new Properties();
			p.load(new FileInputStream(path));
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	public void createIni() {
		write(path, "");
	}

	public void write(String path, String stg) {
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(path)));
			out.write(stg);
			out.newLine();
			out.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// post:liest den Wert der ini an der stelle (key) data ein
	public String load(String key) {
		try {
			if (p.containsKey(key)) {
				return p.getProperty(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public String loadString(String key, String def) {
		try {
			if (p.containsKey(key)) {
				return p.getProperty(key);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}
	
	public int loadInt(String key) {
		return loadInt(key, -1);
	}
	
	public int loadInt(String key, int def) {
		try {
			if (!load(key).equals("")) {
				return Integer.parseInt(load(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	public Double loadDouble(String key, Double def) {
		try {
			if (!load(key).equals("")) {
				return Double.parseDouble(load(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}
	
	public Boolean loadBoolean(String key, Boolean def) {
		try {
			if (!load(key).equals("")) {
				return Boolean.parseBoolean(load(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return def;
	}

	// post:aendert den Eintraf an der stelle data zum Wert val, und
	// ueberschreibt dies in der ini
	public void save(String key, String val) {
		try {
			p.put(key, val);
			FileOutputStream out = new FileOutputStream(path);
			p.store(out, "[FlightLog]");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
