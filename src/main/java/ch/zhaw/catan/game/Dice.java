package ch.zhaw.catan.game;

import static ch.zhaw.catan.game.App.RANDOM;

/**
 * A class for representing a dice in the game of Catan.
 */
public class Dice {

    static final int LOWER_DICE_LIMIT = 1;
    static final int UPPER_DICE_LIMIT = 6;

    /**
     * Rolls the dice and returns the result.
     *
     * @return The result of the dice roll.
     */
    public int roll() {
        return getSingleDiceRoll() + getSingleDiceRoll();
    }

    /**
     * Gets the result of rolling a single dice.
     *
     * @return The result of rolling a single dice.
     */
    private int getSingleDiceRoll() {
        return RANDOM.nextInt(UPPER_DICE_LIMIT) + LOWER_DICE_LIMIT;
    }

}
