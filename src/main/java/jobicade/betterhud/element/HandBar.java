package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;

public class HandBar extends EquipmentDisplay {
    private SettingPosition position;
    private SettingBoolean showItem, offHand, showBars, showNonTools;

    public HandBar() {
        super("handBar");

        position = new SettingPosition(DirectionOptions.BAR, DirectionOptions.NORTH_SOUTH);

        showItem = new SettingBoolean("showItem");
        showItem.setValuePrefix(SettingBoolean.VISIBLE);

        showBars = new SettingBoolean("bars");
        offHand = new SettingBoolean("offhand");
        showNonTools = new SettingBoolean("showNonTools");
        showNonTools.setValuePrefix("betterHud.value.nonTools");

        settings.addChildren(position, new Legend("misc"), showItem, showBars, offHand, showNonTools);
    }

    public void renderBar(ItemStack stack, int x, int y) {
        boolean isTool = stack.isItemStackDamageable();
        if(stack == null || !showNonTools.get() && !isTool) return;

        String text = getText(stack);

        int width = 0;
        if(showItem.get()) width += 21;

        if(text != null) {
            width += MC.fontRenderer.getStringWidth(text);
        }

        if(showItem.get()) {
            MC.mcProfiler.startSection("items");
            GlUtil.renderSingleItem(stack, x + 90 - width / 2, y);
            MC.mcProfiler.endSection();
        }

        if(text != null) {
            MC.mcProfiler.startSection("text");
            GlUtil.drawString(text, new Point(x + 90 - width / 2 + (showItem.get() ? 21 : 0), y + 4), Direction.NORTH_WEST, Color.WHITE);
            MC.mcProfiler.endSection();
        }

        if(isTool && showBars.get()) {
            MC.mcProfiler.startSection("bars");
            GlUtil.drawDamageBar(new Rect(x, y + 16, 180, 2), stack, false);
            MC.mcProfiler.endSection();
        }
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect bounds = position.applyTo(new Rect(180, offHand.get() ? 41 : 18));
        renderBar(MC.player.getHeldItemMainhand(), bounds.getX(), bounds.getBottom() - 18);

        if(offHand.get()) {
            renderBar(MC.player.getHeldItemOffhand(), bounds.getX(), bounds.getY());
        }
        return bounds;
    }
}
