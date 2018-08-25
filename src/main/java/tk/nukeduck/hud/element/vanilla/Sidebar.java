package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.StringGroup;

public class Sidebar extends OverrideElement {
	private final SettingPosition position = new SettingPosition("position", Options.LEFT_RIGHT, Options.WEST_EAST);

	public Sidebar() {
		super("scoreboard");
		settings.add(position);
	}

	@Override
	protected ElementType getType() {
		return null;
	}

	@Override
	public boolean shouldRender(Event event) {
		return getObjective() != null && super.shouldRender(event);
	}

	@Override
	protected Bounds render(Event event) {
		ScoreObjective objective = getObjective();
		Scoreboard scoreboard = objective.getScoreboard();

		List<Score> scores = Lists.newArrayList(Iterables.filter(scoreboard.getSortedScores(objective),
			(score) -> score.getPlayerName() != null && !score.getPlayerName().startsWith("#")));

		if(scores.isEmpty()) {
			return null;
		}

		scores = Lists.reverse(scores);

		if(scores.size() > 15) {
			scores = Lists.newArrayList(Iterables.skip(scores, scores.size() - 15));
		}

		String title = objective.getDisplayName();
		List<String> names  = new ArrayList<>(scores.size());
		List<String> values = new ArrayList<>(scores.size());

		for(Score score : scores) {
			String name = score.getPlayerName();

			names.add(ScorePlayerTeam.formatPlayerName(scoreboard.getPlayersTeam(name), name));
			values.add(String.valueOf(score.getScorePoints()));
		}

		StringGroup namesGroup = new StringGroup(names).setAlignment(position.getContentAlignment().mirrorColumn());
		StringGroup valuesGroup = new StringGroup(values).setColor(0xffff5555).setAlignment(position.getContentAlignment());
		Point valuesSize = valuesGroup.getSize();

		Point size = namesGroup.getSize().add(MC.fontRenderer.getCharWidth(' ') * 2 + valuesSize.getX(), 0);
		size = size.withX(Math.max(size.getX(), MC.fontRenderer.getStringWidth(title)));

		Bounds padding = Bounds.createPadding(0, MC.fontRenderer.FONT_HEIGHT + 1, 0, 0);
		Bounds margin = Bounds.createPadding(1);
		Bounds bounds = new Bounds(size).grow(padding).grow(margin);

		if(!position.isCustom() && position.getDirection().getRow() == 1) {
			bounds = bounds.anchor(MANAGER.getScreen(), position.getDirection());
		} else {
			bounds = position.applyTo(bounds);
		}
		Bounds paddingBounds = bounds.grow(margin.scale(-1));
		Bounds contentBounds = paddingBounds.grow(padding.scale(-1));

		// Translucent background
		Bounds background = new Bounds(bounds).withBottom(contentBounds.getTop() - 1);
		GlUtil.drawRect(background, 0x60000000);

		background = background.withTop(background.getBottom()).withBottom(bounds.getBottom());
		GlUtil.drawRect(background, 0x50000000);

		GlUtil.drawString(title, paddingBounds.getAnchor(Direction.NORTH), Direction.NORTH, Colors.WHITE);
		namesGroup.draw(contentBounds);
		valuesGroup.draw(contentBounds);

		return bounds;
	}

	/** @return The objective to display in the sidebar for the player */
	private ScoreObjective getObjective() {
		Scoreboard scoreboard = MC.world.getScoreboard();
		ScorePlayerTeam team = scoreboard.getPlayersTeam(MC.player.getName());

		if(team != null) {
			int slot = team.getColor().getColorIndex();

			if(slot >= 0) {
				return scoreboard.getObjectiveInDisplaySlot(3 + slot);
			}
		}
		return scoreboard.getObjectiveInDisplaySlot(1);
	}
}
