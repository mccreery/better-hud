package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.Collection;
import java.util.Map;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import tk.nukeduck.hud.gui.GuiElementSettings;
import tk.nukeduck.hud.gui.GuiToggleButton;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;

public class SettingDirection extends Setting<Direction> {
	private GuiToggleButton[] toggles = new GuiToggleButton[9];
	private Bounds bounds;

	public SettingDirection(String name) {
		this(name, Direction.ALL);
	}

	public SettingDirection(String name, Direction... options) {
		this(name, Direction.getFlags(options));
	}

	public SettingDirection(String name, int options) {
		super(name);
		this.options = options;
	}

	private final int options;
	private Direction value = Direction.NORTH_WEST;

	public boolean isValid(Direction direction) {
		return direction.in(options);
	}

	@Override
	public int getGuiParts(java.util.List<Gui> parts, Map<Gui,Setting<?>> callbacks, int width, int y) {
		bounds = new Bounds(width / 2 - 100, y, 64, -2);
		Bounds radio = new Bounds(0, 0, 20, 20);

		for(int row = 0; row < 3; row++) {
			if((options & Direction.getRowFlags(row)) != 0) {
				bounds.bottom(bounds.bottom() + 22);

				for(int col = 0; col < 3; col++) {
					Direction direction = Direction.get(row, col);
					direction.toRow(2).anchor(radio, bounds);

					GuiToggleButton button = (GuiToggleButton)new GuiToggleButton("").setStaticText().setId(direction.ordinal()).setBounds(radio);
					parts.add(button);
					callbacks.put(button, this);

					toggles[direction.ordinal()] = button;
				}
			}
		}
		return bounds.height() == -2 ? -1 : bounds.bottom() + SPACER;
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		((GuiToggleButton)button).set(true);
		value = Direction.get(button.id);
	}

	@Override
	public void updateGuiParts(Collection<Setting<?>> settings) {
		boolean enabled = enabled();

		for(GuiToggleButton button : toggles) {
			if(button != null) {
				button.enabled = enabled && isValid(Direction.values()[button.id]);
				button.set(button.id == value.ordinal());
			}
		}
	}

	@Override
	public void draw() {
		if(options != 0) {
			final String text = getLocalizedName() + ": " + value.getLocalizedName();
			GlUtil.drawString(text, Direction.EAST.getAnchor(bounds).add(SPACER, 0), Direction.WEST, Colors.WHITE);
		}
	}

	@Override
	public String save() {
		return get().name;
	}

	@Override
	public void load(String save) {
		set(Direction.get(save));
	}

	@Override
	public Direction get() {
		return value;
	}

	@Override
	public void set(Direction value) {
		if(isValid(value)) {
			this.value = value;
		}
	}
}
