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

package fr.arakne.swfmaploader.map;

import fr.arakne.swfmaploader.swf.SwfMapStructure;
import fr.arakne.utils.maps.serializer.CellData;

/**
 * Base factory for {@link SimpleMap} and {@link SimpleMapCell}
 */
final public class SimpleMapFactory implements MapFactory<SimpleMapCell, SimpleMap<SimpleMapCell>> {
    @Override
    public SimpleMap<SimpleMapCell> createMap(SwfMapStructure structure, CellData[] cells) {
        return new SimpleMap<>(structure, cells, this);
    }

    @Override
    public SimpleMapCell createCell(SimpleMap<SimpleMapCell> map, int id, CellData cell) {
        return new SimpleMapCell(map, cell, id);
    }
}
