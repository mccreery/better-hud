package tk.nukeduck.hud.element;

import tk.nukeduck.hud.element.entityinfo.ExtraGuiElementBreedIndicator;
import tk.nukeduck.hud.element.entityinfo.ExtraGuiElementEntityInfo;
import tk.nukeduck.hud.element.entityinfo.ExtraGuiElementHorseInfo;
import tk.nukeduck.hud.element.entityinfo.ExtraGuiElementMobInfo;
import tk.nukeduck.hud.element.particles.ExtraGuiElementBlood;
import tk.nukeduck.hud.element.particles.ExtraGuiElementWaterDrops;
import tk.nukeduck.hud.element.text.ExtraGuiElementBiome;
import tk.nukeduck.hud.element.text.ExtraGuiElementConnection;
import tk.nukeduck.hud.element.text.ExtraGuiElementCoordinates;
import tk.nukeduck.hud.element.text.ExtraGuiElementDistance;
import tk.nukeduck.hud.element.text.ExtraGuiElementFoodHealthStats;
import tk.nukeduck.hud.element.text.ExtraGuiElementFps;
import tk.nukeduck.hud.element.text.ExtraGuiElementLightLevel;

public class HudElements {
	public final ExtraGuiElement armorBars, arrowCount, biome, blockViewer, blood,
			clock, compass, connection, coordinates, distance,
			enchantIndicator, experienceInfo, foodHealthStats, fps, fullInvIndicator,
			handBar, healIndicator, hungerIndicator, lightLevel,
			signReader, systemClock, waterDrops;

	public final ExtraGuiElementBreedIndicator breedIndicator;
	public final ExtraGuiElementHidePlayers hidePlayers;
	public final ExtraGuiElementHorseInfo horseInfo;
	public final ExtraGuiElementMobInfo mobInfo;
	public final ExtraGuiElementPickup pickup;
	public final ExtraGuiElementPotionBar potionBar;

	public final ExtraGuiElement[] elements;
	public final ExtraGuiElementEntityInfo[] info;

	public HudElements() {
		this.elements = new ExtraGuiElement[] {
			this.armorBars = new ExtraGuiElementArmorBars(),
			this.arrowCount = new ExtraGuiElementArrowCount(),
			this.biome = new ExtraGuiElementBiome(),
			this.blockViewer = new ExtraGuiElementBlockViewer(),
			this.blood = new ExtraGuiElementBlood(),
			this.breedIndicator = new ExtraGuiElementBreedIndicator(),
			this.clock = new ExtraGuiElementClock(),
			this.compass = new ExtraGuiElementCompass(),
			this.connection = new ExtraGuiElementConnection(),
			this.coordinates = new ExtraGuiElementCoordinates(),
			this.distance = new ExtraGuiElementDistance(),
			this.enchantIndicator = new ExtraGuiElementEnchantIndicator(),
			this.experienceInfo = new ExtraGuiElementExperienceInfo(),
			this.foodHealthStats = new ExtraGuiElementFoodHealthStats(),
			this.fps = new ExtraGuiElementFps(),
			this.fullInvIndicator = new ExtraGuiElementFullInvIndicator(),
			this.handBar = new ExtraGuiElementHandBar(),
			this.healIndicator = new ExtraGuiElementHealIndicator(),
			this.hidePlayers = new ExtraGuiElementHidePlayers(),
			this.horseInfo = new ExtraGuiElementHorseInfo(),
			this.hungerIndicator = new ExtraGuiElementHungerIndicator(),
			this.lightLevel = new ExtraGuiElementLightLevel(),
			this.mobInfo = new ExtraGuiElementMobInfo(),
			this.pickup = new ExtraGuiElementPickup(),
			this.potionBar = new ExtraGuiElementPotionBar(),
			this.signReader = new ExtraGuiElementSignReader(),
			this.systemClock = new ExtraGuiElementSystemClock(),
			this.waterDrops = new ExtraGuiElementWaterDrops()
		};
		this.info = new ExtraGuiElementEntityInfo[] {
			this.breedIndicator,
			this.horseInfo,
			this.mobInfo
		};

		for(ExtraGuiElement element : this.elements) {
			element.init();
		}
	}

	public void loadDefaults() {
		for (ExtraGuiElement element : this.elements) {
			element.loadDefaults();
		}
	}
}
