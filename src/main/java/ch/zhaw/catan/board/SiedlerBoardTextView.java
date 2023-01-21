package ch.zhaw.catan.board;

import ch.zhaw.catan.structure.Road;
import ch.zhaw.catan.structure.Structure;
import ch.zhaw.hexboard.HexBoardTextView;
import ch.zhaw.hexboard.Label;

import java.awt.Point;
import java.util.Map;

import static ch.zhaw.catan.board.SiedlerBoard.MAX_X_COORDINATE;
import static ch.zhaw.catan.board.SiedlerBoard.MAX_Y_COORDINATE;
import static ch.zhaw.catan.game.Config.getStandardDiceNumberPlacement;
import static java.lang.String.format;
import static java.lang.System.lineSeparator;

/**
 * Represents a class responsible to print out the game board created in {@link SiedlerBoard}.
 */
public class SiedlerBoardTextView extends HexBoardTextView<Field, Structure, Road, String> {

    private static final String WHITE_SPACE = " ";
    private static final int ZERO_DIGIT_CHANGE = 10;

    /**
     * Creates a new SiedlerBoardTextView object and initializes it with a {@link SiedlerBoard} object.
     * It also sets the lower field labels of the board to the corresponding dice numbers.
     * For example, if a field has a dice number of 7, the lower field label will be "07".
     *
     * @param board The {@link SiedlerBoard} object to be used for the SiedlerBoardTextView.
     */
    public SiedlerBoardTextView(final SiedlerBoard board) {
        super(board);
        for (Map.Entry<Point, Integer> entry : getStandardDiceNumberPlacement().entrySet()) {
            String label = format("%02d", entry.getValue());
            setLowerFieldLabel(entry.getKey(), new Label(label.charAt(0), label.charAt(1)));
        }
    }

    /**
     * Returns a string representation of the board with coordinate system
     *
     * @return a string representation of the board
     */
    @Override
    public String toString() {
        return getBoardWithCoordinateSystem(super.toString());
    }

    /**
     * This method takes a String representing a board and adds a coordinate system to it.
     * It adds the x and y coordinates to the edges of the board, with the x coordinates
     * at the top and the y coordinates at the left.
     *
     * @param board String representation of the board
     * @return String representation of the board with the coordinate system
     */
    private String getBoardWithCoordinateSystem(final String board) {
        final StringBuilder sb = new StringBuilder();

        sb.append(lineSeparator());
        sb.append(WHITE_SPACE.repeat(4));
        appendXCoordinates(sb);
        sb.append(lineSeparator().repeat(2));

        final String[] lines = board.split(lineSeparator());
        appendYCoordinates(sb, lines);

        return sb.toString();
    }

    /**
     * Appends the y-coordinates to the given {@link StringBuilder} for each line in the given array of lines.
     *
     * @param sb    the {@link StringBuilder} to append the y-coordinates to
     * @param lines the array of lines to append y-coordinates for
     */
    private void appendYCoordinates(final StringBuilder sb, final String[] lines) {
        int currentBoardLine = 0;
        int y = 0;
        while (currentBoardLine < lines.length) {
            if (y < ZERO_DIGIT_CHANGE) {
                sb.append(WHITE_SPACE);
            }

            if (currentBoardLine % 5 == 0) {
                sb.append(y++);
                sb.append(WHITE_SPACE);
                sb.append(lines[currentBoardLine]);

                currentBoardLine++;

                sb.append(lineSeparator());
                sb.append(WHITE_SPACE.repeat(2));
            } else if (y <= MAX_Y_COORDINATE) {
                sb.append(currentBoardLine % 5 == 3 ? y : y++);
            } else {
                sb.append(WHITE_SPACE.repeat(2));
            }

            sb.append(WHITE_SPACE);
            sb.append(lines[currentBoardLine]);
            sb.append(lineSeparator());

            currentBoardLine++;
        }
    }

    /**
     * Appends the X coordinates to the given {@link StringBuilder}.
     *
     * @param sb the {@link StringBuilder} to append the coordinates to
     */
    private void appendXCoordinates(final StringBuilder sb) {
        for (int x = 0; x <= MAX_X_COORDINATE; x++) {
            if (x < ZERO_DIGIT_CHANGE) {
                sb.append(WHITE_SPACE);
            }

            sb.append(x);

            if (x < MAX_X_COORDINATE) {
                sb.append(WHITE_SPACE.repeat(6));
            }
        }
    }

}
