package jobicade.betterhud.element.settings;

import java.util.function.BooleanSupplier;

/**
 * A setting which follows a fluent interface (setter methods return {@code this}).
 */
public abstract class FluentSetting<T extends FluentSetting<T>> extends Setting {
    protected FluentSetting(String name) {
        super(name);
    }

    /**
     * Used by fluent interface methods to ensure return type {@code T}.
     * Should only be implemented by concrete classes.
     * @return {@code this}
     */
    protected abstract T getThis();

    // Fluent interface methods upgraded with covariant return type

    @Override
    public T setEnableOn(BooleanSupplier enableOn) {
        super.setEnableOn(enableOn);
        return getThis();
    }

    @Override
    public T setHidden() {
        super.setHidden();
        return getThis();
    }
}
