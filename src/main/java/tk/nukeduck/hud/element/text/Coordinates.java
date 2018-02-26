package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;

public class Coordinates extends TextElement {
	private final SettingBoolean spaced;
	private final SettingSlider decimalPlaces;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.set(Direction.NORTH);
		spaced.set(true);
		decimalPlaces.set(0.0);
	}

	public Coordinates() {
		super("coordinates", Direction.CORNERS | Direction.flags(Direction.NORTH));

		this.settings.add(new Legend("misc"));
		this.settings.add(spaced = new SettingBoolean("spaced"));
		this.settings.add(decimalPlaces = new SettingSlider("precision", 0, 5, 1).setUnlocalizedValue("betterHud.value.places"));
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, List<String> text) {
		if(!position.getAlignment().in(Direction.VERTICAL) || !spaced.get()) {
			return super.render(event, text);
		}

		Bounds bounds = new Bounds(100 + (MC.fontRenderer.getStringWidth(text.get(0)) + MC.fontRenderer.getStringWidth(text.get(2))) / 2, MC.fontRenderer.FONT_HEIGHT);
		position.applyTo(bounds);

		for(int i = 0, x = bounds.x() + bounds.width() / 2 - 50; i < 3; i++, x += 50) {
			MC.ingameGUI.drawCenteredString(MC.fontRenderer, text.get(i), x, bounds.y(), color.get());
		}
		return bounds;
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
