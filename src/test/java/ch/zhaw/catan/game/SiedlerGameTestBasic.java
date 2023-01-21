package ch.zhaw.catan.game;

import ch.zhaw.catan.ThreePlayerStandard;
import ch.zhaw.catan.board.SiedlerBoardTextView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.awt.Point;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static ch.zhaw.catan.game.App.REQUIRED_WINNING_SCORE;

/**
 * This class contains some basic tests for the {@link SiedlerGame} class
 * <p>
 * </p>
 * <p>
 * DO NOT MODIFY THIS CLASS
 * </p>
 *
 * @author tebe
 */
class SiedlerGameTestBasic {
    private final static int DEFAULT_NUMBER_OF_PLAYERS = 3;

    /**
     * Tests whether the functionality for switching to the next/previous player
     * works as expected for different numbers of players.
     *
     * @param numberOfPlayers the number of players
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4})
    void requirementPlayerSwitching(int numberOfPlayers) {
        SiedlerGame model = new SiedlerGame(REQUIRED_WINNING_SCORE, numberOfPlayers);
        Assertions.assertEquals(numberOfPlayers, model.getPlayerFactions().size(),
                "Wrong number of players returned by getPlayers()");
        // Switching forward
        for (int i = 0; i < numberOfPlayers; i++) {
            Assertions.assertEquals(Config.Faction.values()[i], model.getCurrentPlayerFaction(),
                    "Player order does not match order of Faction.values()");
            model.switchToNextPlayer();
        }
        Assertions.assertEquals(Config.Faction.values()[0], model.getCurrentPlayerFaction(),
                "Player wrap-around from last player to first player did not work.");
        // Switching backward
        for (int i = numberOfPlayers - 1; i >= 0; i--) {
            model.switchToPreviousPlayer();
            Assertions.assertEquals(Config.Faction.values()[i], model.getCurrentPlayerFaction(),
                    "Switching players in reverse order does not work as expected.");
        }
    }

    /**
     * Tests whether the game board meets the required layout/land placement.
     */
    @Test
    void requirementLandPlacementTest() {
        SiedlerGame model = new SiedlerGame(REQUIRED_WINNING_SCORE, DEFAULT_NUMBER_OF_PLAYERS);
        Assertions.assertTrue(Config.getStandardLandPlacement().size() == model.getBoard().getFields().size(),
                "Check if explicit init must be done (violates spec): "
                        + "modify initializeSiedlerGame accordingly.");
        for (Map.Entry<Point, Config.Land> e : Config.getStandardLandPlacement().entrySet()) {
            Assertions.assertEquals(e.getValue(), model.getBoard().getField(e.getKey()).getLand(),
                    "Land placement does not match default placement.");
        }
    }

    /**
     * Tests whether the {@link ThreePlayerStandard#getAfterSetupPhase(int)}} game
     * board is not empty (returns
     * an object) at positions where settlements and roads have been placed.
     */
    @Test
    void requirementSettlementAndRoadPositionsOccupiedThreePlayerStandard() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(REQUIRED_WINNING_SCORE);
        Assertions.assertEquals(DEFAULT_NUMBER_OF_PLAYERS, model.getPlayerFactions().size());
        for (Config.Faction f : model.getPlayerFactions()) {
            Assertions.assertTrue(
                    model.getBoard().getCorner(ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(f).first) != null);
            Assertions.assertTrue(
                    model.getBoard().getCorner(ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(f).second) != null);
            Assertions
                    .assertTrue(model.getBoard().getEdge(ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(f).first,
                            ThreePlayerStandard.INITIAL_ROAD_ENDPOINTS.get(f).first) != null);
            Assertions
                    .assertTrue(model.getBoard().getEdge(ThreePlayerStandard.INITIAL_SETTLEMENT_POSITIONS.get(f).second,
                            ThreePlayerStandard.INITIAL_ROAD_ENDPOINTS.get(f).second) != null);
        }
    }

    /**
     * Checks that the resource card payout for different dice values matches
     * the expected payout for the game state
     * {@link ThreePlayerStandard#getAfterSetupPhase(int)}}.
     * <p>
     * Note, that for the test to work, the {@link Map} returned by
     * {@link SiedlerGame#processDiceRoll(int)}
     * must contain a {@link List} with resource cards (empty {@link List}, if the
     * player gets none)
     * for each of the players.
     * </p>
     *
     * @param diceValue the dice value
     */
    @ParameterizedTest
    @ValueSource(ints = {2, 3, 4, 5, 6, 8, 9, 10, 11, 12})
    void requirementDiceThrowResourcePayoutThreePlayerStandardTest(int diceValue) {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(REQUIRED_WINNING_SCORE);
        Map<Config.Faction, Map<Config.Resource, Integer>> expected = ThreePlayerStandard.INITIAL_DICE_THROW_PAYOUT.get(diceValue);
        Map<Config.Faction, Map<Config.Resource, Integer>> actual = model.processDiceRoll(diceValue).getAffectedResources();
        Assertions.assertEquals(expected, actual);
    }

    /**
     * Tests whether the resource card stock of the players matches the expected
     * stock
     * for the game state {@link ThreePlayerStandard#getAfterSetupPhase(int)}}.
     */
    @Test
    void requirementPlayerResourceCardStockAfterSetupPhase() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(REQUIRED_WINNING_SCORE);
        assertPlayerResourceCardStockEquals(model, ThreePlayerStandard.INITIAL_PLAYER_CARD_STOCK);
    }

    /**
     * Tests whether the resource card stock of the players matches the expected
     * stock
     * for the game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}}.
     */
    @Test
    void requirementPlayerResourceCardStockAfterSetupPhaseAlmostEmptyBank() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(REQUIRED_WINNING_SCORE);
        assertPlayerResourceCardStockEquals(model, ThreePlayerStandard.BANK_ALMOST_EMPTY_RESOURCE_CARD_STOCK);
    }

    /**
     * Tests whether the resource card stock of the players matches the expected
     * stock
     * for the game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}}.
     */
    @Test
    void requirementPlayerResourceCardStockPlayerOneReadyToBuildFifthSettlement() {
        SiedlerGame model = ThreePlayerStandard.getPlayerOneReadyToBuildFifthSettlement(REQUIRED_WINNING_SCORE);
        assertPlayerResourceCardStockEquals(model,
                ThreePlayerStandard.PLAYER_ONE_READY_TO_BUILD_FIFTH_SETTLEMENT_RESOURCE_CARD_STOCK);
    }

    /**
     * Throws each dice value except 7 once and tests whether the resource
     * card stock of the players matches the expected stock.
     */
    @Test
    void requirementDiceThrowPlayerResourceCardStockUpdateTest() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(REQUIRED_WINNING_SCORE);
        for (int i : List.of(2, 3, 4, 5, 6, 8, 9, 10, 11, 12)) {
            model.processDiceRoll(i);
        }
        Map<Config.Faction, Map<Config.Resource, Integer>> expected = Map.of(
                Config.Faction.values()[0], Map.of(Config.Resource.GRAIN, 1, Config.Resource.WOOL, 2,
                        Config.Resource.BRICK, 2, Config.Resource.ORE, 1, Config.Resource.LUMBER, 1),
                Config.Faction.values()[1],
                Map.of(Config.Resource.GRAIN, 1, Config.Resource.WOOL, 5, Config.Resource.BRICK, 0,
                        Config.Resource.ORE, 0, Config.Resource.LUMBER, 0),
                Config.Faction.values()[2],
                Map.of(Config.Resource.GRAIN, 0, Config.Resource.WOOL, 0, Config.Resource.BRICK, 2,
                        Config.Resource.ORE, 0, Config.Resource.LUMBER, 1));

        assertPlayerResourceCardStockEquals(model, expected);
    }

    private void assertPlayerResourceCardStockEquals(SiedlerGame model,
                                                     Map<Config.Faction, Map<Config.Resource, Integer>> expected) {
        for (int i = 0; i < expected.keySet().size(); i++) {
            Config.Faction f = model.getCurrentPlayerFaction();
            for (Config.Resource r : Config.Resource.values()) {
                Assertions.assertEquals(expected.get(f).get(r), model.getCurrentPlayerResourceStock(r),
                        "Resource card stock of player " + i + " [faction " + f + "] for resource type " + r
                                + " does not match.");
            }
            model.switchToNextPlayer();
        }
    }

    /**
     * Tests whether player one can build two roads starting in game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementBuildRoad() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(REQUIRED_WINNING_SCORE);
        Assertions.assertTrue(model.buildRoad(new Point(6, 6), new Point(6, 4)));
        Assertions.assertTrue(model.buildRoad(new Point(6, 4), new Point(7, 3)));
    }

    /**
     * Tests whether player one can build a road and a settlement starting in game
     * state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementBuildSettlement() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(REQUIRED_WINNING_SCORE);
        Assertions.assertTrue(model.buildRoad(new Point(9, 15), new Point(9, 13)));
        Assertions.assertTrue(model.buildSettlement(new Point(9, 13)));
    }

    /**
     * Tests whether payout with multiple settlements of the same player at one
     * field works
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementTwoSettlementsSamePlayerSameFieldResourceCardPayout() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(REQUIRED_WINNING_SCORE);
        for (int diceValue : List.of(2, 6, 6, 11)) {
            model.processDiceRoll(diceValue);
        }
        Assertions.assertTrue(model.buildRoad(new Point(6, 6), new Point(7, 7)));
        Assertions.assertTrue(model.buildSettlement(new Point(7, 7)));
        Assertions.assertEquals(2, model.processDiceRoll(4).getAffectedResources().get(model.getCurrentPlayerFaction())
                .get(Config.Resource.ORE));
    }

    /**
     * Tests whether player one can build a city starting in game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementBuildCity() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(REQUIRED_WINNING_SCORE);
        Assertions.assertTrue(model.buildCity(new Point(10, 16)));
    }

    /**
     * Tests whether player two can trade in resources with the bank and has the
     * correct number of resource cards afterwards. The test starts from game state
     * {@link ThreePlayerStandard#getAfterSetupPhaseAlmostEmptyBank(int)}.
     */
    @Test
    void requirementCanTradeFourToOneWithBank() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhaseAlmostEmptyBank(REQUIRED_WINNING_SCORE);
        model.switchToNextPlayer();

        Map<Config.Resource, Integer> expectedResourceCards = ThreePlayerStandard.BANK_ALMOST_EMPTY_RESOURCE_CARD_STOCK
                .get(model.getCurrentPlayerFaction());
        Assertions.assertEquals(expectedResourceCards.get(Config.Resource.WOOL),
                model.getCurrentPlayerResourceStock(Config.Resource.WOOL));
        Assertions.assertEquals(expectedResourceCards.get(Config.Resource.LUMBER),
                model.getCurrentPlayerResourceStock(Config.Resource.LUMBER));

        model.tradeWithBankFourToOne(Config.Resource.WOOL, Config.Resource.LUMBER);

        int cardsOffered = 4;
        int cardsReceived = 1;
        Assertions.assertEquals(expectedResourceCards.get(Config.Resource.WOOL) - cardsOffered,
                model.getCurrentPlayerResourceStock(Config.Resource.WOOL));
        Assertions.assertEquals(expectedResourceCards.get(Config.Resource.LUMBER) + cardsReceived,
                model.getCurrentPlayerResourceStock(Config.Resource.LUMBER));
    }

    /***
     * This test is not actually a test and should be removed. However,
     * we leave it in for you to have a quick and easy way to look at the
     * game board produced by {@link ThreePlayerStandard#getAfterSetupPhase(int)},
     * augmented by annotations, which you won't need since we do not ask for
     * more advanced trading functionality using harbours.
     */
    @Test
    void print() {
        SiedlerGame model = ThreePlayerStandard.getAfterSetupPhase(REQUIRED_WINNING_SCORE);
        model.getBoard().addFieldAnnotation(new Point(6, 8), new Point(6, 6), "N ");
        model.getBoard().addFieldAnnotation(new Point(6, 8), new Point(5, 7), "NE");
        model.getBoard().addFieldAnnotation(new Point(6, 8), new Point(5, 9), "SE");
        model.getBoard().addFieldAnnotation(new Point(6, 8), new Point(6, 10), "S ");
        model.getBoard().addFieldAnnotation(new Point(6, 8), new Point(7, 7), "NW");
        model.getBoard().addFieldAnnotation(new Point(6, 8), new Point(7, 9), "SW");
        System.out.println(new SiedlerBoardTextView(model.getBoard()));
    }

    private Map<Config.Faction, Map<Config.Resource, Integer>> convertToMap(
            Map<Config.Faction, List<Config.Resource>> factionListMap) {
        final Map<Config.Faction, Map<Config.Resource, Integer>> resultMap = new HashMap<>();
        for (Map.Entry<Config.Faction, List<Config.Resource>> factionListEntry : factionListMap.entrySet()) {
            resultMap.put(factionListEntry.getKey(), getAmountsPerResource(factionListEntry.getValue()));
        }
        return resultMap;
    }

    private Map<Config.Resource, Integer> getAmountsPerResource(final List<Config.Resource> resources) {
        final Map<Config.Resource, Integer> amountsPerResource = new HashMap<>();
        for (Config.Resource resource : resources) {
            amountsPerResource.merge(resource, 1, Integer::sum);
        }
        return amountsPerResource;
    }

}
