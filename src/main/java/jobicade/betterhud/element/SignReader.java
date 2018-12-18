package jobicade.betterhud.element;

import static jobicade.betterhud.BetterHud.MC;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Event;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.util.GlUtil;
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
	public Rect render(Event event) {
		RayTraceResult trace = MC.getRenderViewEntity().rayTrace(200, 1.0F);

		IBlockState state = MC.world.getBlockState(trace.getBlockPos());
		if(state == null || !(state.getBlock() instanceof ITileEntityProvider)) {
			return null;
		}

		TileEntity tileEntity = MC.world.getTileEntity(trace.getBlockPos());
		if(tileEntity == null || !(tileEntity instanceof TileEntitySign)) {
			return null;
		}
		TileEntitySign sign = (TileEntitySign)tileEntity;

		Rect bounds = position.applyTo(new Rect(96, 48));

		MC.getTextureManager().bindTexture(SIGN_TEXTURE);
		Gui.drawScaledCustomSizeModalRect(bounds.getX(), bounds.getY(), 2, 2, 24, 12, 96, 48, 64, 32);

		Point pos = bounds.getAnchor(Direction.NORTH).add(0, 3);
		for(ITextComponent line : sign.signText) {
			if(line != null) {
				GlUtil.drawString(line.getFormattedText(), pos, Direction.NORTH, Color.BLACK);
			}
			pos = pos.add(0, MC.fontRenderer.FONT_HEIGHT + 2);
		}

		return bounds;
	}
}
