package ch.zhaw.catan.structure;

import ch.zhaw.catan.game.Config;
import ch.zhaw.catan.game.Config.Faction;

import static java.util.Objects.requireNonNull;

/**
 * A class representing a structure in the siedler game.
 */
public abstract class Structure {

    /**
     * The faction that this structure belongs to.
     */
    private final Faction faction;

    /**
     * Creates a new structure belonging to the specified {@link Faction}.
     *
     * @param faction the {@link Faction} that this structure belongs to
     */
    protected Structure(final Faction faction) {
        this.faction = requireNonNull(faction, "faction must not be null");
    }

    /**
     * Returns the type of this structure.
     *
     * @return the type of this structure
     */
    public abstract Config.Structure getStructureType();

    /**
     * Returns the score that this structure provides for its {@link Faction}.
     *
     * @return the score provided by this structure
     */
    public abstract int getScore();

    /**
     * Returns a unique identifier for this structure.
     *
     * @return a unique identifier for this structure
     */
    public abstract String getIdentifier();

    /**
     * Returns the number of resources that are required to produce this
     * structure.
     *
     * @return the number of resources required to produce this structure
     */
    public abstract int getAmountPerResource();

    /**
     * Returns the {@link Faction} that this structure belongs to.
     *
     * @return the {@link Faction} that this structure belongs to
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Returns a string representation of this structure.
     *
     * @return a string representation of this structure
     */
    @Override
    public String toString() {
        return getFaction().toString().charAt(0) + getIdentifier();
    }

}
