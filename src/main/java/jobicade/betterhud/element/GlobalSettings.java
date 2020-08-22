package jobicade.betterhud.element;

import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.geom.Rect;

public class GlobalSettings extends HudElement<Object> {
    private SettingSlider billboardScale;
    private SettingSlider billboardDistance;
    private SettingBoolean hideOnDebug;
    private SettingBoolean debugMode;

    public GlobalSettings() {
        super("global");

        billboardScale = new SettingSlider("billboardScale", 0, 1);
        billboardScale.setDisplayPercent();
        addSetting(billboardDistance);

        billboardDistance = new SettingSlider("rayDistance", 5, 200);
        billboardDistance.setUnlocalizedValue("betterHud.hud.meters");
        addSetting(billboardDistance);

        hideOnDebug = new SettingBoolean("hideOnDebug");
        addSetting(hideOnDebug);

        debugMode = new SettingBoolean("debugMode");
        addSetting(debugMode);
    }

    public float getBillboardScale() {
        return billboardScale.getValue();
    }

    public float getBillboardDistance() {
        return billboardDistance.getValue();
    }

    public boolean hideOnDebug() {
        return hideOnDebug.get();
    }

    public boolean isDebugMode() {
        return debugMode.get();
    }

    @Override
    public Rect render(Object context) {
        return null;
    }
}
