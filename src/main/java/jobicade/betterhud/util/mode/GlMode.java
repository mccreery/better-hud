package jobicade.betterhud.util.mode;

import static jobicade.betterhud.BetterHud.*;

import java.util.ArrayDeque;
import java.util.Deque;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.client.renderer.RenderHelper;
import jobicade.betterhud.util.Colors;
import jobicade.betterhud.util.GlUtil;

public class GlMode {
	private Runnable beginCallback, endCallback;

	protected void begin() {
		if(beginCallback != null) beginCallback.run();
	}

	protected void end() {
		if(endCallback != null) endCallback.run();;
	}

	public GlMode() {}

	public GlMode(Runnable beginCallback, Runnable endCallback) {
		this.beginCallback = beginCallback;
		this.endCallback = endCallback;
	}

	public static final GlMode DEFAULT = new GlMode() {
		@Override
		public void begin() {
			GlUtil.color(Colors.WHITE);
			GlStateManager.disableLighting();
			GlStateManager.disableAlpha();
			GlStateManager.enableBlend();
			GlStateManager.disableDepth();
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
			MC.getTextureManager().bindTexture(ICONS);
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
			MC.getTextureManager().bindTexture(ICONS);
			GlStateManager.tryBlendFuncSeparate(SourceFactor.ONE_MINUS_DST_COLOR, DestFactor.ONE_MINUS_SRC_COLOR, SourceFactor.ONE, DestFactor.ZERO);
		}

		@Override
		public void end() {
			GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
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
