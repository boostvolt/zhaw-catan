package ch.zhaw.catan.game;

import ch.zhaw.catan.ThreePlayerStandard;
import ch.zhaw.catan.board.SiedlerBoard;
import ch.zhaw.catan.structure.City;
import ch.zhaw.catan.structure.Road;
import ch.zhaw.catan.structure.Settlement;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.awt.Point;
import java.util.Collections;
import java.util.List;

import static ch.zhaw.catan.game.App.REQUIRED_WINNING_SCORE;
import static ch.zhaw.catan.game.Config.INITIAL_RESOURCE_CARDS_BANK;
import static ch.zhaw.catan.game.SiedlerGame.THIEF_DICE_NUMBER;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;


/***
 * The SiedlerGameTest class is a JUnit test class for the {@link SiedlerGame} class.
 * It contains several test methods to test various aspects of the SiedlerGame class.
 */
class SiedlerGameTest {
    private final static List<Point> validSettlementPoints = List.of(new Point(5, 9), new Point(6, 12), new Point(6, 6), new Point(4, 12));
    private final static List<Point> invalidSettlementPoints = List.of(new Point(0, 0), new Point(3, 3), new Point(14, 12), new Point(5, 5));
    private final static Point firstField = new Point(6, 8);
    private final static Point emptyField = new Point(11, 11);
    private final static Point waterField = new Point(4, 2);
    private final static Point validRoadStartPoint = new Point(6, 10);
    private final static List<Point> validRoadEndPoints = List.of(new Point(5, 9), new Point(6, 12), new Point(7, 9));
    private final static List<Point> invalidRoadEndPoints = List.of(new Point(9, 15), new Point(9, 9), new Point(0, 0));

    private final static List<Point> waterEdge = List.of(new Point(3, 1), new Point(3, 3));

    private final static int DEFAULT_NUMBER_OF_PLAYERS = 2;
    private SiedlerGame siedlerGame;

    private static int getInitialBankInventory() {
        int initialBankInventory = 0;
        for (Config.Resource resource : Config.Resource.values()) {
            initialBankInventory += INITIAL_RESOURCE_CARDS_BANK.get(resource);
        }
        return initialBankInventory;
    }

    @BeforeEach
    void init() {
        siedlerGame = new SiedlerGame(REQUIRED_WINNING_SCORE, DEFAULT_NUMBER_OF_PLAYERS);
    }

    /**
     * This is a test method for the switchToNextPlayer() and switchToPreviousPlayer() methods of the SiedlerGame class.
     * It checks if the methods correctly switch between the current Players of the game.
     * It also checks that calling the switchToNextPlayer() and switchToPreviousPlayer() method repeatedly
     * eventually returns to the first player in the list.
     * <p>
     * Additionally, the test creates a SiedlerGame instance with four players
     * and tests that the methods correctly switch between all four players.
     */
    @Test
    void switchMultiplePlayer() {
        assertEquals(siedlerGame.getPlayerFactions().get(0), siedlerGame.getCurrentPlayerFaction());
        siedlerGame.switchToNextPlayer();
        assertEquals(siedlerGame.getPlayerFactions().get(1), siedlerGame.getCurrentPlayerFaction());
        siedlerGame.switchToNextPlayer();
        assertEquals(siedlerGame.getPlayerFactions().get(0), siedlerGame.getCurrentPlayerFaction());
        siedlerGame.switchToPreviousPlayer();
        siedlerGame.switchToPreviousPlayer();
        assertEquals(siedlerGame.getPlayerFactions().get(0), siedlerGame.getCurrentPlayerFaction());

        SiedlerGame siedlerGameFourPlayers = new SiedlerGame(REQUIRED_WINNING_SCORE, 4);

        assertEquals(siedlerGameFourPlayers.getPlayerFactions().get(0), siedlerGameFourPlayers.getCurrentPlayerFaction());
        siedlerGameFourPlayers.switchToNextPlayer();
        siedlerGameFourPlayers.switchToNextPlayer();
        siedlerGameFourPlayers.switchToNextPlayer();
        siedlerGameFourPlayers.switchToNextPlayer();
        assertEquals(siedlerGameFourPlayers.getPlayerFactions().get(0), siedlerGameFourPlayers.getCurrentPlayerFaction());
        siedlerGameFourPlayers.switchToPreviousPlayer();
        assertEquals(siedlerGameFourPlayers.getPlayerFactions().get(3), siedlerGameFourPlayers.getCurrentPlayerFaction());
    }

    /**
     * This is a test method for the getPlayerFactions() method of the SiedlerGame class.
     * It first calls the getPlayerFactions() method to retrieve the factions of the players in the game,
     * then checks that the returned list is in the correct order and contains the expected factions.
     * It also checks that the function returns an empty list when there are no players in the game.
     */
    @Test
    void getPlayerFactions() {
        List<Config.Faction> playerFactions = siedlerGame.getPlayerFactions();

        Player player1 = siedlerGame.getCurrentPlayer();
        siedlerGame.switchToNextPlayer();
        Player player2 = siedlerGame.getCurrentPlayer();

        assertEquals(2, playerFactions.size());
        assertEquals(player1.getFaction(), playerFactions.get(0));
        assertEquals(player2.getFaction(), playerFactions.get(1));
    }

    /**
     * This method tests that the getPlayerFactions() method of the SiedlerGame class
     * throws an {@link IndexOutOfBoundsException}, when an attempt is made to retrieve a non-existent Player in the list
     * returned by getPlayerFactions().
     */
    @Test
    void getPlayerFactionsOutOfBounds() {
        List<Config.Faction> playerFactions = siedlerGame.getPlayerFactions();

        assertThrows(IndexOutOfBoundsException.class, () -> playerFactions.get(2));
    }

    /**
     * This method tests that the getPlayerFactions() method of the SiedlerGame class
     * returns an empty list when the SiedlerGame object is constructed with zero players.
     */
    @Test
    void getPlayerFactionsEmptyList() {
        assertEquals(Collections.EMPTY_LIST, new SiedlerGame(REQUIRED_WINNING_SCORE, 0).getPlayerFactions());
    }

    /**
     * This test first creates a new game and compares the fields and corners of the game boards,
     * then checks that the edges of the game boards are not equal.
     * It also checks that the getEdge function correctly retrieves the edge between two points on the game board.
     */
    @Test
    void getBoard() {
        SiedlerGame game = ThreePlayerStandard.getAfterSetupPhase(REQUIRED_WINNING_SCORE);

        assertEquals(game.getBoard().getFields(), siedlerGame.getBoard().getFields());
        assertNotEquals(game.getBoard().getCorners(), siedlerGame.getBoard().getCorners());
        assertNotEquals(game.getBoard().getEdge(new Point(5, 7), new Point(6, 6)), siedlerGame.getBoard().getEdge(new Point(5, 7), new Point(6, 6)));
    }

    /**
     * This method tests that the getEdge() method of the Board class throws an {@link IllegalArgumentException}
     * when it is called with the same coordinate twice or otherwise invalid edge coordinates.
     */
    @Test
    void getBoardThrows() {
        SiedlerBoard board = siedlerGame.getBoard();
        Point firstValidSettlementPoint = validSettlementPoints.get(0);
        Point firstInvalidRoadEndPoints = invalidRoadEndPoints.get(0);


        assertThrows(IllegalArgumentException.class, () -> board.getEdge(firstValidSettlementPoint, firstValidSettlementPoint), "Coordinates " + validSettlementPoints.get(0) + " and " + validSettlementPoints.get(0) + " are not coordinates of an edge.");
        assertThrows(IllegalArgumentException.class, () -> board.getEdge(validRoadStartPoint, firstInvalidRoadEndPoints), "Coordinates " + validRoadStartPoint + " and " + invalidRoadEndPoints.get(0) + " are not coordinates of an edge.");
    }

    /**
     * This test first checks that the function returns 0 when the current player has no resources of the specified type,
     * then adds resources to the player's inventory and checks that the function returns the correct number of resources.
     */
    @Test
    void getCurrentPlayerResourceStock() {
        assertEquals(0, siedlerGame.getCurrentPlayerResourceStock(Config.Resource.WOOL));

        siedlerGame.getCurrentPlayer().increaseInventoryItem(Config.Resource.WOOL, 5);
        assertEquals(5, siedlerGame.getCurrentPlayerResourceStock(Config.Resource.WOOL));
    }

    /**
     * Testing for placement of initial settlements and payout.
     * <p>
     * 1. Place invalid settlements
     * 2. Place settlements on valid positions
     * 3. Try to place settlements again
     * 4. Check player Inventory after no payout
     * 5. Compare player inventory with Player that got a payout
     * 6. Check inventory of Player that got payout
     */
    @Test
    void placeInitialSettlement() {
        // Trying to place settlements on invalid positions
        for (Point invalidSettlementPoint : invalidSettlementPoints) {
            assertFalse(siedlerGame.placeInitialSettlement(invalidSettlementPoint, false));
        }

        // place settlements on valid positions
        for (Point validSettlementPoint : validSettlementPoints) {
            assertTrue(siedlerGame.placeInitialSettlement(validSettlementPoint, false));
        }

        // Trying to place settlements on same positions again.
        for (Point validSettlementPoint : validSettlementPoints) {
            assertFalse(siedlerGame.placeInitialSettlement(validSettlementPoint, false));
        }

        // Trying to place settlements next to previously placed settlement
        assertFalse(siedlerGame.placeInitialSettlement(validRoadEndPoints.get(0), false));
        assertFalse(siedlerGame.placeInitialSettlement(validRoadEndPoints.get(1), false));

        assertFalse(siedlerGame.placeInitialSettlement(validRoadStartPoint, false));

        Player playerBeforePlacement = siedlerGame.getCurrentPlayer();

        assertEquals(0, playerBeforePlacement.getTotalAmountOfResources());

        SiedlerGame siedlerGame2 = new SiedlerGame(REQUIRED_WINNING_SCORE, 2);

        // place settlements on valid positions and payout resources
        for (Point validSettlementPoint : validSettlementPoints) {
            assertTrue(siedlerGame2.placeInitialSettlement(validSettlementPoint, true));
        }

        assertNotEquals(playerBeforePlacement.getTotalAmountOfResources(), siedlerGame2.getCurrentPlayer().getTotalAmountOfResources());
        assertEquals(11, siedlerGame2.getCurrentPlayer().getTotalAmountOfResources());
    }

    /**
     * This test first attempts to place a road without any settlements,
     * then creates settlements and attempts to place roads at valid locations.
     * It also attempts to place roads at invalid locations and from a different player's perspective.
     */
    @Test
    void placeInitialRoad() {
        // No settlements exist
        assertFalse(siedlerGame.placeInitialRoad(validRoadStartPoint, validRoadEndPoints.get(0)));
        assertFalse(siedlerGame.placeInitialRoad(waterEdge.get(0), waterEdge.get(1)));

        // Create Settlements
        for (Point settlementPoint : validSettlementPoints) {
            siedlerGame.placeInitialSettlement(settlementPoint, false);
        }

        // Place roads at valid Points
        assertTrue(siedlerGame.placeInitialRoad(validRoadStartPoint, validRoadEndPoints.get(0)));
        assertTrue(siedlerGame.placeInitialRoad(validRoadStartPoint, validRoadEndPoints.get(1)));

        //Place invalid roads
        assertFalse(siedlerGame.placeInitialRoad(validRoadStartPoint, invalidRoadEndPoints.get(0)));
        assertFalse(siedlerGame.placeInitialRoad(invalidSettlementPoints.get(0), validRoadEndPoints.get(0)));

        //Place road of different Player
        siedlerGame.switchToNextPlayer();
        assertFalse(siedlerGame.placeInitialRoad(validRoadStartPoint, validRoadEndPoints.get(2)));
    }

    /**
     * This method tests the buildSettlement() method of the SiedlerGame class. The test first adds some resources to
     * the current player's inventory, and then attempts to build a settlement at an invalid location.
     * It then places an initial settlement and connects it to two roads, and then attempts to build another settlement
     * at an invalid location and a valid location. It uses the assertFalse() and assertTrue() methods to verify that
     * the buildSettlement() method returns false when it is called with an invalid location,and true when it is called
     * with a valid location.
     */
    @Test
    void buildSettlement() {
        // Adding non-critical Resources
        siedlerGame.getCurrentPlayer().increaseInventory(Config.Structure.SETTLEMENT.getCostsAsIntegerMap());
        siedlerGame.getCurrentPlayer().increaseInventory(Config.Structure.SETTLEMENT.getCostsAsIntegerMap());

        // No Settlement / Roads exist yet to build a settlement
        assertFalse(siedlerGame.buildSettlement(invalidSettlementPoints.get(0)));
        assertFalse(siedlerGame.buildSettlement(validSettlementPoints.get(0)));

        // Place Settlement with connecting Roads to new settlement position
        siedlerGame.placeInitialSettlement(validSettlementPoints.get(0), false);
        siedlerGame.placeInitialRoad(validRoadStartPoint, validRoadEndPoints.get(0));
        siedlerGame.placeInitialRoad(validRoadStartPoint, validRoadEndPoints.get(1));

        // No resources are available to build Settlement
        assertFalse(siedlerGame.buildSettlement(invalidSettlementPoints.get(0)));
        assertTrue(siedlerGame.buildSettlement(validSettlementPoints.get(1)));
    }

    /**
     * This method tests the buildSettlement() method of the SiedlerGame class in the case where the player does not
     * have enough resources to build a settlement. The test first adds some resources to the current player's inventory,
     * but not enough to build a settlement. It then attempts to build a settlement at an invalid location and a valid
     * location, and uses the assertFalse() method to verify that the buildSettlement() method returns false in both
     * cases. This test ensures that the buildSettlement() method correctly checks the player's inventory to make sure
     * that they have enough resources to build a settlement, and returns the correct value if they do not.
     */
    @Test
    void buildSettlementWithInsufficientResources() {
        // Adding non-critical Resources
        siedlerGame.getCurrentPlayer().increaseInventory(Config.Structure.CITY.getCostsAsIntegerMap());
        siedlerGame.getCurrentPlayer().increaseInventory(Config.Structure.CITY.getCostsAsIntegerMap());

        // No Settlement / Roads exist yet to build a settlement
        assertFalse(siedlerGame.buildSettlement(invalidSettlementPoints.get(0)));
        assertFalse(siedlerGame.buildSettlement(validSettlementPoints.get(0)));

        // Place Settlement with connecting Roads to new settlement position
        siedlerGame.placeInitialSettlement(validSettlementPoints.get(0), false);
        siedlerGame.placeInitialRoad(validRoadStartPoint, validRoadEndPoints.get(0));
        siedlerGame.placeInitialRoad(validRoadStartPoint, validRoadEndPoints.get(1));

        // No resources are available to build Settlement
        assertFalse(siedlerGame.buildSettlement(invalidSettlementPoints.get(0)));
        assertFalse(siedlerGame.buildSettlement(validSettlementPoints.get(1)));
    }

    /**
     * This test first attempts to build a city without sufficient resources or at an invalid location,
     * then adds resources to the player's inventory and attempts to build a city again at both valid and invalid locations.
     * Finally, it builds a city at a valid location and checks that the function returns true to indicate success.
     */
    @Test
    void buildCity() {
        siedlerGame.placeInitialSettlement(validSettlementPoints.get(0), false);

        // Try to build City without resources
        assertFalse(siedlerGame.buildCity(validSettlementPoints.get(0)));

        // Try to build City without resources at wrong position
        assertFalse(siedlerGame.buildCity(validSettlementPoints.get(1)));
        assertFalse(siedlerGame.buildCity(invalidSettlementPoints.get(0)));

        siedlerGame.getCurrentPlayer().increaseInventory(Config.Structure.CITY.getCostsAsIntegerMap());

        // Try to build City with resources at wrong position
        assertFalse(siedlerGame.buildCity(validSettlementPoints.get(1)));
        assertFalse(siedlerGame.buildCity(invalidSettlementPoints.get(0)));
        // Build city at correct position
        assertTrue(siedlerGame.buildCity(validSettlementPoints.get(0)));
    }

    /**
     * This test first checks that a road cannot be built without sufficient resources or settlements,
     * then adds resources to the player's inventory
     * and attempts to build roads from a valid starting point to both valid and invalid endpoints.
     */
    @Test
    void buildRoad() {
        Player player = siedlerGame.getCurrentPlayer();
        // Player doesn't have enough resources && no settlements exist
        assertFalse(siedlerGame.buildRoad(validRoadStartPoint, validRoadEndPoints.get(0)));

        buildInitialSettlements();

        // Player doesn't have enough resources but settlements exist
        assertFalse(siedlerGame.buildRoad(validRoadStartPoint, validRoadEndPoints.get(0)));
        player.increaseInventoryItem(Config.Resource.LUMBER, 2);
        player.increaseInventoryItem(Config.Resource.BRICK, 2);
        // Player has enough Resources for Road placement
        assertFalse(siedlerGame.buildRoad(validRoadStartPoint, invalidRoadEndPoints.get(0)));
        assertTrue(siedlerGame.buildRoad(validRoadStartPoint, validRoadEndPoints.get(0)));
        assertFalse(siedlerGame.buildRoad(validRoadStartPoint, invalidRoadEndPoints.get(1)));
        assertTrue(siedlerGame.buildRoad(validRoadStartPoint, validRoadEndPoints.get(1)));
    }

    /**
     * This test first asserts that trading with no resources will fail,
     * then adds resources to the player's inventory and trades them for other resources from the bank
     * until the bank runs out of the desired resource.
     * Finally, it checks that the bank's and player's inventories have been updated as expected.
     */
    @Test
    void tradeWithBankFourToOne() {
        // Try trading with no resources
        assertFalse(siedlerGame.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.LUMBER));

        siedlerGame.getCurrentPlayer().increaseInventoryItem(Config.Resource.WOOL, INITIAL_RESOURCE_CARDS_BANK.get(Config.Resource.WOOL) * 4);

        // Trade for all Bank resources
        while (siedlerGame.getBank().getAmountOfResource(Config.Resource.LUMBER) > 0) {
            assertTrue(siedlerGame.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.LUMBER));
        }

        // Bank has no Lumber but all Wool traded. And player has all Lumber but no Wool.
        assertFalse(siedlerGame.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.LUMBER));
        assertEquals(0, siedlerGame.getBank().getAmountOfResource(Config.Resource.LUMBER));
        assertEquals(INITIAL_RESOURCE_CARDS_BANK.get(Config.Resource.WOOL) * 5, siedlerGame.getBank().getAmountOfResource(Config.Resource.WOOL));
        assertEquals(0, siedlerGame.getCurrentPlayer().getAmountOfResource(Config.Resource.WOOL));
        assertEquals(INITIAL_RESOURCE_CARDS_BANK.get(Config.Resource.LUMBER), siedlerGame.getCurrentPlayer().getAmountOfResource(Config.Resource.LUMBER));
    }

    /**
     * This is a JUnit test for the getWinner method.
     * It first asserts that getWinner returns null before any structures have been built,
     * indicating that no winner has been determined yet.
     * </p>
     * Next, it builds three cities and three roads for the current player, which should not be enough to win the game.
     * It then asserts that getWinner still returns null.
     * </p>
     * Finally, it builds one more settlement for the current player, which should trigger the player to win the game.
     * It then asserts that getWinner returns the Faction of the current player, indicating that they have won the game.
     */
    @Test
    void getWinner() {
        assertNull(siedlerGame.getWinner());

        for (int i = 0; i < 3; i++) {
            siedlerGame.getCurrentPlayer().addStructure(new City(siedlerGame.getCurrentPlayerFaction()));
            siedlerGame.getCurrentPlayer().addStructure(new Road(siedlerGame.getCurrentPlayerFaction()));
        }

        assertNull(siedlerGame.getWinner());

        siedlerGame.getCurrentPlayer().addStructure(new Settlement(siedlerGame.getCurrentPlayerFaction()));

        assertEquals(siedlerGame.getCurrentPlayerFaction(), siedlerGame.getWinner());


    }

    /**
     * This test places the initial settlements for both players on the map.
     * It then calculates the initial inventory of the bank by iterating over the resources and summing the initial resource cards in the bank.
     * It then asserts that the sum of the resources in the bank and the resources of the two players is equal to the initial bank inventory.
     * <p>
     * Next, it rolls the dice four times, which should distribute resources to the players based on the numbers rolled and the placement of their settlements on the map.
     * It then checks that the players received the expected number of resources.
     * <p>
     * Finally, it asserts that the sum of the resources in the bank and the resources of the two players is still equal to the initial bank inventory.
     */
    @Test
    void processPayoutResource() {
        buildInitialSettlements();

        int initialBankInventory = getInitialBankInventory();

        assertEquals(initialBankInventory, siedlerGame.getBank().getTotalAmountOfResources() +
                siedlerGame.getCurrentPlayers().get(0).getTotalAmountOfResources() +
                siedlerGame.getCurrentPlayers().get(1).getTotalAmountOfResources());

        // Check starting values for Settlement placement
        // Player 1 = 3 Resources
        // Player 2 = 3 Resources

        siedlerGame.processDiceRoll(4);
        siedlerGame.processDiceRoll(9);
        siedlerGame.processDiceRoll(11);
        siedlerGame.processDiceRoll(11);

        // Check if resources were paid out according to map Placement
        assertEquals(8, siedlerGame.getCurrentPlayers().get(0).getTotalAmountOfResources());
        assertEquals(5, siedlerGame.getCurrentPlayers().get(1).getTotalAmountOfResources());

        assertEquals(initialBankInventory, siedlerGame.getBank().getTotalAmountOfResources() +
                siedlerGame.getCurrentPlayers().get(0).getTotalAmountOfResources() +
                siedlerGame.getCurrentPlayers().get(1).getTotalAmountOfResources());
    }

    /**
     * This method tests that the processPayoutResources() method of the SiedlerGame class correctly handles the case
     * where there are not enough resources in the bank to pay out the resources to the players.
     * The test first creates a SiedlerGame object with three players, and then rolls the dice until the bank has only one wool resource left.
     * It then rolls the dice again, which should trigger a payout of wool resources to the players.
     * Finally, it uses the assertEquals() method to verify that the total number of resources in the bank and the players remains the same as it was before the last dice roll.
     */
    @Test
    void processPayoutResourcesNotEnoughResources() {
        SiedlerGame threePlayerGame = ThreePlayerStandard.getAfterSetupPhaseSomeRoads(REQUIRED_WINNING_SCORE);
        int initialBankInventory = getInitialBankInventory();

        // Reduce Bank Resource Wool until only one is left.
        for (int i = 0; i < 7; i++) {
            threePlayerGame.processDiceRoll(12);
        }

        Integer bankInventory = threePlayerGame.getBank().getTotalAmountOfResources();

        // Roll Wool one more time
        threePlayerGame.processDiceRoll(12);

        assertEquals(bankInventory, threePlayerGame.getBank().getTotalAmountOfResources());
        assertEquals(initialBankInventory, threePlayerGame.getBank().getTotalAmountOfResources() +
                threePlayerGame.getCurrentPlayers().get(0).getTotalAmountOfResources() +
                threePlayerGame.getCurrentPlayers().get(1).getTotalAmountOfResources() +
                threePlayerGame.getCurrentPlayers().get(2).getTotalAmountOfResources());
    }

    /**
     * This is a test method for the processDiceRoll() method when the thief is rolled of the SiedlerGame class.
     * It first creates two players and builds their initial settlements using the buildInitialSettlements() method.
     * Then it rolls the dice several times to simulate the game being played and resources being paid out,
     * and finally it rolls the number for the executeThief() method
     * which checks that the thief is executed and that the players' inventories are correctly modified as a result.
     * It also checks that the total amount of resources in the game remains the same after the thief is executed.
     */
    @Test
    void processThiefRolled() {
        buildInitialSettlements();

        siedlerGame.processDiceRoll(4);
        siedlerGame.processDiceRoll(9);
        siedlerGame.processDiceRoll(11);
        siedlerGame.processDiceRoll(11);

        int initialBankInventory = getInitialBankInventory();

        // Check if thief is executed and cuts players inventory with more than 7
        // Resources in half but doesn't touch the other players resource
        assertTrue(siedlerGame.processDiceRoll(THIEF_DICE_NUMBER).isThiefExecuted());
        assertEquals(4, siedlerGame.getCurrentPlayers().get(0).getTotalAmountOfResources());
        assertEquals(5, siedlerGame.getCurrentPlayers().get(1).getTotalAmountOfResources());

        assertEquals(initialBankInventory, siedlerGame.getBank().getTotalAmountOfResources() +
                siedlerGame.getCurrentPlayers().get(0).getTotalAmountOfResources() +
                siedlerGame.getCurrentPlayers().get(1).getTotalAmountOfResources());
    }

    /**
     * Testing the placeThiefAndStealCard Method
     * <p>
     * The test creates the initial settlements on the board and checks whether the first field is occupied by a thief.
     * It then attempts to place the thief on the water field and on the empty field,
     * and checks whether these attempts were successful.
     * Finally, the test places the thief on the first field, checks whether it is occupied by the thief,
     * and checks the total amount of resources for the current player and the next player.
     */
    @Test
    void placeThiefAndStealCard() {
        buildInitialSettlements();

        assertFalse(siedlerGame.getBoard().getField(firstField).isOccupiedByThief());

        assertFalse(siedlerGame.placeThiefAndStealCard(waterField));
        assertTrue(siedlerGame.placeThiefAndStealCard(emptyField));
        siedlerGame.placeThiefAndStealCard(firstField);

        assertTrue(siedlerGame.getBoard().getField(firstField).isOccupiedByThief());
        assertEquals(4, siedlerGame.getCurrentPlayer().getTotalAmountOfResources());
        assertEquals(2, siedlerGame.getCurrentPlayers().get(1).getTotalAmountOfResources());
    }

    /**
     * This test first checks that the number of players returned by the function matches the number of players in the game,
     * then checks that the factions of the players in the game and the manually generated list of players match.
     */
    @Test
    void getCurrentPlayers() {
        assertEquals(siedlerGame.getPlayerFactions().size(), siedlerGame.getCurrentPlayers().size());
        assertEquals(siedlerGame.getPlayerFactions().get(0), siedlerGame.getCurrentPlayers().get(0).getFaction());
    }

    /**
     * This test first checks that the current player's faction matches the faction of the first player in the list of players in the game,
     * then checks that the player's faction matches the returned value of the getCurrentPlayerFaction function.
     */
    @Test
    void getCurrentPlayer() {
        assertEquals(siedlerGame.getPlayerFactions().get(0), siedlerGame.getCurrentPlayer().getFaction());
        assertEquals(siedlerGame.getCurrentPlayer().getFaction(), siedlerGame.getCurrentPlayerFaction());
    }

    /**
     * This test checks if the inventory of the bank returned by the getBank() method
     * matches the expected INITIAL_RESOURCE_CARDS_BANK value.
     */
    @Test
    void getBank() {
        assertEquals(INITIAL_RESOURCE_CARDS_BANK, siedlerGame.getBank().getInventory());
    }

    private void buildInitialSettlements() {
        siedlerGame.placeInitialSettlement(validSettlementPoints.get(0), true);
        siedlerGame.placeInitialSettlement(validSettlementPoints.get(1), false);
        siedlerGame.switchToNextPlayer();

        siedlerGame.placeInitialSettlement(validSettlementPoints.get(2), true);
        siedlerGame.placeInitialSettlement(validSettlementPoints.get(3), false);
        siedlerGame.switchToNextPlayer();
    }

}
