package tk.nukeduck.hud.element;

import tk.nukeduck.hud.element.entityinfo.BreedIndicator;
import tk.nukeduck.hud.element.entityinfo.EntityInfo;
import tk.nukeduck.hud.element.entityinfo.HorseInfo;
import tk.nukeduck.hud.element.entityinfo.MobInfo;
import tk.nukeduck.hud.element.particles.BloodSplatters;
import tk.nukeduck.hud.element.particles.WaterDrops;
import tk.nukeduck.hud.element.text.BiomeName;
import tk.nukeduck.hud.element.text.Connection;
import tk.nukeduck.hud.element.text.Coordinates;
import tk.nukeduck.hud.element.text.Distance;
import tk.nukeduck.hud.element.text.FoodHealthStats;
import tk.nukeduck.hud.element.text.FpsCount;
import tk.nukeduck.hud.element.text.LightLevel;

// TODO move directly to HudElement?
public class HudElements {
	public final HudElement armorBars, arrowCount, biome, blockViewer, blood,
			clock, compass, connection, coordinates, distance,
			enchantIndicator, experienceInfo, foodHealthStats, fps, fullInvIndicator,
			handBar, healIndicator, hungerIndicator, lightLevel,
			signReader, systemClock, waterDrops;
	public final GlobalSettings globalSettings;

	public final BreedIndicator breedIndicator;
	public final HidePlayers hidePlayers;
	public final HorseInfo horseInfo;
	public final MobInfo mobInfo;
	public final PickupCount pickup;
	public final PotionBar potionBar;

	public final HudElement[] elements;
	public final EntityInfo[] info;

	public HudElements() {
		this.elements = new HudElement[] {
			this.armorBars = new ArmorBars(),
			this.arrowCount = new ArrowCount(),
			this.biome = new BiomeName(),
			this.blockViewer = new BlockViewer(),
			this.blood = new BloodSplatters(),
			this.breedIndicator = new BreedIndicator(),
			this.clock = new Clock(),
			this.compass = new Compass(),
			this.connection = new Connection(),
			this.coordinates = new Coordinates(),
			this.distance = new Distance(),
			this.enchantIndicator = new MaxLevelIndicator(),
			this.experienceInfo = new ExperienceInfo(),
			this.foodHealthStats = new FoodHealthStats(),
			this.fps = new FpsCount(),
			this.fullInvIndicator = new FullInvIndicator(),
			this.handBar = new HandBar(),
			this.healIndicator = new HealIndicator(),
			this.hidePlayers = new HidePlayers(),
			this.horseInfo = new HorseInfo(),
			this.hungerIndicator = new HungerIndicator(),
			this.lightLevel = new LightLevel(),
			this.mobInfo = new MobInfo(),
			this.pickup = new PickupCount(),
			this.potionBar = new PotionBar(),
			this.signReader = new SignReader(),
			this.systemClock = new SystemClock(),
			this.waterDrops = new WaterDrops()
		};
		this.info = new EntityInfo[] {
			this.breedIndicator,
			this.horseInfo,
			this.mobInfo
		};
		this.globalSettings = new GlobalSettings();

		for(HudElement element : this.elements) {
			element.init();
		}
	}

	public void loadDefaults() {
		this.globalSettings.loadDefaults();
		for (HudElement element : elements) {
			element.loadDefaults();
		}
	}
}
