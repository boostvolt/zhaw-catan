package ch.zhaw.catan.structure;

import ch.zhaw.catan.game.Config;
import ch.zhaw.catan.game.Config.Faction;

import static ch.zhaw.catan.game.Config.Structure.SETTLEMENT;

/**
 * A class representing a settlement structure in the siedler game.
 */
public class Settlement extends Structure {

    /**
     * The number of points that this settlement provides for its {@link Faction}.
     */
    static final int SCORE = 1;

    /**
     * The amount of resources that get paid out for this settlement.
     */
    static final int AMOUNT_PER_RESOURCE = 1;

    /**
     * A unique {@link String} identifier for this settlement.
     */
    static final String IDENTIFIER = "S";

    /**
     * Creates a new settlement belonging to the specified {@link Faction}.
     *
     * @param faction the {@link Faction} that this settlement belongs to.
     */
    public Settlement(final Faction faction) {
        super(faction);
    }

    /**
     * Returns the type of this {@link Structure}, which is always `SETTLEMENT`.
     *
     * @return the type of this {@link Structure}
     */
    @Override
    public Config.Structure getStructureType() {
        return SETTLEMENT;
    }

    /**
     * Returns the number of points that this settlement provides for its {@link Faction}.
     *
     * @return the number of points provided by this settlement
     */
    @Override
    public int getScore() {
        return SCORE;
    }

    /**
     * Returns the unique identifier for this settlement.
     *
     * @return the unique identifier for this settlement
     */
    @Override
    public String getIdentifier() {
        return IDENTIFIER;
    }

    /**
     * Returns the number of resources that get paid out for this settlement.
     *
     * @return the number of resources that get paid out for this settlement
     */
    @Override
    public int getAmountPerResource() {
        return AMOUNT_PER_RESOURCE;
    }

}
