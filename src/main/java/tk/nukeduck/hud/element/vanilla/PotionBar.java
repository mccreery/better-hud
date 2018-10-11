package tk.nukeduck.hud.element.vanilla;

import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Ordering;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.resources.I18n;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import tk.nukeduck.hud.BetterHud;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.Direction.Options;
import tk.nukeduck.hud.util.mode.ColorMode;
import tk.nukeduck.hud.util.mode.GlMode;
import tk.nukeduck.hud.util.mode.TextureMode;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.MathUtil;
import tk.nukeduck.hud.util.Point;

public class PotionBar extends OverrideElement {
	public static final ResourceLocation INVENTORY = new ResourceLocation("textures/gui/container/inventory.png");

	private final SettingPosition position = new SettingPosition("position", Options.X, Options.CORNERS);

	public PotionBar() {
		super("potionBar");

		settings.add(position);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();

		position.setPreset(Direction.NORTH_WEST);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
	}

	@SubscribeEvent
	public void onRenderTick(RenderGameOverlayEvent.Pre event) {
		if(BetterHud.isEnabled() && event.getType() == ElementType.POTION_ICONS) {
			event.setCanceled(true);
		}
	}

	@Override
	protected ElementType getType() {
		return null;
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && !MC.player.getActivePotionEffects().isEmpty();
	}

	@Override
	public Bounds render(Event event) {
		List<PotionEffect> effects = new ArrayList<>(MC.player.getActivePotionEffects());

		int pivot = MathUtil.partition(effects, effect -> effect.getPotion().isBeneficial());
		List<PotionEffect> harmful = effects.subList(0, pivot);
		List<PotionEffect> helpful = effects.subList(pivot, effects.size());

		Ordering<PotionEffect> order = Ordering.natural().reverse();
		Collections.sort(harmful, order);
		Collections.sort(helpful, order);

		Bounds bounds = getBounds(harmful.size(), helpful.size());

		Direction alignment = position.getContentAlignment();
		Point icon = new Bounds(24, 24).anchor(bounds, alignment).getPosition();

		int deltaX = alignment.getColumn() == 2 ? -25 : 25;
		for(int i = 0; i < helpful.size(); i++) {
			drawIcon(icon.add(i * deltaX, 0), helpful.get(i));
		}

		icon = icon.add(0, alignment.getRow() == 2 ? -25 : 25);
		for(int i = 0; i < harmful.size(); i++) {
			drawIcon(icon.add(i * deltaX, 0), harmful.get(i));
		}
		return bounds;
	}

	private Bounds getBounds(int harmful, int helpful) {
		// Swap to enforce harmful <= helpful
		if(harmful > helpful) {
			int temp = harmful;
			harmful = helpful;
			helpful = temp;
		}
		if(helpful <= 0) return Bounds.EMPTY;

		Bounds bounds = new Bounds(helpful * 25 - 1, harmful > 0 ? 50 : 24);

		if(position.isDirection(Direction.CENTER)) {
			return bounds.positioned(Direction.CENTER, new Point(SPACER, SPACER), Direction.NORTH_WEST);
		} else {
			return position.applyTo(bounds);
		}
	}

	private void drawIcon(Point position, PotionEffect effect) {
		Potion potion = effect.getPotion();

		if(!potion.shouldRenderHUD(effect) || !effect.doesShowParticles()) {
			return;
		}

		float opacity = 1;
		Bounds background;

		if(effect.getIsAmbient()) {
			background = new Bounds(165, 166, 24, 24);
		} else {
			background = new Bounds(141, 166, 24, 24);

			if(effect.getDuration() <= 200) {
				int durationSeconds = effect.getDuration() / 20;
				opacity = MathHelper.clamp(effect.getDuration() / 100f, 0, .5f)
					+ MathHelper.cos(effect.getDuration() * (float)Math.PI / 5f) * MathHelper.clamp(10 - durationSeconds / 40f, 0f, .25f);
			}
		}

		GlMode.push(new TextureMode(GuiContainer.INVENTORY_BACKGROUND));
		GlUtil.drawTexturedModalRect(position, background);

		GlMode.set(new ColorMode(Colors.setAlpha(Colors.WHITE, Math.round(opacity * 255))));

		if(potion.hasStatusIcon()) {
			int index = potion.getStatusIconIndex();
			Bounds icon = new Bounds((index % 8) * 18, 198 + (index / 8) * 18, 18, 18);

			GlUtil.drawTexturedModalRect(position.add(3, 3), icon);
		}
		potion.renderHUDEffect(position.getX(), position.getY(), effect, MC, opacity);

		GlMode.pop();

		String potionLevel = getPotionLevel(effect);
		if(!potionLevel.isEmpty()) {
			GlUtil.drawString(potionLevel, new Point(position.getX() + 21, position.getY() + 21), Direction.SOUTH_EAST, Colors.WHITE);
		}
	}

	private String getPotionLevel(PotionEffect effect) {
		int amplifier = effect.getAmplifier();

		if(amplifier > 0) {
			String unlocalized = "enchantment.level." + (amplifier + 1);

			if(I18n.hasKey(unlocalized)) {
				return I18n.format(unlocalized);
			}
		}
		return "";
	}
}
