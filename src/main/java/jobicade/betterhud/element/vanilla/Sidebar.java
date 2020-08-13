package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import jobicade.betterhud.element.OverlayElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.ScorePlayerTeam;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.client.gui.ForgeIngameGui;


public class Sidebar extends OverlayElement {
    private SettingPosition position;

    public Sidebar() {
        super("scoreboard");

        position = new SettingPosition(this, "position");
        position.setDirectionOptions(DirectionOptions.LEFT_RIGHT);
        position.setContentOptions(DirectionOptions.WEST_EAST);
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return ForgeIngameGui.renderObjective
            && getObjective(MC.player) != null;
    }

    @Override
    public Rect render(OverlayContext context) {
        ScoreObjective objective = getObjective(MC.player);
        List<Score> scores = getScores(objective);

        Label title = new Label(objective.getDisplayName().getFormattedText()).setShadow(false);
        List<Label> names  = new ArrayList<>(scores.size());
        List<Label> values = new ArrayList<>(scores.size());

        Color valueColor = new Color(255, 255, 55, 55);

        for(Score score : scores) {
            ScorePlayerTeam scoreplayerteam = objective.getScoreboard().getPlayersTeam(score.getPlayerName());
            String formattedName = ScorePlayerTeam.formatMemberName(scoreplayerteam, new StringTextComponent(score.getPlayerName())).getFormattedText();
            String points = String.valueOf(score.getScorePoints());

            names.add(new Label(formattedName).setShadow(false));
            values.add(new Label(points).setColor(valueColor).setShadow(false));
        }

        Grid<Label> namesGroup = new Grid<>(new Point(1, names.size()), names).setStretch(true).setCellAlignment(position.getContentAlignment().mirrorCol());
        Grid<Label> valuesGroup = new Grid<>(new Point(1, values.size()), values).setStretch(true).setCellAlignment(position.getContentAlignment());

        float spaceWidth = MC.fontRenderer.getCharWidth(' ');
        Size size = namesGroup.getPreferredSize().add(valuesGroup.getPreferredSize().getX() + Math.round(spaceWidth * 2), 0);

        int tWidth = title.getPreferredSize().getWidth();
        if(tWidth > size.getWidth()) size = size.withWidth(tWidth);

        Rect padding = Rect.createPadding(1, MC.fontRenderer.FONT_HEIGHT + 1, 1, 1);
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
        return !score.getPlayerName().startsWith("#");
    }

    /**
     * Gets a maximum of 15 scores for the given objective, in descending order.
     * @param objective The objective.
     * @return A list of scores for the given objective.
     */
    private List<Score> getScores(ScoreObjective objective) {
        List<Score> scores = objective.getScoreboard().getSortedScores(objective).stream()
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
        Scoreboard scoreboard = player.getWorldScoreboard();
        ScoreObjective objective = null;
        ScorePlayerTeam team = scoreboard.getPlayersTeam(MC.player.getName().getString());

        if(team != null) {
            int slot = team.getColor().getColorIndex();

            if(slot >= 0) {
                objective = scoreboard.getObjectiveInDisplaySlot(3 + slot);
            }
        }

        if(objective == null) {
            objective = scoreboard.getObjectiveInDisplaySlot(1);
        }
        return objective;
    }
}
