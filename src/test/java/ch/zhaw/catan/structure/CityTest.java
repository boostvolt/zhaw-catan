package ch.zhaw.catan.structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.zhaw.catan.game.Config.Faction.YELLOW;
import static ch.zhaw.catan.game.Config.Structure.CITY;
import static ch.zhaw.catan.structure.City.AMOUNT_PER_RESOURCE;
import static ch.zhaw.catan.structure.City.IDENTIFIER;
import static ch.zhaw.catan.structure.City.SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the {@link City} class.
 *
 */
class CityTest {

    private City city;

    /**
     * Sets up the test fixture by creating a new City object.
     */
    @BeforeEach
    void setup() {
        city = new City(YELLOW);
    }

    /**
     * Tests the {@link City#getStructureType()} method.
     */
    @Test
    void testGetStructureType() {
        assertEquals(CITY, city.getStructureType());
    }

    /**
     * Tests the {@link City#getScore()} method.
     */
    @Test
    void testGetScore() {
        assertEquals(SCORE, city.getScore());
    }

    /**
     * Tests the {@link City#getIdentifier()} method.
     */
    @Test
    void testGetIdentifier() {
        assertEquals(IDENTIFIER, city.getIdentifier());
    }

    /**
     * Tests the {@link City#getAmountPerResource()} method.
     */
    @Test
    void testGetAmountPerResource() {
        assertEquals(AMOUNT_PER_RESOURCE, city.getAmountPerResource());
    }

    /**
     * Tests the {@link City#toString()} method.
     */
    @Test
    void testToString() {
        assertEquals("y" + IDENTIFIER, city.toString());
    }

}
