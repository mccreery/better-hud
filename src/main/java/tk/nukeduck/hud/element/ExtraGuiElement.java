package tk.nukeduck.hud.element;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import tk.nukeduck.hud.element.settings.ElementSetting;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;

public abstract class ExtraGuiElement {
	/** Whether or not this element is currently enabled. */
	public boolean enabled = true;
	/** A list of settings this element contains. */
	public ArrayList<ElementSetting> settings = new ArrayList<ElementSetting>();
	
	/** {@code false} if the element is unsupported by the server. */
	public boolean unsupported = false;
	
	/** @return The unlocalized name of this element. */
	public abstract String getName();
	
	/** @return The localized name of this element. */
	public final String getLocalizedName() {
		return FormatUtil.translatePre("element." + this.getName());
	}
	
	public Bounds getBounds(ScaledResolution resolution) {
		return Bounds.EMPTY;
	}
	
	/** @return Whether or not the element should show up on the profiler. */
	public abstract boolean shouldProfile();
	/** Updates this element, called by the client loop at any registered update speed.
	 * @param mc The current Minecraft instance. */
	public abstract void update(Minecraft mc);
	/** Renders this element to the screen.
	 * @param mc The current Minecraft instance.
	 * @param resolution The resolution of the Minecraft ingame GUI.
	 * @param stringManager A StringManager instance for the current frame.
	 * @param layoutManager A LayoutManager instance for the current frame.
	 * @see StringManager
	 * @see LayoutManager */
	public abstract void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager);
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
		
		for(ElementSetting setting : this.settings) {
			if(setting instanceof ElementSettingDivider) continue;
			if(keyVals.containsKey(setting.getName())) setting.fromString(keyVals.get(setting.getName()));
		}
	}
	
	/** Saves this element's settings into a key-value combination map.
	 * @return Key-value combinations containing this element's setting names and values. */
	public final HashMap<String, String> saveSettings() {
		HashMap<String, String> keyVals = new HashMap<String, String>();
		for(ElementSetting setting : this.settings) {
			keyVals.put(setting.getName(), setting.toString());
		}
		return keyVals;
	}
	
	/** Register updates for this element at the specified speed.
	 * @param speed The speed to update this element at. */
	protected final void registerUpdates(UpdateSpeed speed) {
		speed.elements.add(this);
	}
	
	/** Speeds used to update elements regularly.
	 * @see ExtraGuiElement#registerUpdates(UpdateSpeed)
	 * @see ExtraGuiElement#update(Minecraft) */
	public enum UpdateSpeed {
		/** This element's update method will be called every 10 seconds. */
		SLOW,
		/** This element's update method will be called every 5 seconds. */
		MEDIUM,
		/** This element's update method will be called every second. */
		FAST,
		/** This element's update method will be called 20 times every second. */
		FASTER;
		
		/** A list of elements which are registered to update at this speed. */
		public ArrayList<ExtraGuiElement> elements = new ArrayList<ExtraGuiElement>();
	}
}