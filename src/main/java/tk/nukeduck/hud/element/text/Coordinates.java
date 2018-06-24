package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.StringGroup;
import tk.nukeduck.hud.util.Direction.Options;

public class Coordinates extends TextElement {
	private final SettingBoolean spaced = new SettingBoolean("spaced");
	private final SettingSlider decimalPlaces = new SettingSlider("precision", 0, 5, 1).setUnlocalizedValue("betterHud.value.places");

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH);
		spaced.set(true);
		decimalPlaces.set(0);
		settings.priority.set(-2);
	}

	public Coordinates() {
		super("coordinates", Options.TOP_BOTTOM);

		this.settings.add(new Legend("misc"));
		this.settings.add(spaced);
		this.settings.add(decimalPlaces);
	}

	@Override
	public Bounds render(Event event, List<String> text) {
		if(!spaced.get() || !position.isDirection(Direction.NORTH) && !position.isDirection(Direction.SOUTH)) {
			return super.render(event, text);
		}

		StringGroup group = new StringGroup(text).setAlignment(position.getDirection()).setSpacing(50).setRow();
		Bounds bounds = MANAGER.position(position.getDirection(), new Bounds(group.getSize()));

		return group.draw(bounds);
	}

	@Override
	protected List<String> getText() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits(decimalPlaces.get().intValue());

		String x = format.format(MC.player.posX);
		String y = format.format(MC.player.posY);
		String z = format.format(MC.player.posZ);

		if(spaced.get()) {
			x = I18n.format("betterHud.hud.x", x);
			y = I18n.format("betterHud.hud.y", y);
			z = I18n.format("betterHud.hud.z", z);
			return Arrays.asList(x, y, z);
		} else {
			return Arrays.asList(I18n.format("betterHud.hud.xyz", x, y, z));
		}
	}
}
