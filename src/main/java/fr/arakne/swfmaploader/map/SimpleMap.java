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
import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.serializer.CellData;
import fr.arakne.utils.value.Dimensions;

import java.util.ArrayList;
import java.util.List;

/**
 * Simple implementation of a DofusMap using {@link SwfMapStructure}
 * Extends this class to create more specific implementations
 *
 * @param <C> The cell type
 */
public class SimpleMap<C extends MapCell> implements DofusMap<C> {
    final private Dimensions dimensions;

    final protected List<C> cells;
    final protected SwfMapStructure structure;

    @SuppressWarnings("unchecked")
    public SimpleMap(SwfMapStructure structure, CellData[] cells, MapFactory<C, ? extends SimpleMap<C>> mapFactory) {
        this.structure = structure;
        this.dimensions = new Dimensions(structure.width(), structure.height());
        this.cells = makeCells(cells, (MapFactory<C, SimpleMap<C>>) mapFactory);
    }

    @Override
    final public int size() {
        return cells.size();
    }

    @Override
    final public C get(int id) {
        return cells.get(id);
    }

    @Override
    final public Dimensions dimensions() {
        return dimensions;
    }

    private List<C> makeCells(CellData[] data, MapFactory<C, SimpleMap<C>> factory) {
        List<C> cells = new ArrayList<>(data.length);

        for (int id = 0; id < data.length; ++id) {
            cells.add(factory.createCell(this, id, data[id]));
        }

        return cells;
    }
}
