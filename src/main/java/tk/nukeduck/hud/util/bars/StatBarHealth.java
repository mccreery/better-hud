package tk.nukeduck.hud.util.bars;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Random;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Point;

public class StatBarHealth extends StatBarSided {
	private final EntityLivingBase entity;
	private final Random random = new Random();

	private long prevTime;
	private int healthUpdateCounter, prevHealth, currentHealth, regen;

	private boolean highlight;
	private int absorptionLow;

	public StatBarHealth(EntityLivingBase entity) {
		this.entity = entity;
	}

	@Override
	protected int getCurrent() {
		//System.out.println(prevHealth + " -> " + health);
		return Math.max(prevHealth, currentHealth);
	}

	@Override
	protected int getMaximum() {
		return absorptionLow + MathHelper.ceil(entity.getAbsorptionAmount());
	}

	@Override
	protected Bounds getIcon(IconType icon, int pointsIndex) {
		int y = entity.world.getWorldInfo().isHardcoreModeEnabled() ? 45 : 0;

		if(icon == IconType.BACKGROUND) {
			return new Bounds(highlight ? 25 : 16, y, 9, 9);
		} else {
			int x;
			if(pointsIndex >= absorptionLow) {
				x = 160;
			} else if(entity.isPotionActive(MobEffects.POISON)) {
				x = 88;
			} else if(entity.isPotionActive(MobEffects.WITHER)) {
				x = 124;
			} else {
				x = 52;
			}

			if(pointsIndex >= currentHealth) {
				if(!highlight) return null;
				x += 18;
			}
			if(icon == IconType.HALF) x += 9;

			return new Bounds(x, y, 9, 9);
		}
	}

	@Override
	public Direction getIconAlignment() {
		return Direction.WEST;
	}

	@Override
	public void render(Point position, Direction alignment) {
		int updateCounter = MC.ingameGUI.getUpdateCounter();
		random.setSeed(updateCounter * 312871);
		int updateDelta = healthUpdateCounter - updateCounter;

		highlight = updateDelta > 0 && updateDelta % 6 >= 3;

		long currentTime = Minecraft.getSystemTime();
		int newHealth = MathHelper.ceil(entity.getHealth());

		if(newHealth < currentHealth && entity.hurtResistantTime > 0) {
			prevTime = currentTime;
			healthUpdateCounter = updateCounter + 20;
		} else if(newHealth > currentHealth && entity.hurtResistantTime > 0) {
			prevTime = currentTime;
			healthUpdateCounter = updateCounter + 10;
		}

		if(currentTime - prevTime > 1000) {
			prevTime = currentTime;
			currentHealth = newHealth;
			prevHealth = newHealth;
		}

		currentHealth = newHealth;

		regen = this.entity.isPotionActive(MobEffects.REGENERATION) ? healthUpdateCounter % 25 : -1;
		absorptionLow = MathHelper.ceil(entity.getMaxHealth());

		super.render(position, alignment);
	}

	@Override
	protected int getIconBounce(int pointsIndex) {
		int bounce = 0;

		if(currentHealth <= 4) bounce += random.nextInt(2);
		if(pointsIndex == regen) bounce -= 2;

		return bounce;
	}
}
