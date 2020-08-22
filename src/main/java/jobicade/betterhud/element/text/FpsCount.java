package jobicade.betterhud.element.text;

import static jobicade.betterhud.BetterHud.MC;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.element.settings.SettingBoolean;

public class FpsCount extends TextElement {
    private SettingBoolean numberOnly;

    public FpsCount() {
        super("fpsCount");

        numberOnly = new SettingBoolean("numberOnly");
        addSetting(numberOnly);
    }

    @Override
    protected List<String> getText() {
        String fps = MC.debug.substring(0, MC.debug.indexOf(' '));

        if(!numberOnly.get()) {
            fps = getLocalizedName() + ": " + fps;
        }
        return Arrays.asList(fps);
    }
}
