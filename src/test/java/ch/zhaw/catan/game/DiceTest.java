package ch.zhaw.catan.game;

import org.junit.jupiter.api.RepeatedTest;

import static ch.zhaw.catan.game.Dice.UPPER_DICE_LIMIT;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DiceTest {

    private final Dice dice = new Dice();

    @RepeatedTest(1000)
    void testRoll() {
        int nextRoll = dice.roll();
        assertTrue(nextRoll >= 1 && nextRoll <= (UPPER_DICE_LIMIT * 2));
    }

}
