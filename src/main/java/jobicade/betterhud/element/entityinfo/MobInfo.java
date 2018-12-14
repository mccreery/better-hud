package jobicade.betterhud.element.entityinfo;

import static jobicade.betterhud.BetterHud.ICONS;
import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.bars.StatBarHealth;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.geom.Point;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.render.Color;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;

public class MobInfo extends EntityInfo {
	private final StatBarHealth bar = new StatBarHealth();
	private SettingSlider compress;

	public MobInfo() {
		super("mobInfo");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(compress = new SettingSlider("compress", 0, 200, 20) {
			@Override
			public String getDisplayValue(double value) {
				if(value == 0) {
					return I18n.format("betterHud.value.never");
				} else {
					return super.getDisplayValue(value);
				}
			}
		});
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

		Rect bounds = MANAGER.position(Direction.SOUTH, new Rect(size).grow(SPACER));
		GlUtil.drawRect(bounds, Color.TRANSLUCENT);
		bounds = bounds.grow(-SPACER);

		GlUtil.drawString(text, bounds.getPosition(), Direction.NORTH_WEST, Color.WHITE);
		Rect barRect = new Rect(barSize).anchor(bounds, Direction.SOUTH_WEST);

		MC.getTextureManager().bindTexture(ICONS);
		bar.render(barRect, Direction.NORTH_WEST);
	}
}
