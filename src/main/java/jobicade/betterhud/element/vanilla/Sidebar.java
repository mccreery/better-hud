package jobicade.betterhud.element.vanilla;

import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static jobicade.betterhud.BetterHud.MANAGER;

public class Sidebar extends HudElement {
    public Sidebar() {
        super("scoreboard", new SettingPosition(DirectionOptions.LEFT_RIGHT, DirectionOptions.WEST_EAST));
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
    }

    @Override
    public boolean shouldRender(Event event) {
        return getObjective(Minecraft.getInstance().player) != null && super.shouldRender(event);
    }

    @Override
    protected Rect render(Event event) {
        ScoreObjective objective = getObjective(Minecraft.getInstance().player);
        List<Score> scores = getScores(objective);

        Label title = new Label(objective.getDisplayName()).setShadow(false);
        List<Label> names  = new ArrayList<>(scores.size());
        List<Label> values = new ArrayList<>(scores.size());

        Color valueColor = new Color(255, 255, 55, 55);

        for(Score score : scores) {
            String name = score.getOwner();
            String formattedName = ScorePlayerTeam.func_96667_a(objective.getScoreboard().getPlayersTeam(name), name);
            String points = String.valueOf(score.getScore());

            names.add(new Label(formattedName).setShadow(false));
            values.add(new Label(points).setColor(valueColor).setShadow(false));
        }

        Grid<Label> namesGroup = new Grid<>(new Point(1, names.size()), names).setStretch(true).setCellAlignment(position.getContentAlignment().mirrorCol());
        Grid<Label> valuesGroup = new Grid<>(new Point(1, values.size()), values).setStretch(true).setCellAlignment(position.getContentAlignment());

        int spaceWidth = Minecraft.getInstance().font.func_78263_a(' ');
        Size size = namesGroup.getPreferredSize().add(valuesGroup.getPreferredSize().getX() + spaceWidth * 2, 0);

        int tWidth = title.getPreferredSize().getWidth();
        if(tWidth > size.getWidth()) size = size.withWidth(tWidth);

        Rect padding = Rect.createPadding(1, Minecraft.getInstance().font.lineHeight + 1, 1, 1);
        Rect bounds = new Rect(size).grow(padding);

        if(!position.isCustom() && position.getDirection().getRow() == 1) {
            bounds = bounds.anchor(MANAGER.getScreen(), position.getDirection());
        } else {
            bounds = position.applyTo(bounds);
        }
        Rect inner = bounds.grow(padding.invert());

        // Translucent background
        int titleBottom = inner.getTop() - 1;
        GlUtil.drawRect(bounds.withBottom(titleBottom), new Color(96, 0, 0, 0));
        GlUtil.drawRect(bounds.withTop(titleBottom), new Color(80, 0, 0, 0));

        title.setBounds(new Rect(title.getPreferredSize()).anchor(bounds.grow(-1), Direction.NORTH)).render();
        namesGroup.setBounds(inner).render();
        valuesGroup.setBounds(inner).render();
        return bounds;
    }

    private boolean showScore(Score score) {
        return !score.getOwner().startsWith("#");
    }

    /**
     * Gets a maximum of 15 scores for the given objective, in descending order.
     * @param objective The objective.
     * @return A list of scores for the given objective.
     */
    private List<Score> getScores(ScoreObjective objective) {
        List<Score> scores = objective.getScoreboard().getPlayerScores(objective).stream()
            .filter(this::showScore).collect(Collectors.toCollection(ArrayList::new));

        Collections.reverse(scores);
        if(scores.size() > 15) scores = scores.subList(0, 15);

        return scores;
    }

    /**
     * Gets the objective in the player's sidebar slot.
     * @param player The player.
     * @return The objective in the player's sidebar slot.
     * @see net.minecraft.client.gui.GuiIngame#renderGameOverlay(float)
     */
    private ScoreObjective getObjective(PlayerEntity player) {
        Scoreboard scoreboard = player.getScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam team = scoreboard.getPlayersTeam(Minecraft.getInstance().player.func_70005_c_());

        if(team != null) {
            int slot = team.getColor().getId();

            if(slot >= 0) {
                objective = scoreboard.getDisplayObjective(3 + slot);
            }
        }

        if(objective == null) {
            objective = scoreboard.getDisplayObjective(1);
        }
        return objective;
    }
}
