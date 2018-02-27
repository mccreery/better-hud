package tk.nukeduck.hud.element.entityinfo;

import static tk.nukeduck.hud.BetterHud.ICONS;
import static tk.nukeduck.hud.BetterHud.MANAGER;
import static tk.nukeduck.hud.BetterHud.MC;
import static tk.nukeduck.hud.BetterHud.SPACER;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.EntityLivingBase;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.Colors;
import tk.nukeduck.hud.util.Direction;
import tk.nukeduck.hud.util.GlUtil;
import tk.nukeduck.hud.util.PaddedBounds;
import tk.nukeduck.hud.util.Point;

public class MobInfo extends EntityInfo {
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
		settings.add(compress);
	}

	@Override
	public void loadDefaults() {
		super.loadDefaults();
		compress.set(40.0);
	}

	private static String getCompressString(int rows) {
		return "x" + rows;
	}

	/** Precalculates the size required for {@link #renderH(EntityLivingBase, int, Point)} */
	private static Point getHealthSize(int health, int maxHealth, int compressLimit) {
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
	private static void renderHealth(int health, int maxHealth, int compressLimit, Point position) {
		position = new Point(position);

		if(health > compressLimit) {
			int rows = health / 20;
			MobInfo.renderCompressedRows(rows, position);

			// Remaining health between 0 and 19 points, max health between 0 and 20
			health -= rows * 20;
			if(health == 0) return;
			if(maxHealth > 20) maxHealth = 20;

			position.y += 9;
		} else if(maxHealth > compressLimit) {
			maxHealth = compressLimit;
		}

		GlUtil.renderHealthBar(health, maxHealth, position);
	}

	public void render(EntityLivingBase entity, float partialTicks) {
		int health = (int)entity.getHealth();
		int maxHealth = (int)entity.getMaxHealth();

		String text = String.format("%s %s(%d/%d)", entity.getName(), ChatFormatting.GRAY, health, maxHealth);
		Point textSize = GlUtil.getStringSize(text);

		int compressLimit = compress.getInt();
		if(compressLimit == 0) compressLimit = maxHealth;

		Point healthSize = getHealthSize(health, maxHealth, compressLimit);

		if(healthSize.x > textSize.x) {
			textSize.x = healthSize.x;
		}

		PaddedBounds bounds = new PaddedBounds(new Bounds(textSize), new Bounds(0, healthSize.y), Bounds.PADDING);
		MANAGER.position(Direction.SOUTH, bounds);

		GlUtil.drawRect(bounds, Colors.TRANSLUCENT);
		GlUtil.drawString(text, bounds.contentBounds().position, Direction.NORTH_WEST, Colors.WHITE);

		renderHealth(health, maxHealth, compressLimit, Direction.SOUTH_WEST.getAnchor(bounds.contentBounds()));
	}

	/** Renders a graphic representing a total of {@code rows} compressed rows */
	private static void renderCompressedRows(int rows, Point position) {
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
}
