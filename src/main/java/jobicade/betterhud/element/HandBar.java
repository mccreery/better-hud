package jobicade.betterhud.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class HandBar extends EquipmentDisplay {
    private SettingBoolean showItem, offHand, showBars, showNonTools;

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        position.setPreset(Direction.SOUTH);
        showItem.set(true);
        showBars.set(true);
        offHand.set(false);
        settings.priority.set(100);
    }

    public HandBar() {
        super("handBar", new SettingPosition(DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH));
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(new Legend("misc"));
        settings.add(showItem = new SettingBoolean("showItem").setValuePrefix(SettingBoolean.VISIBLE));
        settings.add(showBars = new SettingBoolean("bars"));
        settings.add(offHand = new SettingBoolean("offhand"));
        settings.add(showNonTools = new SettingBoolean("showNonTools").setValuePrefix("betterHud.value.nonTools"));
    }

    public void renderBar(MatrixStack matrixStack, ItemStack stack, int x, int y) {
        boolean isTool = stack.isDamageableItem();
        if(stack == null || !showNonTools.get() && !isTool) return;

        String text = getText(stack);

        int width = 0;
        if(showItem.get()) width += 21;

        if(text != null) {
            width += Minecraft.getInstance().font.width(text);
        }

        if(showItem.get()) {
            Minecraft.getInstance().getProfiler().push("items");
            GlUtil.renderSingleItem(stack, x + 90 - width / 2, y);
            Minecraft.getInstance().getProfiler().pop();
        }

        if(text != null) {
            Minecraft.getInstance().getProfiler().push("text");
            GlUtil.drawString(matrixStack, text, new Point(x + 90 - width / 2 + (showItem.get() ? 21 : 0), y + 4), Direction.NORTH_WEST, Color.WHITE);
            Minecraft.getInstance().getProfiler().pop();
        }

        if(isTool && showBars.get()) {
            Minecraft.getInstance().getProfiler().push("bars");
            GlUtil.drawDamageBar(new Rect(x, y + 16, 180, 2), stack, false);
            Minecraft.getInstance().getProfiler().pop();
        }
    }

    @Override
    public Rect render(Event event) {
        MatrixStack matrixStack = ((RenderGameOverlayEvent)event).getMatrixStack();
        Rect bounds = position.applyTo(new Rect(180, offHand.get() ? 41 : 18));
        renderBar(matrixStack, Minecraft.getInstance().player.getMainHandItem(), bounds.getX(), bounds.getBottom() - 18);

        if(offHand.get()) {
            renderBar(matrixStack, Minecraft.getInstance().player.getOffhandItem(), bounds.getX(), bounds.getY());
        }
        return bounds;
    }
}
