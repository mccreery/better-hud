package tk.nukeduck.hud.element.text;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.HudElement;
import tk.nukeduck.hud.element.settings.SettingColor;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.StringGroup;
import tk.nukeduck.hud.util.Direction.Options;

public abstract class TextElement extends HudElement {
	protected SettingPosition position;
	protected SettingColor color = new SettingColor("color");

	protected boolean border = false;

	public TextElement(String name) {
		this(name, Options.CORNERS, Options.HORIZONTAL);
	}

	public TextElement(String name, Options directions) {
		this(name, directions, Options.HORIZONTAL);
	}

	public TextElement(String name, Options directions, Options contentAlignments) {
		super(name);

		settings.add(position = new SettingPosition("position", directions, contentAlignments));
		settings.add(color);
	}

	public int getColor() {
		return color.get();
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_EAST);
		color.set(Colors.WHITE);
	}

	protected Bounds getPadding() {
		return border ? Bounds.PADDING : Bounds.EMPTY;
	}

	protected Bounds getMargin() {
		return Bounds.EMPTY;
	}

	protected Bounds moveBounds(Bounds bounds) {
		return position.applyTo(bounds);
	}

	@Override
	public Bounds render(Event event) {
		List<String> text = getText();
		return text == null || text.isEmpty() ? null : render(event, text);
	}

	protected Bounds render(Event event, List<String> text) {
		StringGroup group = new StringGroup(text);
		group.setColor(color.get());
		group.setAlignment(position.getContentAlignment());

		Bounds padding = getPadding();
		Bounds margin = getMargin();

		Bounds bounds = moveBounds(new Bounds(group.getSize().add(padding.getSize()).add(margin.getSize())));

		drawBorder(bounds, padding, margin);
		group.draw(bounds.withInset(margin).withInset(padding));
		drawExtras(bounds);

		return bounds;
	}

	protected void drawBorder(Bounds bounds, Bounds padding, Bounds margin) {
		if(border) GlUtil.drawRect(bounds.withInset(margin), Colors.TRANSLUCENT);
	}

	protected abstract List<String> getText();
	protected void drawExtras(Bounds bounds) {}
}
