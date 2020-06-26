package jobicade.betterhud.element;

import java.util.Arrays;
import java.util.List;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.settings.RootSetting;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.proxy.ClientProxy;
import jobicade.betterhud.registry.HudElements;
import jobicade.betterhud.registry.SortField;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.versioning.InvalidVersionSpecificationException;
import net.minecraftforge.fml.common.versioning.Restriction;
import net.minecraftforge.fml.common.versioning.VersionRange;

/**
 * Settings should be added in the constructor. An empty config file is
 * valid, so the initial state of the settings must be valid.
 *
 * @param T context object passed to render methods.
 */
public abstract class HudElement<T> {
	private final String name;

	/**
	 * @param name The name used for config and localization.
	 * Must not be {@code null} or equal to the string {@code "null"}.
	 */
	public HudElement(String name) {
		if (name == null || name.equals("null")) {
			throw new IllegalArgumentException("Invalid name. "
				+ "Must not be null or equal to the string \"null\"");
		}

		this.name = name;
	}

	/**
	 * The name is not {@code null}.
	 */
	public final String getName() {
		return name;
	}

	public final String getUnlocalizedName() {
		return "betterHud.element." + name;
	}

	public final String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	/** The settings saved to the config file for this element */
	// TODO NASTY PUBLICSES
	public final RootSetting settings = new RootSetting();

	public boolean isEnabled() {
		return settings.isEnabled();
	}

	public void setEnabled(boolean value) {
		settings.setEnabled(value);
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

	public static void normalizePriority() {
		HudElements.get().invalidateSorts(SortField.PRIORITY);
		List<HudElement<?>> prioritySort = HudElements.get().getRegistered(SortField.PRIORITY);

		for(int i = 0; i < prioritySort.size(); i++) {
			prioritySort.get(i).settings.setPriority(i);
		}
	}
}
