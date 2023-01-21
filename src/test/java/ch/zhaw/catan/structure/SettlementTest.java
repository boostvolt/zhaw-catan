package ch.zhaw.catan.structure;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static ch.zhaw.catan.game.Config.Faction.YELLOW;
import static ch.zhaw.catan.game.Config.Structure.SETTLEMENT;
import static ch.zhaw.catan.structure.Settlement.AMOUNT_PER_RESOURCE;
import static ch.zhaw.catan.structure.Settlement.IDENTIFIER;
import static ch.zhaw.catan.structure.Settlement.SCORE;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for the {@link Settlement} class.
 *
 */
class SettlementTest {

    private Settlement settlement;

    /**
     * Sets up the test fixture by creating a new Settlement object.
     */
    @BeforeEach
    void setup() {
        settlement = new Settlement(YELLOW);
    }

    /**
     * Tests the {@link Settlement#getStructureType()} method.
     */
    @Test
    void testGetStructureType() {
        assertEquals(SETTLEMENT, settlement.getStructureType());
    }

    /**
     * Tests the {@link Settlement#getScore()} method.
     */
    @Test
    void testGetScore() {
        assertEquals(SCORE, settlement.getScore());
    }

    /**
     * Tests the {@link Settlement#getIdentifier()} method.
     */
    @Test
    void testGetIdentifier() {
        assertEquals(IDENTIFIER, settlement.getIdentifier());
    }

    /**
     * Tests the {@link Settlement#getAmountPerResource()} method.
     */
    @Test
    void testGetAmountPerResource() {
        assertEquals(AMOUNT_PER_RESOURCE, settlement.getAmountPerResource());
    }

    /**
     * Tests the {@link Settlement#toString()} method.
     */
    @Test
    void testToString() {
        assertEquals("y" + IDENTIFIER, settlement.toString());
    }

}