package flightLogApp.utils;

public class ConverterUtil {
	public static String minsToTimeStr(int mins) {
		int hours = mins / 60; // since both are ints, you get an int
		int minutes = mins % 60;
		return String.format("%d:%02d", hours, minutes);
	}
	
	public static int timeStrToMins(String timeStr){
		try {
			String[] timeParts = (timeStr.split(":"));
			if (timeParts.length == 2) {
				return Integer.parseInt(timeParts[0]) * 60 + Integer.parseInt(timeParts[1]);
			}
			if (timeParts.length == 1) {
				return Integer.parseInt(timeParts[0]);
			}
		} catch (Exception e) { }
		return -1;
	}
	
	public static int timeStrGetMins(String timeStr){
		try {
			String[] timeParts = (timeStr.split(":"));
			if (timeParts.length == 2) {
				return Integer.parseInt(timeParts[1]);
			}
			if (timeParts.length == 1) {
				return Integer.parseInt(timeParts[0]);
			}
		} catch (Exception e) { }
		return -1;
	}
	
	public static int timeStrGetHours(String timeStr){
		try {
			String[] timeParts = (timeStr.split(":"));
			if (timeParts.length == 2) {
				return Integer.parseInt(timeParts[0]);
			}
		} catch (Exception e) { }
		return -1;
	}
	
	public static String getTimeStr(int hour, int min){
		String hourStr = hour + "";
		String minuteStr = min + "";
		while(hourStr.length() < 2){
			hourStr = "0" + hourStr;
		}
		while(minuteStr.length() < 2){
			minuteStr = "0" + minuteStr;
		}
		return hourStr + ":" + minuteStr;
	}
}
