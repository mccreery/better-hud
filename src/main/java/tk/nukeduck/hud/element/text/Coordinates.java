package tk.nukeduck.hud.element.text;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class Coordinates extends TextElement {
	private SettingBoolean spaced;
	private SettingSlider decimalPlaces;
	private String decimalFormat;
	
	private void updateDecimalFormat() {
		this.decimalFormat = "%." + (int) decimalPlaces.value + "f";
	}
	
	@Override
	public void loadDefaults() {
		super.loadDefaults();
		pos.value = Position.TOP_CENTER;
		spaced.value = true;
		decimalPlaces.value = 0;
		updateDecimalFormat();
	}
	
	public Coordinates() {
		super("coordinates");
		this.settings.add(0, new Divider("position"));
		pos.possibleLocations = Position.combine(Position.TOP_LEFT, Position.TOP_CENTER, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT);
		this.settings.add(new Divider("misc"));
		this.settings.add(spaced = new SettingBoolean("spaced"));
		this.settings.add(decimalPlaces = new SettingSlider("decimals", 0, 5) {
			@Override
			public String getSliderText() {
				return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), String.valueOf((int) this.value));
			}
		});
		decimalPlaces.accuracy = 1.0;
	}
	
	Bounds topBounds = Bounds.EMPTY;
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		if(this.posMode.index == 0 && this.pos.value == Position.TOP_CENTER) {
			return topBounds;
		}
		return Bounds.EMPTY;
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		if(posMode.index == 0 && pos.value == Position.TOP_CENTER) {
			int color = this.getColor();
			if(spaced.value) {
				String[] letters = this.getText();
				int spacer = Math.max(Math.max(MC.fontRenderer.getStringWidth(letters[0]), MC.fontRenderer.getStringWidth(letters[1])), MC.fontRenderer.getStringWidth(letters[2])) + 24;
				for(int i = 0; i < 3; i++) {
					MC.ingameGUI.drawCenteredString(MC.fontRenderer, letters[i], event.getResolution().getScaledWidth() / 2 + (i - 1) * spacer, 5, color);
				}
				int strX = event.getResolution().getScaledWidth() / 2 - spacer - MC.fontRenderer.getStringWidth(letters[0]) / 2;
				int width = spacer * 2 + (MC.fontRenderer.getStringWidth(letters[0]) + MC.fontRenderer.getStringWidth(letters[2])) / 2;
				this.topBounds = new Bounds(strX, 5, width, MC.fontRenderer.FONT_HEIGHT);
			} else {
				String xyz = this.getText()[0];
				int strWidth = MC.fontRenderer.getStringWidth(xyz);
				MC.ingameGUI.drawCenteredString(MC.fontRenderer, xyz, event.getResolution().getScaledWidth() / 2, 5, color);
				this.topBounds = new Bounds((event.getResolution().getScaledWidth() - strWidth) / 2, 5, strWidth, MC.fontRenderer.FONT_HEIGHT);
			}
		} else {
			this.topBounds = Bounds.EMPTY;
			super.render(event, stringManager, layoutManager);
		}
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 0 && pos.value != Position.TOP_CENTER;
	}

	@Override
	protected String[] getText() {
		this.updateDecimalFormat();
		String x = String.format(this.decimalFormat, MC.player.posX);
		String y = String.format(this.decimalFormat, MC.player.posY);
		String z = String.format(this.decimalFormat, MC.player.posZ);
		if(this.spaced.value) {
			x = I18n.format("betterHud.strings.x", x);
			y = I18n.format("betterHud.strings.y", y);
			z = I18n.format("betterHud.strings.z", z);
			return new String[] {x, y, z};
		} else {
			return new String[] {I18n.format("betterHud.strings.xyz", x, y, z)};
		}
	}
}