package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;

import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.text.TextElement;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class ExperienceInfo extends TextElement {
	private final SettingBoolean total = new SettingBoolean("showTotalExp").setUnlocalizedValue(SettingBoolean.VISIBLE);
	private final SettingBoolean lifetime = new SettingBoolean("showLifetimeExp").setUnlocalizedValue(SettingBoolean.VISIBLE);

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		total.set(false);
		lifetime.set(false);
		settings.priority.set(2);
	}

	public ExperienceInfo() {
		super("experienceInfo");

		settings.add(new Legend("misc"));
		settings.add(total);
		settings.add(lifetime);
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && MC.playerController.gameIsSurvivalOrAdventure();
	}

	@Override
	public Bounds render(Event event) {
		int fullBar = getExperienceWithinLevel(MC.player.experienceLevel);

		int has = (int)(MC.player.experience * fullBar);
		int needed = fullBar - has;

		String hasDisplay = String.valueOf(has);
		String neededDisplay = String.valueOf(needed);

		Bounds bar = EXPERIENCE.getLastBounds();

		Point text = Direction.WEST.anchor(new Bounds(GlUtil.getStringSize(hasDisplay).sub(0, 2)), bar).position;
		GlUtil.drawBorderedString(hasDisplay, text.x, text.y, Colors.WHITE);

		text = Direction.EAST.anchor(new Bounds(GlUtil.getStringSize(neededDisplay).sub(0, 2)), bar).position;
		GlUtil.drawBorderedString(neededDisplay, text.x, text.y, Colors.WHITE);

		return super.render(event);
	}

	/** @param level The player's current level
	 * @return The total amount of experience in the current experience bar
	 * @see <a href="https://minecraft.gamepedia.com/Experience#Leveling_up">Levelling Up</a> */
	private static int getExperienceWithinLevel(int level) {
		if (level >= 31) {
			return 9 * level - 158;
		} else if (level >= 16) {
			return 5 * level - 38;
		} else {
			return 2 * level + 7;
		}
	}

	/** @return The total amount of experience required to reach {@code level}
	 * @see <a href="https://minecraft.gamepedia.com/Experience#Leveling_up">Levelling Up</a> */
	private static int getExperienceToLevel(int level) {
		/* Result is always integer despite real coefficients
		 * because level and level^2 are either both odd or both even */

		if(level >= 32) {
			return (int)((4.5 * level - 162.5) * level) + 2220;
		} else if(level >= 17) {
			return (int)((2.5 * level - 40.5) * level) + 360;
		} else {
			return (level + 6) * level;
		}
	}

	@Override
	protected List<String> getText() {
		List<String> parts = new ArrayList<String>(2);

		if(total.get()) {
			int totalDisplay = getExperienceToLevel(MC.player.experienceLevel);
			totalDisplay += MC.player.experience * getExperienceWithinLevel(MC.player.experienceLevel);

			parts.add(total.getLocalizedName() + ": "+ totalDisplay);
		}
		if(lifetime.get()) {
			parts.add(lifetime.getLocalizedName() + ": " + MC.player.experienceTotal);
		}
		return parts;
	}
}
