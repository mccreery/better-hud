package jobicade.betterhud.element.vanilla;

import static jobicade.betterhud.BetterHud.MANAGER;
import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import jobicade.betterhud.BetterHud;
import jobicade.betterhud.element.HudElement;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingBoolean;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Boxed;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.util.MathUtil;
import net.minecraft.client.gui.Gui;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PotionBar extends HudElement {
	public static final ResourceLocation INVENTORY = new ResourceLocation("textures/gui/container/inventory.png");

	private SettingBoolean showDuration;

	public PotionBar() {
		super("potionBar", new SettingPosition(DirectionOptions.X, DirectionOptions.CORNERS));
	}

	@Override
	protected void addSettings(List<Setting<?>> settings) {
		super.addSettings(settings);
		settings.add(showDuration = new SettingBoolean("duration").setValuePrefix(SettingBoolean.VISIBLE));
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
		Boxed grid = getGrid();

		Rect bounds = new Rect(grid.getPreferredSize());
		if(position.isDirection(Direction.CENTER)) {
			bounds = bounds.align(MANAGER.getScreen().getAnchor(Direction.CENTER).add(SPACER, SPACER), Direction.NORTH_WEST);
		} else {
			bounds = position.applyTo(bounds);
		}
		grid.render(bounds);
		MC.getTextureManager().bindTexture(Gui.ICONS);

		return bounds;
	}

	private void populateEffects(List<PotionEffect> helpful, List<PotionEffect> harmful) {
		Stream<PotionEffect> source = MC.player
			.getActivePotionEffects().parallelStream()
			.filter(e -> e.doesShowParticles() && e.getPotion().shouldRenderHUD(e));

		MathUtil.splitList(source::iterator, e -> e.getPotion().isBeneficial(), helpful, harmful);
		helpful.sort(Collections.reverseOrder());
		harmful.sort(Collections.reverseOrder());
	}

	private void fillRow(Grid<? super PotionIcon> grid, int row, List<PotionEffect> effects) {
		for(int i = 0; i < effects.size(); i++) {
			grid.setCell(new Point(i, row), new PotionIcon(effects.get(i), showDuration.get()));
		}
	}

	private Boxed getGrid() {
		List<PotionEffect> helpful = new ArrayList<>(), harmful = new ArrayList<>();
		populateEffects(helpful, harmful);

		int rows = 0;
		if(!helpful.isEmpty()) ++rows;
		if(!harmful.isEmpty()) ++rows;

		if(rows > 0) {
			Grid<PotionIcon> grid = new Grid<>(new Point(Math.max(helpful.size(), harmful.size()), rows));
			grid.setAlignment(position.getContentAlignment());
			grid.setGutter(new Point(1, 2));

			int row = 0;
			if(!helpful.isEmpty()) fillRow(grid, row++, helpful);
			if(!harmful.isEmpty()) fillRow(grid, row++, harmful);

			return grid;
		} else {
			return null;
		}
	}
}
