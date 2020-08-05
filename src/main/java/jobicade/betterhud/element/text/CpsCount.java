package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.Tickable;
import net.minecraft.client.resources.I18n;

public class CpsCount extends TextElement implements Tickable {
    private SettingSlider timeoutMax;
    private SettingBoolean showBurst, remember;

    private int[] clickHistory = new int[10];
    private int i = 0;

    private int windowTotal = 0;
    private int burstTotal = 0;
    private int burstLength = 0;
    private int timeout = 0;

    private float cps = 0;

    public CpsCount() {
        super("cps");

        timeoutMax = new SettingSlider("timeout", 1, 10, 1);
        timeoutMax.setUnlocalizedValue("betterHud.hud.seconds");

        showBurst = new SettingBoolean("showBurst");
        remember = new SettingBoolean("remember");

        settings.addChildren(new Legend("misc"), timeoutMax, showBurst, remember);
    }

    public void onClick() {
        ++clickHistory[i];
    }

    @Override
    public void tick() {
        // Tracker should start or is running
        if(timeout < timeoutMax.getValue() || clickHistory[i] > 0) {
            if(clickHistory[i] > 0) {
                if(timeout == timeoutMax.getValue()) { // New burst
                    windowTotal = 0;
                    burstTotal = 0;
                    burstLength = 0;
                } else {
                    burstLength += timeout;
                }

                windowTotal += clickHistory[i];
                burstTotal += clickHistory[i];

                cps = (float)windowTotal / Math.min(++burstLength, clickHistory.length);
                timeout = 0;
            } else if(++timeout == timeoutMax.getValue()) {
                return;
            }
            next();
        }
    }

    /** Moves on to the next cell in the buffer, throwing away the oldest value */
    private void next() {
        i = (i + 1) % clickHistory.length;

        // The oldest value contributed to clicksInWindow during this burst
        if((burstLength + timeout) >= clickHistory.length) {
            windowTotal -= clickHistory[i]; // Subtract oldest value from total
        }
        clickHistory[i] = 0; // Throw away oldest value
    }

    @Override
    protected List<String> getText() {
        float cps = timeout < timeoutMax.getValue() || remember.get() ? this.cps : 0;
        String cpsDisplay = getLocalizedName() + ": " + MathUtil.formatToPlaces(cps, 1);

        if(showBurst.get() && cps > 0) {
            return Arrays.asList(
                cpsDisplay,
                I18n.format("betterHud.hud.burst", burstTotal, burstLength)
            );
        } else {
            return Arrays.asList(cpsDisplay);
        }
    }
}
