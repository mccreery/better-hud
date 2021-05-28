package jobicade.betterhud.gui;

import static jobicade.betterhud.BetterHud.SPACER;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.resources.I18n;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.geom.Size;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.DefaultBoxed;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.config.ConfigManager;
import jobicade.betterhud.config.ConfigSlot;
import jobicade.betterhud.config.FileConfigSlot;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.geom.Point;

public class GuiConfigSaves extends GuiScreen {
    private GuiTextField name;
    private GuiScrollbar scrollbar;
    private Rect viewport;

    private final GuiScreen previous;
    private final ConfigManager manager;

    private Grid<ListItem> list;
    private ConfigSlot selected;

    private GuiActionButton load, save;

    public GuiConfigSaves(ConfigManager manager, GuiScreen previous) {
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
        load.field_146124_l = selected != null;
        save.field_146124_l = selected != null && selected.isDest();
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
    public void func_73866_w_() {
        super.func_73866_w_();

        Point origin = new Point(field_146294_l / 2, field_146295_m / 16 + 20);

        field_146292_n.add(new GuiActionButton(I18n.get("gui.done"))
            .setCallback(b -> Minecraft.getInstance().setScreen(previous))
            .setBounds(new Rect(200, 20).align(origin, Direction.NORTH)));

        Rect textField = new Rect(150, 20);
        Rect smallButton = new Rect(50, 20);

        Rect fieldLine = new Rect(textField.getWidth() + (SPACER + smallButton.getWidth()) * 2, 20).align(origin.add(0, 20 + SPACER), Direction.NORTH);
        textField = textField.anchor(fieldLine, Direction.NORTH_WEST);

        name = new GuiTextField(0, field_146289_q, textField.getX(), textField.getY(), textField.getWidth(), textField.getHeight());
        name.setFocus(true);
        name.setCanLoseFocus(false);

        smallButton = smallButton.move(textField.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));
        field_146292_n.add(load = new GuiActionButton("Load").setCallback(b -> load()).setBounds(smallButton));

        smallButton = smallButton.move(smallButton.getAnchor(Direction.NORTH_EAST).add(SPACER, 0));
        field_146292_n.add(save = new GuiActionButton("Save").setCallback(b -> save()).setBounds(smallButton));

        viewport = new Rect(400, 0).align(fieldLine.getAnchor(Direction.SOUTH).add(0, SPACER), Direction.NORTH).withBottom(field_146295_m - 20);
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
    public void func_73876_c() {
        super.func_73876_c();
        name.tick();
    }

    @Override
    protected void func_73869_a(char typedChar, int keyCode) throws IOException {
        super.func_73869_a(typedChar, keyCode);
        name.func_146201_a(typedChar, keyCode);
        updateSelected();
    }

    @Override
    protected void func_73864_a(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.func_73864_a(mouseX, mouseY, mouseButton);

        name.func_146192_a(mouseX, mouseY, mouseButton);
        scrollbar.mouseClicked(mouseX, mouseY, mouseButton);

        if(viewport.contains(mouseX, mouseY)) {
            for(int i = 0; i < list.getSource().size(); i++) {
                Rect listBounds = getListBounds();

                if(list.getCellBounds(listBounds, new Point(0, i)).contains(mouseX, mouseY)) {
                    name.setValue(list.getSource().get(i).entry.getName());
                    updateSelected();
                }
            }
        }
    }

    @Override
    protected void func_146273_a(int mouseX, int mouseY, int button, long heldTime) {
        super.func_146273_a(mouseX, mouseY, button, heldTime);
        scrollbar.mouseClickMove(mouseX, mouseY, button, heldTime);
    }

    @Override
    public void func_146286_b(int mouseX, int mouseY, int button) {
        super.func_146286_b(mouseX, mouseY, button);
        scrollbar.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public void func_146274_d() throws IOException {
        super.func_146274_d();
        scrollbar.handleMouseInput();
    }

    @Override
    protected void func_146284_a(GuiButton button) throws IOException {
        if(button instanceof GuiActionButton) {
            ((GuiActionButton)button).actionPerformed();
        }
    }

    private Rect getListBounds() {
        Point origin = viewport.getAnchor(Direction.NORTH).sub(0, scrollbar.getScroll() - SPACER);
        return new Rect(list.getPreferredSize().withWidth(300)).align(origin, Direction.NORTH);
    }

    @Override
    public void func_73863_a(int mouseX, int mouseY, float partialTicks) {
        func_146276_q_();
        super.func_73863_a(mouseX, mouseY, partialTicks);

        name.func_146194_f();

        Rect scissorRect = viewport.withY(field_146295_m - viewport.getBottom()).scale(new ScaledResolution(Minecraft.getInstance()).func_78325_e());
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
