package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.WIDGETS;

import java.util.AbstractList;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.spectator.ISpectatorMenuObject;
import net.minecraft.client.gui.spectator.ISpectatorMenuRecipient;
import net.minecraft.client.gui.spectator.SpectatorMenu;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.fml.common.eventhandler.Event;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.Renderable;

public class Hotbar extends OverrideElement implements ISpectatorMenuRecipient {
	private static final ResourceLocation SPECTATOR_WIDGETS = new ResourceLocation("textures/gui/spectator_widgets.png");

	private SettingPosition position = new SettingPosition("position", Options.TOP_BOTTOM, Options.NONE)
		.setEdge(true).setPostSpacer(2);

	private SpectatorMenu menu;
	private long lastSelectionTime;

	public Hotbar() {
		super("hotbar");
		settings.add(position);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		position.setPreset(Direction.SOUTH);
	}

	@Override
	protected ElementType getType() {
		return ElementType.HOTBAR;
	}

	public void onHotbarSelected(int i) {
		this.lastSelectionTime = Minecraft.getSystemTime();

		if(menu != null) {
			menu.selectSlot(i);
		} else {
			menu = new SpectatorMenu(this);
		}
	}

	@Override
	public void onSpectatorMenuClosed(SpectatorMenu menu) {
		menu = null;
		lastSelectionTime = 0;
	}

	@Override
	public boolean shouldRender(Event event) {
		if(MC.player.isSpectator()) {
			if(menu == null) {
				return false;
			} else if(getSpectatorAlpha() <= 0) {
				menu.exit();
				return false;
			}
		}
		return super.shouldRender(event);
	}

	@Override
	protected Bounds render(Event event) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		GlUtil.enableBlendTranslucent();
		MC.getTextureManager().bindTexture(WIDGETS);
	
		Bounds barTexture = new Bounds(182, 22);
		Bounds bar = position.applyTo(new Bounds(barTexture));
	
		GlUtil.drawTexturedModalRect(bar.getPosition(), barTexture);
	
		renderSelectionBox(bar, getSelected());
	
		GlUtil.enableBlendTranslucent();
		GlStateManager.enableRescaleNormal();
		RenderHelper.enableGUIStandardItemLighting();
	
		List<Renderable> hotbarIcons = getHotbarIcons(event);
		for(int i = 0; i < hotbarIcons.size(); i++) {
			hotbarIcons.get(i).render(getSlotBounds(bar, i), null);
		}
	
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableRescaleNormal();
		GlStateManager.disableBlend();
	
		return bar;
	}

	private Bounds getSlotBounds(Bounds bounds, int i) {
		bounds = bounds.withInset(3);
		return bounds.withWidth(bounds.getHeight()).shiftedBy(Direction.EAST, i * 20);
	}

	private int getSelected() {
		return MC.player.isSpectator() ? menu.getSelectedSlot() : MC.player.inventory.currentItem;
	}

	private List<Renderable> getHotbarIcons(Event event) {
		return new AbstractList<Renderable>() {
			@Override
			public Renderable get(int i) {
				return new Renderable() {
					@Override
					protected void renderUnsafe(Bounds bounds, Direction contentAlignment) {
						if(MC.player.isSpectator()) {
							renderSpectatorObject(bounds, menu.getItem(i), i, getSpectatorAlpha());
						} else {
							GlUtil.renderHotbarItem(bounds, MC.player.inventory.mainInventory.get(i), getPartialTicks(event));
						}
					}

					@Override
					public Point getSize() {
						return new Point(16, 16);
					}
				};
			}

			@Override
			public int size() {
				return 9;
			}
		};
	}

	private void renderSpectatorObject(Bounds bounds, ISpectatorMenuObject object, int i, float alpha) {
		if(object == SpectatorMenu.EMPTY_SLOT) return;
	
		MC.getTextureManager().bindTexture(SPECTATOR_WIDGETS);
	
		GlStateManager.pushMatrix();
		GlStateManager.translate((float)bounds.getX(), bounds.getY(), 0);
	
		float brightness = object.isEnabled() ? 1f : 0.25f;
		GlStateManager.color(brightness, brightness, brightness, alpha);
	
		int a = (int)(alpha * 255);
	
		object.renderIcon(brightness, a);
		GlStateManager.popMatrix();
	
		if(a > 3 && object.isEnabled()) {
			String s = MC.gameSettings.keyBindsHotbar[i].getDisplayName();
			int color = Colors.setAlpha(Colors.WHITE, a);
	
			GlUtil.drawString(s, bounds.getPosition().add(17, 9), Direction.NORTH_EAST, color);
		}
	}

	private static void renderSelectionBox(Bounds bounds, int i) {
		Bounds texture = new Bounds(0, 22, 24, 24);
		GlUtil.drawTexturedModalRect(texture.anchoredTo(bounds.withPadding(1), Direction.WEST).shiftedBy(Direction.EAST, i * 20), texture);
	}

	private float getSpectatorAlpha() {
		long deltaTime = Minecraft.getSystemTime() - lastSelectionTime;
		return MathHelper.clamp((5000 - deltaTime) / 2000f, 0, 1);
	}
}
