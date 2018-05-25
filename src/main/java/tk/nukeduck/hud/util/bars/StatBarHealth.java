package tk.nukeduck.hud.util.bars;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.util.math.MathHelper;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Point;

public class StatBarHealth extends StatBar {
	private final Random random = new Random();

	private int currentHealth, displayHealth, maxHealth, absorptionHealth;
	private int flash;
	private int currentUpdateCounter;
	private int regenCounter;

	@Override
	public Direction getNativeAlignment() {
		return Direction.WEST;
	}

	@Override
	protected int getIconBounce(int pointsIndex) {
		int bounce = 0;

		if(currentHealth <= 4) {
			bounce += random.nextInt(2);
		}
		if(regenCounter == pointsIndex) {
			bounce -= 2;
		}
		return bounce;
	}

	@Override
	protected int getMaximum() {
		return BetterHud.ceil(maxHealth, 2) + absorptionHealth;
	}

	@Override
	protected List<Bounds> getIcons(Direction alignment, int pointsIndex) {
		List<Bounds> icons = new ArrayList<>(3);

		EntityLivingBase entity = (EntityLivingBase)MC.getRenderViewEntity(); // TODO

		int fullX;
		if(pointsIndex >= maxHealth) {
			fullX = 160;
		} else if(entity.isPotionActive(MobEffects.POISON)) {
			fullX = 88;
		} else if(entity.isPotionActive(MobEffects.WITHER)) {
			fullX = 124;
		} else {
			fullX = 52;
		}

		int y = entity.getEntityWorld().getWorldInfo().isHardcoreModeEnabled() ? 45 : 0;

		if(flash % 6 >= 3) {
			icons.add(new Bounds(25, y, 9, 9));

			if(pointsIndex + 1 >= currentHealth && pointsIndex < displayHealth) {
				icons.add(new Bounds(pointsIndex + 1 < displayHealth ? fullX + 18 : fullX + 27, y, 9, 9));
			}
		} else {
			icons.add(new Bounds(16, y, 9, 9));
		}

		if(pointsIndex < currentHealth) {
			icons.add(new Bounds(pointsIndex + 1 < currentHealth ? fullX : fullX + 9, y, 9, 9));
		} else if(pointsIndex >= maxHealth) {
			icons.add(new Bounds(pointsIndex + 1 < getMaximum() ? fullX : fullX + 9, y, 9, 9));
		}
		return icons;
	}

	@Override
	public void render(Point position, Direction alignment) {
		int newUpdateCounter = MC.ingameGUI.getUpdateCounter();
		int updateDelta = newUpdateCounter - currentUpdateCounter;

		random.setSeed(newUpdateCounter);
		EntityLivingBase entity = (EntityLivingBase)MC.getRenderViewEntity(); // TODO

		maxHealth = MathHelper.ceil(entity.getMaxHealth());
		absorptionHealth = MathHelper.ceil(entity.getAbsorptionAmount());

		int newHealth = MathHelper.ceil(entity.getHealth());

		if(currentHealth <= 0 && newHealth > 0) {
			displayHealth = newHealth;
			flash = 0;
		} else if(currentHealth != newHealth) {
			if(newHealth < displayHealth) {
				flash = 17;
			} else {
				if(flash < 11) flash = 11;
				displayHealth = newHealth;
			}
		} else if(flash > 0) {
			flash -= updateDelta;
			if(flash <= 0) displayHealth = newHealth;
		}

		if(entity.isPotionActive(MobEffects.REGENERATION)) {
			regenCounter += updateDelta * 2;

			if(regenCounter >= maxHealth + 30) {
				regenCounter = 0;
			}
		} else {
			regenCounter = -2;
		}

		currentHealth = newHealth;
		currentUpdateCounter = newUpdateCounter;

		super.render(position, alignment);
	}
}
