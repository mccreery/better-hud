package jobicade.betterhud.registry;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.entityinfo.BillboardElement;
import jobicade.betterhud.element.entityinfo.HorseInfo;
import jobicade.betterhud.element.entityinfo.MobInfo;
import jobicade.betterhud.element.entityinfo.PlayerInfo;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

@EventBusSubscriber(modid = BetterHud.MODID, value = Dist.CLIENT)
public class BillboardElements extends HudRegistry<BillboardElement> {
    private BillboardElements() {
        super(HudElements.get());
    }

    private static final BillboardElements INSTANCE = new BillboardElements();

    public static BillboardElements get() {
        return INSTANCE;
    }

    public static final HorseInfo HORSE_INFO = new HorseInfo();
    public static final MobInfo MOB_INFO = new MobInfo();
    public static final PlayerInfo PLAYER_INFO = new PlayerInfo();

    public static void registerAll() {
        get().register(
            HORSE_INFO,
            MOB_INFO,
            PLAYER_INFO
        );
    }
}
