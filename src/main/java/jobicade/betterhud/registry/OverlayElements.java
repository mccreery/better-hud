package jobicade.betterhud.registry;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.ArmorBars;
import jobicade.betterhud.element.ArrowCount;
import jobicade.betterhud.element.BlockViewer;
import jobicade.betterhud.element.Compass;
import jobicade.betterhud.element.ExperienceInfo;
import jobicade.betterhud.element.HandBar;
import jobicade.betterhud.element.HealIndicator;
import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.PickupCount;
import jobicade.betterhud.element.SignReader;
import jobicade.betterhud.element.particles.BloodSplatters;
import jobicade.betterhud.element.particles.WaterDrops;
import jobicade.betterhud.element.text.BiomeName;
import jobicade.betterhud.element.text.Connection;
import jobicade.betterhud.element.text.Coordinates;
import jobicade.betterhud.element.text.CpsCount;
import jobicade.betterhud.element.text.Distance;
import jobicade.betterhud.element.text.FpsCount;
import jobicade.betterhud.element.text.FullInvIndicator;
import jobicade.betterhud.element.text.GameClock;
import jobicade.betterhud.element.text.LightLevel;
import jobicade.betterhud.element.text.Saturation;
import jobicade.betterhud.element.text.SystemClock;
import jobicade.betterhud.element.vanilla.AirBar;
import jobicade.betterhud.element.vanilla.ArmorBar;
import jobicade.betterhud.element.vanilla.Crosshair;
import jobicade.betterhud.element.vanilla.Experience;
import jobicade.betterhud.element.vanilla.FoodBar;
import jobicade.betterhud.element.vanilla.HealthBar;
import jobicade.betterhud.element.vanilla.HelmetOverlay;
import jobicade.betterhud.element.vanilla.Hotbar;
import jobicade.betterhud.element.vanilla.JumpBar;
import jobicade.betterhud.element.vanilla.Offhand;
import jobicade.betterhud.element.vanilla.PortalOverlay;
import jobicade.betterhud.element.vanilla.PotionBar;
import jobicade.betterhud.element.vanilla.RidingHealth;
import jobicade.betterhud.element.vanilla.Sidebar;
import jobicade.betterhud.element.vanilla.Vignette;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber(value = { Side.CLIENT }, modid = BetterHud.MODID)
public class OverlayElements extends HudRegistry<OverlayElement> {
    private OverlayElements() {
        super(HudElements.get());
    }

    private static final OverlayElements INSTANCE = new OverlayElements();

    public static OverlayElements get() {
        return INSTANCE;
    }

    public static final AirBar AIR_BAR = new AirBar();
    public static final ArmorBar ARMOR_BAR = new ArmorBar();
    public static final ArmorBars ARMOR_BARS = new ArmorBars();
    public static final ArrowCount ARROW_COUNT = new ArrowCount();
    public static final BiomeName BIOME_NAME = new BiomeName();
    public static final BlockViewer BLOCK_VIEWER = new BlockViewer();
    public static final BloodSplatters BLOOD_SPLATTERS = new BloodSplatters();
    public static final Compass COMPASS = new Compass();
    public static final Connection CONNECTION = new Connection();
    public static final Coordinates COORDINATES = new Coordinates();
    public static final CpsCount CPS = new CpsCount();
    public static final Crosshair CROSSHAIR = new Crosshair();
    public static final Distance DISTANCE = new Distance();
    public static final Experience EXPERIENCE = new Experience();
    public static final ExperienceInfo EXPERIENCE_INFO = new ExperienceInfo();
    public static final FoodBar FOOD_BAR = new FoodBar();
    public static final FpsCount FPS = new FpsCount();
    public static final FullInvIndicator FULL_INV = new FullInvIndicator();
    public static final GameClock CLOCK = new GameClock();
    public static final HandBar HOLDING = new HandBar();
    public static final HealIndicator HEAL = new HealIndicator();
    public static final HealthBar HEALTH = new HealthBar();
    public static final HelmetOverlay HELMET_OVERLAY = new HelmetOverlay();
    public static final Hotbar HOTBAR = new Hotbar();
    public static final JumpBar JUMP_BAR = new JumpBar();
    public static final LightLevel LIGHT_LEVEL = new LightLevel();
    public static final Offhand OFFHAND = new Offhand();
    public static final PickupCount PICKUP = new PickupCount();
    public static final PortalOverlay PORTAL = new PortalOverlay();
    public static final PotionBar POTION_BAR = new PotionBar();
    public static final RidingHealth MOUNT = new RidingHealth();
    public static final Saturation SATURATION = new Saturation();
    public static final Sidebar SIDEBAR = new Sidebar();
    public static final SignReader SIGN_READER = new SignReader();
    public static final SystemClock SYSTEM_CLOCK = new SystemClock();
    public static final Vignette VIGNETTE = new Vignette();
    public static final WaterDrops WATER_DROPS = new WaterDrops();

    @SubscribeEvent
    public static void onHudRegistry(HudRegistryEvent event) {
        get().register(
            AIR_BAR,
            ARMOR_BAR,
            ARMOR_BARS,
            ARROW_COUNT,
            BIOME_NAME,
            BLOCK_VIEWER,
            BLOOD_SPLATTERS,
            COMPASS,
            CONNECTION,
            COORDINATES,
            CPS,
            CROSSHAIR,
            DISTANCE,
            EXPERIENCE,
            EXPERIENCE_INFO,
            FOOD_BAR,
            FPS,
            FULL_INV,
            CLOCK,
            HOLDING,
            HEAL,
            HEALTH,
            HELMET_OVERLAY,
            HOTBAR,
            JUMP_BAR,
            LIGHT_LEVEL,
            OFFHAND,
            PICKUP,
            PORTAL,
            POTION_BAR,
            MOUNT,
            SATURATION,
            SIDEBAR,
            SIGN_READER,
            SYSTEM_CLOCK,
            VIGNETTE,
            WATER_DROPS
        );
    }
}
