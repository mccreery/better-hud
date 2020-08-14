package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;
import jobicade.betterhud.gui.SuperButton;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.resources.I18n;

public class SettingDirection extends SettingAlignable {
    private SuperButton[] toggles = new SuperButton[9];
    private Rect bounds;

    private boolean horizontal = false;

    private DirectionOptions options = DirectionOptions.ALL;
    private Direction value;

    public SettingDirection(HudElement<?> element, String name) {
        super(element, name);
    }

    public SettingDirection(Setting parent, String name) {
        super(parent, name);
    }

    public void setOptions(DirectionOptions options) {
        this.options = options;
    }

    public void setHorizontal() {
        horizontal = true;
        setAlignment(Direction.WEST);
    }

    @Override
    protected int getAlignmentWidth() {
        return horizontal ? 150 : 240;
    }

    @Override
    public void setAlignment(Direction alignment) {
        if (!horizontal) {
            super.setAlignment(alignment);
        }
    }

    @Override
    public void getGuiParts(GuiElementSettings.Populator populator, Rect bounds) {
        this.bounds = bounds;

        Rect radios = new Rect(60, 60).anchor(bounds, horizontal ? Direction.WEST : Direction.SOUTH);
        Rect radio = new Rect(20, 20);

        for(Direction direction : Direction.values()) {
            SuperButton button = populator.add(new SuperButton(b -> value = direction));
            button.setBounds(radio.anchor(radios, direction));

            toggles[direction.ordinal()] = button;
        }
    }

    @Override
    protected Point getSize() {
        return horizontal ? new Point(150, 60) : new Point(60, 60 + SPACER + MC.fontRenderer.FONT_HEIGHT);
    }

    private String getText() {
        return horizontal ? getLocalizedName() + ": " + localizeDirection(value) : getLocalizedName();
    }

    @Override
    public void updateGuiParts() {
        super.updateGuiParts();
        boolean enabled = enabled();

        for (int i = 0; i < toggles.length; i++) {
            SuperButton button = toggles[i];

            boolean forceHovered = value != null && i == value.ordinal();
            button.setForceHovered(forceHovered);
            button.active = forceHovered || enabled && options.isValid(Direction.values()[i]);
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
    public boolean hasValue() {
        return true;
    }

    @Override
    public String getStringValue() {
        return value != null ? value.name() : "null";
    }

    @Override
    public void loadStringValue(String save) {
        try {
            set(Direction.valueOf(save));
        } catch(IllegalArgumentException e) {
            set(null);
        }
    }

    public Direction get() {
        return value;
    }

    public void set(Direction value) {
        this.value = options.apply(value);
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
