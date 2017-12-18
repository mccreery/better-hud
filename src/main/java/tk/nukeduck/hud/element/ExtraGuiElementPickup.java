package tk.nukeduck.hud.element;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.element.settings.ElementSettingSlider;
import tk.nukeduck.hud.network.PickupHandler;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.FormatUtil;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.StringManager;
import tk.nukeduck.hud.util.constants.Colors;

public class ExtraGuiElementPickup extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePosition pos2;
	public ElementSettingSlider fadeSpeed;
	
	public final PickupHandler handler = new PickupHandler();
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.MIDDLE_CENTER;
		pos2.x = 5;
		pos2.y = 5;
		fadeSpeed.value = .5;
	}
	
	@Override
	public String getName() {
		return "itemPickup";
	}
	
	public ExtraGuiElementPickup() {
		this.settings.add(posMode = new ElementSettingMode("posMode", new String[] {"setPos", "absolute"}));
		this.settings.add(pos = new ElementSettingPosition("position", Position.combine(Position.TOP_LEFT, Position.TOP_RIGHT, Position.BOTTOM_LEFT, Position.BOTTOM_RIGHT, Position.MIDDLE_CENTER)) {
			@Override
			public boolean getEnabled() {
				return posMode.index == 0;
			}
		});
		this.settings.add(pos2 = new ElementSettingAbsolutePosition("position2") {
			@Override
			public boolean getEnabled() {
				return posMode.index == 1;
			}
		});
		this.settings.add(fadeSpeed = new ElementSettingSlider("fadeSpeed", 0.0, 1.0) {
			@Override
			public String getSliderText() {
				String display;

				if(this.value == 0.0) display = I18n.format("betterHud.setting.slowest");
				else if(this.value == 1.0) display = I18n.format("betterHud.setting.fastest");
				else {
					display = I18n.format("betterHud.strings.percent", FormatUtil.ONE_PLACE.format(this.value * 100));
				}

				return I18n.format("betterHud.menu.settingButton", this.getLocalizedName(), display);
			}
		});
		this.registerUpdates(UpdateSpeed.FASTER);
	}
	
	public void update(Minecraft mc) {
		handler.update(mc);
	}
	
	private Bounds bounds = Bounds.EMPTY;
	@Override
	public Bounds getBounds(ScaledResolution resolution) {
		return this.bounds;
	}
	
	public void render(Minecraft mc, ScaledResolution resolution, StringManager stringManager, LayoutManager layoutManager) {
		Map<ItemStack, Float> sortedMap = sortByComparator(handler.pickedUp);
		
		//String currentMode = getMode();
		boolean top = pos.value == Position.TOP_LEFT || pos.value == Position.TOP_RIGHT;
		boolean left = pos.value == Position.TOP_LEFT || pos.value == Position.BOTTOM_LEFT;
		
		int n = 0;
		for(Entry<ItemStack, Float> entry : sortedMap.entrySet()) {
			n++;
			
			ItemStack i = entry.getKey();
			String text = i.getCount() + "x " + i.getDisplayName();
			int textWidth = mc.fontRenderer.getStringWidth(text);
			
			int x, y;
			if(posMode.index == 1) {
				x = pos2.x + 21;
				y = pos2.y + 4;
			} else if(pos.value == Position.MIDDLE_CENTER) {
				x = resolution.getScaledWidth() / 2 - (textWidth / 2) + 10;
				y = resolution.getScaledHeight() / 2 - 40 - (mc.fontRenderer.FONT_HEIGHT + 5) * (n - 1);
			} else {
				x = left ? 26 : resolution.getScaledWidth() - textWidth - 5;
				y = top ? (mc.fontRenderer.FONT_HEIGHT + 5) * (n - 1) + layoutManager.get(pos.value) : resolution.getScaledHeight() - (mc.fontRenderer.FONT_HEIGHT + 5) * (n - 1) - mc.fontRenderer.FONT_HEIGHT - layoutManager.get(pos.value);
			}
			
			int a = (int) (entry.getValue() * 255);
			if(a >= 5) mc.ingameGUI.drawString(mc.fontRenderer, text, x, y, Colors.fromARGB(a, 255, 255, 255));
			
			RenderHelper.enableGUIStandardItemLighting();
			int x2 = x - 21;
			int y2 = y - (16 - mc.fontRenderer.FONT_HEIGHT) / 2;
			
			this.bounds = new Bounds(x2, y2, 21 + mc.fontRenderer.getStringWidth(text), 16);
			
			GL11.glPushMatrix(); {
				GL11.glTranslatef(x2 + 8, y2 + 8, 0f);
				float b = entry.getValue();
				GL11.glScalef(b, b, b);
				GL11.glTranslatef(-x2 - 8, -y2 - 10, 0f);
				
		        mc.getRenderItem().renderItemAndEffectIntoGUI(i, x2, y2);
			}
	        GL11.glPopMatrix();
		}
		if(n > 0) layoutManager.add((mc.fontRenderer.FONT_HEIGHT + 5) * n, pos.value);
	}
	
	private static Map<ItemStack, Float> sortByComparator(Map<ItemStack, Float> unsortMap) {
		List<Entry<ItemStack, Float>> list = new LinkedList<Entry<ItemStack, Float>>(unsortMap.entrySet());
		
		Collections.sort(list, new Comparator<Map.Entry<ItemStack, Float>>() {
			public int compare(Map.Entry<ItemStack, Float> o1, Map.Entry<ItemStack, Float> o2) {
				return (o1.getValue()).compareTo(o2.getValue());
			}
		});
		
		Map<ItemStack, Float> sortedMap = new LinkedHashMap<ItemStack, Float>();
		for (Iterator<Map.Entry<ItemStack, Float>> it = list.iterator(); it.hasNext();) {
			Map.Entry<ItemStack, Float> entry = it.next();
			sortedMap.put(entry.getKey(), entry.getValue());
		}
		return sortedMap;
	}

	@Override
	public boolean shouldProfile() {
		return true;
	}
}