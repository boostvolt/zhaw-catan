package ch.zhaw.hexboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.awt.Point;

/***
 * <p>
 * This class performs tests for the class {@link Edge}.
 * </p>
 *
 * @author tebe
 *
 **/
class EdgeTest {

    private final Point[] hexagon22 = {new Point(2, 0), new Point(3, 1), new Point(3, 3), new Point(2, 4),
            new Point(1, 3), new Point(1, 1)};
    private final Point[] hexagon75 = {new Point(7, 3), new Point(8, 4), new Point(8, 6), new Point(7, 7),
            new Point(6, 6), new Point(6, 4)};

    @Test
    void createValidEdge() {
        Assertions.assertDoesNotThrow(() -> new Edge(new Point(0, 0), new Point(1, 1)));
    }

    @Test
    void edgeEqualityStartEndPointReversed() {
        for (int i = 0; i < hexagon22.length - 1; i++) {
            Assertions.assertEquals(new Edge(hexagon22[i], hexagon22[i + 1]),
                    new Edge(hexagon22[i + 1], hexagon22[i]));
        }
        for (int i = 0; i < hexagon75.length - 1; i++) {
            Assertions.assertEquals(new Edge(hexagon75[i], hexagon75[i + 1]),
                    new Edge(hexagon75[i + 1], hexagon75[i]));
        }
    }

    @Test
    void notEquals() {
        Assertions.assertNotEquals(new Edge(hexagon22[0], hexagon22[1]),
                new Edge(hexagon22[1], hexagon22[2]));
    }

    @Test
    void createWithBothArgumentsNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(null, null));
    }

    @Test
    void createWithFirstArgumentNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(null, new Point(1, 0)));
    }

    @Test
    void createWithSecondArgumentNull() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(1, 0), null));
    }

    @Test
    void createWithStartAndEndpointIdentical() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(hexagon22[0], hexagon22[0]));
    }

    @Test
    void notAnEdgeHorizontalOddTop() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(5, 7), new Point(7, 7)));
    }

    @Test
    void notAnEdgeHorizontalOddMiddle() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(3, 2), new Point(5, 2)));
    }

    @Test
    void notAnEdgeHorizontalOddBottom() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(5, 3), new Point(7, 3)));
    }

    @Test
    void notAnEdgeHorizontalEvenTop() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(4, 4), new Point(6, 4)));
    }

    @Test
    void notAnEdgeHorizontalEvenMiddle() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(2, 5), new Point(4, 5)));
    }

    @Test
    void notAnEdgeHorizontalEvenBottom() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(4, 6), new Point(6, 6)));
    }

    @Test
    void notAnEdgeVerticalEven() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(7, 7), new Point(7, 3)));
    }

    @Test
    void notAnEdgeVerticalOdd() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> new Edge(new Point(6, 4), new Point(6, 0)));
    }

}
