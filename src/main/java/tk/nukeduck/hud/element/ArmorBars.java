package tk.nukeduck.hud.element;

import static tk.nukeduck.hud.BetterHud.MC;

import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import tk.nukeduck.hud.element.settings.Setting;
import tk.nukeduck.hud.element.settings.SettingAnchoredPosition;
import tk.nukeduck.hud.element.settings.SettingAnchor;
import tk.nukeduck.hud.element.settings.SettingBoolean;
import tk.nukeduck.hud.element.settings.SettingBooleanLeft;
import tk.nukeduck.hud.element.settings.Divider;
import tk.nukeduck.hud.element.settings.SettingMode;
import tk.nukeduck.hud.element.settings.SettingModeRight;
import tk.nukeduck.hud.element.settings.SettingPosition;
import tk.nukeduck.hud.element.settings.SettingPosition.Position;
import tk.nukeduck.hud.element.settings.SettingPositionHorizontal;
import tk.nukeduck.hud.element.settings.SettingSlider;
import tk.nukeduck.hud.element.settings.SettingSliderPositioned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Constants;

public class ArmorBars extends HudElement {
	private SettingMode bars;
	private SettingBoolean showName;
	private SettingBoolean showDurability;
	private SettingMode durabilityMode;
	private SettingMode posMode;
	private SettingPosition pos;
	private SettingAnchoredPosition pos2;
	private SettingAnchor anchor;
	private SettingPosition alignment;
	private SettingBoolean enableWarnings;
	private SettingSlider[] damageWarnings;
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		bars.index = 2;
		showName.value = true;
		showDurability.value = true;
		durabilityMode.index = 0;
		posMode.index = 0;
		pos.value = Position.MIDDLE_LEFT;
		alignment.value = Position.MIDDLE_LEFT;
		pos2.x = 5;
		pos2.y = 5;
		anchor.value = Position.TOP_LEFT.getFlag();
		enableWarnings.value = true;
		damageWarnings[0].value = 45.0;
		damageWarnings[1].value = 25.0;
		damageWarnings[2].value = 10.0;
	}
	
	private Bounds bounds = Bounds.EMPTY;
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return bounds;
	}
	
	public void updateBounds(Minecraft MC, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		
	}
	
	public ArmorBars() {
		super("armorBars");
		this.settings.add(new Divider("position"));
		this.settings.add(posMode = new SettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new SettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.settings.add(alignment = new SettingPositionHorizontal("alignment", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
			@Override
			public boolean getEnabled() {
				return posMode.index != 0;
			}
			
			@Override
			public void otherAction(Collection<Setting> settings) {
				if(posMode.index == 0) {
					this.value = pos.value;
				}
				super.otherAction(settings);
			}
		});
		this.anchor = new SettingAnchor("anchor");
		this.settings.add(pos2 = new SettingAnchoredPosition("position2", this.anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new Divider("misc"));
		this.settings.add(showDurability = new SettingBooleanLeft("showDurability"));
		this.settings.add(durabilityMode = new SettingModeRight("durabilityMode", new String[] {"values", "percent"}) {
			@Override
			public boolean getEnabled() {
				return showDurability.value;
			}
		});
		this.settings.add(showName = new SettingBoolean("showName"));
		this.settings.add(bars = new SettingMode("barType", new String[] {"hidden", "smallBars", "largeBars"}));
		bars.comments.add("hidden, smallBars, largeBars");
		
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
	
	public String generateText(ItemStack item) {
		ArrayList<String> parts = new ArrayList<String>();
		if(this.showName.value) parts.add(item.getDisplayName());
		int maxDamage = item.getMaxDamage();
		float value = (float) (maxDamage - item.getItemDamage()) / (float) maxDamage;
		if(this.showDurability.value) {
			if(durabilityMode.getValue().equals("percent")) {
				parts.add(I18n.format("betterHud.strings.percent", FormatUtil.ONE_PLACE.format(value * 100.0)));
			} else {
				parts.add(I18n.format("betterHud.strings.outOf", String.valueOf(maxDamage - item.getItemDamage()), String.valueOf(maxDamage)));
			}
		}

		if(enableWarnings.value) {
			int count = -1;
			for(int a = 0; a < this.damageWarnings.length; a++) {
				if(value * 100.0f <= damageWarnings[a].value) {
					count = a;
				}
			}
			String exclamation = count == -1 ? "" : I18n.format("betterHud.strings.damaged." + count);
			return FormatUtil.separate(I18n.format("betterHud.strings.splitter"), parts.toArray(new String[parts.size()])) + " " + exclamation;
		} else {
			return FormatUtil.separate(I18n.format("betterHud.strings.splitter"), parts.toArray(new String[parts.size()]));
		}
	}
	
	String[] textCache = new String[4];
	int[] widthCache = new int[4];
	
	public Point getPosition(ScaledResolution resolution, LayoutManager manager) {
		if(this.posMode.index == 0) {
			if(this.pos.value == Position.MIDDLE_LEFT) {
				return new Point(Constants.SPACER, manager.get(Position.TOP_LEFT));
			} else {
				return new Point(resolution.getScaledWidth() - this.bounds.getWidth() - Constants.SPACER, manager.get(Position.TOP_LEFT));
			}
		}
		return new Point(this.pos2.x, this.pos2.y);
	}
	
	private Bounds generateBounds(ScaledResolution resolution, LayoutManager layoutManager, ItemStack[] armor) {
		Bounds b = Bounds.EMPTY.clone();
		b.setWidth(16);
		
		boolean anyArmor = false;
		for(int i = 0; i < 4; i++) {
			if(isStackValid(armor[i])) {
				anyArmor = true;
				
				if(this.showName.value || this.showDurability.value) {
					this.textCache[i] = generateText(armor[i]);
					widthCache[i] = 21 + MC.fontRenderer.getStringWidth(textCache[i]);
					if(widthCache[i] > b.getWidth()) b.setWidth(widthCache[i]);
				}
			}
		}
		if(!anyArmor) return b;
		
		if(this.bars.index == 2) {
			b.setWidth(Math.max(b.getWidth(), 85));
		} else if(this.bars.index == 1) {
			b.setWidth(Math.max(b.getWidth(), 20));
		}
		
		b.setHeight(70);
		b.setPosition(getPosition(resolution, layoutManager));
		return b;
	}
	
	private boolean isStackValid(ItemStack stack) {
		return stack != null && !stack.isEmpty();
	}
	
	@Override
	public void render(RenderGameOverlayEvent event, StringManager stringManager, LayoutManager layoutManager) {
		TextureMap emptySlots = Minecraft.getMinecraft().getTextureMapBlocks();

		boolean left = alignment.value == Position.MIDDLE_LEFT;
		//int cHeight = layoutManager.get(left ? Position.TOP_LEFT : Position.TOP_RIGHT);
		
		// TODO why do this?????
		ItemStack[] armor = MC.player.inventory.armorInventory.toArray(new ItemStack[MC.player.inventory.armorInventory.size()]);
		ArrayUtils.reverse(armor);
		
		this.bounds = generateBounds(event.getResolution(), layoutManager, armor);
		pos2.update(event.getResolution(), this.bounds);

		if(this.bounds.getHeight() == 0) return;
		
		if(posMode.index == 0) {
			layoutManager.add(this.bounds.getHeight(), left ? Position.TOP_LEFT : Position.TOP_RIGHT);
		}
		
		MC.mcProfiler.startSection("items");
		RenderHelper.enableGUIStandardItemLighting();
		for(int i = 0; i < 4; i++) {
			int x = left ? this.bounds.getX() : this.bounds.getX2() - 16;
			int y = this.bounds.getY() + i * 18;

			if(isStackValid(armor[i])) {
				MC.getRenderItem().renderItemAndEffectIntoGUI(armor[i], x, y);
			} else {
				TextureAtlasSprite empty = emptySlots.getAtlasSprite(ItemArmor.EMPTY_SLOT_NAMES[3-i]);

				if(empty != null) {
					GlStateManager.disableLighting();
					MC.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
					MC.ingameGUI.drawTexturedModalRect(x, y, empty, 16, 16);
					GlStateManager.enableLighting();
				}
			}
		}
		RenderHelper.disableStandardItemLighting();
		MC.mcProfiler.endSection();
		
		if(this.showName.value || this.showDurability.value) {
			MC.mcProfiler.startSection("text");
			int textOffset = this.bars.index == 2 ? 2 : 4; 
			for(int i = 0; i < 4; i++) {
				//ItemStack stack = armor[i];
				if(isStackValid(armor[i])) {
					int x = left ? this.bounds.getX() + 21 : this.bounds.getX2() - widthCache[i];
					MC.ingameGUI.drawString(MC.fontRenderer, this.textCache[i], x, this.bounds.getY() + i * 18 + textOffset, 0xffffff);
				}
			}
			MC.mcProfiler.endSection();
		}

		if(this.bars.index != 0) {
			MC.mcProfiler.startSection("bars");
			for(int i = 0; i < 4; i++) {
				if(!isStackValid(armor[i])) continue;

				if(this.bars.index == 2) {
					int x = this.alignment.value == Position.MIDDLE_LEFT ? this.bounds.getX() + 21 : this.bounds.getX2() - 85;
					int y = this.bounds.getY() + i * 18 + 12 + (this.showName.value || this.showDurability.value ? 0 : -4);
					RenderUtil.drawDamageBar(x, y, 64, 2, armor[i], false);
				} else {
					int x = this.alignment.value == Position.MIDDLE_LEFT ? this.bounds.getX() + 16 : this.bounds.getX2() - 18;
					int y = this.bounds.getY() + i * 18;
					RenderUtil.drawDamageBar(x, y, 2, 16, armor[i], true);
				}
			}
			MC.mcProfiler.endSection();
		}

		/*if(shouldRender) {
			String barType = bars.getValue();
			boolean showBars = !barType.equals("hidden");
			
			ArrayUtils.reverse(armor);
			
			int x = -1, y = -1;
			if(posMode.index == 1) {
				x = pos2.x;
				y = pos2.y;
			}
			
			if(x == -1) {
				bounds.setX(left ? 5 : resolution.getScaledWidth() - 21);
				bounds.setY(cHeight);
			} else {
				bounds.setX(x);
				bounds.setY(y);
			}
			bounds.setWidth(85);
			//if(!left) {
			//	bounds.setX(bounds.getX() - 69);
			//}
			
			bounds.setHeight(70);
			
			String[] texts = new String[4];
			if(name || durability) {
				for(int i = 0; i < 4; i++) {
					if(armor[i] != null) {
						int maxDamage = armor[i].getMaxDamage();
						int yOffset = 18 * i;
						float value = (float) (maxDamage - armor[i].getItemDamage()) / (float) maxDamage;
						
						String text = "";
						
						String dur;
						if(durabilityMode.getValue().equals("percent")) {
							dur = I18n.format("betterHud.strings.percent", FormatUtil.ONE_PLACE.format(value * 100.0));
						} else {
							dur = I18n.format("betterHud.strings.outOf", String.valueOf(maxDamage - armor[i].getItemDamage()), String.valueOf(maxDamage));
						}
						
						if(armor[i].getMaxDamage() != 0) {
    						if(name && durability) {
    							text = FormatUtil.separate(I18n.format("betterHud.strings.splitter"), armor[i].getDisplayName(), dur);
    						} else if(name) {
    							text = armor[i].getDisplayName();
    						} else if(durability) {
    							text = dur;
    						}
						} else if(name) {
							text = armor[i].getDisplayName();
						}
						int textY = (x == -1 ? cHeight : y) + yOffset;
						int textWidth = MC.fontRendererObj.getStringWidth(text);
						this.bounds.setWidth(Math.max(this.bounds.getWidth(), textWidth));
					}
				}
			}
			
			if(name || durability) {
				for(int i = 0; i < 4; i++) {
					if(armor[i] != null) {

						
						//text = I18n.format("betterHud.strings.outOf", String.valueOf(maxDamage - armor[i].getItemDamage()), String.valueOf(maxDamage));
						//if(shouldDoName) text = FormatUtil.separate(I18n.format("betterHud.strings.splitter"), armor[i].getDisplayName(), text + ChatFormatting.RESET);
						
						int textY = (x == -1 ? cHeight : y) + yOffset;
						int textWidth = MC.fontRendererObj.getStringWidth(text);
						if(!barType.equals("largeBars") || armor[i].getMaxDamage() == 0) textY += 4;
						
						if(name || durability) {
							MC.MCProfiler.startSection("text");
							if(x == -1) {
								MC.ingameGUI.drawString(MC.fontRendererObj, text, left ? 26 : resolution.getScaledWidth() - 26 - textWidth, textY, RenderUtil.colorRGB(255, 255, 255));
							} else {
								MC.ingameGUI.drawString(MC.fontRendererObj, text, left ? x + 21 : this.bounds.getX2() - textWidth - 21, textY, RenderUtil.colorRGB(255, 255, 255));
							}
							MC.MCProfiler.endSection();
						}
						
						MC.MCProfiler.startSection("textWarning");
						//int amount = 3 - (int) (value * 10);
						//String exclamation = "";
						//for(int i2 = 0; i2 < amount; i2++) exclamation += "! ";
						
						int count = -1;
						for(int a = 0; a < this.damageWarnings.length; a++) {
							if(value * 100.0f <= damageWarnings[a].value) {
								//System.out.println(damageWarnings[a].value);
								count = a;
							}
						}
						String exclamation = count == -1 ? "" : I18n.format("betterHud.strings.damaged." + count);
						
						if(!exclamation.equals("")) {
    						if(x == -1) {
    							MC.ingameGUI.drawString(MC.fontRendererObj, exclamation, left ? 31 + textWidth : resolution.getScaledWidth() - 31 - textWidth - MC.fontRendererObj.getStringWidth(exclamation), textY, RenderUtil.colorRGB(255, 0, 0));
    						} else {
    							MC.ingameGUI.drawString(MC.fontRendererObj, exclamation, left ? x + 26 + textWidth : x - 10 - textWidth - MC.fontRendererObj.getStringWidth(exclamation), textY, RenderUtil.colorRGB(255, 0, 0));
    						}
						}
						MC.MCProfiler.endSection();
					}
				}
			}
			
			for(int i = 0; i < 4; i++) {
				if(armor[i] != null) {
					int maxDamage = armor[i].getMaxDamage();
					int yOffset = 18 * i;
					float value = (float) (maxDamage - armor[i].getItemDamage()) / (float) maxDamage;
					int green = (int) (255 * value);
					int red = 256 - green;
					
					MC.MCProfiler.startSection("items");
					BetterHud.MC.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
					RenderHelper.enableGUIStandardItemLighting();
					if(x == -1) {
						MC.getRenderItem().renderItemAndEffectIntoGUI(armor[i], left ? 5 : resolution.getScaledWidth() - 21, cHeight + yOffset);
						//RenderUtil.renderItem(MC.getRenderItem(), MC.fontRendererObj, MC.getTextureManager(), armor[i], left ? 5 : resolution.getScaledWidth() - 21, cHeight + yOffset);
					} else {
						MC.getRenderItem().renderItemAndEffectIntoGUI(armor[i], x, y + yOffset);
						//RenderUtil.renderItem(MC.getRenderItem(), MC.fontRendererObj, MC.getTextureManager(), armor[i], x, y + yOffset);
					}
					RenderHelper.disableStandardItemLighting();
					MC.MCProfiler.endSection();
					
					if(showBars && armor[i].getMaxDamage() != 0) {
						MC.MCProfiler.startSection("bars");
						int barY = (x == -1 ? cHeight : y) + (name || durability ? 11 : 7) + yOffset;
						
						if(x == -1) {
							if(barType.equals("largeBars")) {
								RenderUtil.drawRect(left ? 26 : resolution.getScaledWidth() - 90, barY, left ? 90 : resolution.getScaledWidth() - 26, barY + 2, RenderUtil.colorRGB(0, 0, 0));
								RenderUtil.drawRect(left ? 26 : resolution.getScaledWidth() - 90, barY, (int) ((left ? 26 : resolution.getScaledWidth() - 90) + (value * 64)), barY + 1, RenderUtil.colorARGB(255, red, green, 0));
							} else if(barType.equals("smallBars")) {
								RenderUtil.drawRect(left ? 22 : resolution.getScaledWidth() - 24, cHeight + yOffset, left ? 24 : resolution.getScaledWidth() - 22, cHeight + yOffset + 16, RenderUtil.colorRGB(0, 0, 0));
								RenderUtil.drawRect(left ? 22 : resolution.getScaledWidth() - 24, cHeight + yOffset + 16 - (int) Math.ceil(value * 16), left ? 23 : resolution.getScaledWidth() - 23, cHeight + yOffset + 15, RenderUtil.colorARGB(255, red, green, 0));
							}
						} else {
							if(barType.equals("largeBars")) {
								int barX = left ? x + 21 : x - 69;
								RenderUtil.drawRect(barX, barY, barX + 64, barY + 2, RenderUtil.colorRGB(0, 0, 0));
								RenderUtil.drawRect(barX, barY, barX + Math.round(value * 64), barY + 1, RenderUtil.colorARGB(255, red, green, 0));
							} else if(barType.equals("smallBars")) {
								int barX = left ? x + 17 : x - 3;
								RenderUtil.drawRect(barX, y + yOffset, barX + 2, y + yOffset + 16, RenderUtil.colorRGB(0, 0, 0));
								RenderUtil.drawRect(barX, y + yOffset + 16 - (int) Math.ceil(value * 16), barX + 1, y + yOffset + 15, RenderUtil.colorARGB(255, red, green, 0));
							}
						}
						MC.MCProfiler.endSection();
					}
				}
			}
		}*/
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}
