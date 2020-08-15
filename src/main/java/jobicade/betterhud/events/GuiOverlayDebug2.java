package jobicade.betterhud.events;

import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.overlay.DebugOverlayGui;

// Not possible to reuse any code from vanilla or Forge without extending
// The vanilla implementation doesn't expose the left and right lists and
// doesn't include the pie and help lines in the lists
public class GuiOverlayDebug2 extends DebugOverlayGui {
    private final Minecraft mc;

    public GuiOverlayDebug2(Minecraft mc) {
        super(mc);
        this.mc = mc;
    }

    // This should not be used as a real GUI - it's only a wrapper
    // to access the debug info
    @Override
    public void render() {
        throw new UnsupportedOperationException();
    }

    // made public
    @Override
    public List<String> getDebugInfoLeft() {
        List<String> list = super.getDebugInfoLeft();

        list.add("");
        list.add("Debug: Pie [shift]: "
            + (mc.gameSettings.showDebugProfilerChart ? "visible" : "hidden")
            + " FPS [alt]: "
            + (mc.gameSettings.showLagometer ? "visible" : "hidden"));
        list.add("For help: press F3 + Q");

        return list;
    }

    // made public
    @Override
    public List<String> getDebugInfoRight() {
        return super.getDebugInfoRight();
    }
}
