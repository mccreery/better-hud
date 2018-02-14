package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public class MobInfo extends EntityInfo {
	private final SettingBoolean players = new SettingBoolean("players");
	private final SettingBoolean mobs = new SettingBoolean("mobs");

	private final SettingSlider compress = new SettingSlider("compress", 0, 200, 20) {
		@Override
		public String getDisplayValue(double value) {
			if(get() == 0.0) {
				return I18n.format("betterHud.value.never");
			} else {
				return super.getDisplayValue(value);
			}
		}
	};

	public MobInfo() {
		super("mobInfo");

		settings.add(players);
		settings.add(mobs);
		settings.add(compress);
	}

	@Override
	public void loadDefaults() {
		settings.set(true);
		players.set(true);
		mobs.set(true);

		compress.set(40.0);
	}

	private String getCompressString(int rows) {
		return "x" + rows;
	}

	/** Precalculates the size required for {@link #renderH(EntityLivingBase, int, Point)} */
	private Point getHealthSize(int health, int maxHealth, int compressLimit) {
		int width = 0;
		int rows = 0;

		int rowMax = maxHealth > 20 ? 20 : maxHealth;

		if(health > compressLimit) {
			int compressedRows = health / 20;
			String compressString = getCompressString(compressedRows);
			width = 9 * 4 + 9 + SPACER + MC.fontRenderer.getStringWidth(compressString);

			health -= compressedRows * 20;

			if(health == 0) {
				return new Point(width, 9);
			}
			++rows;

			maxHealth = rowMax;
		}
		width = Math.max(width, (rowMax + 1) / 2 * 8 + 1);

		rows += (Math.min(maxHealth, compressLimit) + 19) / 20;
		return new Point(width, rows * 9);
	}

	/** Renders a health bar, compressing if necessary.
	 *
	 * <p>The compression logic is defined as follows:<ul>
	 *  <li>If the {@code health} is more than {@code compressLimit}, compress:<ul>
	 *   <li>Draw a small graphic representing the number of full rows of health
	 *   <li>Reduce remaining {@code health} by the number of rows rendered, leaving at most 19 points
	 *   <li>Limit maximum health at one row (20 points)
	 *  </ul>
	 *  <li>If there are no remaining hearts, return
	 *  <li>Draw the remaining hearts
	 *  <li>Draw empty hearts up to the remaining max health or the compression limit,
	 *  whichever comes first
	 * </ul>
	 *
	 * @param compressLimit The maximum health at which the bar should not be compressed.
	 * Should be a multiple of 20 (one row) */
	private void renderHealth(int health, int maxHealth, int compressLimit, Point position) {
		position = new Point(position);

		if(health > compressLimit) {
			int rows = health / 20;
			renderCompressedRows(rows, position);

			// Remaining health between 0 and 19 points, max health between 0 and 20
			health -= rows * 20;
			if(health == 0) return;
			if(maxHealth > 20) maxHealth = 20;

			position.y += 9;
		} else if(maxHealth > compressLimit) {
			maxHealth = compressLimit;
		}

		renderHealthBar(health, maxHealth, position);
	}

	/** Renders a graphic representing a total of {@code rows} compressed rows */
	private void renderCompressedRows(int rows, Point position) {
		Bounds emptyTexture = new Bounds(16, 0, 9, 9);
		Bounds heartTexture = new Bounds(52, 0, 9, 9);

		position = new Point(position);

		MC.getTextureManager().bindTexture(ICONS);
		GlUtil.color(Colors.WHITE);

		for(int i = 0; i < 10; i++, position.x += 4) {
			GlUtil.drawTexturedModalRect(position, emptyTexture);
			GlUtil.drawTexturedModalRect(position, heartTexture);
		}
		position.x += SPACER;

		MC.fontRenderer.drawString(getCompressString(rows), position.x, position.y, Colors.WHITE);
	}

	/** @see #renderBar(int, int, Point, Bounds, Bounds, Bounds) */
	private void renderHealthBar(int health, int maxHealth, Point position) {
		MC.getTextureManager().bindTexture(ICONS);

		renderBar(health, maxHealth, position,
			new Bounds(16, 0, 9, 9), new Bounds(61, 0, 9, 9), new Bounds(52, 0, 9, 9));
	}

	/** Renders a multi-row health bar with {@code health} full hearts
	 * and {@code maxHealth} total hearts
	 *
	 * @param background The texture coordinates for the background icon
	 * @param half The texture coordinates for the half unit icon
	 * @param full The texture coordinates for a full unit icon */
	private void renderBar(int current, int max, Point position, Bounds background, Bounds half, Bounds full) {
		Point icon = new Point(position);

		GlUtil.color(Colors.WHITE);
		for(int i = 0; i < max; icon.x = position.x, icon.y += 9) {
			for(int j = 0; j < 20 && i < max; i += 2, j += 2, icon.x += 8) {
				if(background != null) {
					GlUtil.drawTexturedModalRect(icon, background);
				}

				if(i + 1 < current) {
					GlUtil.drawTexturedModalRect(icon, full);
				} else if(i < current) {
					GlUtil.drawTexturedModalRect(icon, half);
				}
			}
		}
	}

	public void render(EntityLivingBase entity, float partialTicks) {
		boolean isPlayer = entity instanceof EntityPlayer;
		if((isPlayer && !players.get()) || (!isPlayer && !mobs.get())) {
			return;
		}

		int health = (int)entity.getHealth();
		int maxHealth = (int)entity.getMaxHealth();

		String text = String.format("%s %s(%d/%d)", entity.getName(), ChatFormatting.GRAY, health, maxHealth);
		//List<String> text = new ArrayList<String>();
		//text.add(String.format("%s %s(%d/%d)", entity.getName(), ChatFormatting.GRAY, (int)entity.getHealth(), (int)entity.getMaxHealth()));

		/*if(isPlayer) {
			text.add(String.format("(%d/%d)", entity.getTotalArmorValue(), 20));
		}*/

		Point textSize = getLinesSize(text);

		int compressLimit = compress.getInt();
		if(compressLimit == 0) compressLimit = maxHealth;

		Point healthSize = getHealthSize(health, maxHealth, compressLimit);

		if(healthSize.x > textSize.x) {
			textSize.x = healthSize.x;
		}

		PaddedBounds bounds = new PaddedBounds(new Bounds(textSize), new Bounds(0, healthSize.y), Bounds.PADDING);
		MANAGER.position(Direction.SOUTH, bounds);

		drawRect(bounds, Colors.TRANSLUCENT);
		drawString(text, bounds.contentBounds().position, Direction.NORTH_WEST, Colors.WHITE);

		renderHealth(health, maxHealth, compressLimit, Direction.SOUTH_WEST.getAnchor(bounds.contentBounds()));

		if(isPlayer) {
			PaddedBounds armor = new PaddedBounds(new Bounds(81, 9), Bounds.PADDING, Bounds.EMPTY);
			MANAGER.position(Direction.SOUTH, armor);
			drawRect(armor, Colors.TRANSLUCENT);
			renderBar(entity.getTotalArmorValue(), 20, Direction.NORTH_WEST.getAnchor(armor.contentBounds()), new Bounds(16, 9, 9, 9), new Bounds(25, 9, 9, 9), new Bounds(34, 9, 9, 9));
		}
	}

	private List<String> getEnchantmentLines(ItemStack stack) {
		Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
		List<String> enchantLines = new ArrayList<String>();

		for(Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
			enchantLines.add(ChatFormatting.LIGHT_PURPLE + enchantment.getKey().getTranslatedName(enchantment.getValue()));
		}
		return enchantLines;
	}

	// TODO show what other players are holding
	private void renderHolding(ItemStack stack, int top) {
		final String holding = I18n.format("betterHud.strings.holding");
		MC.ingameGUI.drawString(MC.fontRenderer, holding, SPACER, top + SPACER, Colors.WHITE);

		List<String> enchantLines = getEnchantmentLines(stack);
		drawLines(enchantLines, new Bounds(0, top, 0, 0), Direction.NORTH_WEST, Colors.WHITE);

		String stackName = (stack.hasDisplayName() ? ChatFormatting.ITALIC : "") + "" + (stack.isItemEnchanted() ? ChatFormatting.AQUA : "") + stack.getDisplayName();
		MC.ingameGUI.drawString(MC.fontRenderer, stackName, 10 + 16 + MC.fontRenderer.getStringWidth(holding), top + 5, Colors.WHITE);
	}
}
