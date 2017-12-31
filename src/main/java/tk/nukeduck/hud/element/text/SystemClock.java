package tk.nukeduck.hud.element.text;

import java.text.SimpleDateFormat;
import java.util.Date;

import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Direction;

public class SystemClock extends TextElement {
	private final SettingBoolean twentyFour = new SettingBoolean("24hr");
	private final SettingBoolean showSeconds = new SettingBoolean("showSeconds");
	private final SettingChoose dateType = new SettingChoose("dateType", "dmy", "mdy", "ymd");
	private final SettingBoolean fullYear = new SettingBoolean("fullYear");

	public SystemClock() {
		super("systemClock", Direction.CORNERS);
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
		this.setEnabled(false);
		twentyFour.set(false);
		showSeconds.set(false);
		dateType.index = 1;
		fullYear.set(true);
	}

	private static final String[] dateFormats = {"dd/MM/yy", "MM/dd/yy", "yy/MM/dd"};
	private static final String[] dateFormatsFull = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd"};

	@Override
	protected String[] getText() {
		Date now = new Date();

		// TODO find a better date formatter
		SimpleDateFormat dateFormat = new SimpleDateFormat((fullYear.get() ? dateFormatsFull : dateFormats)[dateType.index]);
		SimpleDateFormat timeFormat = new SimpleDateFormat((twentyFour.get() ? "HH" : "hh") + ":mm" + (showSeconds.get() ? ":ss" : "") + (twentyFour.get() ? "" : " a"));

		String time = timeFormat.format(now);
		String date = dateFormat.format(now);

		return new String[] {time, date};
	}
}
