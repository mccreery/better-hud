package jobicade.betterhud.element;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.settings.RootSetting;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.proxy.ClientProxy;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.SortField;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.Restriction;
import net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * @param T context object passed to render methods.
 */
public abstract class HudElement<T> {
	/** The settings saved to the config file for this element */
	public final RootSetting settings;

	public boolean isEnabled() {
		return settings.get();
	}

	public void setEnabled(boolean value) {
		settings.set(value);
	}

	protected HudElement(String name) {
		List<Setting<?>> rootSettings = new ArrayList<>();
		addSettings(rootSettings);
		this.settings = new RootSetting(this, rootSettings);
	}

	private ResourceLocation registryName;

	public final void setRegistryName(String name) {
		setRegistryName(new ResourceLocation(name));
	}

	public final void setRegistryName(String domain, String path) {
		setRegistryName(new ResourceLocation(domain, path));
	}

	public final void setRegistryName(ResourceLocation name) {
		registryName = name;
	}

	public final ResourceLocation getRegistryName() {
		return registryName;
	}

	private String unlocalizedName;

	/**
	 * @param name The unlocalizedname without prefix {@code hud.}.
	 */
	public final void setUnlocalizedName(String name) {
		unlocalizedName = "hud." + name;
	}

	/**
	 * @return The unlocalized name including prefix {@code hud.}.
	 */
	public final String getUnlocalizedName() {
		return unlocalizedName;
	}

	public final String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	private static final VersionRange DEFAULT_SERVER_DEPENDENCY
		= VersionRange.newRange(null, Arrays.asList(Restriction.EVERYTHING));

	private VersionRange serverDependency = DEFAULT_SERVER_DEPENDENCY;

	/**
	 * Version spec is converted to range using
	 * {@link VersionRange#createFromVersionSpec(String)}.
	 */
	protected final void setServerDependency(String versionSpec) {
		VersionRange serverDependency;
		try {
			serverDependency = VersionRange.createFromVersionSpec(versionSpec);
		} catch (InvalidVersionSpecificationException e) {
			throw new RuntimeException(e);
		}
		setServerDependency(serverDependency);
	}

	protected final void setServerDependency(VersionRange serverDependency) {
		this.serverDependency = serverDependency;
	}

	public final VersionRange getServerDependency() {
		return serverDependency;
	}

	/**
	 * Adds all the element-specific settings to the settings window.
	 * Include {@code super.addSettings()} for all child classes.
	 *
	 * <p>Note that this method is called before instance initializers for the
	 * child class, including member initializers, so look out for {@code null}s.
	 *
	 * @param settings The list of settings to add new settings to.
	 */
	protected void addSettings(List<Setting<?>> settings) {
	}

    /**
     * Checks any conditions for rendering apart from being enabled or
     * compatible. For example, the health bar would return {@code false} in
     * creative mode. Most elements will not need to override this method.
     *
     * @return {@code true} if extra conditions for rendering are met.
     */
	public boolean shouldRender(T context) {
		return true;
	}

	// TODO specify return value when the element is full screen
	/**
	 * @return The bounding box containing the rendered element.
	 */
	public abstract Rect render(T context);

	/** Calls {@link #render(Event)} if the element
	 * should be rendered and caches the bounds so they are available from {@link #getLastRect()} */
	public final void tryRender(Event event) {
		// TODO remove stub
		/*if(shouldRender(event) && isEnabledAndSupported()) {
			Minecraft.getMinecraft().mcProfiler.startSection(name);

			lastBounds = render(event);
			if(lastBounds == null) lastBounds = Rect.empty();
			postRender(event);

			Minecraft.getMinecraft().mcProfiler.endSection();
		}*/
	}

	private Rect lastBounds = Rect.empty();

	protected void postRender(T context) {}

	/** Renders all elements for the current render event
	 * @param event The current render event */
	public static void renderAll(Event event) {
		for(HudElement<?> element : HudElements.get().getRegistered(SortField.PRIORITY)) {
			element.tryRender(event);
		}
	}

	/** @return The last or appropriate bounds for this element.<br>
	 * {@link Rect#empty()} if the element has no appropriate bounds */
	public Rect getLastBounds() {
		return lastBounds;
	}

	/** Calls {@link #init(FMLInitializationEvent)} on all elements
	 * @see #init(FMLInitializationEvent)
	 * @see BetterHud#init(FMLInitializationEvent) */
	public static void initAll(FMLInitializationEvent event) {
		for(HudElement<?> element : HudElements.get().getRegistered()) {
			element.init(event);
		}
	}

	/**
	 * Called for all elements during {@link FMLInitializationEvent} on the
	 * physical client only. Elements registering themselves as event
	 * subscribers can only use client-side events.
	 *
	 * @see ClientProxy#init(FMLInitializationEvent)
	 */
	public void init(FMLInitializationEvent event) {}

	// TODO refactor
	/** Calls {@link #loadDefaults()} on all elements
	 * @see #loadDefaults() */
	public static void loadAllDefaults() {
		HudElements.GLOBAL.loadDefaults();

		for (HudElement<?> element : HudElements.get().getRegistered()) {
			element.loadDefaults();
		}
		normalizePriority();
	}

	public static void normalizePriority() {
		HudElements.get().invalidateSorts(SortField.PRIORITY);
		List<HudElement<?>> prioritySort = HudElements.get().getRegistered(SortField.PRIORITY);

		for(int i = 0; i < prioritySort.size(); i++) {
			prioritySort.get(i).settings.priority.set(i);
		}
	}

	/** Loads this element's default settings.<br>
	 *
	 * You should always call the {@code super} implementation to handle the default enabled value of {@code true}
	 * and to allow for future expansion */
	public void loadDefaults() {
		setEnabled(true);
		settings.priority.set(0);
	}
}
