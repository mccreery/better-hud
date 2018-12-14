package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MC;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.geom.Point;

public class GameClock extends Clock {
	private static final ItemStack BED = new ItemStack(Items.BED);

	private SettingBoolean showDays;
	private SettingChoose requireItem;

	public GameClock() {
		super("gameClock");
		border = true;
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(showDays = new SettingBoolean("showDays").setValuePrefix(SettingBoolean.VISIBLE));
		settings.add(requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand"));
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		showDays.set(true);
		requireItem.setIndex(0);
	}

	@Override
	protected Rect getMargin() {
		return new Rect(0, 0, 21, 0).align(Point.zero(), position.getContentAlignment());
	}

	@Override
	public boolean shouldRender(Event event) {
		if(!super.shouldRender(event)) return false;

		switch(requireItem.getIndex()) {
			case 1:
				return MC.player.inventory.hasItemStack(new ItemStack(Items.CLOCK));
			case 2:
				return MC.player.getHeldItemMainhand().getItem() == Items.CLOCK
					|| MC.player.getHeldItemOffhand().getItem() == Items.CLOCK;
		}
		return true;
	}

	@Override
	public Rect render(Event event) {
		Rect bounds = super.render(event);

		if(!MC.world.isDaytime()) {
			Direction bedAnchor = DirectionOptions.WEST_EAST.apply(position.getContentAlignment());
			Rect bed = new Rect(16, 16).anchor(bounds, bedAnchor);

			GlUtil.renderSingleItem(BED, bed.getPosition());
		}
		return bounds;
	}

	@Override
	protected Date getDate() {
		long worldTime = MC.world.getWorldTime() + 6000;

		// Convert to milliseconds
		worldTime = Math.round(worldTime / 1000. * 3600.) * 1000;

		return new Date(worldTime);
	}

	/* Game time is not localized, so we have to use UTC instead of
	 * the local timezone while formatting */
	private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

	@Override
	protected DateFormat getTimeFormat() {
		DateFormat format = super.getTimeFormat();
		format.setTimeZone(UTC);

		return format;
	}

	@SuppressWarnings("serial")
	@Override
	protected DateFormat getDateFormat() {
		if(showDays.get()) {
			return new DateFormat() {
				@Override
				public StringBuffer format(Date date, StringBuffer buffer, FieldPosition fieldPosition) {
					long day = date.getTime() / 84600000 + 1;

					buffer.append(I18n.format("betterHud.hud.day", day));
					return buffer;
				}

				@Override
				public Date parse(String source, ParsePosition pos) {
					throw new UnsupportedOperationException();
				}
			};
		} else {
			DateFormat format = super.getDateFormat();
			format.setTimeZone(UTC);

			return format;
		}
	}
}
