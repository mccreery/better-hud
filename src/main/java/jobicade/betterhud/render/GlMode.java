package jobicade.betterhud.render;

import java.util.ArrayDeque;
import java.util.Deque;

import com.google.common.util.concurrent.Runnables;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;

public class GlMode {
	private final Runnable beginCallback, endCallback;

	protected void begin() {
		beginCallback.run();
	}

	protected void end() {
		endCallback.run();
	}

	public GlMode() {
		this(Runnables.doNothing(), Runnables.doNothing());
	}

	public GlMode(Runnable beginCallback, Runnable endCallback) {
		this.beginCallback = beginCallback;
		this.endCallback = endCallback;
	}

	public static final GlMode DEFAULT = new GlMode() {
		@Override
		public void begin() {
			Color.WHITE.apply();
			RenderHelper.disableStandardItemLighting();
			GlStateManager.disableRescaleNormal();
			GlStateManager.enableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableDepth();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
		}

		@Override
		public void end() {}
	};

	public static final GlMode ITEM = new GlMode() {
		@Override
		public void begin() {
			GlStateManager.enableDepth();
			RenderHelper.enableGUIStandardItemLighting();
		}

		@Override
		public void end() {
			GlStateManager.disableDepth();
			RenderHelper.disableStandardItemLighting();
		}
	};

	public static final GlMode INVERT = new GlMode() {
		@Override
		public void begin() {
			Minecraft.getMinecraft().getTextureManager().bindTexture(Gui.ICONS);
			GlStateManager.tryBlendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		}

		@Override
		public void end() {
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
		}
	};

	public static final GlMode OUTLINE = new GlMode() {
		@Override
		protected void begin() {
			GlStateManager.enableOutlineMode(Color.WHITE.getPacked());
		}

		@Override
		protected void end() {
			GlStateManager.disableOutlineMode();
		}
	};

	private static final Deque<GlMode> MODE_STACK = new ArrayDeque<>();

	private static void changeMode(GlMode from, GlMode to) {
		if(from != null && from != to) {
			from.end();
		}

		if(to == null) {
			to = GlMode.DEFAULT;
		}
		to.begin();
	}

	public static void push(GlMode mode) {
		changeMode(MODE_STACK.peek(), mode);
		MODE_STACK.push(mode);
	}

	public static void pop() {
		changeMode(MODE_STACK.poll(), MODE_STACK.peek());
	}

	public static void clear() {
		changeMode(MODE_STACK.peek(), null);
		MODE_STACK.clear();
	}

	public static void set(GlMode mode) {
		changeMode(MODE_STACK.poll(), mode);
		MODE_STACK.push(mode);
	}

	public static void clean() {
		DEFAULT.begin();
		changeMode(null, MODE_STACK.peek());
	}
}
