package ch.zhaw.catan.game;

import ch.zhaw.catan.game.Config.Resource;
import ch.zhaw.catan.structure.City;
import ch.zhaw.catan.structure.Road;
import ch.zhaw.catan.structure.Settlement;
import ch.zhaw.catan.structure.Structure;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.zhaw.catan.game.Config.Faction.BLUE;
import static ch.zhaw.catan.game.Config.Faction.RED;
import static ch.zhaw.catan.game.Config.Faction.YELLOW;
import static ch.zhaw.catan.game.Config.Resource.BRICK;
import static ch.zhaw.catan.game.Config.Resource.LUMBER;
import static ch.zhaw.catan.game.Config.Resource.ORE;
import static ch.zhaw.catan.game.Config.Resource.WOOL;
import static ch.zhaw.catan.game.Config.Structure.CITY;
import static java.util.Collections.singletonList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 *  This class performs tests for the class {@link Player}
 */
class PlayerTest {

    private Player player1;
    private Player player2;

    /**
     * Creates a {@link Player} object before each Test.
     */
    @BeforeEach
    public void init() {
        player1 = new Player(RED);
        player2 = new Player(YELLOW);
    }


    /**
     * Tests if the inventory is empty after initialising a player.
     */
    @Test
    void getInventoryTest() {
        final Map<Resource, Integer> inventory = player1.getInventory();

        for (Resource resource : Resource.values()) {
            assertEquals(0, inventory.get(resource));
        }
    }

    /**
     * Tests if the inventory is empty after initialising a new player. And if the method
     * getAmountOfResource works properly.
     */
    @Test
    void getAmountOfResourceTest() {
        assertEquals(0, player1.getAmountOfResource(Resource.WOOL));
    }

    /**
     * Tests if the inventory is empty after initialising a new player. And if the method
     * getTotalAmountOfResources works properly.
     */
    @Test
    void getTotalAmountOfResourcesTest() {
        assertEquals(0, player1.getTotalAmountOfResources());
    }

    /**
     * Tests if the inventory can be increased by a map of resources.
     */
    @Test
    void increaseInventoryTest() {
        final Map<Resource, Integer> toAdd = new HashMap<>();

        for (Resource resource : Resource.values()) {
            toAdd.put(resource, 2);
        }

        player1.increaseInventory(toAdd);
        assertEquals(10, player1.getTotalAmountOfResources());
    }

    /**
     * 1. Tests if a single {@link Resource} can be added to a players inventory.
     * 2. Tests if the method doesn't put a negative amount of resource into inventory.
     */
    @Test
    void increaseInventoryItemTest() {
        player1.increaseInventoryItem(Resource.WOOL, 2);
        assertEquals(2, player1.getAmountOfResource(Resource.WOOL));

        player1.increaseInventoryItem(LUMBER, -5);
        assertEquals(0, player1.getAmountOfResource(LUMBER));
    }

    /**
     * 1. Tests if the inventory can be decreased by a map of resources.
     * 2. Tests if the inventory can't be decreased if there aren't enough resources.
     */
    @Test
    void decreaseInventoryIfApplicableTest() {
        final Map<Resource, Integer> toAdd = new HashMap<>();

        for (Resource resource : Resource.values()) {
            toAdd.put(resource, 10);
        }

        player1.increaseInventory(toAdd);

        final Map<Resource, Integer> toTake1 = new HashMap<>();

        for (Resource resource : Resource.values()) {
            toTake1.put(resource, 6);
        }

        player1.decreaseInventoryIfApplicable(toTake1);
        assertEquals(4, player1.getAmountOfResource(WOOL));
        assertEquals(20, player1.getTotalAmountOfResources());

        final Map<Resource, Integer> toTake2 = new HashMap<>();

        for (Resource resource : Resource.values()) {
            toTake2.put(resource, 6);
        }

        player1.decreaseInventoryIfApplicable(toTake2);
        assertEquals(4, player1.getAmountOfResource(WOOL));
        assertEquals(20, player1.getTotalAmountOfResources());
    }

    /**
     * 1. Tests if the inventory can be decreased by a single item at the time.
     * 2. Tests if the inventory isn't decreased if the number of items to be decreased is higher
     * than the amount of {@link Resource} in the inventory.
     */
    @Test
    void decreaseInventoryItemIfApplicableTest() {
        player1.increaseInventoryItem(WOOL, 5);
        player1.decreaseInventoryItemIfApplicable(WOOL, 3);
        assertEquals(2, player1.getAmountOfResource(WOOL));

        player1.decreaseInventoryItemIfApplicable(WOOL, 5);
        assertEquals(2, player1.getAmountOfResource(WOOL));
    }

    /**
     * Tests if the method getFaction returns the correct faction.
     */
    @Test
    void getFactionTest() {
        assertEquals(RED, player1.getFaction());
        assertNotEquals(BLUE, player1.getFaction());
    }

    /**
     * Tests if the Hashset structures of the player is empty.
     */
    @Test
    void getStructuresTest() {
        assertTrue(player1.getStructures().isEmpty());
    }

    /**
     * 1. Tests if structures can be added to a {@link Player}.
     * 2. Tests if addStructure ads the correct structures to a {@link Player}.
     */
    @Test
    void addStructureTest() {
        Settlement settlement1 = new Settlement(RED);
        Settlement settlement2 = new Settlement(RED);
        City city = new City(RED);
        Road road = new Road(RED);
        player1.addStructure(settlement1);
        player1.addStructure(settlement2);
        player1.addStructure(city);
        player1.addStructure(road);
        assertEquals(4, player1.getStructures().size());

        List<Structure> addedStructures = new ArrayList<>();
        addedStructures.add(settlement1);
        addedStructures.add(settlement2);
        addedStructures.add(city);
        addedStructures.add(road);
        assertEquals(player1.getStructures(), addedStructures);
    }

    /**
     * Tests if a Structure can be removed from a player.
     */
    @Test
    void removeStructureTest() {
        final Settlement settlement = new Settlement(RED);

        player1.addStructure(new Settlement(RED));
        player1.addStructure(settlement);
        assertEquals(2, player1.getStructures().size());

        player1.removeStructure(settlement);
        assertEquals(1, player1.getStructures().size());
    }

    /**
     * Tests if the method hasNotReachedMaxedStock returns the correct boolean.
     * 1. Tests if the initial return value is false
     * 2. Tests that after adding structures the value is still false.
     * 3. Tests that after meeting the required MaxStock the function returns true.
     */
    @Test
    void hasNotReachedMaxedStock() {
        assertTrue(player1.hasNotReachedMaxStockOf(CITY));

        player1.addStructure(new City(RED));
        player1.addStructure(new City(RED));
        player1.addStructure(new City(RED));
        assertTrue(player1.hasNotReachedMaxStockOf(CITY));

        player1.addStructure(new City(RED));
        assertFalse(player1.hasNotReachedMaxStockOf(CITY));
    }

    /**
     * Tests if the method processThief takes away half of the resources. But only when the player
     * has more than 7 resources in total.
     * 1. Tests if processThief doesn't take away resources if the player has less than 7 resources.
     * 2. Tests if processThief does take away half the resources of the player.
     * 3. Tests if processThief does take away one less than half the resources of the player,
     * if the amount of resources was odd.
     * 4. Tests if processThief does take away half the resources of the player if the player has more than 7 resources.
     */
    @Test
    void processThiefTest() {
        player1.increaseInventoryItem(WOOL, 3);
        player1.processThief();
        assertEquals(3, player1.getTotalAmountOfResources());

        player1.increaseInventoryItem(LUMBER, 8);
        player1.increaseInventoryItem(ORE, 11);
        player1.processThief();
        assertEquals(11, player1.getTotalAmountOfResources());

        player1.processThief();
        assertEquals(6, player1.getTotalAmountOfResources());

        player1.increaseInventoryItem(BRICK, 2);
        player1.processThief();
        assertEquals(4, player1.getTotalAmountOfResources());
    }

    /**
     * Tests if the stealRandomResource steals a random resource.
     */
    @Test
    void stealRandomResourceTest() {
        final Map<Resource, Integer> toAdd = new HashMap<>();

        for (Resource resource : Resource.values()) {
            toAdd.put(resource, 10);
        }

        player1.increaseInventory(toAdd);
        player2.increaseInventory(toAdd);
        player1.stealRandomResourceFrom(singletonList(player2));
        assertEquals(51, player1.getTotalAmountOfResources());
        assertEquals(49, player2.getTotalAmountOfResources());
    }

    /**
     * Tests if the getScore method works properly.
     * 1. Tests if the score is 0 if the player has no structures.
     * 2. Tests if the score is correct if the player has a certain amount of structures.
     */
    @Test
    void getScoreTest() {
        assertEquals(0, player1.getScore());

        player1.addStructure(new Settlement(RED));
        player1.addStructure(new Settlement(RED));
        player1.addStructure(new City(RED));
        player1.addStructure(new Road(RED));
        assertEquals(4, player1.getScore());
    }
}
