package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.google.common.collect.Ordering;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.MathUtil;
import jobicade.betterhud.util.geom.Direction;
import jobicade.betterhud.util.geom.Point;
import jobicade.betterhud.util.geom.Rect;
import jobicade.betterhud.util.mode.ColorMode;
import jobicade.betterhud.util.mode.GlMode;
import jobicade.betterhud.util.mode.TextureMode;
import jobicade.betterhud.util.render.Color;
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

public class PotionBar extends HudElement {
	public static final ResourceLocation INVENTORY = new ResourceLocation("textures/gui/container/inventory.png");
	private SettingPosition position;

	public PotionBar() {
		super("potionBar");
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(position = new SettingPosition("position", DirectionOptions.X, DirectionOptions.CORNERS));
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
		if (BetterHud.isEnabled() && event.getType() == ElementType.POTION_ICONS) {
			event.setCanceled(true);
		}
	}

	@Override
	public boolean shouldRender(Event event) {
		return super.shouldRender(event) && !MC.player.getActivePotionEffects().isEmpty();
	}

	@Override
	public Rect render(Event event) {
		List<PotionEffect> effects = new ArrayList<>(MC.player.getActivePotionEffects());

		int pivot = MathUtil.partition(effects, effect -> effect.getPotion().isBeneficial());
		List<PotionEffect> harmful = effects.subList(0, pivot);
		List<PotionEffect> helpful = effects.subList(pivot, effects.size());

		Ordering<PotionEffect> order = Ordering.natural().reverse();
		Collections.sort(harmful, order);
		Collections.sort(helpful, order);

		Rect bounds = getRect(harmful.size(), helpful.size());

		Direction alignment = position.getContentAlignment();
		Point icon = new Rect(24, 24).anchor(bounds, alignment).getPosition();

		int deltaX = alignment.getCol() == 2 ? -25 : 25;
		for(int i = 0; i < helpful.size(); i++) {
			drawIcon(icon.add(i * deltaX, 0), helpful.get(i));
		}

		icon = icon.add(0, alignment.getRow() == 2 ? -25 : 25);
		for(int i = 0; i < harmful.size(); i++) {
			drawIcon(icon.add(i * deltaX, 0), harmful.get(i));
		}
		return bounds;
	}

	private Rect getRect(int harmful, int helpful) {
		// Swap to enforce harmful <= helpful
		if(harmful > helpful) {
			int temp = harmful;
			harmful = helpful;
			helpful = temp;
		}
		if(helpful <= 0) return Rect.empty();

		Rect bounds = new Rect(helpful * 25 - 1, harmful > 0 ? 50 : 24);

		if(position.isDirection(Direction.CENTER)) {
			return bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(SPACER, SPACER), Direction.NORTH_WEST);
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
		Rect background;

		if(effect.getIsAmbient()) {
			background = new Rect(165, 166, 24, 24);
		} else {
			background = new Rect(141, 166, 24, 24);

			if(effect.getDuration() <= 200) {
				int durationSeconds = effect.getDuration() / 20;
				opacity = MathHelper.clamp(effect.getDuration() / 100f, 0, .5f)
					+ MathHelper.cos(effect.getDuration() * (float)Math.PI / 5f) * MathHelper.clamp(10 - durationSeconds / 40f, 0f, .25f);
			}
		}

		GlMode.push(new TextureMode(GuiContainer.INVENTORY_BACKGROUND));
		GlUtil.drawTexturedModalRect(position, background);

		GlMode.set(new ColorMode(Color.WHITE.withAlpha(Math.round(opacity * 255))));

		if(potion.hasStatusIcon()) {
			int index = potion.getStatusIconIndex();
			Rect icon = new Rect((index % 8) * 18, 198 + (index / 8) * 18, 18, 18);

			GlUtil.drawTexturedModalRect(position.add(3, 3), icon);
		}
		potion.renderHUDEffect(position.getX(), position.getY(), effect, MC, opacity);

		GlMode.pop();

		String potionLevel = getPotionLevel(effect);
		if(!potionLevel.isEmpty()) {
			GlUtil.drawString(potionLevel, new Point(position.getX() + 21, position.getY() + 21), Direction.SOUTH_EAST, Color.WHITE);
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
