package jobicade.betterhud.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.RenderMobInfoEvent;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

public class ArrowCount extends HudElement {
    private static final ItemStack ARROW = new ItemStack(Items.ARROW, 1);
    private SettingBoolean overlay;

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        overlay.set(true);
        position.setPreset(Direction.SOUTH_EAST);
        settings.priority.set(1);
    }

    public ArrowCount() {
        super("arrowCount", new SettingPosition(DirectionOptions.CORNERS, DirectionOptions.NONE));
        position.setEnableOn(() -> !overlay.get());
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(overlay = new SettingBoolean("overlay"));
    }

    /** Note this method only cares about arrows which can be shot by a vanilla bow
     * @return The number of arrows in the player's inventory */
    private int arrowCount(PlayerEntity player) {
        int count = 0;

        for(int i = 0; i < player.inventory.getContainerSize(); i++) {
            ItemStack stack = player.inventory.getItem(i);

            if(stack != null && stack.getItem() instanceof ArrowItem) {
                count += stack.getCount();
            }
        }
        return count;
    }

    @Override
    public boolean shouldRender(Event event) {
        if(!super.shouldRender(event)) return false;

        ItemStack stack = Minecraft.getInstance().player.getOffhandItem();
        boolean offhandHeld = stack != null && stack.getItem() == Items.BOW;

        if(overlay.get()) {
            if(HudElement.OFFHAND.isEnabledAndSupported() && offhandHeld) {
                return true;
            }

            if(HudElement.HOTBAR.isEnabledAndSupported()) {
                for(int i = 0; i < 9; i++) {
                    stack = Minecraft.getInstance().player.inventory.getItem(i);

                    if(stack != null && stack.getItem() == Items.BOW) {
                        return true;
                    }
                }
            }
            return false;
        } else if(offhandHeld) {
            return true;
        } else {
            stack = Minecraft.getInstance().player.getMainHandItem();
            return stack != null && stack.getItem() == Items.BOW;
        }
    }

    @Override
    public Rect render(Event event) {
        MatrixStack matrixStack = ((RenderMobInfoEvent)event).getMatrixStack();
        int totalArrows = arrowCount(Minecraft.getInstance().player);

        if(overlay.get()) {
            Rect stackRect = new Rect(16, 16).anchor(HOTBAR.getLastBounds().grow(-3), Direction.WEST);

            for(int i = 0; i < 9; i++) {
                ItemStack stack = Minecraft.getInstance().player.inventory.getItem(i);

                if(stack != null && stack.getItem() == Items.BOW) {
                    drawCounter(matrixStack, stackRect, totalArrows);
                }
                stackRect = stackRect.withX(stackRect.getX() + 20);
            }

            ItemStack stack = Minecraft.getInstance().player.inventory.getItem(40);

            if(stack != null && stack.getItem() == Items.BOW) {
                drawCounter(matrixStack, new Rect(OFFHAND.getLastBounds().getPosition().add(3, 3), new Point(16, 16)), totalArrows);
            }
            return Rect.empty();
        } else {
            Rect bounds = position.applyTo(new Rect(16, 16));

            GlUtil.renderSingleItem(ARROW, bounds.getPosition());
            drawCounter(matrixStack, bounds, totalArrows);

            return bounds;
        }
    }

    private static void drawCounter(MatrixStack matrixStack, Rect stackRect, int count) {
        String countDisplay = String.valueOf(count);

        Rect text = new Rect(GlUtil.getStringSize(countDisplay)).align(stackRect.grow(1, 1, 1, 2).getAnchor(Direction.NORTH_EAST), Direction.NORTH_EAST);

        GlUtil.drawString(matrixStack, countDisplay, text.getPosition(), Direction.NORTH_WEST, Color.WHITE);
    }
}
