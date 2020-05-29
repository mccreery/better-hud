package jobicade.betterhud.util;

import com.google.common.reflect.TypeToken;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.GameData;
import net.minecraftforge.registries.IForgeRegistryEntry;

/**
 * Interface alternative to {@link IForgeRegistryEntry.Impl} using Java 8
 * default interface implementations for the overloaded setters. The purpose of
 * this is to create registries for classes which already have a superclass.
 *
 * <p>Implementors only provide basic getter/setter definitions for
 * {@link #setRegistryNameInner(ResourceLocation)} and
 * {@link #getRegistryName()}.
 */
@SuppressWarnings({ "serial", "unchecked" })
public interface IForgeRegistryEntryImpl<T extends IForgeRegistryEntry<T>> extends IForgeRegistryEntry<T> {
    /**
     * Sets a member variable and nothing else. The member variable must have
     * initial value {@code null}. Do not call directly.
     * @see #setRegistryName(ResourceLocation)
     */
    void setRegistryNameInner(ResourceLocation name);

    default T setRegistryName(String name) {
        if (getRegistryName() != null)
            throw new IllegalStateException("Attempted to set registry name with existing registry name! New: " + name + " Old: " + getRegistryName());

        setRegistryNameInner(GameData.checkPrefix(name));
        return (T)this;
    }

    @Override
    default T setRegistryName(ResourceLocation name) {
        return setRegistryName(name.toString());
    }

    default T setRegistryName(String modID, String name) {
        return setRegistryName(modID + ":" + name);
    }

    /**
     * Does nothing except return the variable set by
     * {@link #setRegistryNameInner(ResourceLocation)}.
     * <p>{@inheritDoc}
     */
    ResourceLocation getRegistryName();

    @Override
    default Class<T> getRegistryType() {
        return (Class<T>)new TypeToken<T>(getClass()) {}.getRawType();
    }
}
