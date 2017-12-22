package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingBooleanLeft;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingModeRight;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.element.settings.SettingSliderPositioned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class HandBar extends HudElement {
	private SettingMode posMode;
	private SettingAnchoredPosition pos;
	private SettingAnchor anchor;
	
	private SettingBoolean showName;
	private SettingBoolean showDurability;
	private SettingMode durabilityMode;
	private SettingBoolean showItem;
	private SettingBoolean offHand;
	private SettingBoolean showBars;
	
	private SettingBoolean enableWarnings;
	private SettingSlider[] damageWarnings;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.x = 5;
		pos.y = 5;
		
		showName.value = true;
		showDurability.value = true;
		durabilityMode.index = 0;
		showItem.value = true;
		showBars.value = true;
		offHand.value = false;
		enableWarnings.value = true;
		damageWarnings[0].value = 45.0;
		damageWarnings[1].value = 25.0;
		damageWarnings[2].value = 10.0;
	}
	
	public HandBar() {
		super("handBar");
		//modes = new String[] {"default", "handBar.barOnly", "armorBars.numbersOnly"};
		//this.settings.add(mode = new ElementSettingMode("mode", new String[] {"default", "handBar.barOnly", "armorBars.numbersOnly"}));
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos = new SettingAnchoredPosition("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new Divider("misc"));
		this.settings.add(showItem = new SettingBoolean("showItem"));
		this.settings.add(showBars = new SettingBoolean("showBars"));
		this.settings.add(showDurability = new SettingBooleanLeft("showDurability"));
		this.settings.add(durabilityMode = new SettingModeRight("durabilityMode", new String[] {"values", "percent"}));
		this.settings.add(showName = new SettingBoolean("showName"));
		this.settings.add(offHand = new SettingBoolean("offHand"));

		this.settings.add(new Divider("damageWarning"));
		this.settings.add(this.enableWarnings = new SettingBoolean("damageWarning"));
		damageWarnings = new SettingSlider[3];
		Position[] positions = new Position[] {Position.MIDDLE_LEFT, Position.MIDDLE_CENTER, Position.MIDDLE_RIGHT};
		for(int i = 0; i < 3; i++) {
			this.settings.add(damageWarnings[i] = new SettingSliderPositioned("damaged." + String.valueOf(i), 1, 100, positions[i]) {
				@Override
				public String getSliderText() {
					return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format("betterHud.strings.percent", String.valueOf((int) this.value)));
				}
			});
			damageWarnings[i].accuracy = 1;
		}
	}
	
	public void update() {}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return bounds;
	}
	
	private String generateText(ItemStack stack) {
		int maxDamage = stack.getMaxDamage();
		float value = (float) (maxDamage - stack.getItemDamage()) / (float) maxDamage;
		
		String dur, text;
		if(durabilityMode.getValue().equals("percent")) {
			dur = I18n.format("betterHud.strings.percent", FormatUtil.ONE_PLACE.format(value * 100.0));
		} else {
			dur = I18n.format("betterHud.strings.outOf", String.valueOf(maxDamage - stack.getItemDamage()), String.valueOf(maxDamage));
		}
		
		if(showName.value && showDurability.value) {
			text = FormatUtil.separate(I18n.format("betterHud.strings.splitter"), stack.getDisplayName(), dur);
		} else if(showName.value) {
			text =  stack.getDisplayName();
		} else if(showDurability.value) {
			text = dur;
		} else {
			text = "";
		}

		if(enableWarnings.value) {
			int count = -1;
			for(int a = 0; a < this.damageWarnings.length; a++) {
				if(value * 100.0f <= damageWarnings[a].value) {
					count = a;
				}
			}
			text += count == -1 ? "" : " " + I18n.format("betterHud.strings.damaged." + count);
		}
		return text;
	}
	
	public void renderBar(ItemStack stack, int x, int y) {
		//byte green = (byte) (255 * value);
		//byte red = (byte) (256 - green);
		
		String text = generateText(stack);
		
		int totalWidth = MC.fontRenderer.getStringWidth(text);
		if(showItem.value) totalWidth += 21;
		
		if(showItem.value) {
			MC.mcProfiler.startSection("items");
			MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderUtil.renderItem(MC.getRenderItem(), MC.fontRenderer, MC.getTextureManager(), stack, x + 90 - totalWidth / 2, y);
			MC.mcProfiler.endSection();
		}
		
		MC.mcProfiler.startSection("text");
		MC.ingameGUI.drawString(MC.fontRenderer, text, x + 90 - totalWidth / 2 + (showItem.value ? 21 : 0), y + 4, Colors.WHITE);
		MC.mcProfiler.endSection();
		
		if(showBars.value) {
			MC.mcProfiler.startSection("bars");
			/*RenderUtil.drawRect(x, y + 16, x + 180, y + 18, Colors.BLACK);
			RenderUtil.drawRect(x, y + 16, x + Math.round((value * 180)), y + 17, Colors.fromARGB(255, red, green, 0));*/
			RenderUtil.drawDamageBar(x, y + 16, 180, 2, stack, false);
			MC.mcProfiler.endSection();
		}
	}
	
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		int x = posMode.index == 0 ? event.getResolution().getScaledWidth() / 2 - 90 : pos.x;
		int y = posMode.index == 0 ? event.getResolution().getScaledHeight() - (offHand.value ? 107 : 84) : pos.y;
		this.bounds = new Bounds(x, y, 180, offHand.value ? 41 : 18);
		// updates after, but that's alright - only one frame when you enable or disable offhand
		this.pos.update(event.getResolution(), this.bounds);

		ItemStack stack = MC.player.getHeldItemMainhand();
		if(stack != null && stack.getMaxDamage() > 0) {
			renderBar(stack, x, this.bounds.getY2() - 18);
		}
		if(offHand.value) {
			stack = MC.player.getHeldItemOffhand();
			if(stack != null && stack.getMaxDamage() > 0) {
				renderBar(stack, x, this.bounds.getY());
			}
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}