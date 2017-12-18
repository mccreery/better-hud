package tk.nukeduck.hud.element;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingPositionHorizontal;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ExtraGuiElementSystemClock extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePositionAnchored pos2;
	private ElementSettingAnchor anchor;
	
	private ElementSettingBoolean twentyFour;
	private ElementSettingBoolean showSeconds;
	private ElementSettingMode dateType;
	private ElementSettingBoolean fullYear;
	
	@Override
	public void loadDefaults() {
		this.enabled = false;
		posMode.index = 0;
		pos.value = Position.MIDDLE_RIGHT;
		anchor.value = Position.TOP_LEFT.getFlag();
		pos2.x = 0;
		pos2.y = 5;
		twentyFour.value = false;
		showSeconds.value = false;
		dateType.index = 1;
		fullYear.value = true;
	}
	
	@Override
	public String getName() {
		return "systemClock";
	}
	
	private Bounds bounds = Bounds.EMPTY.clone();
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public ExtraGuiElementSystemClock() {
		//modes = new String[] {"clock.12hrleft", "clock.24hrleft", "clock.12hrright", "clock.24hrright"};
		//defaultMode = 2;
		staticHeight = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 2 + 12;
		this.registerUpdates(UpdateSpeed.FAST);
		
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos2 = new ElementSettingAbsolutePositionAnchored("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(twentyFour = new ElementSettingBoolean("24hr"));
		this.settings.add(showSeconds = new ElementSettingBoolean("showSeconds"));
		this.settings.add(dateType = new ElementSettingMode("dateType", new String[] {"dmy", "mdy", "ymd"}));
		this.settings.add(fullYear = new ElementSettingBoolean("fullYear"));
	}
	
	private static final String[] dateFormats = {"dd/MM/yy", "MM/dd/yy", "yy/MM/dd"};
	private static final String[] dateFormatsFull = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd"};
	
	int staticHeight;
	
	private static SimpleDateFormat dateFormat;
	private static SimpleDateFormat timeFormat;
	
	private String time;
	private String date;
	
	public void update(Minecraft mc) {
		Date d = new Date();
		dateFormat = new SimpleDateFormat((fullYear.value ? dateFormatsFull : dateFormats)[dateType.index]);
		timeFormat = new SimpleDateFormat((twentyFour.value ? "HH" : "hh") + ":mm" + (showSeconds.value ? ":ss" : "") + (twentyFour.value ? "" : " a"));
		
		time = timeFormat.format(d);
		date = dateFormat.format(d);
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		//boolean twentyFourHour = twentyFour.value;
		boolean right = pos.value == Position.MIDDLE_RIGHT;
		
		int staticWidth = Math.max(mc.fontRenderer.getStringWidth(time), mc.fontRenderer.getStringWidth(date)) + 10;
		
		if(posMode.index == 1) {
			this.bounds = new Bounds(0, 0, staticWidth, staticHeight);
			pos2.update(resolution, bounds);
			
			this.bounds = new Bounds(pos2.x, pos2.y, staticWidth, staticHeight);
			RenderUtil.renderQuad(pos2.x, pos2.y, staticWidth, staticHeight, Colors.TRANSLUCENT);
			//RenderUtil.drawRect(pos2.x, pos2.y, pos2.x + staticWidth, pos2.y + staticHeight, Colors.TRANSLUCENT);
			mc.ingameGUI.drawString(mc.fontRenderer, time, pos2.x + 5, pos2.y + 5, Colors.WHITE);
			mc.ingameGUI.drawString(mc.fontRenderer, date, pos2.x + 5, pos2.y + 7 + mc.fontRenderer.FONT_HEIGHT, Colors.WHITE);
		} else if(right) {
			this.bounds = new Bounds(resolution.getScaledWidth() - staticWidth, layoutManager.get(Position.TOP_RIGHT), staticWidth, staticHeight);
			int rightHeight = layoutManager.get(Position.TOP_RIGHT);
			
			RenderUtil.renderQuad(resolution.getScaledWidth() - staticWidth, rightHeight, staticWidth, staticHeight, Colors.TRANSLUCENT);
			//RenderUtil.drawRect(resolution.getScaledWidth() - staticWidth, rightHeight, resolution.getScaledWidth(), rightHeight + staticHeight, Colors.TRANSLUCENT);
			mc.ingameGUI.drawString(mc.fontRenderer, time, resolution.getScaledWidth() - mc.fontRenderer.getStringWidth(time) - 5, rightHeight + 5, Colors.WHITE);
			mc.ingameGUI.drawString(mc.fontRenderer, date, resolution.getScaledWidth() - mc.fontRenderer.getStringWidth(date) - 5, rightHeight + 7 + mc.fontRenderer.FONT_HEIGHT, Colors.WHITE);
			
			layoutManager.add(staticHeight, Position.TOP_RIGHT);
		} else {
			this.bounds = new Bounds(0, layoutManager.get(Position.TOP_LEFT), staticWidth, staticHeight);
			int leftHeight = layoutManager.get(Position.TOP_LEFT);
			
			RenderUtil.renderQuad(0, leftHeight, staticWidth, staticHeight, Colors.TRANSLUCENT);
			//RenderUtil.drawRect(0, leftHeight, staticWidth, leftHeight + staticHeight, Colors.TRANSLUCENT);
			mc.ingameGUI.drawString(mc.fontRenderer, time, 5, leftHeight + 5, Colors.WHITE);
			mc.ingameGUI.drawString(mc.fontRenderer, date, 5, leftHeight + 7 + mc.fontRenderer.FONT_HEIGHT, Colors.WHITE);
			
			layoutManager.add(staticHeight, Position.TOP_LEFT);
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}