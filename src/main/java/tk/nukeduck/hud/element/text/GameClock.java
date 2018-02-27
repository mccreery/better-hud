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
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;

public class GameClock extends Clock {
	private static final ItemStack BED = new ItemStack(Items.BED);

	private final SettingBoolean showDays = new SettingBoolean("showDays").setUnlocalizedValue(SettingBoolean.VISIBLE);

	public GameClock() {
		super("gameClock");
		border = true;

		settings.add(showDays);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		showDays.set(true);
	}

	@Override
	protected Bounds getMargin() {
		return position.getAnchor().align(new Bounds(0, 0, 21, 0));
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		PaddedBounds bounds = (PaddedBounds)super.render(event);

		if(!MC.world.isDaytime()) {
			Direction bedAnchor = position.getAnchor().in(Direction.RIGHT) ? Direction.WEST : Direction.EAST;
			Bounds bed = bedAnchor.anchor(new Bounds(16, 16), bounds);

			GlUtil.renderSingleItem(BED, bed.position);
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
