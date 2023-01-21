package ch.zhaw.catan.game;

import ch.zhaw.catan.board.Field;
import ch.zhaw.catan.board.SiedlerBoard;
import ch.zhaw.catan.game.Config.Faction;
import ch.zhaw.catan.game.Config.Land;
import ch.zhaw.catan.game.Config.Resource;
import ch.zhaw.catan.structure.City;
import ch.zhaw.catan.structure.Road;
import ch.zhaw.catan.structure.Settlement;
import ch.zhaw.catan.structure.Structure;

import java.awt.Point;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.zhaw.catan.game.Config.Structure.CITY;
import static ch.zhaw.catan.game.Config.Structure.ROAD;
import static ch.zhaw.catan.game.Config.Structure.SETTLEMENT;
import static java.util.EnumSet.allOf;

/**
 * This class performs all actions related to modifying the game state of the Settler of Catan board game.
 * <p>
 * The SiedlerGame class is responsible for maintaining the state of the game, including the {@link Player}s and their inventories,
 * the {@link SiedlerBoard}, and the bank. It provides methods for performing actions such as rolling the dice, building {@link Structure}s,
 * and trading {@link Resource}s.
 */
public class SiedlerGame {

    static final int FOUR_TO_ONE_TRADE_OFFER = 4;
    static final int FOUR_TO_ONE_TRADE_WANT = 1;
    static final int THIEF_DICE_NUMBER = 7;

    private static final List<Faction> FACTION_ASSIGNMENTS = new ArrayList<>();

    static {
        FACTION_ASSIGNMENTS.addAll(allOf(Faction.class));
    }

    private final List<Player> currentPlayers;
    private final SiedlerBoard board;
    private final Bank bank;
    private final int winPoints;

    private int currentPlayerIndex;

    /**
     * Constructs a SiedlerGame game state object.
     *
     * @param winPoints       the number of points required to win the game
     * @param numberOfPlayers the number of {@link Player}s
     */
    public SiedlerGame(final int winPoints, final int numberOfPlayers) {
        this.winPoints = winPoints;
        bank = new Bank();

        currentPlayerIndex = 0;
        currentPlayers = new ArrayList<>();
        for (int i = 0; i < numberOfPlayers; i++) {
            addPlayer(new Player(FACTION_ASSIGNMENTS.get(i)));
        }

        board = new SiedlerBoard();
    }

    /**
     * Switches to the next {@link Player} in the defined sequence of {@link Player}s.
     */
    public void switchToNextPlayer() {
        currentPlayerIndex = (currentPlayerIndex + 1) % currentPlayers.size();
    }

    /**
     * Switches to the previous {@link Player} in the defined sequence of {@link Player}s.
     */
    public void switchToPreviousPlayer() {
        currentPlayerIndex = currentPlayerIndex == 0
                ? currentPlayers.size() - 1
                : currentPlayerIndex - 1;
    }

    /**
     * Returns the {@link Faction}s of the active {@link Player}s.
     * <p>
     * The order of the {@link Player}'s {@link Faction}s in the {@link List} must correspond to the order in which they play. Hence, the
     * {@link Player} that sets the first {@link Settlement} must be at position 0 in the list etc.</p>
     * <p> <strong>Important note:</strong> The {@link List} must contain the {@link Faction}s of active {@link Player}s only.</p>
     *
     * @return the {@link List} with {@link Player}'s {@link Faction}s
     */
    public List<Faction> getPlayerFactions() {
        final List<Faction> list = new ArrayList<>();

        for (Player currentPlayer : currentPlayers) {
            Faction faction = currentPlayer.getFaction();
            list.add(faction);
        }

        return list;
    }

    /**
     * Returns the game {@link SiedlerBoard}.
     *
     * @return the game board
     */
    public SiedlerBoard getBoard() {
        return this.board;
    }

    /**
     * Returns the {@link Faction} of the current {@link Player}.
     *
     * @return the {@link Faction} of the current {@link Player}
     */
    public Faction getCurrentPlayerFaction() {
        return getCurrentPlayer().getFaction();
    }

    /**
     * Returns how many {@link Resource}e cards of the specified type the current {@link Player} owns.
     *
     * @param resource the {@link Resource} type
     * @return the number of {@link Resource} cards of this type
     */
    public int getCurrentPlayerResourceStock(final Resource resource) {
        return getCurrentPlayer().getAmountOfResource(resource);
    }

    /**
     * Places a {@link Settlement} in the founder's phase (phase II) of the game.
     *
     * <p>The placement does not cost any {@link Resource} cards. If payout is set to true, for each adjacent resource-producing
     * {@link Field}, a {@link Resource} card of the type of the {@link Resource} produced by the {@link Field} is taken from the {@link Bank} (if available)
     * and added to the {@link Player}s' stock of {@link Resource} cards.</p>
     *
     * @param position the position of the {@link Settlement}
     * @param payout   if true, the {@link Player} gets one {@link Resource} card per adjacent resource-producing {@link Field}
     * @return true, if the placement was successful, false otherwise
     */
    public boolean placeInitialSettlement(final Point position, final boolean payout) {
        if (board.canPlaceSettlementOnPosition(position, true, getCurrentPlayerFaction())) {
            final Structure settlement = new Settlement(getCurrentPlayerFaction());
            getCurrentPlayer().addStructure(settlement);
            board.setCorner(position, settlement);

            if (payout) {
                for (Land land : board.getLandsForCorner(position)) {
                    bank.decreaseInventoryItemIfApplicable(land.getResource(), 1);
                    getCurrentPlayer().increaseInventoryItem(land.getResource(), 1);
                }
            }

            return true;
        }

        return false;
    }

    /**
     * Places a {@link Road} in the founder's phase (phase II) of the game. The placement does not cost any {@link Resource} cards.
     *
     * @param roadStart position of the start of the {@link Road}
     * @param roadEnd   position of the end of the {@link Road}
     * @return true, if the placement was successful, false otherwise
     */
    public boolean placeInitialRoad(final Point roadStart, final Point roadEnd) {
        if (board.canPlaceRoadOnPosition(roadStart, roadEnd, getCurrentPlayerFaction())) {
            final Road road = new Road(getCurrentPlayerFaction());
            getCurrentPlayer().addStructure(road);
            board.setEdge(roadStart, roadEnd, road);

            return true;
        }

        return false;
    }

    /**
     * This method takes care of actions depending on the dice throw result.
     * <p>
     * A key action is the payout of the {@link Resource} cards to the {@link Player}s according to the payout rules of the game. This
     * includes the"negative payout" in case a 7 is thrown and a player has more than
     * {@link Config#MAX_CARDS_IN_HAND_NO_DROP} {@link Resource} cards.
     * </p><p>
     * If a {@link Player} does not get {@link Resource} cards, the list for this {@link Player}s' {@link Faction} is <b>an empty {@link List} (not
     * {@code null})</b>!.
     * </p><p>
     * The payout rules of the game take into account factors such as, the number of {@link Resource} cards currently available
     * in the {@link Bank}, {@link Settlement} types({@link Settlement} or {@link City}), and the number of {@link Player}s that should get {@link Resource} cards of a
     * certain type (relevant if there are not enough left in the {@link Bank}).
     * </p>
     *
     * @param dicethrow the {@link Resource} cards that have been distributed to the {@link Player}s
     * @return the {@link Resource} cards added to the stock of the different {@link Player}s
     * @deprecated replaced by {@link #processDiceRoll(int)}
     */
    @Deprecated(since = "1.0", forRemoval = true)
    Map<Faction, List<Resource>> throwDice(int dicethrow) {
        throw new UnsupportedOperationException("Method has been replaced by processDiceRoll(int). Please use that method instead.");
    }

    /**
     * Delegates the dice roll to either have the thief executed {@link #executeThief()} or to pay out {@link Resource}s
     * {@link #payoutResources(int)} to the {@link Player}s having a {@link Settlement} or {@link City} nearby.
     *
     * @param diceRoll the latest dice roll by the current {@link Player}
     * @return It returns a {@link DiceResult} object containing the affected {@link Faction}'s {@link Resource}s and an indication if
     * the thief has been executed or not.
     */
    public DiceResult processDiceRoll(final int diceRoll) {
        if (isThiefRolled(diceRoll)) {
            return new DiceResult(executeThief(), true);
        } else {
            return new DiceResult(payoutResources(diceRoll), false);
        }
    }

    /**
     * Builds a {@link Settlement} at the specified position on the board.
     *
     * <p>The {@link Settlement} can be built if:
     * <ul>
     * <li> the {@link Player} possesses the required {@link Resource} cards</li>
     * <li> a {@link Settlement} to place on the board</li>
     * <li> the specified position meets the build rules for {@link Settlement}s</li>
     * </ul>
     *
     * @param position the position of the {@link Settlement}
     * @return true, if the placement was successful, false otherwise
     */
    public boolean buildSettlement(final Point position) {
        if (board.canPlaceSettlementOnPosition(position, false, getCurrentPlayerFaction())
                && getCurrentPlayer().hasNotReachedMaxStockOf(SETTLEMENT)
                && getCurrentPlayer().decreaseInventoryIfApplicable(SETTLEMENT.getCostsAsIntegerMap())) {
            bank.increaseInventory(SETTLEMENT.getCostsAsIntegerMap());
            final Structure settlement = new Settlement(getCurrentPlayerFaction());
            getCurrentPlayer().addStructure(settlement);
            board.setCorner(position, settlement);

            return true;
        }

        return false;
    }

    /**
     * Builds a {@link City} at the specified position on the board.
     *
     * <p>The {@link City} can be built if:
     * <ul>
     * <li> the {@link Player} possesses the required {@link Resource} cards</li>
     * <li> a {@link City} to place on the board</li>
     * <li> the specified position meets the build rules for cities</li>
     * </ul>
     *
     * @param position the position of the {@link City}
     * @return true, if the placement was successful, false otherwise
     */
    public boolean buildCity(final Point position) {
        if (board.canPlaceCityOnPosition(position, getCurrentPlayerFaction())
                && getCurrentPlayer().hasNotReachedMaxStockOf(CITY)
                && getCurrentPlayer().decreaseInventoryIfApplicable(CITY.getCostsAsIntegerMap())) {
            bank.increaseInventory(CITY.getCostsAsIntegerMap());
            final Structure city = new City(getCurrentPlayerFaction());
            getCurrentPlayer().removeStructure(board.getCorner(position));
            getCurrentPlayer().addStructure(city);
            board.setCorner(position, city);

            return true;
        }

        return false;
    }

    /**
     * Builds a {@link Road} at the specified position on the board.
     *
     * <p>The {@link Road} can be built if:
     * <ul>
     * <li> the {@link Player} possesses the required {@link Resource} cards</li>
     * <li> a {@link Road} to place on the board</li>
     * <li> the specified position meets the build rules for {@link Road}s</li>
     * </ul>
     *
     * @param roadStart the position of the start of the {@link Road}
     * @param roadEnd   the position of the end of the {@link Road}
     * @return true, if the placement was successful, false otherwise
     */
    public boolean buildRoad(final Point roadStart, final Point roadEnd) {
        if (board.canPlaceRoadOnPosition(roadStart, roadEnd, getCurrentPlayerFaction())
                && getCurrentPlayer().hasNotReachedMaxStockOf(ROAD)
                && getCurrentPlayer().decreaseInventoryIfApplicable(ROAD.getCostsAsIntegerMap())) {
            bank.increaseInventory(ROAD.getCostsAsIntegerMap());
            final Road road = new Road(getCurrentPlayerFaction());
            getCurrentPlayer().addStructure(road);
            board.setEdge(roadStart, roadEnd, road);

            return true;
        }

        return false;
    }

    /**
     * <p>Trades in {@link #FOUR_TO_ONE_TRADE_OFFER} {@link Resource} cards of the
     * offered type for {@link #FOUR_TO_ONE_TRADE_WANT} {@link Resource} cards of the wanted type.
     * </p><p>
     * The trade only works when {@link Bank} and {@link Player} possess the {@link Resource} cards for the trade before the trade is executed.
     * </p>
     *
     * @param offer offered type
     * @param want  wanted type
     * @return true, if the trade was successful, false otherwise
     */
    public boolean tradeWithBankFourToOne(final Resource offer, final Resource want) {
        if (FOUR_TO_ONE_TRADE_OFFER <= getCurrentPlayerResourceStock(offer) &&
                FOUR_TO_ONE_TRADE_WANT <= bank.getAmountOfResource(want)) {
            getCurrentPlayer().decreaseInventoryItemIfApplicable(offer, FOUR_TO_ONE_TRADE_OFFER);
            getCurrentPlayer().increaseInventoryItem(want, FOUR_TO_ONE_TRADE_WANT);
            bank.decreaseInventoryItemIfApplicable(want, FOUR_TO_ONE_TRADE_WANT);
            bank.increaseInventoryItem(offer, FOUR_TO_ONE_TRADE_OFFER);

            return true;
        }

        return false;
    }

    /**
     * Returns the winner of the game, if any.
     *
     * @return the winner of the game or null, if there is no winner (yet)
     */
    public Faction getWinner() {
        return getCurrentPlayer().getScore() >= winPoints
                ? getCurrentPlayer().getFaction()
                : null;
    }

    /**
     * Places the thief on the specified {@link Field} and steals a random {@link Resource} card (if the {@link Player} has such cards)
     * from a random {@link Player} with a {@link Settlement} at that fieldPosition (if there is a {@link Settlement}) and adds it to the
     * {@link Resource} cards of the current {@link Player}.
     *
     * @param fieldPosition the fieldPosition on which to place the thief
     * @return false, if the specified fieldPosition is not a fieldPosition or the thief cannot be placed there (e.g.,
     * on water)
     */
    public boolean placeThiefAndStealCard(final Point fieldPosition) {
        if (!board.canPlaceThiefOnPosition(fieldPosition)) {
            return false;
        }

        board.switchThiefPosition(fieldPosition);

        final Set<Faction> otherCornerFactions = board.getOtherFieldCornerFactions(getCurrentPlayerFaction());
        getCurrentPlayer().stealRandomResourceFrom(getNearbyPlayersToStealFrom(otherCornerFactions));
        return true;
    }

    /**
     * Returns a {@link List} of {@link Player}s that are nearby and have {@link Resource}s to steal.
     *
     * @param otherCornerFactions the {@link Faction}s of the {@link Player}s in the nearby corners
     * @return a {@link List} of {@link Player}s that can be stolen from
     */
    private List<Player> getNearbyPlayersToStealFrom(final Set<Faction> otherCornerFactions) {
        final List<Player> nearbyPlayers = new ArrayList<>();
        for (Faction faction : otherCornerFactions) {
            final Player player = getPlayerFromFaction(faction);
            if (player != null && player.getTotalAmountOfResources() > 0) {
                nearbyPlayers.add(player);
            }
        }
        return nearbyPlayers;
    }

    /**
     * Returns the {@link List} of {@link Player}s currently playing the game.
     *
     * @return the {@link List} of {@link Player}s
     */
    public List<Player> getCurrentPlayers() {
        return currentPlayers;
    }

    /**
     * Returns the {@link Player} who is currently playing the game.
     *
     * @return the current {@link Player}
     */
    public Player getCurrentPlayer() {
        return currentPlayers.get(currentPlayerIndex);
    }

    /**
     * Returns whether the thief has been rolled on the dice.
     *
     * @param diceRoll the number rolled on the dice
     * @return true if the thief was rolled, false otherwise
     */
    private boolean isThiefRolled(final int diceRoll) {
        return diceRoll == THIEF_DICE_NUMBER;
    }

    /**
     * Takes half of the {@link Resource}s in the {@link Player} inventory is bigger than the required amount.
     * The {@link Resource} {@link Map} is given to the {@link Bank}.
     *
     * @return returns {@link Map} with {@link Faction}s and {@link Resource} and {@link Integer} taken.
     */
    public Map<Faction, Map<Resource, Integer>> executeThief() {
        final Map<Faction, Map<Resource, Integer>> stolenResourcesPerFaction = new EnumMap<>(Faction.class);
        for (Player player : getCurrentPlayers()) {
            Map<Resource, Integer> resourcesStolen = player.processThief();
            stolenResourcesPerFaction.put(player.getFaction(), resourcesStolen);
            bank.increaseInventory(resourcesStolen);
        }

        return stolenResourcesPerFaction;
    }

    /**
     * Returns the {@link Bank}.
     *
     * @return the {@link Bank}
     */
    public Bank getBank() {
        return bank;
    }

    /**
     * Return the {@link Player} object for the specified {@link Faction}.
     *
     * @param playerFaction the {@link Faction} for which the {@link Player} object is requested
     * @return the {@link Player} object for the specified {@link Faction}
     */
    private Player getPlayerFromFaction(final Faction playerFaction) {
        for (Player player : currentPlayers) {
            if (player.getFaction() == playerFaction) {
                return player;
            }
        }

        return null;
    }

    /**
     * Adds a {@link Player} to the game.
     *
     * @param player the {@link Player} to add
     */
    private void addPlayer(final Player player) {
        currentPlayers.add(player);
    }

    /**
     * Calculates the {@link Resource}s that each {@link Player} should receive based on the given dice roll.
     *
     * @param diceRoll the dice roll to use for the calculation
     * @return a {@link Map} of {@link Faction}s to maps of {@link Resource}s to amounts, indicating the {@link Resource}s each {@link Player} should receive
     */
    private Map<Faction, Map<Resource, Integer>> payoutResources(final int diceRoll) {
        final Map<Faction, Map<Resource, Integer>> paidOutResources = new EnumMap<>(Faction.class);

        for (Field field : board.getFieldsForDiceValue(diceRoll)) {
            if (!field.isOccupiedByThief()) {
                final Resource fieldResource = field.getLand().getResource();
                final List<Structure> adjacentStructures = board.getCornersOfField(field.getPosition());

                if (bank.isInventorySufficientForPayoutOfResource(fieldResource, adjacentStructures)) {
                    for (Structure structure : adjacentStructures) {
                        payoutResourcesBasedOnStructureType(structure, fieldResource, paidOutResources);
                    }
                }
            }
        }

        return paidOutResources;
    }

    /**
     * Pay out {@link Resource}s to the {@link Player} who owns the given {@link Structure} based on the type of {@link Structure} and the specified {@link Resource}.
     *
     * @param structure        the {@link Structure} to pay out {@link Resource}s for
     * @param resource         the {@link Resource} to pay out
     * @param paidOutResources a {@link Map} of {@link Resource}s paid out to each {@link Player}
     */
    private void payoutResourcesBasedOnStructureType(final Structure structure, final Resource resource,
                                                     final Map<Faction, Map<Resource, Integer>> paidOutResources) {
        final Faction faction = structure.getFaction();
        final Player playerFromFaction = getPlayerFromFaction(faction);
        final int amountPerResource = structure.getAmountPerResource();

        if (playerFromFaction != null) {
            playerFromFaction.increaseInventoryItem(resource, amountPerResource);
            bank.decreaseInventoryItemIfApplicable(resource, amountPerResource);
            paidOutResources.computeIfAbsent(faction, SiedlerGame::initializeResourceMap)
                    .merge(resource, amountPerResource, Integer::sum);
        }
    }

    /**
     * Initializes a {@link Map} of {@link Resource}s for a {@link Player}'s {@link Faction}.
     *
     * @param f the {@link Player}'s {@link Faction}
     * @return a Map of {@link Resource}s for the given {@link Faction}
     */
    private static Map<Resource, Integer> initializeResourceMap(final Faction f) {
        return new EnumMap<>(Resource.class);
    }

}
