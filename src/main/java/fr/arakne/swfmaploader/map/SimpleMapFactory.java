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
