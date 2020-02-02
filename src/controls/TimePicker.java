package controls;

import javafx.scene.control.TextField;

public class TimePicker extends TextField {

	public TimePicker() {
		super();
		setText("00:00");
	}

	public TimePicker(String arg0) {
		super(arg0);
		setText("00:00");
	}

	@Override
	public void replaceText(int start, int end, String text) {
		if (text.matches("[0-9][0-9]:[0-9][0-9]")) {
			super.replaceText(start, end, text);
		}
	}

	@Override
	public void replaceSelection(String text) {
		if (text.matches("[0-9][0-9]:[0-9][0-9]")) {
			super.replaceSelection(text);
		}
	}
}