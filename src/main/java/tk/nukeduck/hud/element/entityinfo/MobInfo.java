package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.MathUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.bars.StatBarHealth;

public class MobInfo extends EntityInfo {
	private final StatBarHealth bar = new StatBarHealth();

	private final SettingSlider compress = new SettingSlider("compress", 0, 200, 20) {
		@Override
		public String getDisplayValue(double value) {
			if(value == 0) {
				return I18n.format("betterHud.value.never");
			} else {
				return super.getDisplayValue(value);
			}
		}
	};

	public MobInfo() {
		super("mobInfo");
		settings.add(compress);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		compress.set(40.0);
		settings.priority.set(-1);
	}

	@Override
	public void render(EntityLivingBase entity) {
		bar.setHost(entity);
		bar.setCompressThreshold(compress.getInt());

		int health = MathUtil.getHealthForDisplay(entity.getHealth());
		int maxHealth = MathUtil.getHealthForDisplay(entity.getMaxHealth());

		String text = String.format("%s %s(%d/%d)", entity.getName(), ChatFormatting.GRAY, health, maxHealth);

		Point size = GlUtil.getStringSize(text);
		Point barSize = bar.getSize();

		if(barSize.getX() > size.getX()) {
			size = new Point(barSize.getX(), size.getY() + barSize.getY());
		} else {
			size = size.add(0, barSize.getY());
		}

		Bounds bounds = MANAGER.position(Direction.SOUTH, new Bounds(size).grow(SPACER));
		GlUtil.drawRect(bounds, Colors.TRANSLUCENT);
		bounds = bounds.grow(-SPACER);

		GlUtil.drawString(text, bounds.getPosition(), Direction.NORTH_WEST, Colors.WHITE);
		Bounds barBounds = new Bounds(barSize).anchor(bounds, Direction.SOUTH_WEST);

		MC.getTextureManager().bindTexture(ICONS);
		bar.render(barBounds, Direction.NORTH_WEST);
	}
}
