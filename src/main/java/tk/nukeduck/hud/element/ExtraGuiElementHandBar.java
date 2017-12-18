package tk.nukeduck.hud.element;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingBooleanLeft;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingModeRight;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.element.settings.ElementSettingSliderPositioned;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ExtraGuiElementHandBar extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingAbsolutePositionAnchored pos;
	private ElementSettingAnchor anchor;
	
	private ElementSettingBoolean showName;
	private ElementSettingBoolean showDurability;
	private ElementSettingMode durabilityMode;
	private ElementSettingBoolean showItem;
	private ElementSettingBoolean offHand;
	private ElementSettingBoolean showBars;
	
	private ElementSettingBoolean enableWarnings;
	private ElementSettingSlider[] damageWarnings;
	
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
	
	@Override
	public String getName() {
		return "handBar";
	}
	
	public ExtraGuiElementHandBar() {
		//modes = new String[] {"default", "handBar.barOnly", "armorBars.numbersOnly"};
		//this.settings.add(mode = new ElementSettingMode("mode", new String[] {"default", "handBar.barOnly", "armorBars.numbersOnly"}));
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos = new ElementSettingAbsolutePositionAnchored("position", anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(showItem = new ElementSettingBoolean("showItem"));
		this.settings.add(showBars = new ElementSettingBoolean("showBars"));
		this.settings.add(showDurability = new ElementSettingBooleanLeft("showDurability"));
		this.settings.add(durabilityMode = new ElementSettingModeRight("durabilityMode", new String[] {"values", "percent"}));
		this.settings.add(showName = new ElementSettingBoolean("showName"));
		this.settings.add(offHand = new ElementSettingBoolean("offHand"));

		this.settings.add(new ElementSettingDivider("damageWarning"));
		this.settings.add(this.enableWarnings = new ElementSettingBoolean("damageWarning"));
		damageWarnings = new ElementSettingSlider[3];
		Position[] positions = new Position[] {Position.MIDDLE_LEFT, Position.MIDDLE_CENTER, Position.MIDDLE_RIGHT};
		for(int i = 0; i < 3; i++) {
			this.settings.add(damageWarnings[i] = new ElementSettingSliderPositioned("damaged." + String.valueOf(i), 1, 100, positions[i]) {
				@Override
				public String getSliderText() {
					return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), I18n.format("betterHud.strings.percent", String.valueOf((int) this.value)));
				}
			});
			damageWarnings[i].accuracy = 1;
		}
	}
	
	public void update(Minecraft mc) {}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
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
	
	public void renderBar(Minecraft mc, ItemStack stack, int x, int y) {
		//byte green = (byte) (255 * value);
		//byte red = (byte) (256 - green);
		
		String text = generateText(stack);
		
		int totalWidth = mc.fontRenderer.getStringWidth(text);
		if(showItem.value) totalWidth += 21;
		
		if(showItem.value) {
			mc.mcProfiler.startSection("items");
			mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
			RenderUtil.renderItem(mc.getRenderItem(), mc.fontRenderer, mc.getTextureManager(), stack, x + 90 - totalWidth / 2, y);
			mc.mcProfiler.endSection();
		}
		
		mc.mcProfiler.startSection("text");
		mc.ingameGUI.drawString(mc.fontRenderer, text, x + 90 - totalWidth / 2 + (showItem.value ? 21 : 0), y + 4, Colors.WHITE);
		mc.mcProfiler.endSection();
		
		if(showBars.value) {
			mc.mcProfiler.startSection("bars");
			/*RenderUtil.drawRect(x, y + 16, x + 180, y + 18, Colors.BLACK);
			RenderUtil.drawRect(x, y + 16, x + Math.round((value * 180)), y + 17, Colors.fromARGB(255, red, green, 0));*/
			RenderUtil.drawDamageBar(x, y + 16, 180, 2, stack, false);
			mc.mcProfiler.endSection();
		}
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		int x = posMode.index == 0 ? resolution.getScaledWidth() / 2 - 90 : pos.x;
		int y = posMode.index == 0 ? resolution.getScaledHeight() - (offHand.value ? 107 : 84) : pos.y;
		this.bounds = new Bounds(x, y, 180, offHand.value ? 41 : 18);
		// updates after, but that's alright - only one frame when you enable or disable offhand
		this.pos.update(resolution, this.bounds);

		ItemStack stack = mc.player.getHeldItemMainhand();
		if(stack != null && stack.getMaxDamage() > 0) {
			renderBar(mc, stack, x, this.bounds.getY2() - 18);
		}
		if(offHand.value) {
			stack = mc.player.getHeldItemOffhand();
			if(stack != null && stack.getMaxDamage() > 0) {
				renderBar(mc, stack, x, this.bounds.getY());
			}
		}
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}