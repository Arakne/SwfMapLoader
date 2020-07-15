package fr.arakne.swfmaploader.cache;

import fr.arakne.swfmaploader.swf.SwfMapStructure;

import java.util.Optional;

/**
 * No-op cache system
 */
final public class NullMapStructureCache implements MapStructureCache {
    @Override
    public Optional<SwfMapStructure> retrieve(int id) {
        return Optional.empty();
    }

    @Override
    public void store(SwfMapStructure structure) {

    }

    @Override
    public void clear() {

    }
}
