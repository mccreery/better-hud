package jobicade.betterhud.util;

import org.junit.Assert;
import org.junit.Test;

public class TestMathUtil {
    @Test
    public void testLerp() {
        // Test boundaries
        Assert.assertEquals(43, MathUtil.lerp(43, 50, 0), 0);
        Assert.assertEquals(50, MathUtil.lerp(43, 50, 1), 0);
        // Test rounding
        Assert.assertEquals(46.5f, MathUtil.lerp(43, 50, 0.5f), 0);
    }

    @Test
    public void testCeilDiv() {
        // Divide by zero cases
        Assert.assertThrows(ArithmeticException.class, () -> MathUtil.ceilDiv(5, 0));
        Assert.assertThrows(ArithmeticException.class, () -> MathUtil.ceilDiv(-5, 0));
        // 4 cases for signs of dividend and divisor
        Assert.assertEquals(4, MathUtil.ceilDiv(12, 3));
        Assert.assertEquals(4, MathUtil.ceilDiv(-12, -3));
        Assert.assertEquals(-4, MathUtil.ceilDiv(-12, 3));
        Assert.assertEquals(-4, MathUtil.ceilDiv(12, -3));
        // Ceiling for 4 cases
        Assert.assertEquals(4, MathUtil.ceilDiv(10, 3));
        Assert.assertEquals(4, MathUtil.ceilDiv(-10, -3));
        Assert.assertEquals(-3, MathUtil.ceilDiv(-10, 3));
        Assert.assertEquals(-3, MathUtil.ceilDiv(10, -3));
    }
}
