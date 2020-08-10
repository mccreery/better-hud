package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.events.OverlayContext;
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

public class SignReader extends OverlayElement {
    private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");

    private SettingPosition position;

    public SignReader() {
        super("signReader");

        settings.addChild(position = new SettingPosition(DirectionOptions.CORNERS, DirectionOptions.NONE));
    }

    @Override
    public boolean shouldRender(OverlayContext context) {
        return getSign() != null;
    }

    @Override
    public Rect render(OverlayContext context) {
        Rect bounds = position.applyTo(new Rect(96, 48));

        MC.getTextureManager().bindTexture(SIGN_TEXTURE);
        new Quad().setTexture(new Rect(2, 2, 24, 12).scale(4, 8)).setBounds(bounds).render();

        List<Label> labels = Stream.of(getSign().signText)
            .map(line -> new Label(line.getFormattedText()).setColor(Color.BLACK).setShadow(false))
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
        if (MC == null || MC.world == null) {
            return null;
        }

        // Functional approach avoids long null check chain
        return Optional.ofNullable(MC.getRenderViewEntity())
            .map(entity -> entity.rayTrace(200, 1.0f))
            .map(RayTraceResult::getBlockPos)
            .map(MC.world::getTileEntity)
            .filter(TileEntitySign.class::isInstance)
            .map(TileEntitySign.class::cast)
            .orElse(null);
    }
}
