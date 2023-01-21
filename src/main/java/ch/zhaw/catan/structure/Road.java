package ch.zhaw.catan.structure;

import ch.zhaw.catan.game.Config;
import ch.zhaw.catan.game.Config.Faction;

import static ch.zhaw.catan.game.Config.Structure.ROAD;

/**
 * A class representing a road structure in the siedler game.
 */
public class Road extends Structure {

    /**
     * The number of points that this {@link Road} provides for its {@link Faction}.
     */
    static final int SCORE = 0;

    /**
     * The amount of resources that get paid out for this road.
     */
    static final int AMOUNT_PER_RESOURCE = 0;

    /**
     * A unique String identifier for this road.
     */
    static final String IDENTIFIER = "R";

    /**
     * Creates a new road belonging to the specified {@link Faction}.
     *
     * @param faction the {@link Faction} that this road belongs to.
     */
    public Road(final Faction faction) {
        super(faction);
    }

    /**
     * Returns the type of this {@link Structure}, which is always `ROAD`.
     *
     * @return the type of this {@link Structure}
     */
    @Override
    public Config.Structure getStructureType() {
        return ROAD;
    }

    /**
     * Returns the number of points that this road provides for its {@link Faction}.
     *
     * @return the number of points provided by this road
     */
    @Override
    public int getScore() {
        return SCORE;
    }

    /**
     * Returns the unique identifier for this road.
     *
     * @return the unique identifier for this road
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Returns the number of resources that get paid out for this road.
     *
     * @return the number of resources that get paid out for this road.
     */
    @Override
    public int getAmountPerResource() {
        return AMOUNT_PER_RESOURCE;
    }

}
