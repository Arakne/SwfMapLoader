package fr.arakne.swfmaploader;

import fr.arakne.swfmaploader.cache.MapStructureCache;
import fr.arakne.swfmaploader.cache.SqlMapStructureCache;
import fr.arakne.swfmaploader.map.SimpleMap;
import fr.arakne.swfmaploader.map.SimpleMapCell;
import fr.arakne.swfmaploader.map.SimpleMapFactory;
import fr.arakne.swfmaploader.swf.SwfFileLoader;
import fr.arakne.utils.maps.serializer.DefaultMapDataSerializer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URL;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MapLoaderTest {
    private MapLoader<SimpleMapCell, SimpleMap<SimpleMapCell>> loader;
    private MapStructureCache cache;

    @BeforeEach
    void setUp() throws SQLException {
        loader = new MapLoader<>(
            new SwfFileLoader("file:./files", "./tmp"),
            new DefaultMapDataSerializer(),
            cache = new SqlMapStructureCache(DriverManager.getConnection("jdbc:sqlite::memory:")),
            new SimpleMapFactory()
        );
    }

    @Test
    void loadSuccess() {
        SimpleMap<SimpleMapCell> map = loader.load(41, "0701241437", null);

        assertEquals(479, map.size());
        assertEquals(15, map.dimensions().width());
        assertEquals(17, map.dimensions().height());
        assertTrue(map.get(310).walkable());
    }

    @Test
    void loadSuccessWithEmptyStringAsKey() {
        SimpleMap<SimpleMapCell> map = loader.load(41, "0701241437", "");

        assertEquals(479, map.size());
        assertEquals(15, map.dimensions().width());
        assertEquals(17, map.dimensions().height());
        assertTrue(map.get(310).walkable());
    }

    @Test
    void loadSuccessWithKey() {
        SimpleMap<SimpleMapCell> map = loader.load(4250, "0706131721", "2e46236e487e32342d7e505151756f405e705d7065555c6e717a305d6e2075497e417a3973656b6c724344223c6126575166273f577c7e772c4d726361593e3b617d6a3b42756f5b3173544a7a216e2f5a6149383931577b3c397e7539632166494c6568475a70704d2a3c28455b3f4143734d7c3a7c2532357e65572a7638645153516d303f746e264b784e6e704a3f432a626d245721");

        assertEquals(796, map.size());
        assertEquals(19, map.dimensions().width());
        assertEquals(22, map.dimensions().height());
        assertTrue(map.get(310).walkable());
    }

    @Test
    void loadFromCache() {
        loader.load(41, "0701241437", null);

        assertTrue(cache.retrieve(41).isPresent());

        SimpleMap<SimpleMapCell> map = loader.load(41, "0701241437", null);

        assertEquals(479, map.size());
        assertEquals(15, map.dimensions().width());
        assertEquals(17, map.dimensions().height());
        assertTrue(map.get(310).walkable());
    }

    @Test
    void loadWithNewVersionShouldUpdateCache() {
        loader.load(41, "0701241437", null);
        SimpleMap<SimpleMapCell> map = loader.load(41, "0701241438", null);

        assertEquals(479, map.size());
        assertEquals(16, map.dimensions().width());
        assertEquals(16, map.dimensions().height());

        assertEquals("0701241438", cache.retrieve(41).get().version());
    }

    @Test
    void loadNotFound() {
        assertThrows(RuntimeException.class, () -> loader.load(404, "0701241437", null));
    }

    @Test
    void loadFromUrl() throws IOException, InterruptedException {
        SimpleMap<SimpleMapCell> map = loader.loadFromUrl(new URL("file:./files/41_0701241437.swf"), null);

        assertEquals(479, map.size());
        assertEquals(15, map.dimensions().width());
        assertEquals(17, map.dimensions().height());
        assertTrue(map.get(310).walkable());
    }
}
