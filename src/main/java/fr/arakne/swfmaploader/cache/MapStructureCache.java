package fr.arakne.swfmaploader.cache;

import fr.arakne.swfmaploader.swf.SwfMapStructure;

import java.util.Optional;

/**
 * Cache system for map structure
 */
public interface MapStructureCache {
    /**
     * Retrieve map data from the cache
     * If the map is not on cache, an empty optional is returned
     *
     * @param id The map id
     *
     * @return The stored structure
     */
    public Optional<SwfMapStructure> retrieve(int id);

    /**
     * Store a map into the cache
     *
     * @param structure Map to store
     */
    public void store(SwfMapStructure structure);

    /**
     * Clear the cache
     */
    public void clear();
}
