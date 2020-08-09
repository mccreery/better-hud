package jobicade.betterhud.render;

import com.mojang.blaze3d.platform.GlStateManager;

import jobicade.betterhud.BetterHud;

public class GlStateManagerManager {
    private GlStateManagerManager() {}

    /**
     * Fixes corrupt cached flags in {@link GlStateManager} so that OpenGL and
     * the manager agree. Emits a warning if corruption is detected.
     */
    public static void fixCorruptFlags() {
        for (GlFlag flag : GlFlag.values()) {
            fixCorruptFlag(flag);
        }
    }

    private static void fixCorruptFlag(GlFlag flag) {
        boolean state = flag.isEnabled();

        // First toggle brings OpenGL and GlStateManager in line
        flag.setEnabled(!state);

        // If the previous call does nothing, the cache was corrupted
        if (flag.isEnabled() == state) {
            BetterHud.getLogger().warn(
                "GlStateManager corruption: {} = {} but {} cached",
                flag, state, !state
            );
        }
        // Second toggle restores the original value
        flag.setEnabled(state);
    }
}
