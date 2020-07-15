package fr.arakne.swfmaploader.map;

import fr.arakne.utils.maps.AbstractCellDataAdapter;
import fr.arakne.utils.maps.serializer.CellData;

/**
 * Simple implementation of Dofus map cell
 * This implementation is not required to be used with {@link SimpleMap}
 */
public class SimpleMapCell extends AbstractCellDataAdapter<SimpleMap<SimpleMapCell>> {
    @SuppressWarnings("unchecked")
    public SimpleMapCell(SimpleMap map, CellData data, int id) {
        super(map, data, id);
    }
}
