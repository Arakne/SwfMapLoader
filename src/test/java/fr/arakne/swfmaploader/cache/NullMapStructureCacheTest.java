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
