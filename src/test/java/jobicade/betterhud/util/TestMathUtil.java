package jobicade.betterhud.util;

import org.junit.Assert;
import org.junit.Test;

public class TestMathUtil {
    @Test
    public void testLerp() {
        // Test boundaries
        Assert.assertEquals(MathUtil.lerp(43, 50, 0), 43, 0);
        Assert.assertEquals(MathUtil.lerp(43, 50, 1), 50, 0);
        // Test rounding
        Assert.assertEquals(MathUtil.lerp(43, 50, 0.5f), 46.5f, 0);
    }
}
