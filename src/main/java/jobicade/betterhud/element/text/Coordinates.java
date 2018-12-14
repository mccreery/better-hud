package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.util.Bounds;
import jobicade.betterhud.util.Direction;
import jobicade.betterhud.util.StringGroup;
import jobicade.betterhud.util.Direction.Options;

public class Coordinates extends TextElement {
	private SettingBoolean spaced;
	private SettingSlider decimalPlaces;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH);
		spaced.set(true);
		decimalPlaces.set(0);
		settings.priority.set(-2);
	}

	public Coordinates() {
		super("coordinates", new SettingPosition(Options.TOP_BOTTOM, Options.NONE));
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(new Legend("misc"));
		settings.add(spaced = new SettingBoolean("spaced"));
		settings.add(decimalPlaces = new SettingSlider("precision", 0, 5, 1).setUnlocalizedValue("betterHud.value.places"));
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
