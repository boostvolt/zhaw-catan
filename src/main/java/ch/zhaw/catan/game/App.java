package ch.zhaw.catan.game;

import ch.zhaw.catan.game.Config.Structure;
import ch.zhaw.catan.structure.City;
import ch.zhaw.catan.structure.Road;
import ch.zhaw.catan.structure.Settlement;

import java.awt.Point;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Random;

import static ch.zhaw.catan.board.SiedlerBoard.MAX_X_COORDINATE;
import static ch.zhaw.catan.board.SiedlerBoard.MAX_Y_COORDINATE;
import static ch.zhaw.catan.board.SiedlerBoard.MIN_COORDINATE;
import static ch.zhaw.catan.game.Config.Faction;
import static ch.zhaw.catan.game.Config.MAX_CARDS_IN_HAND_NO_DROP;
import static ch.zhaw.catan.game.Config.MIN_NUMBER_OF_PLAYERS;
import static ch.zhaw.catan.game.Config.Resource;
import static ch.zhaw.catan.game.Console.QUIT_SHORTCUT;
import static ch.zhaw.catan.game.SiedlerGame.FOUR_TO_ONE_TRADE_OFFER;
import static ch.zhaw.catan.game.SiedlerGame.FOUR_TO_ONE_TRADE_WANT;
import static java.lang.String.format;
import static java.lang.Thread.sleep;
import static java.util.EnumSet.allOf;

/**
 * App class to start a new game of Siedler. It contains the main game flow.
 */
public class App {

    static final int REQUIRED_WINNING_SCORE = 7;
    static final Random RANDOM = new Random();

    private static final String SETTLEMENT_NAME = Settlement.class.getSimpleName();
    private static final String CITY_NAME = City.class.getSimpleName();
    private static final String ROAD_NAME = Road.class.getSimpleName();
    private static final String THIEF_NAME = "Thief";

    private final Console console;
    private final Dice dice;

    private SiedlerGame game;

    /**
     * Constructs a new App object.
     */
    public App() {
        console = new Console("Catan");
        dice = new Dice();
    }

    /**
     * Main method to start a new game of Siedler.
     *
     * @param args command line arguments
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    public static void main(final String[] args) throws InterruptedException {
        new App().runGame();
    }

    /**
     * Runs the game of Siedler.
     *
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    public void runGame() throws InterruptedException {
        printIntro();

        final int numberOfPlayers = console.readInteger("Please enter the number of players", MIN_NUMBER_OF_PLAYERS, allOf(Faction.class).size());
        game = new SiedlerGame(REQUIRED_WINNING_SCORE, numberOfPlayers);

        printBoardView();
        runInitiationPhase(numberOfPlayers);
        runGameTurns();
    }

    /**
     * Prints the introduction message for the game.
     */
    private void printIntro() {
        console.printLine("Welcome to a new game of Settlers of Catan!");
        console.print("You can exit the game anytime by pressing the key " + QUIT_SHORTCUT + ".");
        console.printEmptyLine();
    }

    /**
     * This method prints the current board view to the console.
     */
    private void printBoardView() {
        console.printLine(game.getBoard().getView().toString());
    }

    /**
     * Runs the initiation phase of the game, where players place their initial {@link Settlement}s and {@link Road}s.
     *
     * @param numberOfPlayers the number of {@link Player}s in the game
     */
    private void runInitiationPhase(final int numberOfPlayers) {
        for (int i = 0; i < numberOfPlayers; i++) {
            placeInitialStructures(false);
            game.switchToNextPlayer();
        }

        for (int i = 0; i < numberOfPlayers; i++) {
            game.switchToPreviousPlayer();
            placeInitialStructures(true);
        }

        console.printLine("Initiation phase ended. Let the game begin!");
        console.printEmptyLine();
    }

    /**
     * Places a {@link Settlement} and a {@link Road} for the current {@link Player}.
     *
     * @param withPayout if true, the {@link Player} will receive {@link Resource}s for placing the {@link Structure}s
     */
    private void placeInitialStructures(final boolean withPayout) {
        placeInitialSettlement(withPayout);
        placeInitialRoad(withPayout);
    }

    /**
     * Lets the current {@link Player} place an initial {@link Settlement} and handles the placement.
     *
     * @param withPayout if true, the {@link Player} will receive {@link Resource}s for placing the {@link Settlement}
     */
    private void placeInitialSettlement(final boolean withPayout) {
        console.printLine(game.getCurrentPlayerFaction() + " please place your " + (withPayout ? "second " : "first ") + SETTLEMENT_NAME + ".");
        boolean isPlaced = false;
        while (!isPlaced) {
            final Point point = readCoordinates("for your " + SETTLEMENT_NAME);
            if (game.placeInitialSettlement(point, withPayout)) {
                isPlaced = true;
                printBoardView();
                printValidPlacement(SETTLEMENT_NAME);
            } else {
                printInvalidPlacement(SETTLEMENT_NAME);
            }
        }
    }

    /**
     * Lets the current {@link Player} place an initial {@link Road} and handles the placement.
     *
     * @param isSecondRoad if true, the print message will be adapted to the second {@link Road}
     */
    private void placeInitialRoad(final boolean isSecondRoad) {
        console.printLine(game.getCurrentPlayerFaction() + " please place your " + (isSecondRoad ? "second " : "first ") + ROAD_NAME + ".");
        boolean isPlaced = false;
        while (!isPlaced) {
            final Point startPoint = readCoordinates("for the " + ROAD_NAME + " start point");
            final Point endPoint = readCoordinates("for the " + ROAD_NAME + " end point");

            if (game.placeInitialRoad(startPoint, endPoint)) {
                isPlaced = true;
                printBoardView();
                printValidPlacement(ROAD_NAME);
            } else {
                printInvalidPlacement(ROAD_NAME);
            }
        }
    }

    /**
     * Reads a coordinate from the user and validates it.
     *
     * @param prompt the type of {@link Structure} ({@link Settlement}, {@link City}, or {@link Road})
     * @return the coordinate of the {@link Structure}
     */
    private Point readCoordinates(final String prompt) {
        final int xCoordinate = console.readInteger(game.getCurrentPlayerFaction() + " please enter the x-coordinate "
                + prompt, MIN_COORDINATE, MAX_X_COORDINATE);
        final int yCoordinate = console.readInteger(game.getCurrentPlayerFaction() + " please enter the y-coordinate "
                + prompt, MIN_COORDINATE, MAX_Y_COORDINATE);
        return new Point(xCoordinate, yCoordinate);
    }

    /**
     * Runs the game turns, where {@link Player}s take their actions in turn until the game ends.
     *
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    private void runGameTurns() throws InterruptedException {
        boolean isGameFinished = false;
        processDiceRoll(dice.roll());
        while (!isGameFinished) {
            console.printEmptyLine();
            final Activity chosenActivity = console.readEnum(game.getCurrentPlayerFaction()
                    + " choose any of the following activities", Activity.class);
            console.printEmptyLine();
            switch (chosenActivity) {
                case DISPLAY_BOARD -> printBoardView();
                case DISPLAY_PLAYER_RESOURCES -> printResourcesOfCurrentPlayer();
                case DISPLAY_BANK_RESOURCES -> printResourcesOfBank();
                case DISPLAY_STRUCTURE_COSTS -> printStructureCosts();
                case DISPLAY_SCORES -> printScores();
                case TRADE -> trade();
                case BUILD_ROAD -> buildRoad();
                case BUILD_SETTLEMENT -> isGameFinished = buildSettlement();
                case BUILD_CITY -> isGameFinished = buildCity();
                case NEXT_TURN -> {
                    game.switchToNextPlayer();
                    printBoardView();
                    processDiceRoll(dice.roll());
                }
                case QUIT -> {
                    console.printLine("Thanks for playing, see you next game!");
                    sleep(3000);
                    console.close();
                    isGameFinished = true;
                }
            }
        }
    }

    /**
     * Processes a dice roll and updates the game state accordingly.
     *
     * @param diceRoll the result of the dice roll
     */
    private void processDiceRoll(final int diceRoll) {
        console.printLine(game.getCurrentPlayerFaction() + " rolled " + diceRoll);
        final DiceResult diceResult = game.processDiceRoll(diceRoll);
        if (diceResult.isThiefExecuted()) {
            console.printEmptyLine();
            console.printLine("The " + THIEF_NAME + " was rolled. Resources of any player who has more than "
                    + MAX_CARDS_IN_HAND_NO_DROP + " were cut in half.");
            placeThiefAndStealCard();
        }
        printDiceResultResources(diceResult);
    }

    /**
     * Lets the current {@link Player} place the thief on the board and steal a {@link Resource} card from another {@link Player}.
     */
    private void placeThiefAndStealCard() {
        boolean isPlaced = false;
        while (!isPlaced) {
            final Point point = readCoordinates("for the new location of the " + THIEF_NAME);
            if (game.placeThiefAndStealCard(point)) {
                isPlaced = true;
                printValidPlacement(THIEF_NAME);
            } else {
                printInvalidPlacement(THIEF_NAME);
            }
        }
    }

    /**
     * Prints the {@link Resource}s received or lost by each {@link Player} as a result of the given dice roll.
     *
     * @param diceResult the result of the dice roll
     */
    private void printDiceResultResources(final DiceResult diceResult) {
        boolean firstLine = true;

        for (Map.Entry<Faction, Map<Resource, Integer>> resourcesPerFaction : diceResult.getAffectedResources().entrySet()) {
            if (resourcesPerFaction.getValue() != null && !resourcesPerFaction.getValue().isEmpty()) {
                if (firstLine) {
                    console.printEmptyLine();
                    firstLine = false;
                }

                console.print(resourcesPerFaction.getKey() + " has " + (diceResult.isThiefExecuted() ? "lost" : "received") + " the following resources: ");
                printAllResources(resourcesPerFaction.getValue());
                console.printEmptyLine();
            }
        }
    }

    /**
     * Prints the amount of each {@link Resource} in the given {@link Map}.
     *
     * @param amountPerResources a {@link Map} of {@link Resource}s and their corresponding amount
     */
    private void printAllResources(final Map<Resource, Integer> amountPerResources) {
        boolean firstLine = true;

        for (Map.Entry<Resource, Integer> resource : amountPerResources.entrySet()) {
            console.print((firstLine ? "" : ", ") + resource.getValue() + " " + resource.getKey().name());
            if (firstLine) {
                firstLine = false;
            }
        }
    }

    /**
     * Prints the {@link Resource}s of the current {@link Player}.
     */
    private void printResourcesOfCurrentPlayer() {
        console.print(game.getCurrentPlayer().getFaction() + " possesses: ");
        final Map<Resource, Integer> inventory = game.getCurrentPlayer().getInventory();
        printAllResources(inventory);
        console.printEmptyLine();
    }

    /**
     * Prints the {@link Resource}s of the {@link Bank}.
     */
    private void printResourcesOfBank() {
        console.print("Bank possesses: ");
        printAllResources(game.getBank().getInventory());
        console.printEmptyLine();
    }

    /**
     * Prints the costs of all {@link Structure}s.
     */
    private void printStructureCosts() {
        console.printLine("Structure costs:");
        for (Structure structure : allOf(Structure.class)) {
            console.print(structure.name() + " requires: ");
            printAllResources(structure.getCostsAsIntegerMap());
            console.printEmptyLine();
        }
    }

    /**
     * Prints the scores of all {@link Player}s.
     */
    private void printScores() {
        final List<Map.Entry<Faction, Integer>> sortedFactionScores = new ArrayList<>();

        for (Player player : game.getCurrentPlayers()) {
            sortedFactionScores.add(new AbstractMap.SimpleEntry<>(player.getFaction(), player.getScore()));
        }

        sortedFactionScores.sort(Map.Entry.comparingByValue(Comparator.reverseOrder()));

        console.printLine("Scores:");
        for (Map.Entry<Faction, Integer> factionScore : sortedFactionScores) {
            console.printLine(factionScore.getKey() + " with " + factionScore.getValue() + " points");
        }
    }

    /**
     * Lets the current {@link Player} trade with the {@link Bank}.
     */
    private void trade() {
        final Resource offer = console.readEnum(game.getCurrentPlayerFaction()
                + " please choose any of the following resources for a " + FOUR_TO_ONE_TRADE_OFFER + ":" + FOUR_TO_ONE_TRADE_WANT
                + " trade with the bank", Resource.class);
        console.printEmptyLine();
        final Resource requisition = console.readEnum("Which resource would you like from the bank?", Resource.class);
        console.printEmptyLine();
        if (game.tradeWithBankFourToOne(offer, requisition)) {
            console.printLine("Trade was successful. " + FOUR_TO_ONE_TRADE_WANT + " " + requisition.name() + " has been added to your inventory.");
        } else {
            console.printLine("Trade failed. Either you or the bank have insufficient resources.");
        }
    }

    /**
     * Lets the current {@link Player} build a {@link Settlement}.
     *
     * @return boolean to indicate if the current {@link Player} has won the game
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    private boolean buildSettlement() throws InterruptedException {
        if (game.buildSettlement(readCoordinates("for the " + SETTLEMENT_NAME))) {
            printBoardView();
            printValidPlacement(SETTLEMENT_NAME);
            return hasPlayerWon();
        } else {
            printInvalidPlacement(SETTLEMENT_NAME);
            return false;
        }
    }

    /**
     * Lets the current {@link Player} build a {@link Road}.
     */
    private void buildRoad() {
        if (game.buildRoad(readCoordinates("for start point of " + ROAD_NAME),
                readCoordinates("for end point of " + ROAD_NAME))) {
            printBoardView();
            printValidPlacement(ROAD_NAME);
        } else {
            printInvalidPlacement(ROAD_NAME);
        }
    }

    /**
     * Lets the current {@link Player} build a {@link City}.
     *
     * @return boolean to indicate if the current {@link Player} has won the game
     * @throws InterruptedException if thread is interrupted while sleeping
     */
    private boolean buildCity() throws InterruptedException {
        if (game.buildCity(readCoordinates("for the " + CITY_NAME))) {
            printBoardView();
            printValidPlacement(CITY_NAME);
            return hasPlayerWon();
        } else {
            printInvalidPlacement(CITY_NAME);
            return false;
        }
    }

    /**
     * Checks if the current {@link Player} has won the game. If the {@link Player} has won, a message is printed.
     *
     * @return true if the current {@link Player} has won the game, false otherwise
     */
    private boolean hasPlayerWon() throws InterruptedException {
        final Faction winner = game.getWinner();
        if (winner != null) {
            console.printLine(winner + " has won the game. Congratulations!");
            console.print("You can exit the game by pressing the key " + QUIT_SHORTCUT + ".");
            console.celebrate();
            return true;
        } else {
            return false;
        }
    }

    /**
     * Prints a message indicating that the given type of {@link Structure} has been placed.
     *
     * @param type the type of {@link Structure} that has been placed
     */
    private void printValidPlacement(final String type) {
        console.printLine(String.format("%s has been placed.", type));
    }

    /**
     * Prints a message indicating that the given type of object could not be placed.
     *
     * @param type the type of object that could not be placed
     */
    private void printInvalidPlacement(final String type) {
        console.printLine(format("Not able to place %s.", type));
        console.printEmptyLine();
    }

}
