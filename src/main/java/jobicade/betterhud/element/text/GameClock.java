package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MC;

import java.text.DateFormat;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.util.Date;
import java.util.TimeZone;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingChoose;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraftforge.common.extensions.IForgeDimension.SleepResult;

public class GameClock extends Clock {
    private static final ItemStack BED = new ItemStack(Items.RED_BED);

    private SettingBoolean showDays;
    private SettingBoolean showSleepIndicator;
    private SettingChoose requireItem;

    public GameClock() {
        super("gameClock");

        showDays = new SettingBoolean("showDays");
        showDays.setValuePrefix(SettingBoolean.VISIBLE);

        showSleepIndicator = new SettingBoolean("showSleepIndicator");
        showSleepIndicator.setValuePrefix(SettingBoolean.VISIBLE);

        requireItem = new SettingChoose("requireItem", "disabled", "inventory", "hand");

        settings.addChildren(showDays, showSleepIndicator, requireItem);
    }

    @Override
    protected Rect getMargin() {
        return new Rect(0, 0, 21, 0).align(Point.zero(), position.getContentAlignment());
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        switch(requireItem.getIndex()) {
            case 1:
                return MC.player.inventory.hasItemStack(new ItemStack(Items.CLOCK));
            case 2:
                return MC.player.getHeldItemMainhand().getItem() == Items.CLOCK
                    || MC.player.getHeldItemOffhand().getItem() == Items.CLOCK;
        }
        return true;
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect bounds = super.render(context);

        if(showSleepIndicator(context.getPartialTicks())) {
            Direction bedAnchor = DirectionOptions.WEST_EAST.apply(position.getContentAlignment().mirrorCol());
            Rect bed = new Rect(16, 16).anchor(bounds, bedAnchor);

            GlUtil.renderSingleItem(BED, bed.getPosition());
        }
        return bounds;
    }

    private boolean showSleepIndicator(float partialTicks) {
        return showSleepIndicator.get()
                && MC.world.dimension.canSleepAt(MC.player, MC.player.getPosition()) == SleepResult.ALLOW
                // Taken from EntityPlayer#trySleep, ignores enemies and bed position
                && !MC.player.isSleeping()
                && MC.player.isAlive()
                && MC.world.dimension.isSurfaceWorld()
                // World#isDayTime is server only
                //&& !MC.world.isDaytime();
                && MC.world.getSkylightSubtracted() >= 4;
    }

    @Override
    protected Date getDate() {
        long worldTime = MC.world.getDayTime() + 6000;

        // Convert to milliseconds
        worldTime = Math.round(worldTime / 1000. * 3600.) * 1000;

        return new Date(worldTime);
    }

    /* Game time is not localized, so we have to use UTC instead of
     * the local timezone while formatting */
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    @Override
    protected DateFormat getTimeFormat() {
        DateFormat format = super.getTimeFormat();
        format.setTimeZone(UTC);

        return format;
    }

    @SuppressWarnings("serial")
    @Override
    protected DateFormat getDateFormat() {
        if(showDays.get()) {
            return new DateFormat() {
                @Override
                public StringBuffer format(Date date, StringBuffer buffer, FieldPosition fieldPosition) {
                    long day = date.getTime() / 84600000 + 1;

                    buffer.append(I18n.format("betterHud.hud.day", day));
                    return buffer;
                }

                @Override
                public Date parse(String source, ParsePosition pos) {
                    throw new UnsupportedOperationException();
                }
            };
        } else {
            DateFormat format = super.getDateFormat();
            format.setTimeZone(UTC);

            return format;
        }
    }
}
