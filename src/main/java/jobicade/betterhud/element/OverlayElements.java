package jobicade.betterhud.element;

import jobicade.betterhud.BetterHud;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

// :o
@SideOnly(Side.CLIENT)
@ObjectHolder(BetterHud.MODID)
public class OverlayElements {
    private OverlayElements() {}

    @ObjectHolder(ArmorBars.NAME)
    public static final OverlayElement ARMOR_BARS = null;
}
