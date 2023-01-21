package ch.zhaw.catan.game;

import ch.zhaw.catan.game.Config.Resource;

import java.util.EnumMap;
import java.util.Map;

import static java.util.Map.entry;
import static java.util.Objects.requireNonNull;

/**
 * InventoryOwner super Class to hold Inventory of {@link Resource}s in an {@link EnumMap}
 * The Inventory either can be initiated empty or with a given amount of starting resources.
 * The class also provides functionality to increase or decrease the Inventory.
 */
public abstract class InventoryOwner {

    private static final Integer NO_RESOURCES = 0;
    private final Map<Resource, Integer> inventory;

    /**
     * Default constructor which fills all {@link Resource}s using the default amount {@link #NO_RESOURCES}
     */
    protected InventoryOwner() {
        inventory = new EnumMap<>(Resource.class);

        for (Resource resource : Resource.values()) {
            inventory.put(resource, NO_RESOURCES);
        }
    }

    /**
     * Constructor of Inventory with a given Inventory List prepared.
     *
     * @param inventory {@link EnumMap} of {@link Resource} and {@link Integer} values
     */
    protected InventoryOwner(final Map<Resource, Integer> inventory) {
        this.inventory = requireNonNull(inventory, "inventory must not be null");
    }

    /**
     * Getter for the current Inventory
     *
     * @return inventory of Owner as {@link EnumMap}
     */
    public Map<Resource, Integer> getInventory() {
        return inventory;
    }

    /**
     * Gives the amount for a specific {@link Resource} which is being held.
     *
     * @param resource the {@link Resource} to get the amount from
     * @return the amount of said {@link Resource}
     */
    public int getAmountOfResource(final Resource resource) {
        return inventory.get(resource);
    }

    /**
     * Gives the amount of all {@link Resource}s which are being held.
     *
     * @return the amount of all {@link Resource}s
     */
    public int getTotalAmountOfResources() {
        int sum = 0;
        for (Integer amountPerResource : getInventory().values()) {
            sum += amountPerResource;
        }
        return sum;
    }

    /**
     * Function to increase an Inventory.
     * Receives a {@link Map} of {@link Resource} with their respective amount to increment
     *
     * @param resourcesToAdd {@link Map} of {@link Resource} Objects and {@link Integer}
     */
    public void increaseInventory(final Map<Resource, Integer> resourcesToAdd) {
        for (Map.Entry<Resource, Integer> resourceToAdd : resourcesToAdd.entrySet()) {
            increaseInventoryItem(resourceToAdd.getKey(), resourceToAdd.getValue());
        }
    }

    /**
     * Function to Increase the inventory by a single {@link Resource} only.
     * Receives the {@link Resource} and {@link Integer} with a positive amount to increment.
     *
     * @param resource {@link Resource}
     * @param amount   Amount of the {@link Resource} that should be added.
     */
    public void increaseInventoryItem(final Resource resource, final Integer amount) {
        if (isValidIncrease(amount)) {
            inventory.put(resource, inventory.get(resource) + amount);
        }
    }

    /**
     * Function to decrease an Inventory.
     * Receives a {@link Map} of {@link Resource} with their respective amount to decrement
     * Checks first if the decrease is valid based on the current inventory.
     *
     * @param resourcesToRemove {@link Map} of {@link Resource} Objects and {@link Integer}
     * @return returns {@code true} if the change was valid and could be made.
     */
    public boolean decreaseInventoryIfApplicable(final Map<Resource, Integer> resourcesToRemove) {
        for (Map.Entry<Resource, Integer> resourceToRemove : resourcesToRemove.entrySet()) {
            if (!isValidDecrease(resourceToRemove.getKey(), resourceToRemove.getValue())) {
                return false;
            }
        }

        for (Map.Entry<Resource, Integer> resourceToRemove : resourcesToRemove.entrySet()) {
            decreaseInventoryItemIfApplicable(resourceToRemove.getKey(), resourceToRemove.getValue());
        }

        return true;
    }

    /**
     * Function to decrease the inventory by a single {@link Resource} only.
     * Receives the {@link Resource} and {@link Integer} with a positive amount to decrement.
     *
     * @param resource {@link Resource}
     * @param amount   Amount of the {@link Resource} that should be removed.
     * @return Map.Entry with {@link Resource} and {@link Integer} or null if nothing could be decreased.
     */
    public Map.Entry<Resource, Integer> decreaseInventoryItemIfApplicable(final Resource resource,
                                                                          final Integer amount) {
        if (isValidDecrease(resource, amount)) {
            inventory.put(resource, inventory.get(resource) - amount);
            return entry(resource, amount);
        }

        return null;
    }

    /**
     * Checks whether an amount is a valid increase.
     *
     * @param amount the amount to check
     * @return true if the amount is greater than zero, false otherwise
     */
    private boolean isValidIncrease(final Integer amount) {
        return amount > 0;
    }

    /**
     * Checks whether a decrease in a given {@link Resource} is valid.
     *
     * @param resource the {@link Resource} to check
     * @param amount   the amount of the {@link Resource} to decrease
     * @return true if the decrease is valid, false otherwise
     */
    private boolean isValidDecrease(final Resource resource, final Integer amount) {
        return amount > 0 && getAmountOfResource(resource) >= amount;
    }

}
