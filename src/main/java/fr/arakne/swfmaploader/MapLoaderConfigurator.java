package fr.arakne.swfmaploader;

import fr.arakne.swfmaploader.cache.MapStructureCache;
import fr.arakne.swfmaploader.cache.NullMapStructureCache;
import fr.arakne.swfmaploader.cache.SqlMapStructureCache;
import fr.arakne.swfmaploader.map.MapFactory;
import fr.arakne.swfmaploader.map.SimpleMapFactory;
import fr.arakne.swfmaploader.swf.SwfFileLoader;
import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.serializer.DefaultMapDataSerializer;

import java.sql.SQLException;

/**
 * Utility class for configure and create a {@link MapLoader}
 *
 * <code>
 *     MapLoaderConfigurator configurator = new MapLoaderConfigurator();
 *
 *     MapLoader loader = configurator
 *         .baseUrl("http://my-server.com/dofus/swf/maps")
 *         .tempDir("/tmp/my-client")
 *         .cacheFile("map-cache.sqlite")
 *         .factory(new MyCustomMapFactory())
 *         .create()
 *    ;
 *
 *    MyMap map = loader.load(gmd.id(), gmd.version(), gmd.key());
 * </code>
 *
 * @param <C> The cell type
 * @param <M> The map type
 */
final public class MapLoaderConfigurator<C extends MapCell, M extends DofusMap<C>> {
    private MapStructureCache cache;
    private DefaultMapDataSerializer serializer;
    private MapFactory<C, M> factory;
    private SwfFileLoader loader;

    private String tempDir = "./tmp";
    private String baseUrl = "file:./data/maps";

    /**
     * Define the cache system to use
     */
    public MapLoaderConfigurator<C, M> cache(MapStructureCache cache) {
        this.cache = cache;
        return this;
    }

    /**
     * Define the serializer to use
     */
    public MapLoaderConfigurator<C, M> serializer(DefaultMapDataSerializer serializer) {
        this.serializer = serializer;
        return this;
    }

    /**
     * Enable the cell cache for the inner map data serializer
     */
    public MapLoaderConfigurator<C, M> enableCellCache() {
        DefaultMapDataSerializer serializer = new DefaultMapDataSerializer();
        serializer.enableCache();

        return serializer(serializer);
    }

    /**
     * Define the factory to use
     */
    public MapLoaderConfigurator<C, M> factory(MapFactory<C, M> factory) {
        this.factory = factory;
        return this;
    }

    /**
     * Define the SWF loader to use
     *
     * @see MapLoaderConfigurator#baseUrl(String) To configure loader with the given base url
     * @see MapLoaderConfigurator#tempDir(String) To configure loader with the given temporary directory
     */
    public MapLoaderConfigurator<C, M> loader(SwfFileLoader loader) {
        this.loader = loader;
        return this;
    }

    /**
     * Define the used temporary directory for load SWF files
     */
    public MapLoaderConfigurator<C, M> tempDir(String tempDir) {
        this.tempDir = tempDir;
        return this;
    }

    /**
     * Define the base URL of the maps CDN
     */
    public MapLoaderConfigurator<C, M> baseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
        return this;
    }

    /**
     * Define the SQLite database file for the map cache
     */
    public MapLoaderConfigurator<C, M> cacheFile(String filename) throws SQLException {
        return cache(SqlMapStructureCache.createBySqliteFile(filename));
    }

    /**
     * Create the {@link MapLoader} instance
     */
    @SuppressWarnings("unchecked")
    public MapLoader<C, M> create() {
        return new MapLoader<>(
            loader == null ? new SwfFileLoader(baseUrl, tempDir) : loader,
            serializer == null ? new DefaultMapDataSerializer() : serializer,
            cache == null ? new NullMapStructureCache() : cache,
            factory == null ? (MapFactory<C, M>) new SimpleMapFactory() : factory
        );
    }
}
