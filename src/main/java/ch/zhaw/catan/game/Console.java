package ch.zhaw.catan.game;

import org.beryx.textio.TerminalProperties;
import org.beryx.textio.TextIO;
import org.beryx.textio.swing.SwingTextTerminal;

import java.awt.Color;

import static java.awt.Color.BLACK;
import static java.awt.Color.BLUE;
import static java.awt.Color.CYAN;
import static java.awt.Color.GREEN;
import static java.awt.Color.ORANGE;
import static java.awt.Color.PINK;
import static java.awt.Color.RED;
import static java.awt.Color.WHITE;
import static java.awt.Color.YELLOW;
import static java.lang.Thread.sleep;

/**
 * Represents a console that provides input and output capabilities for the game.
 */
public class Console {

    static final String QUIT_SHORTCUT = "Q";

    private final TextIO textIO;
    private final SwingTextTerminal textTerminal;

    /**
     * Constructs a new Console with the given pane title.
     *
     * @param paneTitle the title of the terminal pane
     */
    public Console(final String paneTitle) {
        textIO = new TextIO(new SwingTextTerminal());
        textTerminal = (SwingTextTerminal) textIO.getTextTerminal();
        initProperties(paneTitle);
    }

    /**
     * Initializes the properties of the {@link org.beryx.textio.TextTerminal}.
     *
     * @param paneTitle the title of the terminal pane
     */
    private void initProperties(final String paneTitle) {
        textTerminal.setUserInterruptKey(QUIT_SHORTCUT);
        textTerminal.setPaneTitle(paneTitle);
        final TerminalProperties<SwingTextTerminal> properties = getProperties();
        properties.setPromptColor(WHITE);
        properties.setInputBold(true);
        properties.setInputColor(ORANGE);
        properties.setPaneDimension(1300, 1000);
    }

    private TerminalProperties<SwingTextTerminal> getProperties() {
        return textTerminal.getProperties();
    }

    /**
     * Prints the given message to the console without any line break.
     *
     * @param message the message to print
     */
    public void print(final String message) {
        textTerminal.print(message);
    }

    /**
     * Prints the given message to the console and inserts a line break afterwards.
     *
     * @param message the message to print
     */
    public void printLine(final String message) {
        textTerminal.println(message);
    }

    /**
     * Prints an empty line to the console.
     */
    public void printEmptyLine() {
        printLine("");
    }

    /**
     * Reads an {@link Integer} from the console.
     *
     * @param prompt   the prompt to display
     * @param minValue the minimum value that is required
     * @param maxValue the maximum value that is allowed
     * @return the {@link Integer} that was read from the console
     */
    public Integer readInteger(final String prompt, final int minValue, final int maxValue) {
        return textIO.newIntInputReader()
                .withMinVal(minValue)
                .withMaxVal(maxValue)
                .read(prompt);
    }

    /**
     * Reads an EnumClass from the console.
     *
     * @param prompt    the prompt to display
     * @param <E>       the {@link Enum} class
     * @param enumClass the {@link Enum} class to read
     * @return the {@link Enum} that was read from the console
     */
    public <E extends Enum<E>> E readEnum(final String prompt, final Class<E> enumClass) {
        return textIO.newEnumInputReader(enumClass)
                .read(prompt);
    }

    /**
     * Disposes the {@link TextIO} and {@link org.beryx.textio.TextTerminal} instances.
     */
    public void close() {
        textIO.dispose();
        textTerminal.dispose();
    }

    /**
     * Changes the background color of the console to indicate that a {@link Player} has won. Celebration time!
     *
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    public void celebrate() throws InterruptedException {
        for (int i = 0; i < 10; i++) {
            colorChange();
        }
        getProperties().setPaneBackgroundColor(BLACK);
    }

    /**
     * Changes the color of the pane in a sequence of colors.
     *
     * @throws InterruptedException if the thread is interrupted while sleeping
     */
    private void colorChange() throws InterruptedException {
        changePaneColor(GREEN);
        changePaneColor(RED);
        changePaneColor(BLUE);
        changePaneColor(YELLOW);
        changePaneColor(CYAN);
        changePaneColor(PINK);
    }

    /**
     * Changes the background color of the pane to the specified color.
     *
     * @param color the color to set the pane's background color to
     * @throws InterruptedException if the current thread is interrupted while sleeping
     */
    private void changePaneColor(final Color color) throws InterruptedException {
        getProperties().setPaneBackgroundColor(color);
        sleep(75);
    }

}
