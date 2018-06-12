package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.WIDGETS;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPositionAligned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;

public class Hotbar extends OverrideElement {
	private SettingPosition position = new SettingPositionAligned("position", Direction.CORNERS | Direction.getFlags(Direction.NORTH, Direction.SOUTH), Direction.ALL)
		.setEdge(true).setPostSpacer(2);

	public Hotbar() {
		super("hotbar");
		settings.add(position);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.set(Direction.SOUTH);
	}

	@Override
	protected ElementType getType() {
		return ElementType.HOTBAR;
	}

	@Override
	public boolean shouldRender(Event event) {
		return MC.getRenderViewEntity() instanceof EntityPlayer && super.shouldRender(event);
	}

	@Override
	protected Bounds render(Event event) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		MC.getTextureManager().bindTexture(WIDGETS);

		Bounds barTexture = new Bounds(182, 22);
		Bounds selected = new Bounds(0, 22, 24, 24);

		Bounds bar = position.applyTo(new Bounds(barTexture));

		EntityPlayer player = (EntityPlayer)MC.getRenderViewEntity();

		GlUtil.enableBlendTranslucent();

		GlUtil.drawTexturedModalRect(bar.getPosition(), barTexture);
		GlUtil.drawTexturedModalRect(bar.getPosition().add(-1 + player.inventory.currentItem * 20, -1), selected);

		GlUtil.enableBlendTranslucent();
		GlStateManager.enableRescaleNormal();
		RenderHelper.enableGUIStandardItemLighting();

		for (int i = 0; i < 9; i++) {
			renderHotbarItem(bar.getPosition().add(3 + i*20, 3), getPartialTicks(event), player, player.inventory.mainInventory.get(i));
		}

		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
		return bar;
	}

	public static void renderHotbarItem(Point position, float partialTicks, EntityPlayer player, ItemStack stack) {
		if(!stack.isEmpty()) {
			float animationTicks = (float)stack.getAnimationsToGo() - partialTicks;

			if(animationTicks > 0.0F) {
				float factor = 1 + animationTicks / 5;

				GlStateManager.pushMatrix();
				GlStateManager.translate(position.getX() + 8, position.getY() + 12, 0);
				GlStateManager.scale(1 / factor, (factor + 1) / 2, 1);
				GlStateManager.translate(-(position.getX() + 8), -(position.getY() + 12), 0.0F);
			}

			MC.getRenderItem().renderItemAndEffectIntoGUI(player, stack, position.getX(), position.getY());

			if(animationTicks > 0.0F) {
				GlStateManager.popMatrix();
			}

			MC.getRenderItem().renderItemOverlays(MC.fontRenderer, stack, position.getX(), position.getY());
		}
	}
}
