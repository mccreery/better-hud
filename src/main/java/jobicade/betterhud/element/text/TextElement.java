package jobicade.betterhud.element.text;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingColor;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.StringGroup;

public abstract class TextElement extends HudElement {
	private SettingColor color;

	protected boolean border = false;

	public TextElement(String name) {
		this(name, new SettingPosition(DirectionOptions.CORNERS, DirectionOptions.CORNERS));
	}

	public TextElement(String name, SettingPosition position) {
		super(name, position);
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(color = new SettingColor("color"));
	}

	public Color getColor() {
		return color.get();
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_EAST);
		color.set(Color.WHITE);
	}

	protected Rect getPadding() {
		return border ? Rect.createPadding(BetterHud.SPACER) : Rect.empty();
	}

	protected Rect getMargin() {
		return Rect.empty();
	}

	protected Rect moveRect(Rect bounds) {
		return position.applyTo(bounds);
	}

	@Override
	public Rect render(Event event) {
		List<String> text = getText();
		return text == null || text.isEmpty() ? null : render(event, text);
	}

	protected Rect render(Event event, List<String> text) {
		StringGroup group = new StringGroup(text);
		group.setColor(color.get());

		Direction contentAlignment = position.getContentAlignment();
		if(contentAlignment != null) group.setAlignment(contentAlignment);

		Rect padding = getPadding();
		Rect margin = getMargin();

		Rect bounds = moveRect(new Rect(group.getSize().add(padding.getSize()).add(margin.getSize())));

		drawBorder(bounds, padding, margin);
		group.draw(bounds.grow(margin.grow(padding).invert()));
		drawExtras(bounds);

		return bounds;
	}

	protected void drawBorder(Rect bounds, Rect padding, Rect margin) {
		if(border) GlUtil.drawRect(bounds.grow(margin.invert()), Color.TRANSLUCENT);
	}

	protected abstract List<String> getText();
	protected void drawExtras(Rect bounds) {}
}
