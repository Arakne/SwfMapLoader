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
