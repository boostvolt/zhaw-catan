package ch.zhaw.catan.game;

import ch.zhaw.catan.game.Config.Resource;
import ch.zhaw.catan.structure.City;
import ch.zhaw.catan.structure.Settlement;
import ch.zhaw.catan.structure.Structure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static ch.zhaw.catan.game.Config.Faction.GREEN;
import static ch.zhaw.catan.game.Config.Faction.RED;
import static ch.zhaw.catan.game.Config.Resource.LUMBER;
import static ch.zhaw.catan.game.Config.Resource.ORE;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class performs tests for the class {@link Bank}.
 */
class BankTest {

    private Bank bank;
    private List<Structure> structures;


    /**
     * Creates a player object before each Test.
     */
    @BeforeEach
    void init() {
        bank = new Bank();
        structures = new ArrayList<>();
        structures.add(new City(GREEN));
        structures.add(new Settlement(RED));
    }

    /**
     * Tests if isInventorySufficientForPayoutOfResource returns true if the bank has all resources left.
     */
    @Test
    void isInventorySufficientForPayoutOfResourceTestWithAllBankResourcesLeft() {
        assertTrue(bank.isInventorySufficientForPayoutOfResource(ORE, structures));
    }

    /**
     * Tests if isInventorySufficientForPayoutOfResource returns true if the bank has more than enough resources left.
     */
    @Test
    void isInventorySufficientForPayoutOfResourceTestWithMoreThanEnoughResourcesLeft() {
        for (Resource resource : Resource.values()) {
            bank.decreaseInventoryItemIfApplicable(resource, 10);
        }

        assertTrue(bank.isInventorySufficientForPayoutOfResource(LUMBER, structures));
    }

    /**
     * Tests if isInventorySufficientForPayoutOfResource returns true if the bank has just enough resources left.
     */
    @Test
    void isInventorySufficientForPayoutOfResourceTestWithJustEnoughResourcesLeft() {
        for (Resource resource : Resource.values()) {
            bank.decreaseInventoryItemIfApplicable(resource, 16);
        }

        assertTrue(bank.isInventorySufficientForPayoutOfResource(LUMBER, structures));
    }

    /**
     * Tests if isInventorySufficientForPayoutOfResource returns false if the bank has one resource less left.
     */
    @Test
    void isInventorySufficientForPayoutOfResourceTestWithOneResourceLessLeft() {
        for (Resource resource : Resource.values()) {
            bank.decreaseInventoryItemIfApplicable(resource, 17);
        }

        assertFalse(bank.isInventorySufficientForPayoutOfResource(LUMBER, structures));
    }

    /**
     * Tests if isInventorySufficientForPayoutOfResource returns false if the bank has no resource left.
     */
    @Test
    void isInventorySufficientForPayoutOfResourceTestWithNoResourceLessLeft() {
        for (Resource resource : Resource.values()) {
            bank.decreaseInventoryItemIfApplicable(resource, 19);
        }

        assertFalse(bank.isInventorySufficientForPayoutOfResource(LUMBER, structures));
    }

}
