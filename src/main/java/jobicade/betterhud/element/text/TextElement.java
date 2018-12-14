package jobicade.betterhud.element.text;

import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingColor;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.Bounds;
import jobicade.betterhud.util.Colors;
import jobicade.betterhud.util.Direction;
import jobicade.betterhud.util.Direction.Options;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.StringGroup;

public abstract class TextElement extends HudElement {
	private SettingColor color;

	protected boolean border = false;

	public TextElement(String name) {
		this(name, new SettingPosition(Options.CORNERS, Options.CORNERS));
	}

	public TextElement(String name, SettingPosition position) {
		super(name, position);
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(color = new SettingColor("color"));
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

		Direction contentAlignment = position.getContentAlignment();
		if(contentAlignment != null) group.setAlignment(contentAlignment);

		Bounds padding = getPadding();
		Bounds margin = getMargin();

		Bounds bounds = moveBounds(new Bounds(group.getSize().add(padding.getSize()).add(margin.getSize())));

		drawBorder(bounds, padding, margin);
		group.draw(bounds.grow(margin.grow(padding).scale(-1)));
		drawExtras(bounds);

		return bounds;
	}

	protected void drawBorder(Bounds bounds, Bounds padding, Bounds margin) {
		if(border) GlUtil.drawRect(bounds.grow(margin.scale(-1)), Colors.TRANSLUCENT);
	}

	protected abstract List<String> getText();
	protected void drawExtras(Bounds bounds) {}
}
