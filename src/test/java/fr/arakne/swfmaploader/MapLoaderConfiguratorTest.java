/*
 * This file is part of Swf Map Loader.
 *
 * Swf Map Loader is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Swf Map Loader is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Swf Map Loader.  If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (c) 2020-2020 Vincent Quatrevieux
 */

package fr.arakne.swfmaploader;

import fr.arakne.swfmaploader.map.MapFactory;
import fr.arakne.swfmaploader.map.SimpleMap;
import fr.arakne.swfmaploader.map.SimpleMapCell;
import fr.arakne.swfmaploader.swf.SwfFileLoader;
import fr.arakne.swfmaploader.swf.SwfMapStructure;
import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.serializer.CellData;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class MapLoaderConfiguratorTest {
    @Test
    void defaults() throws IOException, InterruptedException {
        MapLoader loader = new MapLoaderConfigurator<>().create();

        DofusMap map = loader.loadFromUrl(new URL("file:./files/41_0701241437.swf"), null);

        assertEquals(479, map.size());
        assertEquals(15, map.dimensions().width());
        assertEquals(17, map.dimensions().height());
        assertTrue(map.get(310).walkable());
    }

    class MyCustomMap extends SimpleMap<MyCustomCell> {
        public MyCustomMap(SwfMapStructure structure, CellData[] cells, MapFactory<MyCustomCell, MyCustomMap> mapFactory) {
            super(structure, cells, mapFactory);
        }
    }

    class MyCustomCell extends SimpleMapCell {
        public MyCustomCell(SimpleMap map, CellData data, int id) {
            super(map, data, id);
        }

        public CellData data() {
            return data;
        }
    }

    class MyCustomFactory implements MapFactory<MyCustomCell, MyCustomMap> {
        @Override
        public MyCustomMap createMap(SwfMapStructure structure, CellData[] cells) {
            return new MyCustomMap(structure, cells, this);
        }

        @Override
        public MyCustomCell createCell(MyCustomMap map, int id, CellData cell) {
            return new MyCustomCell(map, cell, id);
        }
    }

    @Test
    void customFactory() throws IOException, InterruptedException {
        MapLoader<MyCustomCell, MyCustomMap> loader = new MapLoaderConfigurator<MyCustomCell, MyCustomMap>()
            .factory(new MyCustomFactory())
            .create()
        ;

        MyCustomMap map = loader.loadFromUrl(new URL("file:./files/41_0701241437.swf"), null);
        assertTrue(map instanceof MyCustomMap);
        assertTrue(map.get(1) instanceof MyCustomCell);
    }

    @Test
    void enableCellCache() throws IOException, InterruptedException {
        MapLoader<MyCustomCell, MyCustomMap> loader = new MapLoaderConfigurator<MyCustomCell, MyCustomMap>()
            .factory(new MyCustomFactory())
            .enableCellCache()
            .create()
        ;

        MyCustomMap map1 = loader.loadFromUrl(new URL("file:./files/41_0701241437.swf"), null);
        MyCustomMap map2 = loader.loadFromUrl(new URL("file:./files/41_0701241437.swf"), null);

        for (int i = 0; i < 479; ++i) {
            assertSame(map1.get(i).data(), map2.get(i).data());
        }
    }

    @Test
    void baseUrl() {
        MapLoader loader = new MapLoaderConfigurator<>().baseUrl("file:./files").create();

        DofusMap map = loader.load(41, "0701241437", null);

        assertEquals(479, map.size());
        assertEquals(15, map.dimensions().width());
        assertEquals(17, map.dimensions().height());
        assertTrue(map.get(310).walkable());
    }

    @Test
    void cacheFile() throws SQLException {
        File file = new File("./tmp/cache.sqlite");

        MapLoader loader = new MapLoaderConfigurator<>()
            .baseUrl("file:./files")
            .cacheFile(file.getAbsolutePath())
            .create()
        ;

        loader.load(41, "0701241437", null);
        assertTrue(file.exists());
        file.delete();
    }

    @Test
    void loader() {
        MapLoader loader = new MapLoaderConfigurator<>()
            .loader(new SwfFileLoader("file:./files", "/tmp"))
            .create()
        ;

        DofusMap map = loader.load(41, "0701241437", null);

        assertEquals(479, map.size());
        assertEquals(15, map.dimensions().width());
        assertEquals(17, map.dimensions().height());
        assertTrue(map.get(310).walkable());
    }

    @Test
    void tempDir() {
        File temp = new File("/tmp/my-client");

        MapLoader loader = new MapLoaderConfigurator<>()
            .baseUrl("file:./files")
            .tempDir(temp.getAbsolutePath())
            .create()
        ;

        loader.load(41, "0701241437", null);

        assertTrue(temp.isDirectory());
        temp.delete();
    }
}
