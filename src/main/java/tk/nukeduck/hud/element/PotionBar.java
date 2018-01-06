package tk.nukeduck.hud.element;

import static org.lwjgl.opengl.GL11.GL_BLEND;
import static org.lwjgl.opengl.GL11.glColor4f;
import static org.lwjgl.opengl.GL11.glEnable;
import static tk.nukeduck.hud.BetterHud.MC;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.resources.I18n;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.nukeduck.hud.element.settings.Legend;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.LayoutManager;

public class PotionBar extends HudElement {
	private final SettingPosition position = new SettingPosition("position", Direction.CORNERS | Direction.CENTER.flag());
	private final SettingBoolean disableDefault = new SettingBoolean("disableDefault");

	public static final ResourceLocation INVENTORY = new ResourceLocation("textures/gui/container/inventory.png");

	public boolean disableDefault() {
		return disableDefault.get();
	}

	@Override
	public void loadDefaults() {
		this.settings.set(true);
		position.set(Direction.NORTH_WEST);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	public PotionBar() {
		super("potionBar");

		settings.add(position);
		this.settings.add(new Legend("potionsUseless"));
		this.settings.add(disableDefault);
	}

	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent.Pre event) {
		if(event.getType() == ElementType.POTION_ICONS) {
			event.setCanceled(true);
		}
	}

	@Override
	public Bounds render(RenderGameOverlayEvent event, LayoutManager manager) {
		int amount = MC.player.getActivePotionEffects().size();
		Bounds bounds = position.applyTo(new Bounds(amount * 16, 16), manager);

		glEnable(GL_BLEND);
		GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

		// TODO combine into one loop

		// Render potion icons
		FMLClientHandler.instance().getClient().renderEngine.bindTexture(INVENTORY);

		int x = bounds.x();
		for(PotionEffect effect : MC.player.getActivePotionEffects()) {
			int iconIndex = effect.getPotion().getStatusIconIndex();
			glColor4f(1.0F, 1.0F, 1.0F, ((float) effect.getDuration() / 600F));

			MC.ingameGUI.drawTexturedModalRect(x, bounds.y(), 18 * (iconIndex % 8), 198 + ((iconIndex >> 3) * 18), 18, 18); // >> 3 = / 8
			x += 16;
		}

		// Render potion potencies
		x = bounds.x();
		for(PotionEffect effect : MC.player.getActivePotionEffects()) {
			MC.ingameGUI.drawString(MC.fontRenderer, I18n.format("potion.potency." + effect.getAmplifier()).replace("potion.potency.", ""), x, bounds.y(), Colors.WHITE);
			x += 16;
		}
		return bounds;
	}
}
