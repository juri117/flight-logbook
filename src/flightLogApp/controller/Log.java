package flightLogApp.controller;

import java.io.FileOutputStream;
import java.io.PrintWriter;

import javafx.application.Platform;
import javafx.scene.control.TextArea;

public class Log extends Thread {

	private TextArea infoText;
	private Boolean debug;
	private Boolean errorLog = true;

	public Log(TextArea infoText) {
		this.infoText = infoText;
	}
	
	public Log() {
		this.infoText = new TextArea();
	}
	
	public void setTextArea(TextArea newInfoText){
		newInfoText.setText(this.infoText.getText());
		this.infoText = newInfoText;
	}
	
	public void setDebug(Boolean debug){
		stat("debug: " + debug);
		this.debug = debug;
	}
	
	public Boolean getDebug(){
		return debug;
	}
	
	public void setErrorLog(Boolean errorLog){
		stat("errorLog: " + errorLog);
		this.errorLog = errorLog;
	}
	
	public Boolean getErrorLog(){
		return errorLog;
	}
	

	public void stat(String txt) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				infoText.setText(txt + "\n" + infoText.getText());
			}
		});
	}

	public void debug(String txt) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				if (debug)
					infoText.setText(txt + "\n" + infoText.getText());
			}
		});
	}
	
	public void error(String txt){
		error(txt, null);
	}

	public void error(String txt, Exception e) {
		if (e != null && errorLog) {
			logToFile(e);
			e.printStackTrace();
		}
		logToFile(txt);
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				infoText.setText("ERROR: " + txt + "\n" + infoText.getText());
			}
		});
	}

	private void logToFile(Exception ex) {
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream("error.log", true));
		    ex.printStackTrace(pw);
		    pw.close();
		} catch (Exception e) {
			debug("unable to write to error.log");
		}
	}
	
	private void logToFile(String txt) {
		try {
			PrintWriter pw = new PrintWriter(new FileOutputStream("error.log", true));
		    pw.print(txt);
		    pw.close();
		} catch (Exception e) {
			debug("unable to write to error.log");
		}
	}
	
	public String getText(){
		return infoText.getText();
	}
}
