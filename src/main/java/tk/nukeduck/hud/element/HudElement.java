package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.entityinfo.EntityInfo;
import tk.nukeduck.hud.element.entityinfo.HorseInfo;
import tk.nukeduck.hud.element.entityinfo.MobInfo;
import tk.nukeduck.hud.element.particles.BloodSplatters;
import tk.nukeduck.hud.element.particles.WaterDrops;
import tk.nukeduck.hud.element.settings.RootSetting;
import tk.nukeduck.hud.element.text.BiomeName;
import tk.nukeduck.hud.element.text.Clock;
import tk.nukeduck.hud.element.text.Connection;
import tk.nukeduck.hud.element.text.Coordinates;
import tk.nukeduck.hud.element.text.CpsCount;
import tk.nukeduck.hud.element.text.Distance;
import tk.nukeduck.hud.element.text.FoodHealthStats;
import tk.nukeduck.hud.element.text.FpsCount;
import tk.nukeduck.hud.element.text.FullInvIndicator;
import tk.nukeduck.hud.element.text.LightLevel;
import tk.nukeduck.hud.element.text.SystemClock;
import tk.nukeduck.hud.network.Version;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;

public abstract class HudElement { // Can't extend Gui due to @SideOnly
	public static final ArmorBars ARMOR_BARS = new ArmorBars();
	public static final ArrowCount ARROW_COUNT = new ArrowCount();
	public static final BiomeName BIOME_NAME = new BiomeName();
	public static final BlockViewer BLOCK_VIEWER = new BlockViewer();
	public static final BloodSplatters BLOOD_SPLATTERS = new BloodSplatters();
	public static final Clock CLOCK = new Clock();
	public static final Compass COMPASS = new Compass();
	public static final Connection CONNECTION = new Connection();
	public static final Coordinates COORDINATES = new Coordinates();
	public static final Distance DISTANCE = new Distance();
	public static final MaxLevelIndicator MAX_LEVEL = new MaxLevelIndicator();
	public static final ExperienceInfo EXPERIENCE = new ExperienceInfo();
	public static final FoodHealthStats STATS = new FoodHealthStats();
	public static final FpsCount FPS = new FpsCount();
	public static final FullInvIndicator FULL_INV = new FullInvIndicator();
	public static final HandBar HOLDING = new HandBar();
	public static final HealIndicator HEAL = new HealIndicator();
	public static final HungerIndicator HUNGER = new HungerIndicator();
	public static final LightLevel LIGHT_LEVEL = new LightLevel();
	public static final SignReader SIGN_READER = new SignReader();
	public static final SystemClock SYSTEM_CLOCK = new SystemClock();
	public static final WaterDrops WATER_DROPS = new WaterDrops();
	public static final GlobalSettings GLOBAL = new GlobalSettings();
	public static final HidePlayers HIDE_PLAYERS = new HidePlayers();
	public static final PickupCount PICKUP = new PickupCount();
	public static final PotionBar POTION_BAR = new PotionBar();
	public static final HorseInfo HORSE_INFO = new HorseInfo();
	public static final MobInfo MOB_INFO = new MobInfo();
	public static final CpsCount CPS = new CpsCount();

	public static final HudElement[] ELEMENTS = {
		ARMOR_BARS, ARROW_COUNT, BIOME_NAME, BLOCK_VIEWER, BLOOD_SPLATTERS,
		CLOCK, COMPASS, CONNECTION, COORDINATES, DISTANCE,
		MAX_LEVEL, EXPERIENCE, STATS, FPS, FULL_INV,
		HOLDING, HEAL, HUNGER, LIGHT_LEVEL, SIGN_READER,
		SYSTEM_CLOCK, WATER_DROPS, HIDE_PLAYERS,
		HORSE_INFO, MOB_INFO, PICKUP, POTION_BAR, CPS
	};

	public static final EntityInfo[] ENTITY_INFO = {
		HORSE_INFO, MOB_INFO
	};

	public static final ResourceLocation HUD_ICONS = new ResourceLocation("hud", "textures/gui/icons_hud.png");
	public static final ResourceLocation PARTICLES = new ResourceLocation("textures/particle/particles.png");

	public final RootSetting settings = new RootSetting();

	public boolean isEnabled() {
		return settings.get() && isSupportedByServer();
	}

	public final String name;

	protected HudElement(String name) {
		this.name = name;
	}

	public Version getMinimumServerVersion() {
		return Version.ZERO;
	}

	public boolean isSupportedByServer() {
		return BetterHud.serverVersion.compareTo(getMinimumServerVersion()) >= 0;
	}

	public final String getLocalizedName() {
		return I18n.format(getUnlocalizedName());
	}

	public final String getUnlocalizedName() {
		return "betterHud.element." + name;
	}

	public boolean shouldRender() {return true;}

	/** Renders this element to the screen
	 * @return The bounds containing the element drawn */
	public abstract Bounds render(RenderGameOverlayEvent event, LayoutManager manager);

	/** Loads this element's default settings. */
	public abstract void loadDefaults();

	/** Called for all elements during {@link FMLInitializationEvent}
	 * @see BetterHud#init(FMLInitializationEvent) */
	public void init(FMLInitializationEvent event) {}

	/** Loads this element's settings from the key-value combination map.
	 * @param keyVals Key-value combinations containing setting names and values. */
	@Deprecated // TODO respond to children
	public final void loadSettings(HashMap<String, String> keyVals) {
		settings.load(keyVals.get(settings.name));

		/*for(Setting setting : this.settings) {
			if(setting instanceof Legend || setting.name == "enabled") continue;
			if(keyVals.containsKey(setting.name)) setting.load(keyVals.get(setting.name));
		}*/
	}

	/** Saves this element's settings into a key-value combination map. TODO investigate
	 * @return Key-value combinations containing this element's setting names and values. */
	@Deprecated // TODO
	public final HashMap<String, String> saveSettings() {
		HashMap<String, String> keyVals = new HashMap<String, String>();
		keyVals.put(settings.name, settings.save());
		/*for(Setting setting : this.settings) {
			keyVals.put(setting.getName(), setting.toString());
		}*/
		return keyVals;
	}

	/** Calls {@link #init(FMLInitializationEvent)} on all elements
	 * @see #init(FMLInitializationEvent)
	 * @see BetterHud#init(FMLInitializationEvent) */
	public static void initAll(FMLInitializationEvent event) {
		for(HudElement element : ELEMENTS) {
			element.init(event);
		}
	}

	/** Calls {@link #loadDefaults()} on all elements
	 * @see #loadDefaults() */
	public static void loadAllDefaults() {
		GLOBAL.loadDefaults();

		for (HudElement element : ELEMENTS) {
			element.loadDefaults();
		}
	}

	// Rendering utility functions

	/** @see Gui#drawRect(int, int, int, int, int) */
	public static void drawRect(Bounds bounds, int color) {
		Gui.drawRect(bounds.left(), bounds.top(), bounds.right(), bounds.bottom(), color);
	}

	/** {@code progress} defaults to the durability of {@code stack}
	 * @see #drawProgressBar(Bounds, float, boolean) */
	public static void drawDamageBar(Bounds bounds, ItemStack stack, boolean vertical) {
		float progress = (float)(stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage();
		drawProgressBar(bounds, progress, vertical);
	}

	/** Draws a progress bar for item damage
	 * @param progress Index of progress between 0 and 1
	 * @param vertical {@code true} to render bar from bottom to top */
	public static void drawProgressBar(Bounds bounds, float progress, boolean vertical) {
		drawRect(bounds, Colors.BLACK);
		progress = MathHelper.clamp(progress, 0, 1);

		int color = MathHelper.hsvToRGB(progress / 3, 1, 1) | 0xff000000;

		Bounds bar;
		if(vertical) {
			bar = new Bounds(bounds.width() - 1, (int)(progress * bounds.height()));
			Direction.SOUTH_WEST.anchor(bar, bounds);
		} else {
			bar = new Bounds((int)(progress * bounds.width()), bounds.height() - 1);
			Direction.NORTH_WEST.anchor(bar, bounds);
		}
		drawRect(bar, color);
	}

	/** @return The height of {@code lines} of text
	 * @see #drawLines(String[], Bounds, Direction, int) */
	public static int getLinesHeight(int lines) {
		return lines > 0 ? (MC.fontRenderer.FONT_HEIGHT + 2) * lines - 2 : 0;
	}

	/** @see #getLinesSize(Collection) */
	public static Point getLinesSize(String... strings) {
		return getLinesSize(Arrays.asList(strings));
	}

	/** @return The size of {@code strings}
	 * @see #drawLines(String[], Bounds, Direction, int) */
	public static Point getLinesSize(Collection<String> strings) {
		if(strings.isEmpty()) {
			return Point.ZERO;
		}
		int maxWidth = 0;

		for(String string : strings) {
			if(string != null) {
				int width = MC.fontRenderer.getStringWidth(string);
				if(width > maxWidth) maxWidth = width;
			}
		}
		return new Point(maxWidth, getLinesHeight(strings.size()));
	}

	/** Draws a line of text aligned around {@code position} by {@code anchor} */
	public static Bounds drawString(String string, Point position, Direction anchor, int color) {
		Bounds bounds = anchor.align(new Bounds(position, getLinesSize(string)));

		MC.fontRenderer.drawStringWithShadow(string, bounds.x(), bounds.y(), color);
		return bounds;
	}

	/** @see #drawLines(Collection, Bounds, Direction, int) */
	public static Bounds drawLines(String[] strings, Bounds bounds, Direction anchor, int color) {
		return drawLines(Arrays.asList(strings), bounds, anchor, color);
	}

	/** Draws multiple lines of text anchored to {@code anchor} within {@code bounds} */
	public static Bounds drawLines(Collection<String> strings, Bounds bounds, Direction anchor, int color) {
		bounds = anchor.anchor(new Bounds(getLinesSize(strings)), bounds);
		Bounds drawBounds = new Bounds(bounds);

		if(anchor.in(Direction.LEFT)) anchor = Direction.NORTH_WEST;
		else if(anchor.in(Direction.RIGHT)) anchor = Direction.NORTH_EAST;
		else anchor = Direction.NORTH;

		// Render lines top to bottom
		for(String line : strings) {
			drawBounds.top(drawString(line, anchor.getAnchor(drawBounds), anchor, color).bottom() + 2);
		}
		return bounds;
	}
}
