package ch.zhaw.catan.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;

import static ch.zhaw.catan.board.Field.THIEF_IDENTIFIER;
import static ch.zhaw.catan.game.Config.Land.HILLS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class FieldTest {

    private Field field;

    @BeforeEach
    void setup() {
        field = new Field(HILLS, new Point(1, 2));
    }

    @Test
    void testUnoccupiedByThief() {
        assertFalse(field.isOccupiedByThief());
        assertEquals(HILLS.getResource().toString(), field.toString());
    }

    @Test
    void testOccupiedByThief() {
        field.setOccupiedByThief(true);
        assertEquals(THIEF_IDENTIFIER, field.toString());
    }

}
