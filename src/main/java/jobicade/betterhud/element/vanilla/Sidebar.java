package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.StringGroup;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.fml.common.eventhandler.Event;

public class Sidebar extends HudElement {
	private SettingPosition position;

	public Sidebar() {
		super("scoreboard");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(position = new SettingPosition("position", DirectionOptions.LEFT_RIGHT, DirectionOptions.WEST_EAST));
	}

	@Override
	public boolean shouldRender(Event event) {
		return getObjective() != null && super.shouldRender(event);
	}

	@Override
	protected Rect render(Event event) {
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

		StringGroup namesGroup = new StringGroup(names).setAlignment(position.getContentAlignment().mirrorCol());
		StringGroup valuesGroup = new StringGroup(values).setColor(new Color(255, 255, 55, 55)).setAlignment(position.getContentAlignment());
		Point valuesSize = valuesGroup.getSize();

		Point size = namesGroup.getSize().add(MC.fontRenderer.getCharWidth(' ') * 2 + valuesSize.getX(), 0);
		size = size.withX(Math.max(size.getX(), MC.fontRenderer.getStringWidth(title)));

		Rect padding = Rect.createPadding(0, MC.fontRenderer.FONT_HEIGHT + 1, 0, 0);
		Rect margin = Rect.createPadding(1);
		Rect bounds = new Rect(size).grow(padding).grow(margin);

		if(!position.isCustom() && position.getDirection().getRow() == 1) {
			bounds = bounds.anchor(MANAGER.getScreen(), position.getDirection());
		} else {
			bounds = position.applyTo(bounds);
		}
		Rect paddingRect = bounds.grow(margin.invert());
		Rect contentRect = paddingRect.grow(padding.invert());

		// Translucent background
		Rect background = new Rect(bounds).withBottom(contentRect.getTop() - 1);
		GlUtil.drawRect(background, new Color(96, 0, 0, 0));

		background = background.withTop(background.getBottom()).withBottom(bounds.getBottom());
		GlUtil.drawRect(background, new Color(80, 0, 0, 0));

		GlUtil.drawString(title, paddingRect.getAnchor(Direction.NORTH), Direction.NORTH, Color.WHITE);
		namesGroup.draw(contentRect);
		valuesGroup.draw(contentRect);

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
