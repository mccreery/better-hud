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

        billboardScale = new SettingSlider(this, "billboardScale", 0, 1);
        billboardScale.setDisplayPercent();
        billboardDistance = new SettingSlider(this, "rayDistance", 5, 200);
        billboardDistance.setUnlocalizedValue("betterHud.hud.meters");

        hideOnDebug = new SettingBoolean(this, "hideOnDebug");
        debugMode = new SettingBoolean(this, "debugMode");
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
