package tk.nukeduck.hud.element.settings;

import static tk.nukeduck.hud.BetterHud.MC;
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
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.Direction.Options;

public class SettingDirection extends SettingAlignable<Direction> {
	private GuiToggleButton[] toggles = new GuiToggleButton[9];
	private Bounds bounds;

	private boolean horizontal = false;

	private final Options options;
	private Direction value;

	public SettingDirection(String name, Direction alignment) {
		this(name, alignment, Options.ALL);
	}

	public SettingDirection(String name, Direction alignment, Options options) {
		super(name, alignment);
		this.options = options;
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
	public SettingAlignable<Direction> setAlignment(Direction alignment) {
		if(!horizontal) {
			return super.setAlignment(alignment);
		} else {
			return this;
		}
	}

	@Override
	public void getGuiParts(java.util.List<Gui> parts, Map<Gui,Setting<?>> callbacks, Bounds bounds) {
		this.bounds = bounds;

		Bounds radios = (horizontal ? Direction.WEST : Direction.SOUTH).anchor(new Bounds(60, 60), bounds);
		Bounds radio = new Bounds(20, 20);

		for(Direction direction : Direction.values()) {
			GuiToggleButton button = (GuiToggleButton)new GuiToggleButton("")
				.setStaticText().setId(direction.ordinal())
				.setBounds(direction.anchor(radio, radios));

			parts.add(button);
			callbacks.put(button, this);
			toggles[direction.ordinal()] = button;
		}
	}

	@Override
	protected Point getSize() {
		return horizontal ? new Point(150, 60) : new Point(60, 60 + SPACER + MC.fontRenderer.FONT_HEIGHT);
	}

	private String getText() {
		return horizontal ? getLocalizedName() + ": " + value.getLocalizedName() : getLocalizedName();
	}

	@Override
	public void actionPerformed(GuiElementSettings gui, GuiButton button) {
		value = Direction.values()[button.id];
	}

	@Override
	public void updateGuiParts(Collection<Setting<?>> settings) {
		super.updateGuiParts(settings);
		boolean enabled = enabled();

		for(GuiToggleButton button : toggles) {
			button.set(button.id == value.ordinal());
			button.enabled = button.get() || enabled && options.isValid(Direction.values()[button.id]);
		}
	}

	@Override
	public void draw() {
		String text = getText();

		if(horizontal) {
			GlUtil.drawString(text, Direction.EAST.getAnchor(bounds.withWidth(60 + SPACER)), Direction.WEST, Colors.WHITE);
		} else {
			GlUtil.drawString(text, Direction.NORTH.getAnchor(bounds), Direction.NORTH, Colors.WHITE);
		}
	}

	@Override
	public String save() {
		return Direction.toString(get());
	}

	@Override
	public void load(String save) {
		set(Direction.fromString(save));
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

	public Options getOptions() {
		return options;
	}
}
