package ch.zhaw.catan.game;

import ch.zhaw.catan.game.Config.Faction;
import ch.zhaw.catan.game.Config.Resource;

import java.util.Map;

import static java.util.Objects.requireNonNull;

/**
 * Represents the result of a dice roll. It indicates the affected {@link Resource}s per {@link Faction} by the dice roll and whether
 * the thief was executed.
 */
public class DiceResult {

    private final Map<Faction, Map<Resource, Integer>> affectedResources;
    private final boolean thiefExecuted;

    /**
     * Creates a new `DiceResult` with the given affected {@link Resource}s and thief execution status.
     *
     * @param affectedResources the {@link Resource}s that were affected by this dice roll
     * @param thiefExecuted     whether the thief was executed as a result of this dice roll
     */
    public DiceResult(final Map<Faction, Map<Resource, Integer>> affectedResources,
                      final boolean thiefExecuted) {
        this.affectedResources = requireNonNull(affectedResources, "affectedResources must not be null");
        this.thiefExecuted = thiefExecuted;
    }

    /**
     * Returns the {@link Resource}s that were affected by this dice roll.
     *
     * @return the affected {@link Resource}s
     */
    public Map<Faction, Map<Resource, Integer>> getAffectedResources() {
        return affectedResources;
    }

    /**
     * Returns whether the thief was executed as a result of this dice roll.
     *
     * @return whether the thief was executed
     */
    public boolean isThiefExecuted() {
        return thiefExecuted;
    }

}
