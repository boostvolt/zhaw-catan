package ch.zhaw.catan.game;

import ch.zhaw.catan.game.Config.Faction;
import ch.zhaw.catan.game.Config.Resource;
import ch.zhaw.catan.structure.Structure;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static ch.zhaw.catan.game.App.RANDOM;
import static ch.zhaw.catan.game.Config.MAX_CARDS_IN_HAND_NO_DROP;
import static java.util.Objects.requireNonNull;

/**
 * Class for the players playing the game.
 * The class holds information about score
 * and gets the {@link Resource} inventory functionality from the {@link InventoryOwner} Class
 */
public class Player extends InventoryOwner {

    private final Faction faction;
    private final List<Structure> structures;

    /**
     * Constructor creates new Player Object with the default amount of {@link Resource}s.
     * As well as the given {@link Faction}
     *
     * @param faction {@link Faction} that the player is assigned for the game.
     */
    public Player(final Faction faction) {
        super();
        this.faction = requireNonNull(faction, "faction must not be null");
        structures = new ArrayList<>();
    }

    /**
     * Getter for the {@link Faction} of the player
     *
     * @return the players {@link Faction}
     */
    public Faction getFaction() {
        return faction;
    }

    /**
     * Returns a list of all {@link Structure}s in this player's possession.
     *
     * @return a {@link List} of all {@link Structure}s in this player's possession
     */
    public List<Structure> getStructures() {
        return structures;
    }

    /**
     * Adds the given {@link Structure} to this player's possession.
     *
     * @param structure the {@link Structure} to add
     */
    public void addStructure(final Structure structure) {
        structures.add(structure);
    }

    /**
     * Removes the given {@link Structure} from this player's possession.
     *
     * @param structure the {@link Structure} to remove
     */
    public void removeStructure(final Structure structure) {
        structures.remove(structure);
    }

    /**
     * Returns true if this player has not reached the maximum stock of the given {@link Structure} type.
     *
     * @param structureType the type of {@link Structure} to check
     * @return true if this player has not reached the maximum stock of the given {@link Structure} type, false otherwise
     */
    public boolean hasNotReachedMaxStockOf(final Config.Structure structureType) {
        return getCurrentAmountOfStructureType(structureType) < structureType.getStockPerPlayer();
    }

    /**
     * Processes a thief's action, which involves stealing half of the total number of {@link Resource}s
     * from the player's inventory (rounded down).
     *
     * @return a {@link Map} of {@link Resource}s and their corresponding quantities that were stolen
     */
    public Map<Resource, Integer> processThief() {
        final int totalResources = getTotalAmountOfResources();
        final Map<Resource, Integer> stolenResources = new EnumMap<>(Resource.class);

        if (totalResources > MAX_CARDS_IN_HAND_NO_DROP) {
            final List<Resource> nonZeroResources = getNonZeroResources();
            int i = 0;
            while (i < totalResources / 2) {
                final int resourceIndex = RANDOM.nextInt(nonZeroResources.size());
                final Entry<Resource, Integer> stolenResource = decreaseInventoryItemIfApplicable(nonZeroResources.get(resourceIndex), 1);
                if (stolenResource != null) {
                    stolenResources.merge(stolenResource.getKey(), 1, Integer::sum);
                    i++;
                } else {
                    nonZeroResources.remove(resourceIndex);
                }
            }
        }
        return stolenResources;
    }

    /**
     * Returns a list of non-zero {@link Resource}s from the player's inventory.
     *
     * @return a list of non-zero {@link Resource}s from the player's inventory
     */
    private List<Resource> getNonZeroResources() {
        final List<Resource> resources = new ArrayList<>();
        for (Resource resource : getInventory().keySet()) {
            if (getAmountOfResource(resource) > 0) {
                resources.add(resource);
            }
        }
        return resources;
    }

    /**
     * Steals a random {@link Resource} from one of the provided nearby players.
     *
     * @param nearbyPlayersToStealFrom a {@link List} of nearby players to steal from
     */
    public void stealRandomResourceFrom(final List<Player> nearbyPlayersToStealFrom) {
        if (!nearbyPlayersToStealFrom.isEmpty()) {
            final Player playerToStealFrom = nearbyPlayersToStealFrom.get(RANDOM.nextInt(nearbyPlayersToStealFrom.size()));
            final List<Resource> resourcesToStealFrom = playerToStealFrom.getNonZeroResources();
            final Resource resourceToSteal = resourcesToStealFrom.get(RANDOM.nextInt(resourcesToStealFrom.size()));
            playerToStealFrom.decreaseInventoryItemIfApplicable(resourceToSteal, 1);
            this.increaseInventoryItem(resourceToSteal, 1);
        }
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
     * Returns the current amount of a specific {@link Structure} type owned by the player.
     *
     * @param structureType the type of {@link Structure} to check
     * @return the current amount of the specified {@link Structure} type
     */
    private int getCurrentAmountOfStructureType(final Config.Structure structureType) {
        int amountOfStructure = 0;

        for (Structure existingStructure : getStructures()) {
            if (existingStructure.getStructureType().equals(structureType)) {
                amountOfStructure++;
            }
        }

        return amountOfStructure;
    }

    /**
     * Returns the current score for this player.
     *
     * @return the current score
     */
    public int getScore() {
        int score = 0;

        for (final Structure structure : getStructures()) {
            score += structure.getScore();
        }

        return score;
    }

}
