package flightLogApp.controller;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import flightLogApp.utils.ConverterUtil;

public class TimeController {
	TextField timeText;
	Slider hSlide;
	Slider mSlide;

	int hour = 0;
	int minute = 0;

	public TimeController(Slider hSlide, Slider mSlide) {
		this.hSlide = hSlide;
		this.mSlide = mSlide;

		hSlide.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				changeHourSlide(new_val.intValue());
			}
		});

		mSlide.valueProperty().addListener(new ChangeListener<Number>() {
			public void changed(ObservableValue<? extends Number> ov, Number old_val, Number new_val) {
				changeMinuteSlide(new_val.intValue());
			}
		});
	}

	public void updateTextField(TextField txt) {
		this.timeText = txt;
		textChanged();
	}
	
	public TextField getTextField(){
		return this.timeText;
	}

	private void changeHourSlide(int newValue) {
		hour = newValue;
		setTimeText();
	}

	private void changeMinuteSlide(int newValue) {
		minute = newValue;
		setTimeText();
	}

	private void setTimeText() {
		timeText.setText(ConverterUtil.getTimeStr(hour, minute));
	}

	private void updateSlider() {
		hSlide.setValue(hour);
		mSlide.setValue(minute);
	}

	public void textChanged() {
		if (timeText == null)
			return;
		if (timeText.getLength() == 5) {
			hour = ConverterUtil.timeStrGetHours(timeText.getText());
			minute = ConverterUtil.timeStrGetMins(timeText.getText());
			updateSlider();
		}
	}

	public void textFieldEntered() {

	}
}
