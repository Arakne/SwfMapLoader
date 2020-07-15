package fr.arakne.swfmaploader.map;

import fr.arakne.swfmaploader.swf.SwfMapStructure;
import fr.arakne.utils.maps.DofusMap;
import fr.arakne.utils.maps.MapCell;
import fr.arakne.utils.maps.serializer.CellData;

public interface MapFactory<C extends MapCell, M extends DofusMap<C>> {
    public M createMap(SwfMapStructure structure, CellData[] cells);

    public C createCell(M map, int id, CellData cell);
}
