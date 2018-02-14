package tk.nukeduck.hud.element.text;

import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingColor;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPositionAligned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public abstract class TextElement extends HudElement {
	protected SettingPosition position;
	protected SettingColor color = new SettingColor("color");

	protected boolean border = false;

	public TextElement(String name) {
		this(name, Direction.CORNERS);
	}

	public TextElement(String name, Direction... directions) {
		this(name, Direction.flags(directions));
	}

	public TextElement(String name, int directions) {
		super(name);

		settings.add(position = new SettingPositionAligned("position", directions, Direction.ALL));
		settings.add(color);
	}

	public int getColor() {
		return color.get();
	}

	@Override
	public void loadDefaults() {
		settings.set(true);
		position.set(Direction.NORTH_WEST);
		color.set(Colors.WHITE);
	}

	protected Bounds getPadding() {
		return border ? Bounds.PADDING : Bounds.EMPTY;
	}

	protected Bounds getMargin() {
		return Bounds.EMPTY;
	}

	protected PaddedBounds moveBounds(PaddedBounds bounds) {
		return position.applyTo(bounds);
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event) {
		String[] text = getText();
		if(text.length == 0) return null;

		Point size = GlUtil.getLinesSize(text);
		PaddedBounds bounds = moveBounds(new PaddedBounds(new Bounds(size), getPadding(), getMargin()));

		drawBorder(event, bounds);
		GlUtil.drawLines(text, bounds.contentBounds(), position.getAnchor(), color.get());
		drawExtras(event, bounds);

		return bounds;
	}

	protected void drawBorder(RenderGameOverlayEvent event, PaddedBounds bounds) {
		if(border) GlUtil.drawRect(bounds.paddingBounds(), Colors.TRANSLUCENT);
	}

	protected abstract String[] getText();
	protected void drawExtras(RenderGameOverlayEvent event, PaddedBounds bounds) {}
}
