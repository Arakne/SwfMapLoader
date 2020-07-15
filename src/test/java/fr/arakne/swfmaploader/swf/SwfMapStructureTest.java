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
