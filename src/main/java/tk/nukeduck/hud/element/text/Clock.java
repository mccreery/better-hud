package tk.nukeduck.hud.element.text;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;

public abstract class Clock extends TextElement {
	private final SettingBoolean twentyFour = new SettingBoolean("24hr");
	private final SettingBoolean showSeconds = new SettingBoolean("showSeconds").setUnlocalizedValue(SettingBoolean.VISIBLE);

	private final SettingChoose dateType = new SettingChoose("dateType", "dmy", "mdy", "ymd");
	private final SettingBoolean fullYear = new SettingBoolean("fullYear").setUnlocalizedValue(SettingBoolean.VISIBLE);

	public Clock(String name) {
		super(name, new SettingPosition(Options.CORNERS, Options.WEST_EAST));
		border = true;

		settings.add(new Legend("misc"));
		settings.add(twentyFour);
		settings.add(showSeconds);

		settings.add(dateType);
		settings.add(fullYear);
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
