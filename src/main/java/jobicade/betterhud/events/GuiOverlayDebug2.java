package jobicade.betterhud.events;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiOverlayDebug;
import net.minecraft.client.gui.ScaledResolution;

// Not possible to reuse any code from vanilla or Forge without extending
public class GuiOverlayDebug2 extends GuiOverlayDebug {
    private final Minecraft mc;

    public GuiOverlayDebug2(Minecraft mc) {
        super(mc);
        this.mc = mc;
    }

    @Override
    public void renderDebugInfo(ScaledResolution scaledResolutionIn) {
        throw new UnsupportedOperationException();
    }

    /**
     * @see GuiOverlayDebug#renderDebugInfoLeft()
     */
    public List<String> getDebugInfoLeft() {
        List<String> list = call();

        list.add("");
        list.add("Debug: Pie [shift]: "
            + (mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden")
            + " FPS [alt]: "
            + (mc.gameSettings.showLagometer ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");

        return list;
    }

    @Override
    public List<String> getDebugInfoRight() {
        return super.getDebugInfoRight();
    }
}
