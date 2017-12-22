package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.text.SimpleDateFormat;
import java.util.Date;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingPositionHorizontal;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.Ticker;
import tk.nukeduck.hud.util.constants.Colors;

public class SystemClock extends HudElement {
	private SettingMode posMode;
	private SettingPosition pos;
	private SettingAnchoredPosition pos2;
	private SettingAnchor anchor;
	
	private SettingBoolean twentyFour;
	private SettingBoolean showSeconds;
	private SettingMode dateType;
	private SettingBoolean fullYear;
	
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
	
	private Bounds bounds = Bounds.EMPTY.clone();
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return bounds;
	}
	
	public SystemClock() {
		super("systemClock");
		staticHeight = Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * 2 + 12;
		Ticker.FAST.register(this);
		
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos2 = new SettingAnchoredPosition("position2", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new Divider("misc"));
		this.settings.add(twentyFour = new SettingBoolean("24hr"));
		this.settings.add(showSeconds = new SettingBoolean("showSeconds"));
		this.settings.add(dateType = new SettingMode("dateType", new String[] {"dmy", "mdy", "ymd"}));
		this.settings.add(fullYear = new SettingBoolean("fullYear"));
	}
	
	private static final String[] dateFormats = {"dd/MM/yy", "MM/dd/yy", "yy/MM/dd"};
	private static final String[] dateFormatsFull = {"dd/MM/yyyy", "MM/dd/yyyy", "yyyy/MM/dd"};
	
	int staticHeight;
	
	private static SimpleDateFormat dateFormat;
	private static SimpleDateFormat timeFormat;
	
	private String time;
	private String date;
	
	public void update() {
		Date d = new Date();
		dateFormat = new SimpleDateFormat((fullYear.value ? dateFormatsFull : dateFormats)[dateType.index]);
		timeFormat = new SimpleDateFormat((twentyFour.value ? "HH" : "hh") + ":mm" + (showSeconds.value ? ":ss" : "") + (twentyFour.value ? "" : " a"));
		
		time = timeFormat.format(d);
		date = dateFormat.format(d);
	}
	
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		//boolean twentyFourHour = twentyFour.value;
		boolean right = pos.value == Position.MIDDLE_RIGHT;
		
		int staticWidth = Math.max(MC.fontRenderer.getStringWidth(time), MC.fontRenderer.getStringWidth(date)) + 10;
		
		if(posMode.index == 1) {
			this.bounds = new Bounds(0, 0, staticWidth, staticHeight);
			pos2.update(event.getResolution(), bounds);
			
			this.bounds = new Bounds(pos2.x, pos2.y, staticWidth, staticHeight);
			RenderUtil.renderQuad(pos2.x, pos2.y, staticWidth, staticHeight, Colors.TRANSLUCENT);
			//RenderUtil.drawRect(pos2.x, pos2.y, pos2.x + staticWidth, pos2.y + staticHeight, Colors.TRANSLUCENT);
			MC.ingameGUI.drawString(MC.fontRenderer, time, pos2.x + 5, pos2.y + 5, Colors.WHITE);
			MC.ingameGUI.drawString(MC.fontRenderer, date, pos2.x + 5, pos2.y + 7 + MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
		} else if(right) {
			this.bounds = new Bounds(event.getResolution().getScaledWidth() - staticWidth, layoutManager.get(Position.TOP_RIGHT), staticWidth, staticHeight);
			int rightHeight = layoutManager.get(Position.TOP_RIGHT);
			
			RenderUtil.renderQuad(event.getResolution().getScaledWidth() - staticWidth, rightHeight, staticWidth, staticHeight, Colors.TRANSLUCENT);
			//RenderUtil.drawRect(resolution.getScaledWidth() - staticWidth, rightHeight, resolution.getScaledWidth(), rightHeight + staticHeight, Colors.TRANSLUCENT);
			MC.ingameGUI.drawString(MC.fontRenderer, time, event.getResolution().getScaledWidth() - MC.fontRenderer.getStringWidth(time) - 5, rightHeight + 5, Colors.WHITE);
			MC.ingameGUI.drawString(MC.fontRenderer, date, event.getResolution().getScaledWidth() - MC.fontRenderer.getStringWidth(date) - 5, rightHeight + 7 + MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
			
			layoutManager.add(staticHeight, Position.TOP_RIGHT);
		} else {
			this.bounds = new Bounds(0, layoutManager.get(Position.TOP_LEFT), staticWidth, staticHeight);
			int leftHeight = layoutManager.get(Position.TOP_LEFT);
			
			RenderUtil.renderQuad(0, leftHeight, staticWidth, staticHeight, Colors.TRANSLUCENT);
			//RenderUtil.drawRect(0, leftHeight, staticWidth, leftHeight + staticHeight, Colors.TRANSLUCENT);
			MC.ingameGUI.drawString(MC.fontRenderer, time, 5, leftHeight + 5, Colors.WHITE);
			MC.ingameGUI.drawString(MC.fontRenderer, date, 5, leftHeight + 7 + MC.fontRenderer.FONT_HEIGHT, Colors.WHITE);
			
			layoutManager.add(staticHeight, Position.TOP_LEFT);
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}