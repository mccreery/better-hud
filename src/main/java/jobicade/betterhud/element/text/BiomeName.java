package jobicade.betterhud.element.text;

import java.util.Arrays;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import jobicade.betterhud.element.settings.DirectionOptions;
import jobicade.betterhud.element.settings.SettingPosition;
import jobicade.betterhud.geom.Direction;

public class BiomeName extends TextElement {
    @Override
    public void loadDefaults() {
        super.loadDefaults();
        position.setPreset(Direction.NORTH);
        settings.priority.set(-1);
    }

    public BiomeName() {
        super("biome", new SettingPosition(DirectionOptions.TOP_BOTTOM, DirectionOptions.NONE));
    }

    @Override
    protected List<String> getText() {
        BlockPos pos = new BlockPos((int)Minecraft.getInstance().player.field_70165_t, 0, (int)Minecraft.getInstance().player.field_70161_v);

        return Arrays.asList(getLocalizedName() + ": " + Minecraft.getInstance().level.getBiomeForCoordsBody(pos).func_185359_l());
    }
}
