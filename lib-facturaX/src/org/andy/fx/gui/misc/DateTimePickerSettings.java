package org.andy.fx.gui.misc;

import java.awt.Color;
import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.Locale;

import com.github.lgooddatepicker.components.DatePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings;
import com.github.lgooddatepicker.components.TimePickerSettings.TimeIncrement;
import com.github.lgooddatepicker.zinternaltools.HighlightInformation;

public class DateTimePickerSettings {
	
	public DatePickerSettings dpSettings() {
		DatePickerSettings d = new DatePickerSettings();
		d.setFormatForDatesCommonEra("dd.MM.yyyy");
		d.setFirstDayOfWeek(DayOfWeek.MONDAY);
		d.setWeekNumbersDisplayed(true, true);
		d.setHighlightPolicy(date -> {
		    if (date.getDayOfWeek() == DayOfWeek.SATURDAY || date.getDayOfWeek() == DayOfWeek.SUNDAY) {
		        return new HighlightInformation(new Color(255, 210, 210), Color.RED);
		    }
		    return null;
		});
		return d;
	}
	
	public TimePickerSettings tpSettings() {
		TimePickerSettings s = new TimePickerSettings(Locale.GERMAN);
		s.use24HourClockFormat();
		s.initialTime = LocalTime.of(0, 0);
		s.generatePotentialMenuTimes(TimeIncrement.FifteenMinutes, null, null);
		s.setDisplaySpinnerButtons(true);
		return s;
	}
	
}
