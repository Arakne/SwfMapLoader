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

import fr.arakne.utils.maps.AbstractCellDataAdapter;
import fr.arakne.utils.maps.serializer.CellData;

/**
 * Simple implementation of Dofus map cell
 * This implementation is not required to be used with {@link SimpleMap}
 */
public class SimpleMapCell extends AbstractCellDataAdapter<SimpleMap<SimpleMapCell>, SimpleMapCell> {
    @SuppressWarnings("unchecked")
    public SimpleMapCell(SimpleMap map, CellData data, int id) {
        super(map, data, id);
    }
}
