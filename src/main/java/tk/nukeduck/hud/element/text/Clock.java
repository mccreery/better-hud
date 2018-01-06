package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Util;

// TODO make subclass of system clock
public class Clock extends TextElement {
	private static final ItemStack BED = new ItemStack(Items.BED);

	private final SettingBoolean twentyFour = new SettingBoolean("24hr");

	public Clock() {
		super("clock", Direction.CORNERS);
		border = true;

		settings.add(new Legend("misc"));
		settings.add(twentyFour);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.NORTH_EAST);
		twentyFour.set(false);
	}

	@Override
	protected Bounds getMargin() {
		return position.getAnchor().align(new Bounds(0, 0, 21, 0));
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		PaddedBounds bounds = (PaddedBounds)super.render(event, manager);

		if(!MC.world.isDaytime()) {
			Direction bedAnchor = position.getAnchor().in(Direction.RIGHT) ? Direction.WEST : Direction.EAST;
			Bounds bed = bedAnchor.anchor(new Bounds(16, 16), bounds);

			Util.renderItem(BED, bed.position);
		}
		return bounds;
	}

	@Override
	protected String[] getText() {
		long t = (MC.world.getWorldTime() + 6000) % 24000;
		String day = I18n.format("betterHud.strings.day", (MC.world.getWorldTime() + 6000) / 24000 + 1);
		String time;
		int h = (int) (t / 1000);

		time = Clock.formatTime(h, (int) ((t % 1000) / 1000.0 * 60.0), twentyFour.get());

		return new String[] {time, day};
	}

	private static String formatTime(int hours, int minutes, boolean twentyFourHour) {
		String unlocalized, hourS, minuteS;
	
		if(twentyFourHour) {
			unlocalized = "betterHud.strings.time";
			hourS = String.format("%02d", hours);
		} else {
			final boolean pm = hours >= 12;
			if(pm) hours -= 12;
			if(hours == 0) hours = 12;
	
			unlocalized = pm ? "betterHud.strings.time.pm" : "betterHud.strings.time.am";
			hourS = String.valueOf(hours);
		}
	
		minuteS = String.format("%02d", minutes);
		return I18n.format(unlocalized, hourS, minuteS);
	}
}
