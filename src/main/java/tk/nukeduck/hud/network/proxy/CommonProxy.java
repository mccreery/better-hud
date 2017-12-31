package tk.nukeduck.hud.network.proxy;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CommonProxy {
	@SideOnly(Side.CLIENT)
	public KeyBinding openMenu, disable; // TODO why?

	public void init() {}
	public void initElements() {}
	public void initKeys() {}
	public void loadDefaults() {}
	public void loadSettings() {}
	public void notifyServer(boolean supported) {}
}
