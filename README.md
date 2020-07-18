# Swf Map Loader
[![Build Status](https://scrutinizer-ci.com/g/Arakne/SwfMapLoader/badges/build.png?b=master)](https://scrutinizer-ci.com/g/Arakne/SwfMapLoader/build-status/master) [![Scrutinizer Code Quality](https://scrutinizer-ci.com/g/Arakne/SwfMapLoader/badges/quality-score.png?b=master)](https://scrutinizer-ci.com/g/Arakne/SwfMapLoader/?branch=master) [![Code Coverage](https://scrutinizer-ci.com/g/Arakne/SwfMapLoader/badges/coverage.png?b=master)](https://scrutinizer-ci.com/g/Arakne/SwfMapLoader/?branch=master) [![javadoc](https://javadoc.io/badge2/fr.arakne/swf-map-loader/javadoc.svg)](https://javadoc.io/doc/fr.arakne/swf-map-loader) [![Maven Central](https://img.shields.io/maven-central/v/fr.arakne/swf-map-loader)](https://search.maven.org/artifact/fr.arakne/swf-map-loader) 
 
Load and parse Dofus 1.29 maps from swf file and CDN.

## Installation

For installing using maven, add this dependency into the `pom.xml` :

```xml
<dependency>
    <groupId>fr.arakne</groupId>
    <artifactId>swf-map-loader</artifactId>
    <version>0.1-alpha</version>
</dependency>
```

## Usage

### Create your Map class

Create your map and cell classes. The map should inherit [SimpleMap](src/main/java/fr/arakne/swfmaploader/map/SimpleMap.java), which provide simple adapter for [SwfMapStructure](src/main/java/fr/arakne/swfmaploader/swf/SwfMapStructure.java).
More details about maps implementation [here](https://github.com/Arakne/ArakneUtils/tree/master/arakne-map#map-implementation).

```java
// The SimpleMap parameter should be the cell implementation
public class MyMap extends SimpleMap<MyCell> {
    public MyMap(SwfMapStructure structure, CellData[] cells, MapFactory<MyCell, MyMap> mapFactory) {
        super(structure, cells, mapFactory);
    }

    // Add methods here.
    // structure and cells attributes are accessible here (with protected access)
}

// The AbstractCellDataAdapter should be the map implementation
public class MyCell extends AbstractCellDataAdapter<MyMap> {
    public MyCell(MyMap map, CellData data, int id) {
        super(map, data, id);
    }

    // Implements other methods here...
}
```

After that, you can create your factory for instantiate those classes :

```java
class MyMapFactory implements MapFactory<MyCell, MyMap> {
    @Override
    public MyMap createMap(SwfMapStructure structure, CellData[] cells) {
        return new MyMap(structure, cells, this);
    }

    @Override
    public MyCell createCell(MyCustomMap map, int id, CellData cell) {
        return new MyCell(map, cell, id);
    }
}
```

### Configure the loader

Once map and cell classes implemented, you can configure the [MapLoader](src/main/java/fr/arakne/swfmaploader/MapLoader.java).
For this purpose, you can use [MapLoaderConfigurator](src/main/java/fr/arakne/swfmaploader/MapLoaderConfigurator.java) :

```java
// Create the configurator. Note: parameters must match with map and cell implementations
MapLoaderConfigurator<MyCell, MyMap> configurator = new MapLoaderConfigurator<>();

configurator
    .factory(new MyMapFactory()) // Configure the factory : link the custom map implementation to the loader
    .baseUrl("http://my-server.com/dofus/maps") // Define the CDN URL. SWF files must be located at the given path
    .cacheFile("./map-cache.sqlite") // Enable SQLite cache system : maps will be parsed once, and will be retrieved from the cache for further access.
;

// Create the map loader
MapLoader<MyCell, MyMap> loader = configurator.create();
```

### Load maps

Final step : you can load the map !

```java
// Handler for the GDM packet
class LoadMapPacketHandler {
    private MapLoader<MyCell, MyMap> loader;
    // ...

    public void handlePacket(String data) {
        String[] parts = data.split("\\|");

        // Load the map
        client.setCurrentMap(
            loader.load(Integer.parseInt(parts[0]), parts[1], parts[2])
        );
    }
}
```

You can also directly load a SWF file :

```java
File swfFile = new File("...");
String mapKey = xxx;

// Convert File to URL, and loads it
MyMap map = loader.load(swfFile.toURI().toURL(), mapKey);
```

## Licence

This project is licensed under the LGPLv3 licence. See [COPYING](./COPYING) and [COPYING.LESSER](./COPYING.LESSER) files for details.

It also links [FFDec Library](https://github.com/jindrapetrik/jpexs-decompiler) which is licensed with GNU LGPL v3, for parsing SWF files.
See [lib/ffdec](lib/com/jpexs/ffdec-lib) for more details.
