package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.bars.StatBar;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

import static jobicade.betterhud.BetterHud.MANAGER;

public abstract class Bar extends OverrideElement {
    protected SettingChoose side;

    private StatBar<? super ClientPlayerEntity> bar;

    public Bar(String name, StatBar<? super ClientPlayerEntity> bar) {
        super(name, new SettingPosition(DirectionOptions.BAR, DirectionOptions.CORNERS));
        this.bar = bar;
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(side = new SettingChoose("side", "west", "east") {
            @Override
            public boolean enabled() {
                return position.isDirection(Direction.SOUTH);
            }
        });
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        position.setPreset(Direction.SOUTH);
    }

    @Override
    public boolean shouldRender(Event event) {
        bar.setHost(Minecraft.getInstance().player);
        return super.shouldRender(event) && bar.shouldRender();
    }

    /** @return {@link Direction#WEST} or {@link Direction#EAST} */
    protected Direction getContentAlignment() {
        if(position.isDirection(Direction.SOUTH)) {
            return side.getIndex() == 1 ? Direction.SOUTH_EAST : Direction.SOUTH_WEST;
        } else {
            return position.getContentAlignment();
        }
    }

    @Override
    protected Rect render(Event event) {
        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        Direction contentAlignment = getContentAlignment();

        Rect bounds = new Rect(bar.getPreferredSize());

        if(position.isDirection(Direction.SOUTH)) {
            bounds = MANAGER.positionBar(bounds, contentAlignment.withRow(1), 1);
        } else {
            bounds = position.applyTo(bounds);
        }

        bar.setContentAlignment(contentAlignment).setBounds(bounds).render();
        return bounds;
    }
}
