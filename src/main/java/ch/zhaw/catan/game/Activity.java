package ch.zhaw.catan.game;

/**
 * Represents all available in-game activities.
 */

public enum Activity {

    /**
     * Displays the current state of the {@link ch.zhaw.catan.board.SiedlerBoard}.
     */
    DISPLAY_BOARD,

    /**
     * Displays the {@link ch.zhaw.catan.game.Config.Resource} of the current {@link Player}.
     */
    DISPLAY_PLAYER_RESOURCES,

    /**
     * Displays the costs for all the structures defined in {@link ch.zhaw.catan.game.Config.Structure}
     */
    DISPLAY_BANK_RESOURCES,

    /**
     * Displays the costs for all the structures defined in {@link ch.zhaw.catan.game.Config.Structure}
     */
    DISPLAY_STRUCTURE_COSTS,

    /**
     * Displays the scores of all participating {@link Player}s
     */
    DISPLAY_SCORES,

    /**
     * Trades {@link ch.zhaw.catan.game.Config.Resource} from current {@link Player} with {@link Bank}, with the ratio 4:1
     */
    TRADE,

    /**
     * Builds a {@link ch.zhaw.catan.structure.Road} between two corners.
     */
    BUILD_ROAD,

    /**
     * Builds a {@link ch.zhaw.catan.structure.Settlement} on a corner.
     */
    BUILD_SETTLEMENT,

    /**
     *Replaces a {@link ch.zhaw.catan.structure.Settlement} with a {@link ch.zhaw.catan.structure.City}.
     */
    BUILD_CITY,

    /**
     *Swaps to the next {@link Player} and starts a new turn.
     */
    NEXT_TURN,

    /**
     * Ends the Game.
     */
    QUIT

}
