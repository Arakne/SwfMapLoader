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

package fr.arakne.swfmaploader.swf;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SwfMapStructureTest {
    @Test
    void valid() {
        SwfMapStructure structure = new SwfMapStructure();

        assertFalse(structure.valid());

        structure.setId(14);
        structure.setWidth(15);
        structure.setHeight(17);
        structure.setMapData("map data");

        assertTrue(structure.valid());

        structure.setId(-1);
        assertFalse(structure.valid());

        structure.setId(14);
        structure.setWidth(-1);
        assertFalse(structure.valid());

        structure.setWidth(15);
        structure.setHeight(-1);
        assertFalse(structure.valid());

        structure.setHeight(17);
        structure.setMapData("");
        assertFalse(structure.valid());
    }

    @Test
    void string() {
        SwfMapStructure structure = new SwfMapStructure();

        structure.setId(14);
        structure.setWidth(15);
        structure.setHeight(17);
        structure.setMapData("map data");
        structure.setBackgroundNum(5);
        structure.setMusicId(12);
        structure.setAmbianceId(3);

        assertEquals("MapStructure{id=14, width=15, height=17, backgroundNum=5, ambianceId=3, musicId=12, outdoor=false, capabilities=0, mapData='map data'}", structure.toString());
    }
}
