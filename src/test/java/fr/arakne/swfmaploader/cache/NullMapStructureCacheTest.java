package fr.arakne.swfmaploader.cache;

import fr.arakne.swfmaploader.swf.SwfMapStructure;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NullMapStructureCacheTest {
    @Test
    void test() {
        NullMapStructureCache cache = new NullMapStructureCache();

        assertFalse(cache.retrieve(41).isPresent());

        SwfMapStructure structure = new SwfMapStructure();
        structure.setId(41);

        cache.store(structure);
        assertFalse(cache.retrieve(41).isPresent());

        cache.clear();
        assertFalse(cache.retrieve(41).isPresent());
    }
}
