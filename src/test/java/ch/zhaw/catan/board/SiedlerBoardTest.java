package ch.zhaw.catan.board;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SiedlerBoardTest {
    SiedlerBoard siedlerBoard = new SiedlerBoard();

    @BeforeEach
    void SiedlerBoard() {
        siedlerBoard = new SiedlerBoard();
    }

    /**
     * This test first creates a string representation of the game board and splits it into individual lines,
     * then creates the expected first line of the board view and checks that it matches the actual first line.
     * It also checks that the last line of the board view has the correct number of characters.
     */
    @Test
    void getBoardView() {
        String[] boardView = siedlerBoard.getView().toString().split(System.lineSeparator());
        String whiteSpace = " ";

        StringBuilder firstLine = new StringBuilder(whiteSpace.repeat(5));
        for (int i = 0; i <= 14; i++) {
            if (i < 9) {
                firstLine.append(i).append(whiteSpace.repeat(7));
            } else if (i < 14) {
                firstLine.append(i).append(whiteSpace.repeat(6));
            } else {
                firstLine.append(i);
            }

        }

        assertEquals(firstLine.toString(), boardView[1]);
        assertEquals(whiteSpace.repeat(131).length(), boardView[42].length());
    }
}