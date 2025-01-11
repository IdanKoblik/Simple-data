package dev.idank.data;

import java.util.List;

/**
 * A singleton utility class for managing and validating collection names.
 * This class provides a centralized way to handle and verify supported collection names.
 */
public class CollectionNameHelper {

    /**
     * The single instance of the CollectionNameHelper class.
     */
    private static CollectionNameHelper instance;

    /**
     * Retrieves the singleton instance of the CollectionNameHelper.
     *
     * @return The single instance of CollectionNameHelper.
     */
    public static CollectionNameHelper getInstance() {
        return instance == null ? (instance = new CollectionNameHelper()) : instance;
    }

    /**
     * The collection name for games.
     */
    public static final String GAME = "game";

    /**
     * The collection name for game player statistics.
     */
    public static final String GAME_STATS = "game-stats";

    /**
     * The collection name for pre game load data (min_players, max_players, etc...)
     */
    public static final String GAME_DATA = "game-data";

    /**
     * A list of allowed collection names.
     */
    private final List<String> allowedCollections = List.of(
            GAME,
            GAME_STATS
    );

    /**
     * Checks if the given collection name is supported.
     *
     * @param collectionName The name of the collection to check.
     * @return {@code true} if the collection name is supported; {@code false} otherwise.
     */
    public boolean isSupported(String collectionName) {
        return this.allowedCollections.stream().anyMatch(collectionName::equals);
    }
}
