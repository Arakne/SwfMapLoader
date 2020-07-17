package fr.arakne.swfmaploader;

import fr.arakne.swfmaploader.cache.MapStructureCache;
import fr.arakne.swfmaploader.map.MapFactory;
import fr.arakne.swfmaploader.swf.SwfFileLoader;
import fr.arakne.swfmaploader.swf.SwfMapStructure;
import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.serializer.DefaultMapDataSerializer;
import fr.arakne.utils.maps.serializer.MapDataSerializer;

import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Loader for SWF Dofus maps
 *
 * @param <C> The cell type
 * @param <M> The map type
 */
final public class MapLoader<C extends MapCell, M extends DofusMap<C>> {
    final private SwfFileLoader loader;
    final private DefaultMapDataSerializer serializer;
    final private MapStructureCache cache;
    final private MapFactory<C, M> factory;

    /**
     * @param loader The swf file loader
     * @param serializer Map date serializer
     * @param cache Cache system
     * @param factory The map factory
     */
    public MapLoader(SwfFileLoader loader, DefaultMapDataSerializer serializer, MapStructureCache cache, MapFactory<C, M> factory) {
        this.loader = loader;
        this.serializer = serializer;
        this.cache = cache;
        this.factory = factory;
    }

    /**
     * Load a map from CDN
     *
     * @param id The map id
     * @param version The map version string
     * @param key The encryption key. null is the map is not encrypted
     *
     * @return The map instance
     *
     * @throws RuntimeException If the map cannot be loaded or is not found
     */
    public M load(int id, String version, String key) {
        final SwfMapStructure structure = cache.retrieve(id)
            .filter(s -> s.version().equals(version))
            .orElseGet(() -> {
                try {
                    SwfMapStructure loaded = loader.load(id, version, key);
                    cache.store(loaded);
                    return loaded;
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();

                    throw new RuntimeException(e);
                } catch (IOException e) {
                    throw new RuntimeException("Cannot load map " + id, e);
                }
            })
        ;

        return createMap(structure, key);
    }

    /**
     * Load a map from SWF file, by URL
     * The cache is not used by the method : the file is always downloaded (is needed) and parsed
     *
     * @param url The file URL
     * @param key The encryption key. null is the map is not encrypted
     *
     * @return The map instance
     *
     * @throws IOException When cannot load the map from the URL
     * @throws InterruptedException When an interruption occurs during loading the SWF file
     * @throws IllegalArgumentException When an invalid map file is loaded
     */
    public M loadFromUrl(URL url, String key) throws IOException, InterruptedException {
        final SwfMapStructure structure = loader.load(url);

        return createMap(structure, key);
    }

    /**
     * Get the serializer for the given key
     *
     * @param key Key to use
     *
     * @return The serializer
     */
    private MapDataSerializer serializer(String key) {
        return key == null || key.isEmpty() ? serializer : serializer.withKey(key);
    }

    /**
     * Create the map instance
     *
     * @param structure The structure
     * @param key The encryption key
     *
     * @return The map instance
     */
    private M createMap(SwfMapStructure structure, String key) {
        return factory.createMap(structure, serializer(key).deserialize(structure.mapData()));
    }
}
