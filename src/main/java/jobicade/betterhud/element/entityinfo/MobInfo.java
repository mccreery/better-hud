package jobicade.betterhud.element.entityinfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.RenderMobInfoEvent;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.bars.StatBarHealth;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.SPACER;

public class MobInfo extends EntityInfo {
    private final StatBarHealth bar = new StatBarHealth();
    private SettingSlider compress;

    public MobInfo() {
        super("mobInfo");
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(compress = new SettingSlider("compress", 0, 200, 20) {
            @Override
            public String getDisplayValue(double value) {
                if(value == 0) {
                    return I18n.get("betterHud.value.never");
                } else {
                    return super.getDisplayValue(value);
                }
            }
        });
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        compress.set(40.0);
        settings.priority.set(-1);
    }

    @Override
    public Rect render(Event event) {
        MatrixStack matrixStack = ((RenderMobInfoEvent)event).getMatrixStack();
        LivingEntity entity = ((RenderMobInfoEvent)event).getEntity();
        bar.setHost(entity);
        bar.setCompressThreshold(compress.getInt());

        int health = MathUtil.getHealthForDisplay(entity.getHealth());
        int maxHealth = MathUtil.getHealthForDisplay(entity.getMaxHealth());

        String text = String.format("%s %s(%d/%d)", entity.getName(), TextFormatting.GRAY, health, maxHealth);

        Point size = GlUtil.getStringSize(text);
        Point barSize = bar.getPreferredSize();

        if(barSize.getX() > size.getX()) {
            size = new Point(barSize.getX(), size.getY() + barSize.getY());
        } else {
            size = size.add(0, barSize.getY());
        }

        Rect bounds = MANAGER.position(Direction.SOUTH, new Rect(size).grow(SPACER));
        GlUtil.drawRect(bounds, Color.TRANSLUCENT);
        bounds = bounds.grow(-SPACER);

        GlUtil.drawString(matrixStack, text, bounds.getPosition(), Direction.NORTH_WEST, Color.WHITE);
        Rect barRect = new Rect(barSize).anchor(bounds, Direction.SOUTH_WEST);

        Minecraft.getInstance().getTextureManager().bind(AbstractGui.GUI_ICONS_LOCATION);
        bar.setMatrixStack(matrixStack);
        bar.setBounds(barRect).render();
        return null;
    }
}
