package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import com.mojang.blaze3d.systems.RenderSystem;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingDirection;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class Compass extends OverlayElement {
    private static final String[] DIRECTIONS = { "S", "E", "N", "W" };

    private SettingPosition position;
    private SettingChoose mode, requireItem;
    private SettingSlider directionScaling;
    private SettingBoolean showNotches;

    private static final int[] notchX = new int[9];

    static {
        int x = 0;

        for(double i = 0.1; i <= 0.9; i += 0.1, x++) {
            notchX[x] = (int) (Math.asin(i) / Math.PI * 180);
        }
    }

    public Compass() {
        super("compass");

        position = new SettingPosition(this, "position");
        position.setDirectionOptions(DirectionOptions.TOP_BOTTOM);
        position.setContentOptions(DirectionOptions.NORTH_SOUTH);

        mode = new SettingChoose(this, "mode", "visual", "text");

        new Legend(this, "misc");
        directionScaling = new SettingSlider(this, "letterScale", 0, 1);
        directionScaling.setDisplayPercent();

        showNotches = new SettingBoolean(this, "showNotches");
        showNotches.setValuePrefix(SettingBoolean.VISIBLE);

        requireItem = new SettingChoose(this, "requireItem", "disabled", "inventory", "hand");
    }

    private void drawBackground(Rect bounds) {
        GlUtil.drawRect(bounds, new Color(170, 0, 0, 0));
        GlUtil.drawRect(bounds.grow(-50, 0, -50, 0), new Color(85, 85, 85, 85));

        Direction alignment = position.getContentAlignment();

        Rect smallRect = bounds.grow(2);
        Rect largeNotch = new Rect(1, 7);

        Rect smallNotch = new Rect(1, 6);
        Rect largeRect = bounds.grow(0, 3, 0, 3);

        if(showNotches.get()) {
            for(int loc : notchX) {
                Rect notchTemp = smallNotch.anchor(smallRect, alignment);
                GlUtil.drawRect(notchTemp.translate(loc, 0), Color.WHITE);
                GlUtil.drawRect(notchTemp.translate(-loc, 0), Color.WHITE);
            }
        }

        GlUtil.drawRect(largeNotch.anchor(largeRect, alignment.withCol(0)), Color.RED);
        GlUtil.drawRect(largeNotch.anchor(largeRect, alignment.withCol(1)), Color.RED);
        GlUtil.drawRect(largeNotch.anchor(largeRect, alignment.withCol(2)), Color.RED);
    }

    private void drawDirections(Rect bounds) {
        float angle = (float)Math.toRadians(MC.player.rotationYaw);

        float radius = bounds.getWidth() / 2 + SPACER;
        boolean bottom = position.getContentAlignment() == Direction.SOUTH;

        Point origin = bounds.grow(-2).getAnchor(position.getContentAlignment());

        for(int i = 0; i < 4; i++, angle += Math.PI / 2) {
            double cos = Math.cos(angle);

            Point letter = origin.add(-(int)(Math.sin(angle) * radius), 0);
            double scale = 1 + directionScaling.getValue() * cos * 2;

            RenderSystem.pushMatrix();

            RenderSystem.translatef(letter.getX(), letter.getY(), 0);
            GlUtil.scale((float)scale);

            Color color = i == 0 ? Color.BLUE : i == 2 ? Color.RED : Color.WHITE;
            color = color.withAlpha((int)(((cos + 1) / 2) * 255));

            // Super low alphas can render opaque for some reason
            if(color.getAlpha() > 3) {
                GlUtil.drawString(DIRECTIONS[i], Point.zero(), bottom ? Direction.SOUTH : Direction.NORTH, color);
            }

            RenderSystem.popMatrix();
        }
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        switch(requireItem.getIndex()) {
            case 1:
                return MC.player.inventory.hasItemStack(new ItemStack(Items.COMPASS));
            case 2:
                return MC.player.getHeldItemMainhand().getItem() == Items.COMPASS
                    || MC.player.getHeldItemOffhand().getItem() == Items.COMPASS;
        }
        return true;
    }

    public String getText() {
        net.minecraft.util.Direction enumfacing = MC.player.getHorizontalFacing();

        String coord;
        Direction direction;

        switch(enumfacing) {
            case NORTH: coord = "-Z"; direction = Direction.NORTH; break;
            case SOUTH: coord = "+Z"; direction = Direction.SOUTH; break;
            case WEST: coord = "-X"; direction = Direction.WEST; break;
            case EAST: coord = "+X"; direction = Direction.EAST; break;
            default: return "?";
        }
        return I18n.format("betterHud.hud.facing", SettingDirection.localizeDirection(direction), coord);
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect bounds;

        if(mode.getIndex() == 0) {
            bounds = position.applyTo(new Rect(180, 12));

            MC.getProfiler().startSection("background");
            drawBackground(bounds);
            MC.getProfiler().endStartSection("text");
            drawDirections(bounds);
            MC.getProfiler().endSection();
        } else {
            String text = getText();
            bounds = position.applyTo(new Rect(GlUtil.getStringSize(text)));

            MC.getProfiler().startSection("text");
            GlUtil.drawString(text, bounds.getPosition(), Direction.NORTH_WEST, Color.WHITE);
            MC.getProfiler().endSection();
        }

        return bounds;
    }
}
