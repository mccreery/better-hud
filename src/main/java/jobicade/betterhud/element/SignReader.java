package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.render.Quad;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;

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

		MC.getTextureManager().bindTexture(SIGN_TEXTURE);
		new Quad().setTexture(new Rect(2, 2, 24, 12).scale(4, 8)).render(bounds);

		List<Label> labels = Stream.of(getSign().signText)
			.map(line -> new Label(line.getFormattedText()).setColor(Color.BLACK).setShadow(false))
			.collect(Collectors.toList());

		Grid<Label> grid = new Grid<>(new Point(1, labels.size()), labels);
		grid.render(bounds.grow(-3));

		return bounds;
	}

	private TileEntitySign getSign() {
		RayTraceResult trace = MC.getRenderViewEntity().rayTrace(200, 1.0F);
		IBlockState state = MC.world.getBlockState(trace.getBlockPos());

		if(state.getBlock() instanceof ITileEntityProvider) {
			TileEntity tileEntity = MC.world.getTileEntity(trace.getBlockPos());

			if(tileEntity instanceof TileEntitySign) {
				return (TileEntitySign)tileEntity;
			}
		}
		return null;
	}
}
