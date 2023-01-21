package ch.zhaw.catan.game;

import ch.zhaw.catan.game.Config.Resource;
import ch.zhaw.catan.structure.Structure;

import java.util.EnumMap;
import java.util.List;

import static ch.zhaw.catan.game.Config.INITIAL_RESOURCE_CARDS_BANK;

/**
 * Represents a bank that provides resources to the {@link Player}s. It also tracks its own inventory.
 */
public class Bank extends InventoryOwner {

    /**
     * Constructs a new bank with the given initial {@link Resource} cards.
     */
    Bank() {
        super(new EnumMap<>(INITIAL_RESOURCE_CARDS_BANK));
    }

    /**
     * Determines whether the bank has sufficient inventory for the payout of a given {@link Resource} and {@link List} of {@link Structure}s.
     *
     * @param resource   the {@link Resource} to check for
     * @param structures the {@link List} of {@link Structure}s to consider
     * @return true if the bank has sufficient inventory, false otherwise
     */
    public boolean isInventorySufficientForPayoutOfResource(final Resource resource, final List<Structure> structures) {
        int availableBankResourceCards = getAmountOfResource(resource);

        for (Structure structure : structures) {
            availableBankResourceCards -= structure.getAmountPerResource();
        }

        return availableBankResourceCards >= 0;
    }

}

