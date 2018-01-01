package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import java.text.DecimalFormat;

import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;

public class Coordinates extends TextElement {
	private final SettingBoolean spaced;
	private final SettingSlider decimalPlaces;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.load(Direction.NORTH);
		spaced.set(true);
		decimalPlaces.value = 0;
	}

	public Coordinates() {
		super("coordinates", Direction.CORNERS | Direction.flags(Direction.NORTH));

		this.settings.add(new Legend("misc"));
		this.settings.add(spaced = new SettingBoolean("spaced"));
		this.settings.add(decimalPlaces = new SettingSlider("decimals", 0, 5, 1) {
			@Override
			public String getSliderText() {
				return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), String.valueOf((int) this.value));
			}
		});
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		if(position.getAnchor() == Direction.NORTH && spaced.get()) {
			String[] coordinates = getText();

			Bounds bounds = new Bounds(100 + (MC.fontRenderer.getStringWidth(coordinates[0]) + MC.fontRenderer.getStringWidth(coordinates[2])) / 2, MC.fontRenderer.FONT_HEIGHT);
			position.applyTo(bounds, manager);

			for(int i = 0, x = bounds.x() + bounds.width() / 2 - 50; i < 3; i++, x += 50) {
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, coordinates[i], x, bounds.y(), color.get());
			}
			return bounds;
		} else {
			return super.render(event, manager);
		}
	}

	@Override
	protected String[] getText() {
		DecimalFormat format = new DecimalFormat();
		format.setMaximumFractionDigits((int)decimalPlaces.value);

		String x = format.format(MC.player.posX);
		String y = format.format(MC.player.posY);
		String z = format.format(MC.player.posZ);

		if(spaced.get()) {
			x = I18n.format("betterHud.strings.x", x);
			y = I18n.format("betterHud.strings.y", y);
			z = I18n.format("betterHud.strings.z", z);
			return new String[] {x, y, z};
		} else {
			return new String[] {I18n.format("betterHud.strings.xyz", x, y, z)};
		}
	}
}
