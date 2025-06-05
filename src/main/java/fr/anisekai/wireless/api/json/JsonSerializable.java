package fr.anisekai.wireless.api.json;

/**
 * Interface allowing any object to be converted to {@link AnisekaiJson}.
 */
public interface JsonSerializable {

    /**
     * Convert this instance into {@link AnisekaiJson}.
     *
     * @return An {@link AnisekaiJson}
     */
    AnisekaiJson toJson();

}
