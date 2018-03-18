package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.Gui;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntitySign;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;

public class SignReader extends HudElement {
	private static final ResourceLocation SIGN_TEXTURE = new ResourceLocation("textures/entity/sign.png");

	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS);

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.NORTH_WEST);
	}

	public SignReader() {
		super("signReader");
		settings.add(position);
	}

	@Override
	public Bounds render(Event event) {
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

		Bounds bounds = position.applyTo(new Bounds(96, 48));

		MC.getTextureManager().bindTexture(SIGN_TEXTURE);
		Gui.drawScaledCustomSizeModalRect(bounds.x(), bounds.y(), 2, 2, 24, 12, 96, 48, 64, 32);

		int y = bounds.y() + 3;

		for(ITextComponent line : sign.signText) {
			if(line != null) {
				String text = line.getFormattedText();
				int textWidth = MC.fontRenderer.getStringWidth(text);

				MC.fontRenderer.drawString(text, bounds.x() + (bounds.width() - textWidth) / 2, y, Colors.BLACK);
				//MC.ingameGUI.drawCenteredString(MC.fontRenderer, text, bounds.x() + bounds.width() / 2, y, Colors.BLACK);
			}
			y += MC.fontRenderer.FONT_HEIGHT + 2;
		}

		return bounds;
	}
}
