package tk.nukeduck.hud.element;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public abstract class HudElement {
	/** Whether or not this element is currently enabled. */
	public boolean enabled = true;

	/** A list of settings this element contains. */
	public ArrayList<Setting> settings = new ArrayList<Setting>();
	
	public final String name;
	protected HudElement(String name) {
		this.name = name;
	}
	
	/** {@code false} if the element is unsupported by the server. */
	public boolean unsupported = false; // TODO private?
	
	/** @return The localized name of this element. */
	public final String getLocalizedName() {
		return I18n.format("betterHud.element." + name);
	}
	
	// TODO contain anchors within bounds
	public Bounds getBounds(ScaledResolution resolution) {
		return Bounds.EMPTY;
	}
	
	/** @return Whether or not the element should show up on the profiler. */
	public boolean shouldProfile() {
		return true;
	}

	/** Updates this element, called by the client loop at any registered update speed.*/
	public void update() {}

	/** Renders this element to the screen.
	 * @param event The resolution of the Minecraft ingame GUI.
	 * @param stringManager A StringManager instance for the current frame.
	 * @param layoutManager A LayoutManager instance for the current frame.
	 * @see StringManager
	 * @see LayoutManager */
	public abstract void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager);

	/** Loads this element's default settings. */
	public abstract void loadDefaults();
	
	/** Allows registration of events etc. */
	public void init() {}
	
	/** When overriding, always {@code ||} your result with {@code super.shouldRender()},<br/>
	 * or else the element cannot be disabled! */
	public boolean shouldRender() {
		return this.enabled;
	}
	
	/** Loads this element's settings from the key-value combination map.
	 * @param keyVals Key-value combinations containing setting names and values. */
	public final void loadSettings(HashMap<String, String> keyVals) {
		if(keyVals.containsKey("enabled")) this.enabled = Boolean.parseBoolean(keyVals.get("enabled"));
		
		for(Setting setting : this.settings) {
			if(setting instanceof Divider || setting.getName() == "enabled") continue;
			if(keyVals.containsKey(setting.getName())) setting.fromString(keyVals.get(setting.getName()));
		}
	}
	
	/** Saves this element's settings into a key-value combination map.
	 * @return Key-value combinations containing this element's setting names and values. */
	/*public final HashMap<String, String> saveSettings() {
		HashMap<String, String> keyVals = new HashMap<String, String>();
		for(ElementSetting setting : this.settings) {
			keyVals.put(setting.getName(), setting.toString());
		}
		return keyVals;
	}*/
}
