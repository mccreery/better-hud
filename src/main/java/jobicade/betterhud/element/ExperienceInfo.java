package jobicade.betterhud.element;

import java.util.ArrayList;
import java.util.List;

import jobicade.betterhud.element.settings.Legend;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.text.TextElement;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.registry.OverlayElements;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.RenderGameOverlayEvent;

public class ExperienceInfo extends TextElement {
	private SettingBoolean total;
	private SettingBoolean lifetime;

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		total.set(false);
		lifetime.set(false);
		settings.priority.set(2);
	}

	public ExperienceInfo() {
		setRegistryName("experience_info");
		setUnlocalizedName("experienceInfo");

		settings.addChildren(
			new Legend("misc"),
			total = new SettingBoolean("showTotalExp").setValuePrefix(SettingBoolean.VISIBLE),
			lifetime = new SettingBoolean("showLifetimeExp").setValuePrefix(SettingBoolean.VISIBLE)
		);
	}

	@Override
	public boolean shouldRender(RenderGameOverlayEvent context) {
		return Minecraft.getMinecraft().playerController.gameIsSurvivalOrAdventure();
	}

	@Override
	public Rect render(RenderGameOverlayEvent context) {
		int fullBar = getExperienceWithinLevel(Minecraft.getMinecraft().player.experienceLevel);

		int has = (int)(Minecraft.getMinecraft().player.experience * fullBar);
		int needed = fullBar - has;

		String hasDisplay = String.valueOf(has);
		String neededDisplay = String.valueOf(needed);

		Rect bar = OverlayElements.EXPERIENCE.getLastBounds();

		Point text = new Rect(GlUtil.getStringSize(hasDisplay).sub(0, 2)).anchor(bar, Direction.WEST).getPosition();
		GlUtil.drawBorderedString(hasDisplay, text.getX(), text.getY(), Color.WHITE);

		text = new Rect(GlUtil.getStringSize(neededDisplay).sub(0, 2)).anchor(bar, Direction.EAST).getPosition();
		GlUtil.drawBorderedString(neededDisplay, text.getX(), text.getY(), Color.WHITE);

		return super.render(context);
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
			int totalDisplay = getExperienceToLevel(Minecraft.getMinecraft().player.experienceLevel);
			totalDisplay += Minecraft.getMinecraft().player.experience * getExperienceWithinLevel(Minecraft.getMinecraft().player.experienceLevel);

			parts.add(total.getLocalizedName() + ": "+ totalDisplay);
		}
		if(lifetime.get()) {
			parts.add(lifetime.getLocalizedName() + ": " + Minecraft.getMinecraft().player.experienceTotal);
		}
		return parts;
	}
}
