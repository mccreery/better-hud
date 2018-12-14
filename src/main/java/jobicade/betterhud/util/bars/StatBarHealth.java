package jobicade.betterhud.util.bars;

import static jobicade.betterhud.BetterHud.ICONS;
import static jobicade.betterhud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import jobicade.betterhud.util.Bounds;
import jobicade.betterhud.util.Direction;
import jobicade.betterhud.util.MathUtil;

public class StatBarHealth extends StatBar<EntityLivingBase> {
	private final Random random = new Random();

	private int currentHealth, displayHealth, maxHealth, absorptionHealth;
	private int flash;
	private int currentUpdateCounter;
	private int regenCounter;

	@Override
	public void setHost(EntityLivingBase host) {
		if(this.host != host) {
			currentHealth = 0;
		}
		super.setHost(host);
	}

	@Override
	public Direction getNativeAlignment() {
		return Direction.WEST;
	}

	@Override
	protected int getIconBounce(int pointsIndex) {
		int bounce = 0;

		if(currentHealth <= MathUtil.ceilDiv(maxHealth, 5)) {
			bounce += random.nextInt(2);
		}
		if(regenCounter == pointsIndex) {
			bounce -= 2;
		}
		return bounce;
	}

	private int compressThreshold = -1;

	public void setCompressThreshold(int compressThreshold) {
		this.compressThreshold = compressThreshold;
	}

	@Override
	protected boolean shouldCompress() {
		return compressThreshold > 0 && currentHealth > compressThreshold;
	}

	@Override
	protected int getMaximum() {
		int maximum = MathUtil.ceil(maxHealth, 2) + absorptionHealth;

		if(shouldCompress()) {
			return Math.min(MathUtil.ceil(currentHealth, getRowPoints()), maximum);
		} else if(compressThreshold > 0) {
			return Math.min(maximum, compressThreshold);
		} else {
			return maximum;
		}
	}

	@Override
	protected int getRowSpacing() {
		return shouldCompress() ? super.getRowSpacing() :
			Math.max(10 - (MathUtil.ceilDiv(getMaximum(), 20) - 2), 3);
	}

	@Override
	protected List<Bounds> getIcons(int pointsIndex) {
		List<Bounds> icons = new ArrayList<>(3);

		int fullX;
		if(pointsIndex >= maxHealth) {
			fullX = 160;
		} else if(host.isPotionActive(MobEffects.POISON)) {
			fullX = 88;
		} else if(host.isPotionActive(MobEffects.WITHER)) {
			fullX = 124;
		} else {
			fullX = 52;
		}

		int y = host.getEntityWorld().getWorldInfo().isHardcoreModeEnabled() ? 45 : 0;

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
	public void renderUnsafe(Bounds bounds, Direction contentAlignment) {
		int newUpdateCounter = MC.ingameGUI.getUpdateCounter();
		int updateDelta = newUpdateCounter - currentUpdateCounter;

		random.setSeed(newUpdateCounter);

		maxHealth = MathUtil.getHealthForDisplay(host.getMaxHealth());
		absorptionHealth = MathUtil.getHealthForDisplay(host.getAbsorptionAmount());

		int newHealth = MathUtil.getHealthForDisplay(host.getHealth());

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

		if(host.isPotionActive(MobEffects.REGENERATION)) {
			regenCounter += updateDelta * 2;

			if(regenCounter >= maxHealth + 30) {
				regenCounter = 0;
			}
		} else {
			regenCounter = -2;
		}

		currentHealth = newHealth;
		currentUpdateCounter = newUpdateCounter;

		MC.getTextureManager().bindTexture(ICONS);
		super.renderUnsafe(bounds, contentAlignment);
	}
}
