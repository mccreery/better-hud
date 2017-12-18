package tk.nukeduck.hud.element.text;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementCoordinates extends ExtraGuiElementText {
	private ElementSettingBoolean spaced;
	private ElementSettingSlider decimalPlaces;
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
	
	@Override
	public String getName() {
		return "coordinates";
	}
	
	public ExtraGuiElementCoordinates() {
		super();
		this.settings.add(0, new ElementSettingDivider("position"));
		pos.possibleLocations = Position.combine(Position.TOP_LEFT, Position.TOP_CENTER, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(spaced = new ElementSettingBoolean("spaced"));
		this.settings.add(decimalPlaces = new ElementSettingSlider("decimals", 0, 5) {
			@Override
			public String getSliderText() {
				return FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), String.valueOf((int) this.value));
			}
		});
		decimalPlaces.accuracy = 1.0;
	}
	
	public void update(Minecraft mc) {}
	
	Bounds topBounds = Bounds.EMPTY;
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		if(this.posMode.index == 0 && this.pos.value == Position.TOP_CENTER) {
			return topBounds;
		}
		return super.getBounds(resolution);
	}
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		if(posMode.index == 0 && pos.value == Position.TOP_CENTER) {
			int color = this.getColor();
			if(spaced.value) {
				String[] letters = this.getText(mc);
				int spacer = Math.max(Math.max(mc.fontRendererObj.getStringWidth(letters[0]), mc.fontRendererObj.getStringWidth(letters[1])), mc.fontRendererObj.getStringWidth(letters[2])) + 24;
				for(int i = 0; i < 3; i++) {
					mc.ingameGUI.drawCenteredString(mc.fontRendererObj, letters[i], resolution.getScaledWidth() / 2 + (i - 1) * spacer, 5, color);
				}
				int strX = resolution.getScaledWidth() / 2 - spacer - mc.fontRendererObj.getStringWidth(letters[0]) / 2;
				int width = spacer * 2 + (mc.fontRendererObj.getStringWidth(letters[0]) + mc.fontRendererObj.getStringWidth(letters[2])) / 2;
				this.topBounds = new Bounds(strX, 5, width, mc.fontRendererObj.FONT_HEIGHT);
			} else {
				String xyz = this.getText(mc)[0];
				int strWidth = mc.fontRendererObj.getStringWidth(xyz);
				mc.ingameGUI.drawCenteredString(mc.fontRendererObj, xyz, resolution.getScaledWidth() / 2, 5, color);
				this.topBounds = new Bounds((resolution.getScaledWidth() - strWidth) / 2, 5, strWidth, mc.fontRendererObj.FONT_HEIGHT);
			}
		} else {
			this.topBounds = Bounds.EMPTY;
			super.render(mc, resolution, stringManager, layoutManager);
		}
	}

	@Override
	public boolean shouldProfile() {
		return posMode.index == 0 && pos.value != Position.TOP_CENTER;
	}

	@Override
	protected String[] getText(Minecraft mc) {
		this.updateDecimalFormat();
		String x = String.format(this.decimalFormat, mc.thePlayer.posX);
		String y = String.format(this.decimalFormat, mc.thePlayer.posY);
		String z = String.format(this.decimalFormat, mc.thePlayer.posZ);
		if(this.spaced.value) {
			x = FormatUtil.translatePre("strings.x", x);
			y = FormatUtil.translatePre("strings.y", y);
			z = FormatUtil.translatePre("strings.z", z);
			return new String[] {x, y, z};
		} else {
			return new String[] {FormatUtil.translatePre("strings.xyz", x, y, z)};
		}
	}
}