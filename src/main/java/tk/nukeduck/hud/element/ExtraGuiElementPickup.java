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
import net.minecraft.item.ItemStack;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePosition;
import tk.nukeduck.hud.element.settings.ElementSettingAbsolutePositionAnchored;
import tk.nukeduck.hud.element.settings.ElementSettingAnchor;
import tk.nukeduck.hud.element.settings.ElementSettingMode;
import tk.nukeduck.hud.element.settings.ElementSettingPosition;
import tk.nukeduck.hud.element.settings.ElementSettingPosition.Position;
import tk.nukeduck.hud.network.PickupHandler;
import tk.nukeduck.hud.util.Bounds;
import tk.nukeduck.hud.util.LayoutManager;
import tk.nukeduck.hud.util.RenderUtil;
import tk.nukeduck.hud.util.StringManager;

public class ExtraGuiElementPickup extends ExtraGuiElement {
	private ElementSettingMode posMode;
	private ElementSettingPosition pos;
	private ElementSettingAbsolutePosition pos2;
	
	public final PickupHandler handler = new PickupHandler();
	
	@Override
	public void loadDefaults() {
		this.enabled = true;
		posMode.index = 0;
		pos.value = Position.MIDDLE_CENTER;
		pos2.x = 5;
		pos2.y = 5;
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
			String text = i.stackSize + "x " + i.getDisplayName();
			int textWidth = mc.fontRendererObj.getStringWidth(text);
			
			int x, y;
			if(posMode.index == 1) {
				x = pos2.x + 21;
				y = pos2.y + 4;
			} else if(pos.value == Position.MIDDLE_CENTER) {
				x = resolution.getScaledWidth() / 2 - (textWidth / 2) + 10;
				y = resolution.getScaledHeight() / 2 - 40 - (mc.fontRendererObj.FONT_HEIGHT + 5) * (n - 1);
			} else {
				x = left ? 26 : resolution.getScaledWidth() - textWidth - 5;
				y = top ? (mc.fontRendererObj.FONT_HEIGHT + 5) * (n - 1) + layoutManager.get(pos.value) : resolution.getScaledHeight() - (mc.fontRendererObj.FONT_HEIGHT + 5) * (n - 1) - mc.fontRendererObj.FONT_HEIGHT - layoutManager.get(pos.value);
			}
			
			int a = (int) (entry.getValue() * 255);
			if(a >= 5) mc.ingameGUI.drawString(mc.fontRendererObj, text, x, y, RenderUtil.colorARGB(a, 255, 255, 255));
			
			RenderHelper.enableGUIStandardItemLighting();
			int x2 = x - 21;
			int y2 = y - (16 - mc.fontRendererObj.FONT_HEIGHT) / 2;
			
			this.bounds = new Bounds(x2, y2, 21 + mc.fontRendererObj.getStringWidth(text), 16);
			
			GL11.glPushMatrix(); {
				GL11.glTranslatef(x2 + 8, y2 + 8, 0f);
				float b = entry.getValue();
				GL11.glScalef(b, b, b);
				GL11.glTranslatef(-x2 - 8, -y2 - 10, 0f);
				
		        mc.getRenderItem().renderItemAndEffectIntoGUI(i, x2, y2);
			}
	        GL11.glPopMatrix();
		}
		if(n > 0) layoutManager.add((mc.fontRendererObj.FONT_HEIGHT + 5) * n, pos.value);
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