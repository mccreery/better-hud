package jobicade.betterhud.registry;

import jobicade.betterhud.element.GlobalSettings;
import jobicade.betterhud.element.HudElement;

public class HudElements extends HudRegistry<HudElement<?>> {
    private HudElements() {
        super(null);
    }

    private static final HudElements INSTANCE = new HudElements();

    public static HudElements get() {
        return INSTANCE;
    }

    // Not registered
    public static final GlobalSettings GLOBAL = new GlobalSettings();
}
