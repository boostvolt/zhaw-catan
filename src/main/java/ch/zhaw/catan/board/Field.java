package ch.zhaw.catan.board;

import ch.zhaw.catan.game.Config.Land;

import java.awt.Point;

import static ch.zhaw.catan.game.Config.INITIAL_THIEF_POSITION;
import static java.util.Objects.requireNonNull;

/**
 * A class representing a Field on the game board of Siedler.
 */
public class Field {

    static final String THIEF_IDENTIFIER = "XX";

    private final Land land;
    private final Point position;
    private boolean occupiedByThief;

    /**
     * Constructs a new Field with the given {@link Land} type and position.
     *
     * @param land     the type of {@link Land} for this Field
     * @param position the position of this Field on the game board
     */
    public Field(final Land land, final Point position) {
        this.land = requireNonNull(land, "land must not be null");
        this.position = requireNonNull(position, "position must not be null");
        occupiedByThief = position.equals(INITIAL_THIEF_POSITION);
    }

    /**
     * Returns the type of {@link Land} for this Field.
     *
     * @return the {@link Land} type for this Field
     */
    public Land getLand() {
        return land;
    }

    /**
     * Returns the position of this Field on the game board.
     *
     * @return the position of this Field on the game board
     */
    public Point getPosition() {
        return position;
    }

    /**
     * Returns whether this Field is occupied by a thief.
     *
     * @return true if this Field is occupied by a thief, false otherwise
     */
    public boolean isOccupiedByThief() {
        return occupiedByThief;
    }

    /**
     * Sets whether this Field is occupied by a thief.
     *
     * @param occupiedByThief true if this Field is occupied by a thief, false otherwise
     */
    public void setOccupiedByThief(final boolean occupiedByThief) {
        this.occupiedByThief = occupiedByThief;
    }

    /**
     * Returns a {@link String} representation of this Field. If this Field is occupied by a thief, the THIEF_IDENTIFIER is returned.
     * Otherwise, the toString() method of the {@link Land} type is returned.
     *
     * @return a {@link String} representation of this Field
     */
    @Override
    public String toString() {
        return isOccupiedByThief() ? THIEF_IDENTIFIER : getLand().toString();
    }

}
