package jobicade.betterhud.element.text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.geom.Direction;

public abstract class Clock extends TextElement {
	private SettingBoolean twentyFour, showSeconds, fullYear;
	private SettingChoose dateType;

	public Clock() {
		settings.addChildren(
			new Legend("misc"),
			twentyFour = new SettingBoolean("24hr"),
			showSeconds = new SettingBoolean("showSeconds").setValuePrefix(SettingBoolean.VISIBLE),
			dateType = new SettingChoose("dateType", "dmy", "mdy", "ymd"),
			fullYear = new SettingBoolean("fullYear").setValuePrefix(SettingBoolean.VISIBLE)
		);

		border = true;
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_EAST);
		twentyFour.set(false);
		showSeconds.set(false);

		dateType.setIndex(1);
		fullYear.set(true);
		settings.priority.set(-1);
	}

	protected DateFormat getTimeFormat() {
		StringBuilder format = new StringBuilder();
		format.append("HH:mm");

		if(showSeconds.get()) {
			format.append(":ss");
		}
		if(!twentyFour.get()) {
			format.append(" a");
			format.replace(0, 2, "hh");
		}
		return new SimpleDateFormat(format.toString());
	}

	private static final String[] dateFormats = {"dd/MM/yy", "MM/dd/yy", "yy/MM/dd"};
	private static final String[] dateFormatsFull = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd"};

	protected DateFormat getDateFormat() {
		return new SimpleDateFormat((fullYear.get() ? dateFormatsFull : dateFormats)[dateType.getIndex()]);
	}

	@Override
	protected List<String> getText() {
		Date date = getDate();

		return Arrays.asList(
			getTimeFormat().format(date),
			getDateFormat().format(date)
		);
	}

	protected abstract Date getDate();
}
