package ch.zhaw.hexboard;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;

/***
 * <p>
 * Tests for the class {@link HexBoard}.
 * </p>
 * @author tebe
 */
class HexBoardTest {
    private HexBoard<String, String, String, String> board;
    private Point[] corner;

    /**
     * Setup for a test - Instantiates a board and adds one field at (7,5).
     *
     * <pre>
     *         0    1    2    3    4    5    6    7    8
     *         |    |    |    |    |    |    |    |    |   ...
     *
     *  0----
     *
     *  1----
     *
     *  2----
     *
     *  3----                                     C
     *                                         /     \
     *  4----                                C         C
     *
     *  5----                                |    F    |        ...
     *
     *  6----                                C         C
     *                                         \     /
     *  7----                                     C
     * </pre>
     */
    @BeforeEach
    public void setUp() {
        board = new HexBoard<>();
        board.addField(new Point(7, 5), "00");
        Point[] singleField = {new Point(7, 3), new Point(8, 4), new Point(8, 6), new Point(7, 7),
                new Point(6, 6), new Point(6, 4)};
        this.corner = singleField;
    }

    // Edge retrieval
    @Test
    void edgeTest() {
        for (int i = 0; i < corner.length - 1; i++) {
            Assertions.assertNull(board.getEdge(corner[i], corner[i + 1]));
            board.setEdge(corner[i], corner[i + 1], Integer.toString(i));
            Assertions.assertEquals(board.getEdge(corner[i], corner[i + 1]), Integer.toString(i));
        }
    }

    @Test
    void noEdgeCoordinatesTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> board.getEdge(new Point(2, 2), new Point(0, 2)));
    }

    @Test
    void edgeDoesNotExistTest() {
        Assertions.assertThrows(IllegalArgumentException.class,
                () -> board.getEdge(new Point(0, 2), new Point(3, 1)));
    }

    // Corner retrieval
    @Test
    void cornerTest() {
        for (Point p : corner) {
            Assertions.assertNull(board.getCorner(p));
            board.setCorner(p, p.toString());
            Assertions.assertEquals(board.getCorner(p), p.toString());
        }
    }

    @Test
    void noCornerCoordinateTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> board.getCorner(new Point(2, 2)));
    }

    @Test
    void cornerDoesNotExistTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> board.getCorner(new Point(2, 2)));
    }

    // Field addition/retrieval
    @Test
    void fieldAreadyExistsErrorTest() {
        board.addField(new Point(2, 2), "22");
        Assertions.assertThrows(IllegalArgumentException.class, () -> board.addField(new Point(2, 2), "22"));
    }

    @Test
    void fieldRetrievalTest() {
        Point field = new Point(2, 2);
        board.addField(field, "22");
        Assertions.assertTrue(board.hasField(field));
        Assertions.assertEquals("22", board.getField(field));
    }

    @Test
    void fieldRetrievalWrongCoordinatesOutsideTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> board.getField(new Point(10, 10)));
    }

    @Test
    void fieldRetrievalWrongCoordinatesInsideTest() {
        Assertions.assertThrows(IllegalArgumentException.class, () -> board.getField(new Point(2, 2)));
    }
}
