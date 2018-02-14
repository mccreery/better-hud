package tk.nukeduck.hud.util;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MC;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.Profile;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.client.config.GuiUtils;

public class GlUtil {
	private static final double TEXTURE_NORMALIZE = 1.0 / 256.0;

	/** Enables general blending for translucent primitives */
	public static void enableBlendTranslucent() {
		GlStateManager.enableBlendProfile(Profile.PLAYER_SKIN);
	}

	/** Sets the OpenGL color to the 32-bit RGBA composite color */
	public static void color(int color) {
		GlStateManager.color(Colors.red(color), Colors.green(color), Colors.blue(color), Colors.alpha(color));
	}

	/** All axes default to {@code scale}
	 * @see GlStateManager#scale(float, float, float) */
	public static void scale(float scale) {
		GlStateManager.scale(scale, scale, scale);
	}

	/** @see Gui#drawRect(int, int, int, int, int) */
	public static void drawRect(Bounds bounds, int color) {
		Gui.drawRect(bounds.left(), bounds.top(), bounds.right(), bounds.bottom(), color);
	}

	/** @see #drawTexturedModalRect(int, int, int, int, int, int) */
	public static void drawTexturedModalRect(Point position, Bounds texture) {
		drawTexturedModalRect(position.x, position.y, texture.x(), texture.y(), texture.width(), texture.height());
	}

	/** @see net.minecraft.client.gui.Gui#drawTexturedModalRect(int, int, int, int, int, int) */
	public static void drawTexturedModalRect(int x, int y, int u, int v, int width, int height) {
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder builder = tessellator.getBuffer();
		builder.begin(7, DefaultVertexFormats.POSITION_TEX);
	
		builder.pos(x,         y + height, 0).tex( u          * TEXTURE_NORMALIZE, (v + height) * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x + width, y + height, 0).tex((u + width) * TEXTURE_NORMALIZE, (v + height) * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x + width, y,          0).tex((u + width) * TEXTURE_NORMALIZE,  v           * TEXTURE_NORMALIZE).endVertex();
		builder.pos(x,         y,          0).tex( u          * TEXTURE_NORMALIZE,  v           * TEXTURE_NORMALIZE).endVertex();
	
		tessellator.draw();
	}

	/** Draws text with black borders on all sides */
	public static void drawBorderedString(String text, int x, int y, int color) {
		// Borders
		MC.fontRenderer.drawString(text, x + 1, y, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x - 1, y, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x, y + 1, Colors.BLACK, false);
		MC.fontRenderer.drawString(text, x, y - 1, Colors.BLACK, false);
	
		MC.fontRenderer.drawString(text, x, y, color, false);
	}

	/** @see #renderSingleItem(ItemStack, int, int) */
	public static void renderSingleItem(ItemStack stack, Point point) {
		renderSingleItem(stack, point.x, point.y);
	}

	/** Renders {@code stack} to the GUI, and reverts lighting side effects
	 *
	 * @see RenderHelper#enableGUIStandardItemLighting()
	 * @see net.minecraft.client.renderer.RenderItem#renderItemAndEffectIntoGUI(ItemStack, int, int)
	 * @see RenderHelper#disableStandardItemLighting() */
	public static void renderSingleItem(ItemStack stack, int x, int y) {
		RenderHelper.enableGUIStandardItemLighting();
		MC.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y);
		RenderHelper.disableStandardItemLighting();
	}

	/** @see GuiUtils#drawHoveringText(ItemStack, List, int, int, int, int, int, net.minecraft.client.gui.FontRenderer) */
	public static void drawTooltipBox(int x, int y, int w, int h) {
		enableBlendTranslucent();
		GlStateManager.disableRescaleNormal();
		RenderHelper.disableStandardItemLighting();
		GlStateManager.disableLighting();
		GlStateManager.disableDepth();

		final int zLevel	  = 300;
		final int bgColor	  = 0xb7100010;
		final int borderStart = 0x505000ff;
		final int borderEnd   = (borderStart & 0xfefefe) >> 1 | borderStart & 0xff000000;

		// Box
		GuiUtils.drawGradientRect(zLevel, x+1, y,	 x+w-1, y+1,   bgColor, bgColor); // Top
		GuiUtils.drawGradientRect(zLevel, x,   y+1,   x+w,   y+h-1, bgColor, bgColor); // Middle
		GuiUtils.drawGradientRect(zLevel, x+1, y+h-1, x+w-1, y+h,   bgColor, bgColor); // Bottom

		// Borders
		GuiUtils.drawGradientRect(zLevel, x+1,   y+1,   x+w-1, y+2,   borderStart, borderStart); // Top
		GuiUtils.drawGradientRect(zLevel, x+1,   y+2,   x+2,   y+h-2, borderStart, borderEnd);   // Left
		GuiUtils.drawGradientRect(zLevel, x+w-2, y+2,   x+w-1, y+h-2, borderStart, borderEnd);   // Right
		GuiUtils.drawGradientRect(zLevel, x+1,   y+h-2, x+w-1, y+h-1, borderEnd,   borderEnd);   // Bottom

		GlStateManager.enableDepth();
	}

	/** Applies transformations such that the Z axis faces directly towards the player
	 * and (0, 0) is translated to above {@code entity}'s head.
	 * <p>This is similar to the method used to render player names, but any functionality can be implemented
	 *
	 * @param scaleFactor Linearly affects the size of things drawn to the billboard
	 * @see net.minecraft.client.renderer.EntityRenderer#drawNameplate(net.minecraft.client.gui.FontRenderer, String, float, float, float, int, float, float, boolean, boolean) */
	public static void setupBillboard(Entity entity, float partialTicks, float scaleFactor) {
		double dx = (entity.prevPosX + (entity.posX - entity.prevPosX) * partialTicks) - (MC.player.prevPosX + (MC.player.posX - MC.player.prevPosX) * partialTicks); 
		double dy = (entity.prevPosY + (entity.posY - entity.prevPosY) * partialTicks) - (MC.player.prevPosY + (MC.player.posY - MC.player.prevPosY) * partialTicks); 
		double dz = (entity.prevPosZ + (entity.posZ - entity.prevPosZ) * partialTicks) - (MC.player.prevPosZ + (MC.player.posZ - MC.player.prevPosZ) * partialTicks);

		dy += entity.height + 0.5;
		GlStateManager.translate(dx, dy, dz);

		dy -= MC.player.getEyeHeight();
		float distance = (float)Math.sqrt(dx*dx + dy*dy + dz*dz);
		scale(distance * (scaleFactor + 0.5f) / 300f);

		GlStateManager.rotate(-MC.player.rotationYaw,  0, 1, 0);
		GlStateManager.rotate(MC.player.rotationPitch, 1, 0, 0);
		GlStateManager.rotate(180, 0, 0, 1);
	}

	/** The icons used are those for the health bar
	 * @see #renderBar(int, int, Point, Bounds, Bounds, Bounds) */
	public static void renderHealthBar(int health, int maxHealth, Point position) {
		MC.getTextureManager().bindTexture(ICONS);

		renderBar(health, maxHealth, position,
			new Bounds(16, 0, 9, 9), new Bounds(61, 0, 9, 9), new Bounds(52, 0, 9, 9));
	}

	/** The icons used are those for the armor bar
	 * @see #renderBar(int, int, Point, Bounds, Bounds, Bounds) */
	public static void renderArmorBar(int armor, int maxArmor, Point position) {
		MC.getTextureManager().bindTexture(ICONS);

		renderBar(armor, maxArmor, position,
			new Bounds(16, 9, 9, 9), new Bounds(25, 9, 9, 9), new Bounds(34, 9, 9, 9));
	}

	/** Renders a multi-row health bar with {@code health} full hearts
	 * and {@code maxHealth} total hearts
	 *
	 * @param background The texture coordinates for the background icon
	 * @param half The texture coordinates for the half unit icon
	 * @param full The texture coordinates for a full unit icon */
	public static void renderBar(int current, int max, Point position, Bounds background, Bounds half, Bounds full) {
		Point icon = new Point(position);

		color(Colors.WHITE);
		for(int i = 0; i < max; icon.x = position.x, icon.y += 9) {
			for(int j = 0; j < 20 && i < max; i += 2, j += 2, icon.x += 8) {
				if(background != null) {
					drawTexturedModalRect(icon, background);
				}

				if(i + 1 < current) {
					drawTexturedModalRect(icon, full);
				} else if(i < current) {
					drawTexturedModalRect(icon, half);
				}
			}
		}
	}

	/** {@code progress} defaults to the durability of {@code stack}
	 * @see #drawProgressBar(Bounds, float, boolean) */
	public static void drawDamageBar(Bounds bounds, ItemStack stack, boolean vertical) {
		float progress = (float)(stack.getMaxDamage() - stack.getItemDamage()) / stack.getMaxDamage();
		drawProgressBar(bounds, progress, vertical);
	}

	/** Draws a progress bar for item damage
	 * @param progress Index of progress between 0 and 1
	 * @param vertical {@code true} to render bar from bottom to top */
	public static void drawProgressBar(Bounds bounds, float progress, boolean vertical) {
		drawRect(bounds, Colors.BLACK);
		progress = MathHelper.clamp(progress, 0, 1);

		int color = Colors.fromHSV(progress / 3, 1, 1);

		Bounds bar;
		if(vertical) {
			bar = new Bounds(bounds.width() - 1, (int)(progress * bounds.height()));
			Direction.SOUTH_WEST.anchor(bar, bounds);
		} else {
			bar = new Bounds((int)(progress * bounds.width()), bounds.height() - 1);
			Direction.NORTH_WEST.anchor(bar, bounds);
		}
		drawRect(bar, color);
	}

	/** Draws a line of text aligned around {@code position} by {@code anchor} */
	public static Bounds drawString(String string, Point position, Direction anchor, int color) {
		Bounds bounds = anchor.align(new Bounds(position, getLinesSize(string)));
	
		MC.fontRenderer.drawStringWithShadow(string, bounds.x(), bounds.y(), color);
		return bounds;
	}

	/** @return The height of {@code lines} of text
	 * @see #drawLines(String[], Bounds, Direction, int) */
	public static int getLinesHeight(int lines) {
		return lines > 0 ? (MC.fontRenderer.FONT_HEIGHT + 2) * lines - 2 : 0;
	}

	/** @see #getLinesSize(Collection) */
	public static Point getLinesSize(String... strings) {
		return getLinesSize(Arrays.asList(strings));
	}

	/** @return The size of {@code strings}
	 * @see #drawLines(String[], Bounds, Direction, int) */
	public static Point getLinesSize(Collection<String> strings) {
		if(strings.isEmpty()) {
			return new Point(Point.ZERO);
		}
		int maxWidth = 0;

		for(String string : strings) {
			if(string != null) {
				int width = MC.fontRenderer.getStringWidth(string);
				if(width > maxWidth) maxWidth = width;
			}
		}
		return new Point(maxWidth, getLinesHeight(strings.size()));
	}

	/** @see #drawLines(Collection, Bounds, Direction, int) */
	public static Bounds drawLines(String[] strings, Bounds bounds, Direction anchor, int color) {
		return drawLines(Arrays.asList(strings), bounds, anchor, color);
	}

	/** Draws multiple lines of text anchored to {@code anchor} within {@code bounds} */
	public static Bounds drawLines(Collection<String> strings, Bounds bounds, Direction anchor, int color) {
		bounds = anchor.anchor(new Bounds(getLinesSize(strings)), bounds);
		Bounds drawBounds = new Bounds(bounds);

		if(anchor.in(Direction.LEFT)) anchor = Direction.NORTH_WEST;
		else if(anchor.in(Direction.RIGHT)) anchor = Direction.NORTH_EAST;
		else anchor = Direction.NORTH;

		// Render lines top to bottom
		for(String line : strings) {
			drawBounds.top(drawString(line, anchor.getAnchor(drawBounds), anchor, color).bottom() + 2);
		}
		return bounds;
	}
}
