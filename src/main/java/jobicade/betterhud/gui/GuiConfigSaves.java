package jobicade.betterhud.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.ConfigSlot;
import jobicade.betterhud.config.FileConfigSlot;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.DefaultBoxed;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;
import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import static jobicade.betterhud.BetterHud.SPACER;

public class GuiConfigSaves extends Screen {
    private TextFieldWidget name;
    private GuiScrollbar scrollbar;
    private Rect viewport;

    private final Screen previous;
    private final ConfigManager manager;

    private Grid<ListItem> list;
    private ConfigSlot selected;

    private GuiActionButton load, save;

    public GuiConfigSaves(ConfigManager manager, Screen previous) {
        super(new StringTextComponent(""));
        this.previous = previous;
        this.manager = manager;
    }

    private ConfigSlot getSelectedEntry() {
        if(StringUtils.isBlank(name.getValue())) return null;

        return list.getSource().stream().map(li -> li.entry)
            .filter(e -> e.matches(name.getValue())).findFirst()
            .orElseGet(() -> new FileConfigSlot(manager.getRootDirectory().resolve(name.getValue() + ".cfg")));
    }

    private void updateSelected() {
        selected = getSelectedEntry();
        load.active = selected != null;
        save.active = selected != null && selected.isDest();
    }

    private void save() {
        try {
            manager.getConfig().save();
            selected.copyFrom(manager.getConfigPath());
            updateList();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void load() {
        try {
            selected.copyTo(manager.getConfigPath());
            manager.reloadConfig();
        } catch(IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void init() {
        super.init();

        Point origin = new Point(width / 2, height / 16 + 20);

        buttons.add(new GuiActionButton(I18n.get("gui.done"))
            .setCallback(b -> Minecraft.getInstance().setScreen(previous))
            .setBounds(new Rect(200, 20).align(origin, Direction.NORTH)));

        Rect textField = new Rect(150, 20);
        Rect smallButton = new Rect(50, 20);

        Rect fieldLine = new Rect(textField.getWidth() + (SPACER + smallButton.getWidth()) * 2, 20).align(origin.add(0, 20 + SPACER), Direction.NORTH);
        textField = textField.anchor(fieldLine, Direction.NORTH_WEST);

        name = new TextFieldWidget(font, textField.getX(), textField.getY(), textField.getWidth(), textField.getHeight(), StringTextComponent.EMPTY);
        name.setFocus(true);
        name.setCanLoseFocus(false);

        smallButton = smallButton.move(textField.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));
        buttons.add(load = new GuiActionButton("Load").setCallback(b -> load()).setBounds(smallButton));

        smallButton = smallButton.move(smallButton.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));
        buttons.add(save = new GuiActionButton("Save").setCallback(b -> save()).setBounds(smallButton));

        viewport = new Rect(400, 0).align(fieldLine.getAnchor(Direction.SOUTH).add(0, SPACER), Direction.NORTH).withBottom(height - 20);
        scrollbar = new GuiScrollbar(viewport, 0);
        updateList();
    }

    private void updateList() {
        List<ListItem> listItems = manager.getSlots().stream().map(ListItem::new).collect(Collectors.toList());
        list = new Grid<>(new Point(1, listItems.size()), listItems);
        list.setStretch(true);

        scrollbar.setContentHeight(list.getPreferredSize().getHeight());
        updateSelected();
    }

    @Override
    public void tick() {
        super.tick();
        name.tick();
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        super.keyPressed(keyCode, scanCode, modifiers);
        name.keyPressed(keyCode, scanCode, modifiers);
        updateSelected();
        return false;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        super.mouseClicked(mouseX, mouseY, mouseButton);

        name.mouseClicked(mouseX, mouseY, mouseButton);
        scrollbar.mouseClicked(mouseX, mouseY, mouseButton);

        if(viewport.contains((int)mouseX, (int)mouseY)) {
            for(int i = 0; i < list.getSource().size(); i++) {
                Rect listBounds = getListBounds();

                if(list.getCellBounds(listBounds, new Point(0, i)).contains((int)mouseX, (int)mouseY)) {
                    name.setValue(list.getSource().get(i).entry.getName());
                    updateSelected();
                }
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double p_231045_6_, double p_231045_8_) {
        super.mouseDragged(mouseX, mouseY, button, p_231045_6_, p_231045_8_);
        scrollbar.mouseClickMove((int)mouseX, (int)mouseY, button, 0);
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        scrollbar.mouseReleased((int)mouseX, (int)mouseY, button);
        return false;
    }

    @Override
    public boolean mouseScrolled(double p_231043_1_, double p_231043_3_, double p_231043_5_) {
        super.mouseScrolled(p_231043_1_, p_231043_3_, p_231043_5_);
        scrollbar.mouseScrolled((int)p_231043_5_);
        return false;
    }

    private Rect getListBounds() {
        Point origin = viewport.getAnchor(Direction.NORTH).sub(0, scrollbar.getScroll() - SPACER);
        return new Rect(list.getPreferredSize().withWidth(300)).align(origin, Direction.NORTH);
    }

    private MatrixStack matrixStack;

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
        this.matrixStack = matrixStack;
        renderBackground(matrixStack);
        super.render(matrixStack, mouseX, mouseY, partialTicks);

        name.render(matrixStack, mouseX, mouseY, partialTicks);

        Rect scissorRect = viewport.withY(height - viewport.getBottom()).scale((int)Minecraft.getInstance().getWindow().getGuiScale());
        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(scissorRect.getX(), scissorRect.getY(), scissorRect.getWidth(), scissorRect.getHeight());

        list.setBounds(getListBounds()).render();

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        scrollbar.drawScrollbar(mouseX, mouseY);
    }

    private class ListItem extends DefaultBoxed {
        private final ConfigSlot entry;
        private final Label label;

        private ListItem(ConfigSlot entry) {
            this.entry = entry;

            this.label = new Label(matrixStack, entry.getName());
            if(!entry.isDest()) label.setColor(Color.GRAY);
        }

        @Override
        public Size getPreferredSize() {
            return label.getPreferredSize().add(40, 0).withHeight(20);
        }

        @Override
        public Size negotiateSize(Point size) {
            Size labelSize = label.getPreferredSize();
            int minWidth = labelSize.getWidth() + 5;
            int minHeight = Math.max(labelSize.getHeight(), 20);

            return new Size(Math.max(minWidth, size.getX()), Math.max(minHeight, size.getY()));
        }

        @Override
        public void render() {
            if(selected == this.entry) {
                GlUtil.drawRect(bounds, new Color(48, 0, 0, 0));
                GlUtil.drawBorderRect(bounds, new Color(160, 144, 144, 144));
            }
            label.setBounds(new Rect(label.getPreferredSize()).anchor(bounds, Direction.CENTER)).render();
        }
    }
}
