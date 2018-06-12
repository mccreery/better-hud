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
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.StringGroup;

public class Sidebar extends OverrideElement {
	public Sidebar() {
		super("scoreboard");
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

		StringGroup namesGroup = new StringGroup(names);
		StringGroup valuesGroup = new StringGroup(values).setColor(0xffff5555).setAlignment(Direction.EAST);
		Point valuesSize = valuesGroup.getSize();

		Point size = namesGroup.getSize();
		size.x += MC.fontRenderer.getCharWidth(' ') * 2 + valuesSize.x;
		size.x = Math.max(size.x, MC.fontRenderer.getStringWidth(title));

		PaddedBounds bounds = new PaddedBounds(new Bounds(size), Bounds.getPadding(0, MC.fontRenderer.FONT_HEIGHT + 1, 0, 0), Bounds.getPadding(1));
		Direction.EAST.anchor(bounds,MANAGER.getScreen());

		// Translucent background
		Bounds background = new Bounds(bounds);
		background.bottom(bounds.contentBounds().top() - 1);
		GlUtil.drawRect(background, 0x60000000);

		background.top(background.bottom());
		background.bottom(bounds.bottom());
		GlUtil.drawRect(background, 0x50000000);

		GlUtil.drawString(title, Direction.NORTH.getAnchor(bounds.paddingBounds()), Direction.NORTH, Colors.WHITE);
		namesGroup.draw(bounds.contentBounds());
		valuesGroup.draw(bounds.contentBounds());

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
