package jobicade.betterhud.element.text;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.world.WorldProvider.WorldSleepResult;

public class GameClock extends Clock {
	private static final ItemStack BED = new ItemStack(Items.BED, 1, 14);

	private SettingBoolean showDays;
	private SettingBoolean showSleepIndicator;
	private SettingChoose requireItem;

	public GameClock() {
		super("gameClock");

		settings.addChildren(
			showDays = new SettingBoolean("showDays").setValuePrefix(SettingBoolean.VISIBLE),
			showSleepIndicator = new SettingBoolean("showSleepIndicator").setValuePrefix(SettingBoolean.VISIBLE),
			requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand")
		);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		showDays.set(true);
		showSleepIndicator.set(false);
		requireItem.setIndex(0);
	}

	@Override
	protected Rect getMargin() {
		return new Rect(0, 0, 21, 0).align(Point.zero(), position.getContentAlignment());
	}

	@Override
	public boolean shouldRender(OverlayContext context) {
		switch(requireItem.getIndex()) {
			case 1:
				return Minecraft.getMinecraft().player.inventory.hasItemStack(new ItemStack(Items.CLOCK));
			case 2:
				return Minecraft.getMinecraft().player.getHeldItemMainhand().getItem() == Items.CLOCK
					|| Minecraft.getMinecraft().player.getHeldItemOffhand().getItem() == Items.CLOCK;
		}
		return true;
	}

	@Override
	public Rect render(OverlayContext context) {
		Rect bounds = super.render(context);

		if(showSleepIndicator(context.getPartialTicks())) {
			Direction bedAnchor = DirectionOptions.WEST_EAST.apply(position.getContentAlignment().mirrorCol());
			Rect bed = new Rect(16, 16).anchor(bounds, bedAnchor);

			GlUtil.renderSingleItem(BED, bed.getPosition());
		}
		return bounds;
	}

	private boolean showSleepIndicator(float partialTicks) {
		return showSleepIndicator.get()
				&& Minecraft.getMinecraft().world.provider.canSleepAt(Minecraft.getMinecraft().player, Minecraft.getMinecraft().player.getPosition()) == WorldSleepResult.ALLOW
				// Taken from EntityPlayer#trySleep, ignores enemies and bed position
				&& !Minecraft.getMinecraft().player.isPlayerSleeping()
				&& Minecraft.getMinecraft().player.isEntityAlive()
				&& Minecraft.getMinecraft().world.provider.isSurfaceWorld()
				// World#isDayTime is server only
				//&& !Minecraft.getMinecraft().world.isDaytime();
				&& Minecraft.getMinecraft().world.calculateSkylightSubtracted(partialTicks) >= 4;
	}

	@Override
	protected Date getDate() {
		long worldTime = Minecraft.getMinecraft().world.getWorldTime() + 6000;

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
