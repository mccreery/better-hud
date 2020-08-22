package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.events.OverlayHook;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class ArrowCount extends OverlayElement {
    private static final ItemStack ARROW = new ItemStack(Items.ARROW, 1);

    private SettingPosition position;
    private SettingBoolean overlay;

    public ArrowCount() {
        super("arrowCount");

        position = new SettingPosition("position");
        position.setDirectionOptions(DirectionOptions.CORNERS);
        position.setContentOptions(DirectionOptions.NONE);
        addSetting(position);

        overlay = new SettingBoolean("overlay");
        addSetting(overlay);

        position.setEnableOn(() -> !overlay.get());
    }

    /** Note this method only cares about arrows which can be shot by a vanilla bow
     * @return The number of arrows in the player's inventory
     * @see net.minecraft.item.ItemBow#isArrow(ItemStack) */
    private int arrowCount(PlayerEntity player) {
        int count = 0;

        for(int i = 0; i < player.inventory.getSizeInventory(); i++) {
            ItemStack stack = player.inventory.getStackInSlot(i);

            if(stack != null && stack.getItem() instanceof ArrowItem) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        ItemStack stack = MC.player.getHeldItemOffhand();
        boolean offhandHeld = stack != null && stack.getItem() == Items.BOW;

        if(overlay.get()) {
            if (OverlayHook.shouldRender(OverlayElements.OFFHAND, context) && offhandHeld) {
                return true;
            }

            if (OverlayHook.shouldRender(OverlayElements.HOTBAR, context)) {
                for(int i = 0; i < 9; i++) {
                    stack = MC.player.inventory.getStackInSlot(i);

                    if(stack != null && stack.getItem() == Items.BOW) {
                        return true;
                    }
                }
            }
            return false;
        } else if(offhandHeld) {
            return true;
        } else {
            stack = MC.player.getHeldItemMainhand();
            return stack != null && stack.getItem() == Items.BOW;
        }
    }

    @Override
    public Rect render(OverlayContext context) {
        int totalArrows = arrowCount(MC.player);

        if(overlay.get()) {
            Rect stackRect = new Rect(16, 16).anchor(OverlayElements.HOTBAR.getLastBounds().grow(-3), Direction.WEST);

            for(int i = 0; i < 9; i++) {
                ItemStack stack = MC.player.inventory.getStackInSlot(i);

                if(stack != null && stack.getItem() == Items.BOW) {
                    drawCounter(stackRect, totalArrows);
                }
                stackRect = stackRect.withX(stackRect.getX() + 20);
            }

            ItemStack stack = MC.player.inventory.getStackInSlot(40);

            if(stack != null && stack.getItem() == Items.BOW) {
                drawCounter(new Rect(OverlayElements.OFFHAND.getLastBounds().getPosition().add(3, 3), new Point(16, 16)), totalArrows);
            }
            return Rect.empty();
        } else {
            Rect bounds = position.applyTo(new Rect(16, 16));

            GlUtil.renderSingleItem(ARROW, bounds.getPosition());
            drawCounter(bounds, totalArrows);

            return bounds;
        }
    }

    private static void drawCounter(Rect stackRect, int count) {
        String countDisplay = String.valueOf(count);

        Rect text = new Rect(GlUtil.getStringSize(countDisplay)).align(stackRect.grow(1, 1, 1, 2).getAnchor(Direction.NORTH_EAST), Direction.NORTH_EAST);

        GlUtil.drawString(countDisplay, text.getPosition(), Direction.NORTH_WEST, Color.WHITE);
    }
}
