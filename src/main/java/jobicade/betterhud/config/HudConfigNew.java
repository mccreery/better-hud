package jobicade.betterhud.config;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.tuple.Pair;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.registry.HudElements;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.common.ForgeConfigSpec.ConfigValue;

public class HudConfigNew {
    public static final HudConfigNew CLIENT;
    public static final ForgeConfigSpec CLIENT_SPEC;

    static {
        Pair<HudConfigNew, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(HudConfigNew::new);
        CLIENT = specPair.getLeft();
        CLIENT_SPEC = specPair.getRight();
    }

    private final ConfigValue<List<? extends HudElement<?>>> enabled;

    private final Map<Setting, ConfigValue<String>> valueMap = new HashMap<>();

    public HudConfigNew(ForgeConfigSpec.Builder builder) {
        builder.push("betterhud");
        enabled = builder.defineList("enabled", Collections.<HudElement<?>>emptyList(),
            name -> name instanceof String
            && HudElements.get().getRegistered((String)name) != null);
        builder.pop();

        for (HudElement<?> element : HudElements.get().getRegistered()) {
            builder.push(element.getName());
            mapValues(builder, element.settings);
            builder.pop();
        }
    }

    private void mapValues(ForgeConfigSpec.Builder builder, Setting setting) {
        String name = setting.getName();
        boolean hasName = name != null && !name.isEmpty();

        if (hasName) {
            builder.push(name);

            if (setting.hasValue()) {
                valueMap.put(setting, builder.define(Collections.emptyList(), ""));
            }
        }

        for (Setting childSetting : setting.getChildren()) {
            mapValues(builder, childSetting);
        }

        if (hasName) {
            builder.pop();
        }
    }
}
