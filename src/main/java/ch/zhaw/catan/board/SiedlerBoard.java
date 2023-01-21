package ch.zhaw.catan.board;

import ch.zhaw.catan.game.Config;
import ch.zhaw.catan.game.Config.Faction;
import ch.zhaw.catan.game.Config.Land;
import ch.zhaw.catan.game.Player;
import ch.zhaw.catan.structure.City;
import ch.zhaw.catan.structure.Road;
import ch.zhaw.catan.structure.Settlement;
import ch.zhaw.catan.structure.Structure;
import ch.zhaw.hexboard.HexBoard;

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static ch.zhaw.catan.game.Config.INITIAL_THIEF_POSITION;
import static ch.zhaw.catan.game.Config.Land.WATER;
import static ch.zhaw.catan.game.Config.Structure.SETTLEMENT;
import static ch.zhaw.catan.game.Config.getStandardDiceNumberPlacement;
import static ch.zhaw.catan.game.Config.getStandardLandPlacement;

/**
 * Class representing the Board of the game. Class holds information of the board and provides methods to fetch
 * data about the various fields, corners and edges and to assert if certain structures can be built.
 */
public class SiedlerBoard extends HexBoard<Field, Structure, Road, String> {

    public static final int MIN_COORDINATE = 0;
    public static final int MAX_X_COORDINATE = 14;
    public static final int MAX_Y_COORDINATE = 22;

    private final SiedlerBoardTextView view;

    private Field currentThiefField;

    /**
     * Constructs a new SiedlerBoard with the standard {@link Land} placement.
     * The initial thief field is also set to the initial thief position.
     */
    public SiedlerBoard() {
        for (Map.Entry<Point, Land> entry : getStandardLandPlacement().entrySet()) {
            super.addField(entry.getKey(), new Field(entry.getValue(), entry.getKey()));
        }
        setCurrentThiefField(getField(INITIAL_THIEF_POSITION));
        view = new SiedlerBoardTextView(this);
    }

    /**
     * Returns the {@link Field}s associated with the specified dice value.
     *
     * @param dice the dice value
     * @return the fields associated with the dice value
     */
    public List<Field> getFieldsForDiceValue(final int dice) {
        final List<Field> rolledFields = new ArrayList<>();

        for (Map.Entry<Point, Integer> diceValuePerPoint : getStandardDiceNumberPlacement().entrySet()) {
            if (diceValuePerPoint.getValue() == dice) {
                rolledFields.add(getField(diceValuePerPoint.getKey()));
            }
        }

        return rolledFields;
    }


    /**
     * Returns the {@link Land}s adjacent to the specified corner.
     *
     * @param corner the corner
     * @return the list with the adjacent {@link Land}s
     */
    public List<Land> getLandsForCorner(final Point corner) {
        final List<Land> landList = new ArrayList<>();

        if (hasCorner(corner)) {
            final List<Field> fields = getFields(corner);

            for (Field field : fields) {
                landList.add(field.getLand());
            }

            landList.removeIf(land -> land.getResource() == null);
        }

        return landList;
    }

    /**
     * Returns the current thief {@link Field}.
     *
     * @return the current thief {@link Field}
     */
    public Field getCurrentThiefField() {
        return currentThiefField;
    }

    /**
     * Sets the current thief {@link Field}.
     *
     * @param currentThiefField the current thief {@link Field}
     */
    public void setCurrentThiefField(final Field currentThiefField) {
        this.currentThiefField = currentThiefField;
    }

    /**
     * Returns the text view of the game board.
     *
     * @return the text view of the game board
     */
    public SiedlerBoardTextView getView() {
        return view;
    }

    /**
     * Returns whether the thief can be placed on the specified position on the game board.
     *
     * @param position the position to check
     * @return true if the thief can be placed on the position, false otherwise
     */
    public boolean canPlaceThiefOnPosition(final Point position) {
        return hasField(position)
                && getField(position) != null
                && getField(position).getLand() != WATER;
    }

    /**
     * Moves the thief to the specified {@link Field} on the game board.
     *
     * @param fieldPosition the position of the {@link Field} to move the thief to
     */
    public void switchThiefPosition(final Point fieldPosition) {
        getCurrentThiefField().setOccupiedByThief(false);
        final Field field = getField(fieldPosition);
        field.setOccupiedByThief(true);
        setCurrentThiefField(field);
    }


    /**
     * Returns a set of {@link Faction}s of the {@link Player}s who have {@link Structure}s on the corners of the current thief {@link Field} that
     * belong to a different {@link Faction} than the current {@link Player}'s {@link Faction}.
     *
     * @param currentPlayerFaction the {@link Faction} of the current {@link Player}
     * @return a {@link Set} of {@link Faction}s of the {@link Player}s on the corners of the current thief {@link Field}
     */
    public Set<Faction> getOtherFieldCornerFactions(final Faction currentPlayerFaction) {
        final Set<Faction> factions = new HashSet<>();

        for (Structure structure : getCornersOfField(getCurrentThiefField().getPosition())) {
            if (structure.getFaction() != currentPlayerFaction) {
                factions.add(structure.getFaction());
            }
        }

        return factions;
    }

    /**
     * Checks if the current {@link Point} on {@link SiedlerBoard}
     * can hold a {@link Settlement} and has no surrounding occupied corners and is not occupied itself.
     *
     * @param position  position on {@link SiedlerBoard} as {@link Point}
     * @param initiationPhase boolean for set up phase, removes adjacent {@link Road}s requirement
     * @return true if {@link Settlement} can be placed
     */
    public boolean canPlaceSettlementOnPosition(final Point position, final boolean initiationPhase,
                                                final Faction currentPlayerFaction) {
        if (!hasCorner(position)) {
            return false;
        }

        final Structure corner = getCorner(position);
        final boolean isCornerFree = !getLandsForCorner(position).isEmpty()
                && corner == null;
        final boolean hasNoNeighbour = hasNoNeighbour(position);
        return initiationPhase
                ? isCornerFree && hasNoNeighbour
                : isCornerFree && hasNoNeighbour && isOwnRoadAdjacent(position, currentPlayerFaction);
    }

    /**
     * Checks if a {@link Settlement} of the current {@link Player}
     * can be upgraded into a {@link City} on position
     *
     * @param position position on {@link SiedlerBoard} as {@link Point}
     * @return true if {@link City} can be placed
     */
    public boolean canPlaceCityOnPosition(final Point position, final Faction currentPlayerFaction) {
        if (!hasCorner(position)) {
            return false;
        }

        return isSpecificOwnStructureAdjacent(position, SETTLEMENT, currentPlayerFaction);
    }

    /**
     * Checks if the Start- and End-{@link Point} on {@link SiedlerBoard}
     * can hold a {@link Road} and has a {@link Structure} of the current {@link Player}s {@link Faction}
     *
     * @param startPosition position on {@link SiedlerBoard} as {@link Point}
     * @param endPosition   position on {@link SiedlerBoard} as {@link Point}
     * @return true if {@link Road} can be placed, false otherwise
     */
    public boolean canPlaceRoadOnPosition(final Point startPosition, final Point endPosition,
                                          final Faction currentPlayerFaction) {
        if (!hasEdge(startPosition, endPosition)
                || getEdge(startPosition, endPosition) != null) {
            return false;
        }

        if (getLandsForCorner(startPosition).isEmpty() && getLandsForCorner(endPosition).isEmpty()) {
            return false;
        }

        return isOwnStructureAdjacent(startPosition, currentPlayerFaction)
                || isOwnStructureAdjacent(endPosition, currentPlayerFaction)
                || isOwnRoadAdjacent(startPosition, currentPlayerFaction)
                || isOwnRoadAdjacent(endPosition, currentPlayerFaction);
    }

    /**
     * Returns whether the given position has no neighbours with a {@link Faction}.
     *
     * @param position the position to check
     * @return true if the position has no neighbours with a {@link Faction}, false otherwise
     */
    private boolean hasNoNeighbour(final Point position) {
        return getNeighboursOfCorner(position).isEmpty();
    }

    /**
     * Determines if any of the {@link Road}s adjacent to the given position are owned by the current {@link Player}.
     *
     * @param position             the position to check
     * @param currentPlayerFaction the {@link Faction} of the current {@link Player}
     * @return true if at least one adjacent {@link Road} is owned by the current {@link Player}, false otherwise
     */
    private boolean isOwnRoadAdjacent(final Point position, final Faction currentPlayerFaction) {
        for (Road road : getAdjacentEdges(position)) {
            if (road != null && road.getFaction() == currentPlayerFaction) {
                return true;
            }
        }
        return false;
    }


    /**
     * Returns whether any {@link Structure} owned by the current {@link Player} is adjacent to the specified position.
     *
     * @param position             the position to check
     * @param currentPlayerFaction the {@link Faction} of the current {@link Player}
     * @return true if a {@link Structure} owned by the current {@link Player} is adjacent to the specified position, false otherwise
     */
    private boolean isOwnStructureAdjacent(final Point position, final Faction currentPlayerFaction) {
        return isSpecificOwnStructureAdjacent(position, null, currentPlayerFaction);
    }

    /**
     * Returns whether a specific type of {@link Structure} owned by the current {@link Player} is adjacent to the given position.
     *
     * @param position             the position to check
     * @param structureType        the type of {@link Structure} to check, or null if any type is acceptable
     * @param currentPlayerFaction the {@link Faction} of the current {@link Player}
     * @return true if a specific type of {@link Structure} owned by the current {@link Player} is adjacent to the given position,
     * false otherwise
     */
    private boolean isSpecificOwnStructureAdjacent(final Point position, final Config.Structure structureType,
                                                   final Faction currentPlayerFaction) {
        final Structure corner = getCorner(position);
        final boolean isOwnStructureAdjacent = corner != null && corner.getFaction() == currentPlayerFaction;
        return structureType == null
                ? isOwnStructureAdjacent
                : isOwnStructureAdjacent && corner.getStructureType() == structureType;
    }

}
