package ro.hasna.ts.math.ml.distance;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import ro.hasna.ts.math.util.TimeSeriesPrecision;

import static org.junit.Assert.*;

/**
 * @since 1.0
 */
public class DynamicTimeWarpingTest {
    private DynamicTimeWarping distance;

    @Before
    public void setUp() throws Exception {
        distance = new DynamicTimeWarping(5);
    }

    @After
    public void tearDown() throws Exception {
        distance = null;
    }

    @Test
    public void testTriangleInequality() throws Exception {
        int n = 128;
        double a[] = new double[n];
        double b[] = new double[n];
        double c[] = new double[n];

        for (int i = 0; i < n; i++) {
            a[i] = i;
            b[i] = n - i;
            c[i] = i * i;
        }

        double ab = distance.compute(a, b);
        double ba = distance.compute(b, a);
        double bc = distance.compute(b, c);
        double ac = distance.compute(a, c);

        Assert.assertEquals(ab, ba, TimeSeriesPrecision.EPSILON);
        Assert.assertTrue(ab + bc >= ac);
        Assert.assertTrue(ab + ac >= bc);
        Assert.assertTrue(ac + bc >= ab);
    }

    @Test
    public void testEquality() throws Exception {
        int n = 128;
        double a[] = new double[n];
        double b[] = new double[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
            b[i] = i;
        }

        double result = distance.compute(a, b);

        Assert.assertEquals(0, result, TimeSeriesPrecision.EPSILON);
    }

    @Test
    public void testOverflow() throws Exception {
        int n = 128;
        double a[] = new double[n];
        double b[] = new double[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
            b[i] = i + 6;
        }

        double result = distance.compute(a, b, 99);

        Assert.assertEquals(Double.POSITIVE_INFINITY, result, TimeSeriesPrecision.EPSILON);
    }

    @Test
    public void testResult() throws Exception {
        int n = 100;
        double a[] = new double[n];
        double b[] = new double[n];
        for (int i = 0; i < n; i++) {
            a[i] = i;
            b[i] = i + 2;
        }

        double result = distance.compute(a, b);

        Assert.assertEquals(6, result, TimeSeriesPrecision.EPSILON);
    }
}