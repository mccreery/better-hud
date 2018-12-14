package jobicade.betterhud.element.text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.geom.Direction;

public abstract class Clock extends TextElement {
	private SettingBoolean twentyFour, showSeconds, fullYear;
	private SettingChoose dateType;

	public Clock(String name) {
		super(name, new SettingPosition(DirectionOptions.CORNERS, DirectionOptions.WEST_EAST));
		border = true;
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);

		settings.add(new Legend("misc"));
		settings.add(twentyFour = new SettingBoolean("24hr"));
		settings.add(showSeconds = new SettingBoolean("showSeconds").setValuePrefix(SettingBoolean.VISIBLE));

		settings.add(dateType = new SettingChoose("dateType", "dmy", "mdy", "ymd"));
		settings.add(fullYear = new SettingBoolean("fullYear").setValuePrefix(SettingBoolean.VISIBLE));
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
