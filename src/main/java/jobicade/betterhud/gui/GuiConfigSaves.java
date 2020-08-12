package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.MC;
import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

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
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.StringTextComponent;

public class GuiConfigSaves extends Screen {
    private TextFieldWidget name;
    private GuiScrollbar scrollbar;
    private Rect viewport;

    private final Screen previous;
    private final ConfigManager manager;

    private Grid<ListItem> list;
    private ConfigSlot selected;

    private SuperButton load, save;

    public GuiConfigSaves(ConfigManager manager, Screen previous) {
        super(new StringTextComponent(""));
        this.previous = previous;
        this.manager = manager;
    }

    private ConfigSlot getSelectedEntry() {
        if(StringUtils.isBlank(name.getText())) return null;

        return list.getSource().stream().map(li -> li.entry)
            .filter(e -> e.matches(name.getText())).findFirst()
            .orElseGet(() -> new FileConfigSlot(manager.getRootDirectory().resolve(name.getText() + ".cfg")));
    }

    private void updateSelected() {
        selected = getSelectedEntry();
        load.active = selected != null;
        save.active = selected != null && selected.isDest();
    }

    private void save() {
        try {
            manager.getConfig().saveSettings();
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

        SuperButton done = new SuperButton(b -> MC.displayGuiScreen(previous));
        done.setBounds(new Rect(200, 20).align(origin, Direction.NORTH));
        done.setMessage(I18n.format("gui.done"));
        buttons.add(done);

        Rect textField = new Rect(150, 20);
        Rect smallButton = new Rect(50, 20);

        Rect fieldLine = new Rect(textField.getWidth() + (SPACER + smallButton.getWidth()) * 2, 20).align(origin.add(0, 20 + SPACER), Direction.NORTH);
        textField = textField.anchor(fieldLine, Direction.NORTH_WEST);

        name = new TextFieldWidget(font, textField.getX(), textField.getY(), textField.getWidth(), textField.getHeight(), "");
        name.changeFocus(true);
        name.setCanLoseFocus(false);

        smallButton = smallButton.move(textField.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));

        load = new SuperButton(b -> load());
        load.setBounds(smallButton);
        load.setMessage("Load");
        buttons.add(load);

        smallButton = smallButton.move(smallButton.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));

        save = new SuperButton(b -> save());
        save.setBounds(smallButton);
        save.setMessage("Save");
        buttons.add(save);

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
    public boolean keyPressed(int p_keyPressed_1_, int p_keyPressed_2_, int p_keyPressed_3_) {
        boolean b = name.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_) ||
            super.keyPressed(p_keyPressed_1_, p_keyPressed_2_, p_keyPressed_3_);

        updateSelected();
        return b;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int mouseButton) {
        if (name.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }
        if (scrollbar.mouseClicked(mouseX, mouseY, mouseButton)) {
            return true;
        }

        if(viewport.contains((int)mouseX, (int)mouseY)) {
            for(int i = 0; i < list.getSource().size(); i++) {
                Rect listBounds = getListBounds();

                if(list.getCellBounds(listBounds, new Point(0, i)).contains((int)mouseX, (int)mouseY)) {
                    name.setText(list.getSource().get(i).entry.getName());
                    updateSelected();
                }
            }
        }

        return super.mouseClicked(mouseX, mouseY, mouseButton);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long heldTime) {
        super.mouseClickMove(mouseX, mouseY, button, heldTime);
        scrollbar.mouseClickMove(mouseX, mouseY, button, heldTime);
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int button) {
        super.mouseReleased(mouseX, mouseY, button);
        scrollbar.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        scrollbar.handleMouseInput();
    }

    @Override
    protected void actionPerformed(Button button) throws IOException {
        if(button instanceof GuiActionButton) {
            ((GuiActionButton)button).actionPerformed();
        }
    }

    private Rect getListBounds() {
        Point origin = viewport.getAnchor(Direction.NORTH).sub(0, scrollbar.getScroll() - SPACER);
        return new Rect(list.getPreferredSize().withWidth(300)).align(origin, Direction.NORTH);
    }

    @Override
    public void render(int mouseX, int mouseY, float partialTicks) {
        renderBackground();
        super.render(mouseX, mouseY, partialTicks);

        name.render(mouseX, mouseY, partialTicks);

        float scaleFactor = (float)MC.getMainWindow().getGuiScaleFactor();

        Rect scissorRect = viewport.withY(height - viewport.getBottom()).scale(scaleFactor, scaleFactor);
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

            this.label = new Label(entry.getName());
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
