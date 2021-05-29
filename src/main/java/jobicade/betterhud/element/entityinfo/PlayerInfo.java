package jobicade.betterhud.element.entityinfo;

import com.mojang.blaze3d.matrix.MatrixStack;
import jobicade.betterhud.element.settings.Setting;
import jobicade.betterhud.element.settings.SettingSlider;
import jobicade.betterhud.events.RenderMobInfoEvent;
import jobicade.betterhud.geom.Direction;
import jobicade.betterhud.geom.Point;
import jobicade.betterhud.geom.Rect;
import jobicade.betterhud.render.Color;
import jobicade.betterhud.render.Grid;
import jobicade.betterhud.render.Label;
import jobicade.betterhud.util.GlUtil;
import jobicade.betterhud.util.bars.StatBar;
import jobicade.betterhud.util.bars.StatBarArmor;
import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.eventbus.api.Event;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static jobicade.betterhud.BetterHud.MANAGER;

public class PlayerInfo extends EntityInfo {
    private StatBar<? super PlayerEntity> bar = new StatBarArmor();

    private SettingSlider tooltipLines;

    public PlayerInfo() {
        super("playerInfo");
    }

    @Override
    protected void addSettings(List<Setting<?>> settings) {
        super.addSettings(settings);
        settings.add(tooltipLines = new SettingSlider("tooltipLines", -1, 10, 1) {
            @Override
            public String getDisplayValue(double scaledValue) {
                if(scaledValue == -1) {
                    return I18n.get("betterHud.value.unlimited");
                } else {
                    return super.getDisplayValue(scaledValue);
                }
            }
        });
    }

    @Override
    public boolean shouldRender(Event event) {
        return super.shouldRender(event) && ((RenderMobInfoEvent)event).getEntity() instanceof PlayerEntity;
    }

    @Override
    public Rect render(Event event) {
        PlayerEntity player = (PlayerEntity)((RenderMobInfoEvent)event).getEntity();
        bar.setHost(player);
        List<String> tooltip = new ArrayList<String>();

        ItemStack held = player.getMainHandItem();

        if(!held.isEmpty()) {
            tooltip.add(I18n.get("betterHud.hud.holding", getStackName(held)));
            getEnchantmentLines(held, tooltip);

            int lines = tooltipLines.getInt();
            if(lines != -1 && lines < tooltip.size()) {
                tooltip = tooltip.subList(0, lines);
            }
        }

        MatrixStack matrixStack = ((RenderMobInfoEvent)event).getMatrixStack();
        List<Label> tooltipLabels = tooltip.stream().map(s -> new Label(matrixStack, s)).collect(Collectors.toList());
        Grid<Label> grid = new Grid<Label>(new Point(1, tooltip.size()), tooltipLabels)
            .setCellAlignment(Direction.WEST).setGutter(new Point(2, 2));

        Rect bounds = new Rect(grid.getPreferredSize().add(10, 10));
        if(bar.shouldRender()) bounds = bounds.grow(0, 0, 0, bar.getPreferredSize().getY() + 2);
        bounds = MANAGER.position(Direction.SOUTH, bounds);
        GlUtil.drawRect(bounds, Color.TRANSLUCENT);

        Rect inner = bounds.grow(-5);
        grid.setBounds(new Rect(grid.getPreferredSize()).anchor(inner, Direction.NORTH_WEST)).render();
        bar.setMatrixStack(matrixStack);
        if(bar.shouldRender()) bar.setBounds(new Rect(bar.getPreferredSize()).anchor(inner, Direction.SOUTH_WEST)).render();

        return null;
    }

    /** @see ItemStack#getTooltipLines(PlayerEntity, ITooltipFlag) */
    private String getStackName(ItemStack stack) {
        StringBuilder builder = new StringBuilder();

        if(stack.hasCustomHoverName()) {
            builder.append(TextFormatting.ITALIC);
        }
        if(stack.isEnchanted()) {
            builder.append(TextFormatting.AQUA);
        } else {
            builder.append(TextFormatting.GRAY);
        }
        builder.append(stack.getDisplayName());
        return builder.toString();
    }

    /** Adds strings representing the enchantments on {@code stack} to {@code dest} */
    private void getEnchantmentLines(ItemStack stack, List<String> dest) {
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);

        for(Map.Entry<Enchantment, Integer> enchantment : enchantments.entrySet()) {
            if(enchantment.getKey() != null && enchantment.getValue() > 0) {
                dest.add(TextFormatting.GRAY + enchantment.getKey().getFullname(enchantment.getValue()).toString());
            }
        }
    }

    @Override
    public void loadDefaults() {
        super.loadDefaults();
        tooltipLines.set(-1);
    }
}
