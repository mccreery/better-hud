package jobicade.betterhud.element;

import org.apache.maven.artifact.versioning.InvalidVersionSpecificationException;
import org.apache.maven.artifact.versioning.VersionRange;

import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingStub;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.gui.ElementCategory;
import net.minecraft.client.resources.I18n;

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
    public final Setting settings = new SettingStub();

    private static final VersionRange DEFAULT_SERVER_DEPENDENCY = VersionRange.createFromVersion("1.0");

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

    /**
     * @return The bounding box containing the rendered element. The bounding
     * box of an element which is always fullscreen should be empty.
     */
    public abstract Rect render(T context);

    private Rect lastBounds = Rect.empty();
    /**
     * @return The most recent bounding box for parenting and relative layout.
     */
    public Rect getLastBounds() {
        return lastBounds;
    }

    public void setLastBounds(Rect lastBounds) {
        this.lastBounds = lastBounds;
    }

    private ElementCategory category = ElementCategory.MISC;
    public final ElementCategory getCategory() {
        return category;
    }

    public final void setCategory(ElementCategory category) {
        if (category != null) {
            this.category = category;
        } else {
            throw new IllegalArgumentException("null");
        }
    }

    // Each element must have exactly one instance

    @Override
    public final boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public final int hashCode() {
        return super.hashCode();
    }
}
