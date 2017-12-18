package tk.nukeduck.hud.element;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.lang3.ArrayUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSetting;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingBoolean;
import tk.nukeduck.hud.element.settings.ElementSettingBooleanLeft;
import tk.nukeduck.hud.element.settings.ElementSettingDivider;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingModeRight;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingPositionHorizontal;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.element.settings.ElementSettingSliderPositioned;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.Point;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Constants;

public class ExtraGuiElementArmorBars extends ExtraGuiElement {
	private ElementSettingMode bars;
	private ElementSettingBoolean showName;
	private ElementSettingBoolean showDurability;
	private ElementSettingMode durabilityMode;
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePositionAnchored pos2;
	private ElementSettingAnchor anchor;
	private ElementSettingPosition alignment;
	private ElementSettingSlider[] damageWarnings;
	
	public static final DecimalFormat ONE_PLACE = new DecimalFormat("#.#");
	
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
		damageWarnings[0].value = 45.0;
		damageWarnings[1].value = 25.0;
		damageWarnings[2].value = 10.0;
	}
	
	@Override
	public String getName() {
		return "armorBars";
	}
	
	private Bounds bounds = Bounds.EMPTY;
	
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public void updateBounds(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		
	}
	
	public ExtraGuiElementArmorBars() {
		this.settings.add(new ElementSettingDivider("position"));
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPositionHorizontal("position", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.settings.add(alignment = new ElementSettingPositionHorizontal("alignment", Position.combine(Position.MIDDLE_LEFT, Position.MIDDLE_RIGHT)) {
			@Override
			public boolean getEnabled() {
				return posMode.index != 0;
			}
			
			@Override
			public void otherAction(Collection<ElementSetting> settings) {
				if(posMode.index == 0) {
					this.value = pos.value;
				}
				super.otherAction(settings);
			}
		});
		this.anchor = new ElementSettingAnchor("anchor");
		this.settings.add(pos2 = new ElementSettingAbsolutePositionAnchored("position2", this.anchor) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(anchor);
		this.settings.add(new ElementSettingDivider("misc"));
		this.settings.add(showDurability = new ElementSettingBooleanLeft("showDurability"));
		this.settings.add(durabilityMode = new ElementSettingModeRight("durabilityMode", new String[] {"values", "percent"}) {
			@Override
			public boolean getEnabled() {
				return showDurability.value;
			}
		});
		this.settings.add(showName = new ElementSettingBoolean("showName"));
		this.settings.add(bars = new ElementSettingMode("barType", new String[] {"hidden", "smallBars", "largeBars"}));
		bars.comments.add("hidden, smallBars, largeBars");
		
		this.settings.add(new ElementSettingDivider("damageWarning"));
		damageWarnings = new ElementSettingSlider[3];
		Position[] positions = new Position[] {Position.MIDDLE_LEFT, Position.MIDDLE_CENTER, Position.MIDDLE_RIGHT};
		for(int i = 0; i < 3; i++) {
			this.settings.add(damageWarnings[i] = new ElementSettingSliderPositioned("damaged." + String.valueOf(i), 1, 100, positions[i]) {
				@Override
				public String getSliderText() {
					return FormatUtil.translatePre("menu.settingButton", this.getLocalizedName(), FormatUtil.translatePre("strings.percent", String.valueOf((int) this.value)));
				}
			});
			damageWarnings[i].accuracy = 1;
		}
	}
	
	public void update(Minecraft mc) {}
	
	public String generateText(ItemStack item) {
		ArrayList<String> parts = new ArrayList<String>();
		if(this.showName.value) parts.add(item.getDisplayName());
		int maxDamage = item.getMaxDamage();
		float value = (float) (maxDamage - item.getItemDamage()) / (float) maxDamage;
		if(this.showDurability.value) {
			if(durabilityMode.getValue().equals("percent")) {
				parts.add(FormatUtil.translatePre("strings.percent", ONE_PLACE.format(value * 100.0)));
			} else {
				parts.add(FormatUtil.translatePre("strings.outOf", String.valueOf(maxDamage - item.getItemDamage()), String.valueOf(maxDamage)));
			}
		}

		int count = -1;
		for(int a = 0; a < this.damageWarnings.length; a++) {
			if(value * 100.0f <= damageWarnings[a].value) {
				count = a;
			}
		}
		String exclamation = count == -1 ? "" : FormatUtil.translatePre("strings.damaged." + count);
		return FormatUtil.separate(parts.toArray(new String[parts.size()])) + " " + exclamation;
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
	
	private Bounds generateBounds(Minecraft mc, ScaledResolution resolution, LayoutManager layoutManager, ItemStack[] armor) {
		Bounds b = Bounds.EMPTY.clone();
		b.setWidth(16);
		
		boolean anyArmor = false;
		for(int i = 0; i < 4; i++) {
			ItemStack item = armor[i];
			if(item != null) {
				anyArmor = true;
				
				if(this.showName.value || this.showDurability.value) {
					this.textCache[i] = generateText(item);
					widthCache[i] = 21 + mc.fontRendererObj.getStringWidth(textCache[i]);
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
	
	@Override
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		boolean left = alignment.value == Position.MIDDLE_LEFT;
		int cHeight = layoutManager.get(left ? Position.TOP_LEFT : Position.TOP_RIGHT);
		
		ItemStack[] armor = mc.thePlayer.inventory.armorInventory.clone();
		ArrayUtils.reverse(armor);
		
		this.bounds = generateBounds(mc, resolution, layoutManager, armor);
		pos2.update(resolution, this.bounds);

		if(this.bounds.getHeight() == 0) return;
		
		if(posMode.index == 0) {
			layoutManager.add(this.bounds.getHeight(), left ? Position.TOP_LEFT : Position.TOP_RIGHT);
		}
		
		mc.mcProfiler.startSection("items");
		RenderHelper.enableGUIStandardItemLighting();
		for(int i = 0; i < 4; i++) {
			ItemStack stack = armor[i];
			if(stack != null) {
				mc.getRenderItem().renderItemAndEffectIntoGUI(stack, left ? this.bounds.getX() : this.bounds.getX2() - 16, this.bounds.getY() + i * 18);
			}
		}
		RenderHelper.disableStandardItemLighting();
		mc.mcProfiler.endSection();
		
		if(this.showName.value || this.showDurability.value) {
    		mc.mcProfiler.startSection("text");
    		int textOffset = this.bars.index == 2 ? 2 : 4; 
    		for(int i = 0; i < 4; i++) {
    			ItemStack stack = armor[i];
    			if(stack != null) {
    				int x = left ? this.bounds.getX() + 21 : this.bounds.getX2() - widthCache[i];
    				mc.ingameGUI.drawString(mc.fontRendererObj, this.textCache[i], x, this.bounds.getY() + i * 18 + textOffset, 0xffffff);
    			}
    		}
    		mc.mcProfiler.endSection();
		}
		
		if(this.bars.index != 0) {
    		mc.mcProfiler.startSection("bars");
    		for(int i = 0; i < 4; i++) {
    			if(armor[i] == null) continue;
    			
    			float value = (float) (armor[i].getMaxDamage() - armor[i].getItemDamage()) / (float) armor[i].getMaxDamage();
    			if(this.bars.index == 2) {
    				int x = this.alignment.value == Position.MIDDLE_LEFT ? this.bounds.getX() + 21 : this.bounds.getX2() - 85;
    				int y = this.bounds.getY() + i * 18 + 12 + (this.showName.value || this.showDurability.value ? 0 : -4);
    				RenderUtil.drawProgressBar(x, y, x + 64, y + 2, value);
    			} else {
    				int x = this.alignment.value == Position.MIDDLE_LEFT ? this.bounds.getX() + 16 : this.bounds.getX2() - 18;
    				int y = this.bounds.getY() + i * 18;
    				RenderUtil.drawProgressBarV(x, y, x + 2, y + 16, value);
    			}
    		}
    		mc.mcProfiler.endSection();
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
							dur = FormatUtil.translatePre("strings.percent", ONE_PLACE.format(value * 100.0));
						} else {
							dur = FormatUtil.translatePre("strings.outOf", String.valueOf(maxDamage - armor[i].getItemDamage()), String.valueOf(maxDamage));
						}
						
						if(armor[i].getMaxDamage() != 0) {
    						if(name && durability) {
    							text = FormatUtil.translatePre("strings.separated", armor[i].getDisplayName(), dur);
    						} else if(name) {
    							text = armor[i].getDisplayName();
    						} else if(durability) {
    							text = dur;
    						}
						} else if(name) {
							text = armor[i].getDisplayName();
						}
						int textY = (x == -1 ? cHeight : y) + yOffset;
						int textWidth = mc.fontRendererObj.getStringWidth(text);
						this.bounds.setWidth(Math.max(this.bounds.getWidth(), textWidth));
					}
				}
			}
			
			if(name || durability) {
				for(int i = 0; i < 4; i++) {
					if(armor[i] != null) {

						
						//text = FormatUtil.translatePre("strings.outOf", String.valueOf(maxDamage - armor[i].getItemDamage()), String.valueOf(maxDamage));
						//if(shouldDoName) text = FormatUtil.translatePre("strings.separated", armor[i].getDisplayName(), text + ChatFormatting.RESET);
						
						int textY = (x == -1 ? cHeight : y) + yOffset;
						int textWidth = mc.fontRendererObj.getStringWidth(text);
						if(!barType.equals("largeBars") || armor[i].getMaxDamage() == 0) textY += 4;
						
						if(name || durability) {
							mc.mcProfiler.startSection("text");
							if(x == -1) {
								mc.ingameGUI.drawString(mc.fontRendererObj, text, left ? 26 : resolution.getScaledWidth() - 26 - textWidth, textY, RenderUtil.colorRGB(255, 255, 255));
							} else {
								mc.ingameGUI.drawString(mc.fontRendererObj, text, left ? x + 21 : this.bounds.getX2() - textWidth - 21, textY, RenderUtil.colorRGB(255, 255, 255));
							}
							mc.mcProfiler.endSection();
						}
						
						mc.mcProfiler.startSection("textWarning");
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
						String exclamation = count == -1 ? "" : FormatUtil.translatePre("strings.damaged." + count);
						
						if(!exclamation.equals("")) {
    						if(x == -1) {
    							mc.ingameGUI.drawString(mc.fontRendererObj, exclamation, left ? 31 + textWidth : resolution.getScaledWidth() - 31 - textWidth - mc.fontRendererObj.getStringWidth(exclamation), textY, RenderUtil.colorRGB(255, 0, 0));
    						} else {
    							mc.ingameGUI.drawString(mc.fontRendererObj, exclamation, left ? x + 26 + textWidth : x - 10 - textWidth - mc.fontRendererObj.getStringWidth(exclamation), textY, RenderUtil.colorRGB(255, 0, 0));
    						}
						}
						mc.mcProfiler.endSection();
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
					
					mc.mcProfiler.startSection("items");
					BetterHud.mc.getTextureManager().bindTexture(TextureMap.locationBlocksTexture);
					RenderHelper.enableGUIStandardItemLighting();
					if(x == -1) {
						mc.getRenderItem().renderItemAndEffectIntoGUI(armor[i], left ? 5 : resolution.getScaledWidth() - 21, cHeight + yOffset);
						//RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), armor[i], left ? 5 : resolution.getScaledWidth() - 21, cHeight + yOffset);
					} else {
						mc.getRenderItem().renderItemAndEffectIntoGUI(armor[i], x, y + yOffset);
						//RenderUtil.renderItem(mc.getRenderItem(), mc.fontRendererObj, mc.getTextureManager(), armor[i], x, y + yOffset);
					}
					RenderHelper.disableStandardItemLighting();
					mc.mcProfiler.endSection();
					
					if(showBars && armor[i].getMaxDamage() != 0) {
						mc.mcProfiler.startSection("bars");
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
						mc.mcProfiler.endSection();
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
