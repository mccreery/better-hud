package tk.nukeduck.hud.element.settings;

import java.util.Collection;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.constants.Constants;

public class SettingAnchoredPosition extends SettingAbsolutePosition {
	private Point size;
	private Bounds bounds;
	private final SettingAnchor anchor;

	public SettingAnchoredPosition(String name, SettingAnchor anchor) {
		super(name);
		this.anchor = anchor;
		ScaledResolution size = new ScaledResolution(Minecraft.getMinecraft());
		this.size = new Point(size.getScaledWidth(), size.getScaledHeight());
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		super.actionPerformed(gui, button);
		if(this.isPicking) return;
		ScaledResolution next = new ScaledResolution(Minecraft.getMinecraft());
		this.size.setX(next.getScaledWidth());
		this.size.setY(next.getScaledHeight());
	}

	@Override
	public void otherAction(Collection<Setting> settings) {
		super.otherAction(settings);
		this.anchor.enabled = this.getEnabled();
	}

	public void update(ScaledResolution next, Bounds bounds) {
		Point p = new Point(next.getScaledWidth(), next.getScaledHeight());
		if(this.size == null) this.size = p;
		if(this.bounds == null) this.bounds = bounds;

		if(this.size != p) {
			Point p2 = new Point(this.x, this.y);
			/*if(bounds != null) {
				p2.setX(p2.getX() + bounds.getWidth() / 2);
				p2.setY(p2.getY() + bounds.getHeight() / 2);
			}*/
			p2 = this.anchor.translateAnchor(p2, this.size, p, this.bounds, bounds);
			this.bounds = bounds;
			/*if(bounds != null) {
				p2.setX(p2.getX() - bounds.getWidth() / 2);
				p2.setY(p2.getY() - bounds.getHeight() / 2);
			}*/
			
			this.x = p2.getX();
			this.y = p2.getY();

			this.size = p;
			if(this.xBox != null && this.yBox != null) {
				this.updateText();
			}
		}
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(this.x).append(Constants.VALUE_SEPARATOR).append(this.y);
		if(this.size != null) {
			builder.append(Constants.VALUE_SEPARATOR).append(this.size.getX()).append(Constants.VALUE_SEPARATOR).append(this.size.getY());
		} else {
			builder.append(Constants.VALUE_SEPARATOR).append("-1").append(Constants.VALUE_SEPARATOR).append("-1");
		}
		if(this.bounds != null) {
			builder.append(Constants.VALUE_SEPARATOR).append(this.bounds.getWidth()).append(Constants.VALUE_SEPARATOR).append(this.bounds.getHeight());
		}
		return builder.toString();
	}
	
	@Override
	public void fromString(String val) {
		if(val.contains(Constants.VALUE_SEPARATOR)) {
			String[] xy = val.split(Constants.VALUE_SEPARATOR);
			if(xy.length >= 6) {
				this.bounds = new Bounds(0, 0, Integer.parseInt(xy[4]), Integer.parseInt(xy[5]));
			}
			if(xy.length >= 4) {
				try {
					if(xy[2].charAt(0) != '-') {
						this.size = new Point(Integer.parseInt(xy[2]), Integer.parseInt(xy[3]));
					}
				} catch(NumberFormatException e) {}
			}
			if(xy.length >= 2) {
				try {
					this.x = Integer.parseInt(xy[0]);
					this.y = Integer.parseInt(xy[1]);
				} catch(NumberFormatException e) {}
			}
		}
	}
}
