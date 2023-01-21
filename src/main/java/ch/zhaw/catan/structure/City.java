package ch.zhaw.catan.structure;

import ch.zhaw.catan.game.Config;
import ch.zhaw.catan.game.Config.Faction;

import static ch.zhaw.catan.game.Config.Structure.CITY;

/**
 * A class representing a city structure in the siedler game.
 */
public class City extends Structure {

    /**
     * The number of points that this city provides for its {@link Faction}.
     */
    static final int SCORE = 2;

    /**
     * The amount of resources that get paid out for this city.
     */
    static final int AMOUNT_PER_RESOURCE = 2;

    /**
     * A unique String identifier for this city.
     */
    static final String IDENTIFIER = "C";

    /**
     * Creates a new city belonging to the specified {@link Faction}.
     *
     * @param faction the {@link Faction} that this city belongs to.
     */
    public City(final Faction faction) {
        super(faction);
    }

    /**
     * Returns the type of this {@link Structure}, which is always `CITY`.
     *
     * @return the type of this {@link Structure}
     */
    @Override
    public Config.Structure getStructureType() {
        return CITY;
    }

    /**
     * Returns the number of points that this city provides for its {@link Faction}.
     *
     * @return the number of points provided by this city
     */
    @Override
    public int getScore() {
        return SCORE;
    }

    /**
     * Returns the unique identifier for this city.
     *
     * @return the unique identifier for this city
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Returns the number of resources that get paid out for this city.
     *
     * @return the number of resources that get paid out for this city.
     */
    @Override
    public int getAmountPerResource() {
        return AMOUNT_PER_RESOURCE;
    }

}
