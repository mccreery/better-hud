package jobicade.betterhud.gui;

import net.minecraft.client.resources.I18n;

public enum ElementCategory {
    VANILLA("vanilla"),
    TEXT("text"),
    BILLBOARD("billboard"),
    MISC("misc");

    private final String unlocalizedName;
    private ElementCategory(String name) {
        unlocalizedName = "betterHud.category." + name;
    }

    public String getUnlocalizedName() {
        return unlocalizedName;
    }

    public String getLocalizedName() {
        return I18n.format(unlocalizedName);
    }
}
