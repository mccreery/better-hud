package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.SPACER;

import java.util.Collection;
import java.util.Map;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiActionButton;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;

public class SettingDirection extends SettingAlignable<Direction, SettingDirection> {
	private GuiActionButton[] toggles = new GuiActionButton[9];
	private Rect bounds;

	private boolean horizontal = false;

	private final DirectionOptions options;
	private Direction value;

	public SettingDirection(String name, Direction alignment) {
		this(name, alignment, DirectionOptions.ALL);
	}

	public SettingDirection(String name, Direction alignment, DirectionOptions options) {
		super(name, alignment);
		this.options = options;
	}

	@Override
	protected SettingDirection getThis() {
		return this;
	}

	public SettingDirection setHorizontal() {
		horizontal = true;
		setAlignment(Direction.WEST);

		return this;
	}

	@Override
	protected int getAlignmentWidth() {
		return horizontal ? 150 : 240;
	}

	@Override
	public SettingDirection setAlignment(Direction alignment) {
		if(!horizontal) {
			return super.setAlignment(alignment);
		} else {
			return this;
		}
	}

	@Override
	public void getGuiParts(java.util.List<Gui> parts, Map<Gui, Setting<?, ?>> callbacks, Rect bounds) {
		this.bounds = bounds;

		Rect radios = new Rect(60, 60).anchor(bounds, horizontal ? Direction.WEST : Direction.SOUTH);
		Rect radio = new Rect(20, 20);

		for(Direction direction : Direction.values()) {
			GuiActionButton button = new GuiActionButton("")
				.setId(direction.ordinal())
				.setBounds(radio.anchor(radios, direction));

			parts.add(button);
			callbacks.put(button, this);
			toggles[direction.ordinal()] = button;
		}
	}

	@Override
	protected Point getSize() {
		return horizontal ? new Point(150, 60) : new Point(60, 60 + SPACER + Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT);
	}

	private String getText() {
		return horizontal ? getLocalizedName() + ": " + localizeDirection(value) : getLocalizedName();
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		value = Direction.values()[button.id];
	}

	@Override
	public void updateGuiParts(Collection<Setting<?, ?>> settings) {
		super.updateGuiParts(settings);
		boolean enabled = enabled();

		for(GuiActionButton button : toggles) {
			button.glowing = value != null && button.id == value.ordinal();
			button.enabled = button.glowing || enabled && options.isValid(Direction.values()[button.id]);
		}
	}

	@Override
	public void draw() {
		String text = getText();

		if(horizontal) {
			GlUtil.drawString(text, bounds.withWidth(60 + SPACER).getAnchor(Direction.EAST), Direction.WEST, Color.WHITE);
		} else {
			GlUtil.drawString(text, bounds.getAnchor(Direction.NORTH), Direction.NORTH, Color.WHITE);
		}
	}

	@Override
	public String getStringValue() {
		return value != null ? value.name() : "null";
	}

	@Override
	public String getDefaultValue() {
		return "null";
	}

	@Override
	public void loadStringValue(String save) {
		try {
			set(Direction.valueOf(save));
		} catch(IllegalArgumentException e) {
			set(null);
		}
	}

	@Override
	public void loadDefaultValue() {
		value = options.apply(Direction.NORTH_WEST);
	}

	@Override
	public Direction get() {
		return value;
	}

	@Override
	public void set(Direction value) {
		value = options.apply(value);

		if(options.isValid(value)) {
			this.value = value;
		}
	}

	@Override
	protected boolean shouldBreak() {
		return horizontal || alignment == Direction.EAST;
	}

	public DirectionOptions getOptions() {
		return options;
	}

	public static String localizeDirection(Direction direction) {
		String name = "none";

		if (direction != null) {
			switch(direction) {
				case NORTH_WEST: name = "northWest"; break;
				case NORTH:      name = "north"; break;
				case NORTH_EAST: name = "northEast"; break;
				case WEST:       name = "west"; break;
				case CENTER:     name = "center"; break;
				case EAST:       name = "east"; break;
				case SOUTH_WEST: name = "southWest"; break;
				case SOUTH:      name = "south"; break;
				case SOUTH_EAST: name = "southEast"; break;
			}
		}
		return I18n.format("betterHud.value." + name);
	}
}
