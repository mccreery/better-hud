package jobicade.betterhud.element.settings;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.Collection;
import java.util.function.BooleanSupplier;

import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.GuiElementSettings;

public class SettingPosition extends Setting {
    private boolean edge = false;
    private int postSpacer = SPACER;

    private final SettingChoose mode;

    private final SettingDirection direction;
    private final SettingAbsolutePosition offset;

    private final SettingElement parent;
    private final SettingDirection anchor, alignment, contentAlignment;
    private final SettingLock lockAlignment, lockContent;

    public DirectionOptions getDirectionOptions() {
        return direction.getOptions();
    }

    public DirectionOptions getContentOptions() {
        return contentAlignment.getOptions();
    }

    public SettingPosition(DirectionOptions directionOptions, DirectionOptions contentOptions) {
        this("position", directionOptions, contentOptions);
    }

    public SettingPosition(String name) {
        this(name, DirectionOptions.ALL, DirectionOptions.ALL);
    }

    public SettingPosition(String name, DirectionOptions directionOptions, DirectionOptions contentOptions) {
        super(name);

        mode = new SettingChoose("position", "preset", "custom");
        BooleanSupplier isPreset = () -> mode.getIndex() == 0;
        BooleanSupplier isCustom = () -> mode.getIndex() == 1;

        direction = new SettingDirection("direction", Direction.WEST, directionOptions);
        direction.setEnableOn(isPreset);
        direction.setHorizontal();

        parent = new SettingElement("parent", Direction.CENTER);
        parent.setEnableOn(isCustom);
        anchor = new SettingDirection("anchor", Direction.WEST);
        anchor.setEnableOn(isCustom);

        alignment = new SettingDirection("alignment", Direction.CENTER) {
            @Override
            public void updateGuiParts(Collection<Setting> settings) {
                if(lockAlignment.get()) set(anchor.get());
                super.updateGuiParts(settings);
            }
        };

        contentAlignment = new SettingDirection("contentAlignment", Direction.EAST, contentOptions) {
            @Override
            public void updateGuiParts(Collection<Setting> settings) {
                if(lockContent.get()) set(SettingPosition.this.alignment.get());
                super.updateGuiParts(settings);
            }
        };

        lockAlignment = new SettingLock("lockAlignment");
        lockAlignment.setEnableOn(isCustom);
        lockContent = new SettingLock("lockContent");
        lockContent.setEnableOn(isCustom);
        offset = new SettingAbsolutePosition("origin", this);
        offset.setEnableOn(isCustom);

        alignment.setEnableOn(() -> isCustom.getAsBoolean() && !lockAlignment.get());
        contentAlignment.setEnableOn(() -> isCustom.getAsBoolean() && !lockContent.get());

        addChildren(
            new Legend("position"),
            mode, direction, parent, anchor, alignment, contentAlignment,
            lockAlignment, lockContent, offset
        );
    }

    public boolean isDirection(Direction direction) {
        return !isCustom() && this.direction.get() == direction;
    }

    public boolean isCustom() {
        return mode.getIndex() == 1;
    }

    public Direction getDirection() {
        if(isCustom()) throw new IllegalStateException("Position is not preset");
        return direction.get();
    }

    public Rect getParent() {
        if(!isCustom()) throw new IllegalStateException("Position is not custom");

        if(parent.get() != null) {
            Rect bounds = parent.get().getLastBounds();
            if(!bounds.isEmpty()) return bounds;
        }
        return MANAGER.getScreen();
    }

    public Point getOffset() {
        if(!isCustom()) throw new IllegalStateException("Position is not custom");
        return offset.get();
    }

    public void setOffset(Point offset) {
        this.offset.set(offset);
    }

    public Direction getAnchor() {
        if(!isCustom()) throw new IllegalStateException("Position is not custom");
        return anchor.get();
    }

    public Direction getAlignment() {
        if(!isCustom()) throw new IllegalStateException("Position is not custom");
        return alignment.get();
    }

    public Direction getContentAlignment() {
        return isCustom() ? contentAlignment.get() : contentAlignment.getOptions().apply(direction.get());
    }

    public void setEdge(boolean edge) {
        this.edge = edge;
    }

    public void setPostSpacer(int postSpacer) {
        this.postSpacer = postSpacer;
    }

    /** Moves the given bounds to the correct location and returns them */
    public Rect applyTo(Rect bounds) {
        if(isCustom()) {
            return bounds.align(getParent().getAnchor(anchor.get()).add(offset.get()), alignment.get());
        } else {
            return MANAGER.position(direction.get(), bounds, edge, postSpacer);
        }
    }

    public void setPreset(Direction direction) {
        mode.setIndex(0);
        this.direction.set(direction);

        // Reset custom
        offset.set(Point.zero());
        anchor.set(Direction.NORTH_WEST);
        alignment.set(Direction.NORTH_WEST);
        contentAlignment.set(Direction.NORTH_WEST);

        lockAlignment.set(true);
        lockContent.set(true);
    }

    public void setCustom(Direction anchor, Direction alignment, Direction contentAlignment, Point offset, boolean lockAlignment, boolean lockContent) {
        // Reset preset
        mode.setIndex(1);
        direction.set(Direction.NORTH_WEST);

        this.anchor.set(anchor);
        this.alignment.set(alignment);
        this.contentAlignment.set(contentAlignment);
        this.offset.set(offset);

        this.lockAlignment.set(lockAlignment);
        this.lockContent.set(lockContent);
    }

    @Override
    public Point getGuiParts(GuiElementSettings.Populator populator, Point origin) {
        Point lockOffset = new Point(30 + SPACER, 173);

        lockAlignment.setRect(new Rect(20, 10).align(origin.add(lockOffset.withX(-lockOffset.getX())), Direction.EAST));
        lockContent.setRect(new Rect(20, 10).align(origin.add(lockOffset), Direction.WEST));

        return super.getGuiParts(populator, origin);
    }
}
