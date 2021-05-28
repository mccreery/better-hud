package jobicade.betterhud.element.text;

import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.geom.Direction;
import net.minecraft.client.Minecraft;

import java.util.Arrays;
import java.util.List;

public class FpsCount extends TextElement {
    private SettingBoolean numberOnly;

    @Override
    public void loadDefaults() {
        super.loadDefaults();

        position.setPreset(Direction.SOUTH_WEST);
        numberOnly.set(false);
    }

    public FpsCount() {
        super("fpsCount");
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(numberOnly = new SettingBoolean("numberOnly"));
    }

    @Override
    protected List<String> getText() {
        String fps = Minecraft.getInstance().fpsString.substring(0, Minecraft.getInstance().fpsString.indexOf(' '));

        if(!numberOnly.get()) {
            fps = getLocalizedName() + ": " + fps;
        }
        return Arrays.asList(fps);
    }
}
