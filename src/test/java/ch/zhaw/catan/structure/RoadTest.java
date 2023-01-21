package ch.zhaw.catan.structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.zhaw.catan.game.Config.Faction.YELLOW;
import static ch.zhaw.catan.game.Config.Structure.ROAD;
import static ch.zhaw.catan.structure.Road.AMOUNT_PER_RESOURCE;
import static ch.zhaw.catan.structure.Road.IDENTIFIER;
import static ch.zhaw.catan.structure.Road.SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the {@link Road} class.
 *
 */
class RoadTest {

    private Road road;

    /**
     * Sets up the test fixture by creating a new Road object.
     */
    @BeforeEach
    void setup() {
        road = new Road(YELLOW);
    }

    /**
     * Tests the {@link Road#getStructureType()} method.
     */
    @Test
    void testGetStructureType() {
        assertEquals(ROAD, road.getStructureType());
    }

    /**
     * Tests the {@link Road#getScore()} method.
     */
    @Test
    void testGetScore() {
        assertEquals(SCORE, road.getScore());
    }

    /**
     * Tests the {@link Road#getIdentifier()} method.
     */
    @Test
    void testGetIdentifier() {
        assertEquals(IDENTIFIER, road.getIdentifier());
    }

    /**
     * Tests the {@link Road#getAmountPerResource()} method.
     */
    @Test
    void testGetAmountPerResource() {
        assertEquals(AMOUNT_PER_RESOURCE, road.getAmountPerResource());
    }

    /**
     * Tests the {@link Road#toString()} method.
     */
    @Test
    void testToString() {
        assertEquals("y" + IDENTIFIER, road.toString());
    }

}