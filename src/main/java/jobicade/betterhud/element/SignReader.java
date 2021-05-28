package jobicade.betterhud.element;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.render.Quad;
import net.minecraft.client.Minecraft;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SignReader extends HudElement {
    private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        position.setPreset(Direction.NORTH_WEST);
    }

    public SignReader() {
        super("signReader", new SettingPosition(DirectionOptions.CORNERS, DirectionOptions.NONE));
    }

    @Override
    public boolean shouldRender(Event event) {
        return getSign() != null;
    }

    @Override
    public Rect render(Event event) {
        Rect bounds = position.applyTo(new Rect(96, 48));

        Minecraft.getInstance().getTextureManager().bind(SIGN_TEXTURE);
        new Quad().setTexture(new Rect(2, 2, 24, 12).scale(4, 8)).setBounds(bounds).render();

        List<Label> labels = Stream.of(getSign().messages)
            .map(line -> new Label(line.func_150254_d()).setColor(Color.BLACK).setShadow(false))
            .collect(Collectors.toList());

        Grid<Label> grid = new Grid<>(new Point(1, labels.size()), labels);
        grid.setBounds(bounds.grow(-3)).render();

        return bounds;
    }

    /**
     * Finds the sign directly in the player's line of sight.
     *
     * @return The sign the player is looking at or {@code null} if the player
     * is not looking at a sign.
     */
    private TileEntitySign getSign() {
        // Sanity check, but can continue normally if null
        if (Minecraft.getInstance() == null || Minecraft.getInstance().level == null) {
            return null;
        }

        // Functional approach avoids long null check chain
        return Optional.ofNullable(Minecraft.getInstance().getCameraEntity())
            .map(entity -> entity.func_174822_a(200, 1.0f))
            .map(RayTraceResult::func_178782_a)
            .map(Minecraft.getInstance().level::getBlockEntity)
            .filter(TileEntitySign.class::isInstance)
            .map(TileEntitySign.class::cast)
            .orElse(null);
    }
}
