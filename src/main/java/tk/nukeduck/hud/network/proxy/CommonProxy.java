package tk.nukeduck.hud.network.proxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.element.HudElements;

public class CommonProxy {
	// TODO no need to proxy?
	public HudElements elements;

	@SideOnly(Side.CLIENT)
	public KeyBinding openMenu, disable;

	public void init() {}
	public void initElements() {}
	public void initKeys() {}
	public void loadDefaults() {}
	public void loadSettings() {}
	public void notifyPresence() {}
	public void clearPresence() {}
}
