package tk.nukeduck.hud.element.text;

import java.util.List;

import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingColor;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPositionAligned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.StringGroup;

public abstract class TextElement extends HudElement {
	protected SettingPosition position;
	protected SettingColor color = new SettingColor("color");

	protected boolean border = false;

	public TextElement(String name) {
		this(name, Direction.CORNERS);
	}

	public TextElement(String name, Direction... directions) {
		this(name, Direction.getFlags(directions));
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
		super.loadDefaults();

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
	public Bounds render(RenderPhase phase) {
		List<String> text = getText();
		return text == null || text.isEmpty() ? null : render(phase, text);
	}

	protected Bounds render(RenderPhase phase, List<String> text) {
		StringGroup group = new StringGroup(text);
		group.setColor(color.get());
		group.setAlignment(position.getAlignment());

		PaddedBounds bounds = moveBounds(new PaddedBounds(new Bounds(group.getSize()), getPadding(), getMargin()));

		drawBorder(bounds);
		group.draw(bounds.contentBounds());
		drawExtras(bounds);

		return bounds;
	}

	protected void drawBorder(PaddedBounds bounds) {
		if(border) GlUtil.drawRect(bounds.paddingBounds(), Colors.TRANSLUCENT);
	}

	protected abstract List<String> getText();
	protected void drawExtras(PaddedBounds bounds) {}
}
