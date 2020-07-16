package fr.arakne.swfmaploader.swf;

import com.jpexs.decompiler.flash.AbortRetryIgnoreHandler;
import com.jpexs.decompiler.flash.SWF;
import com.jpexs.decompiler.flash.exporters.modes.ScriptExportMode;
import com.jpexs.decompiler.flash.exporters.settings.ScriptExportSettings;
import org.apache.commons.lang3.StringUtils;

import java.io.*;
import java.net.URL;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Load and parse swf map file
 *
 * Note: the load method is synchronized, so it not scale on multithreading application.
 */
final public class SwfFileLoader {
    @FunctionalInterface
    interface Setter<T> {
        public void set(SwfMapStructure map, T value);
    }

    final private String baseUrl;
    final private String tempDir;

    final private Map<String, Setter<String>> fields = new HashMap<>();

    /**
     * @param baseUrl The base URL to load map data (must contains the protocol, host and path to maps directory)
     * @param tempDir Directory to use for store decompiled action script files
     */
    public SwfFileLoader(String baseUrl, String tempDir) {
        this.baseUrl = baseUrl;
        this.tempDir = tempDir;

        declareFields();
    }

    /**
     * Load a map file from CDN
     *
     * @param id The map id
     * @param version The map version string
     * @param key The encryption key
     *
     * @return The loaded map structure
     *
     * @throws IOException When cannot load swf file
     * @throws InterruptedException When an interruption occurs during loading the swf file
     * @throws IllegalArgumentException When the swf file do not contains a valid map structure
     */
    public SwfMapStructure load(int id, String version, String key) throws IOException, InterruptedException {
        SwfMapStructure structure = load(new URL(baseUrl + "/" + id + "_" + version + (key == null ? "" : "X") + ".swf"));

        structure.setVersion(version);

        if (structure.id() != id) {
            throw new IllegalArgumentException("Invalid loaded map id : expected " + id + " but get " + structure.id());
        }

        return structure;
    }

    /**
     * Load a map file from an URL
     *
     * @param mapFileUrl The file URL
     *
     * @return The loaded map structure
     *
     * @throws IOException When cannot load swf file
     * @throws InterruptedException When an interruption occurs during loading the swf file
     * @throws IllegalArgumentException When the swf file do not contains a valid map structure
     */
    synchronized public SwfMapStructure load(URL mapFileUrl) throws IOException, InterruptedException {
        File outdir = new File(tempDir + File.separator + StringUtils.substringAfterLast(mapFileUrl.getPath(), "/"));
        outdir.mkdirs();

        try (InputStream stream = mapFileUrl.openStream()) {
            SWF swf = new SWF(stream, false);

            List<File> sources = swf.exportActionScript(
                new AbortRetryIgnoreHandler() {
                    @Override
                    public int handle(Throwable throwable) {
                        return AbortRetryIgnoreHandler.ABORT;
                    }

                    @Override
                    public AbortRetryIgnoreHandler getNewInstance() {
                        return this;
                    }
                },
                outdir.getAbsolutePath(),
                new ScriptExportSettings(ScriptExportMode.AS, false),
                false,
                null
            );

            if (sources.size() != 1 || !sources.get(0).getName().equalsIgnoreCase("DoAction.as")) {
                throw new IllegalArgumentException("Invalid SWF file : missing the ActionScript file");
            }

            return parseActionScript(new FileInputStream(sources.get(0)));
        } finally {
            clearTemp(outdir);
        }
    }

    /**
     * Parse an action script file
     *
     * @param stream The file stream
     *
     * @return The map structure
     *
     * @throws IOException When cannot load swf file
     * @throws IllegalArgumentException When the swf file do not contains a valid map structure
     */
    public SwfMapStructure parseActionScript(InputStream stream) throws IOException {
        SwfMapStructure structure = new SwfMapStructure();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            for (String line = reader.readLine(); line != null; line = reader.readLine()) {
                parseLine(structure, line);
            }
        }

        if (!structure.valid()) {
            throw new IllegalArgumentException("Invalid data");
        }

        return structure;
    }

    /**
     * Parse a single action script line
     *
     * @param structure The map structure to fill
     * @param line The line to parse
     */
    private void parseLine(SwfMapStructure structure, String line) {
        String[] parts = StringUtils.split(line, "=", 2);

        if (parts.length != 2) {
            return;
        }

        if (!parts[1].endsWith(";")) {
            throw new IllegalArgumentException();
        }

        String varName = parts[0].trim();
        String rawValue = StringUtils.substring(parts[1], 0, -1).trim();

        if (!fields.containsKey(varName)) {
            return;
        }

        fields.get(varName).set(structure, rawValue);
    }

    /**
     * Declare file structure
     */
    private void declareFields() {
        integer("id", SwfMapStructure::setId);
        integer("width", SwfMapStructure::setWidth);
        integer("height", SwfMapStructure::setHeight);
        integer("backgroundNum", SwfMapStructure::setBackgroundNum);
        integer("ambianceId", SwfMapStructure::setAmbianceId);
        integer("musicId", SwfMapStructure::setMusicId);
        bool("bOutdoor", SwfMapStructure::setOutdoor);
        integer("capabilities", SwfMapStructure::setCapabilities);
        string("mapData", SwfMapStructure::setMapData);
    }

    /**
     * Declare an integer field
     *
     * @param name Field name
     * @param setter The setter
     */
    private void integer(String name, Setter<Integer> setter) {
        fields.put(name, (map, value) -> setter.set(map, Integer.parseInt(value)));
    }

    /**
     * Declare an boolean field
     *
     * @param name Field name
     * @param setter The setter
     */
    private void bool(String name, Setter<Boolean> setter) {
        fields.put(name, (map, value) -> setter.set(map, value.equals("true") || value.equals("1")));
    }

    /**
     * Declare an string field
     *
     * @param name Field name
     * @param setter The setter
     */
    private void string(String name, Setter<String> setter) {
        fields.put(name, (map, value) -> {
            if (value.charAt(0) != '"' || value.charAt(value.length() - 1) != '"') {
                throw new IllegalArgumentException();
            }

            setter.set(map, value.substring(1, value.length() - 1));
        });
    }

    private void clearTemp(File tempdir) throws IOException {
        if (!tempdir.exists()) {
            return;
        }

        Files.walkFileTree(tempdir.toPath(), new SimpleFileVisitor<Path>() {
            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);

                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);

                return FileVisitResult.CONTINUE;
            }
        });
    }
}
