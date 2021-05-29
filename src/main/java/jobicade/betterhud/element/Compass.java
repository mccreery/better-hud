package jobicade.betterhud.element;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.element.settings.SettingDirection;
import jobicade.betterhud.element.settings.SettingPercentage;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

import static jobicade.betterhud.BetterHud.SPACER;

public class Compass extends HudElement {
    private static final String[] DIRECTIONS = { "S", "E", "N", "W" };

    private SettingChoose mode, requireItem;
    private SettingSlider directionScaling;
    private SettingBoolean showNotches;

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        position.setPreset(Direction.NORTH);
        directionScaling.set(0.5);
        showNotches.set(true);
        requireItem.setIndex(0);
        settings.priority.set(-3);
    }

    private static final int[] notchX = new int[9];

    static {
        int x = 0;

        for(double i = 0.1; i <= 0.9; i += 0.1, x++) {
            notchX[x] = (int) (Math.asin(i) / Math.PI * 180);
        }
    }

    public Compass() {
        super("compass", new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NORTH_SOUTH));
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(mode = new SettingChoose("mode", "visual", "text"));
        settings.add(new Legend("misc"));
        settings.add(directionScaling = new SettingPercentage("letterScale"));
        settings.add(showNotches = new SettingBoolean("showNotches").setValuePrefix(SettingBoolean.VISIBLE));
        settings.add(requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand"));
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

    private void drawDirections(MatrixStack matrixStack, Rect bounds) {
        float angle = (float)Math.toRadians(Minecraft.getInstance().player.yRot);

        float radius = bounds.getWidth() / 2 + SPACER;
        boolean bottom = position.getContentAlignment() == Direction.SOUTH;

        Point origin = bounds.grow(-2).getAnchor(position.getContentAlignment());

        for(int i = 0; i < 4; i++, angle += Math.PI / 2) {
            double cos = Math.cos(angle);

            Point letter = origin.add(-(int)(Math.sin(angle) * radius), 0);
            double scale = 1 + directionScaling.get() * cos * 2;

            matrixStack.pushPose();

            matrixStack.translate(letter.getX(), letter.getY(), 0);
            GlUtil.scale(matrixStack, (float)scale);

            Color color = i == 0 ? Color.BLUE : i == 2 ? Color.RED : Color.WHITE;
            color = color.withAlpha((int)(((cos + 1) / 2) * 255));

            // Super low alphas can render opaque for some reason
            if(color.getAlpha() > 3) {
                GlUtil.drawString(DIRECTIONS[i], Point.zero(), bottom ? Direction.SOUTH : Direction.NORTH, color);
            }

            matrixStack.popPose();
        }
    }

    @Override
    public boolean shouldRender(Event event) {
        if(!super.shouldRender(event)) return false;

        switch(requireItem.getIndex()) {
            case 1:
                return Minecraft.getInstance().player.inventory.contains(new ItemStack(Items.COMPASS));
            case 2:
                return Minecraft.getInstance().player.getMainHandItem().getItem() == Items.COMPASS
                    || Minecraft.getInstance().player.getOffhandItem().getItem() == Items.COMPASS;
        }
        return true;
    }

    public String getText() {
        net.minecraft.util.Direction enumfacing = Minecraft.getInstance().player.getDirection();

        String coord;
        Direction direction;

        switch(enumfacing) {
            case NORTH: coord = "-Z"; direction = Direction.NORTH; break;
            case SOUTH: coord = "+Z"; direction = Direction.SOUTH; break;
            case WEST: coord = "-X"; direction = Direction.WEST; break;
            case EAST: coord = "+X"; direction = Direction.EAST; break;
            default: return "?";
        }
        return I18n.get("betterHud.hud.facing", SettingDirection.localizeDirection(direction), coord);
    }

    @Override
    public Rect render(Event event) {
        Rect bounds;

        if(mode.getIndex() == 0) {
            MatrixStack matrixStack = ((RenderGameOverlayEvent)event).getMatrixStack();
            bounds = position.applyTo(new Rect(180, 12));

            Minecraft.getInstance().profiler.push("background");
            drawBackground(bounds);
            Minecraft.getInstance().profiler.func_76318_c("text");
            drawDirections(matrixStack, bounds);
            Minecraft.getInstance().profiler.pop();
        } else {
            String text = getText();
            bounds = position.applyTo(new Rect(GlUtil.getStringSize(text)));

            Minecraft.getInstance().profiler.push("text");
            GlUtil.drawString(text, bounds.getPosition(), Direction.NORTH_WEST, Color.WHITE);
            Minecraft.getInstance().profiler.pop();
        }

        return bounds;
    }
}
