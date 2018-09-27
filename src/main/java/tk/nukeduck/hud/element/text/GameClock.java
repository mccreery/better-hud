package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingChoose;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class GameClock extends Clock {
	private static final ItemStack BED = new ItemStack(Items.BED);

	private final SettingBoolean showDays = new SettingBoolean("showDays").setUnlocalizedValue(SettingBoolean.VISIBLE);
	private final SettingChoose requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand");

	public GameClock() {
		super("gameClock");
		border = true;

		settings.add(showDays);
		settings.add(requireItem);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		showDays.set(true);
		requireItem.setIndex(0);
	}

	@Override
	protected Bounds getMargin() {
		return new Bounds(0, 0, 21, 0).align(Point.ZERO, position.getContentAlignment());
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
	public Bounds render(Event event) {
		Bounds bounds = super.render(event);

		if(!MC.world.isDaytime()) {
			Direction bedAnchor = Options.WEST_EAST.apply(position.getContentAlignment());
			Bounds bed = new Bounds(16, 16).anchor(bounds, bedAnchor);

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
