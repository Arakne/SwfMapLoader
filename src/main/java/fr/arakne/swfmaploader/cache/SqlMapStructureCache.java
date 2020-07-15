package fr.arakne.swfmaploader.cache;

import fr.arakne.swfmaploader.swf.SwfMapStructure;

import java.sql.*;
import java.util.Optional;

/**
 * SQL implementation for the cache system
 */
final public class SqlMapStructureCache implements MapStructureCache {
    final static public int SCHEMA_VERSION = 1;

    final private Connection connection;

    public SqlMapStructureCache(Connection connection) throws SQLException {
        this.connection = connection;

        initDatabase();
    }

    @Override
    public Optional<SwfMapStructure> retrieve(int id) {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT * FROM `MAP_CACHE` WHERE `ID` = ?")) {
            stmt.setInt(1, id);

            try (ResultSet rs = stmt.executeQuery()) {
                if (!rs.next()) {
                    return Optional.empty();
                }

                SwfMapStructure structure = new SwfMapStructure();

                structure.setId(rs.getInt("ID"));
                structure.setWidth(rs.getInt("WIDTH"));
                structure.setHeight(rs.getInt("HEIGHT"));
                structure.setBackgroundNum(rs.getInt("BACKGROUND_NUM"));
                structure.setAmbianceId(rs.getInt("AMBIANCE_ID"));
                structure.setMusicId(rs.getInt("MUSIC_ID"));
                structure.setOutdoor(rs.getInt("OUTDOOR") == 1);
                structure.setCapabilities(rs.getInt("CAPABILITIES"));
                structure.setMapData(rs.getString("MAP_DATA"));
                structure.setVersion(rs.getString("VERSION"));

                return Optional.of(structure);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Cannot read map " + id + " from cache", e);
        }
    }

    @Override
    public void store(SwfMapStructure structure) {
        try (PreparedStatement stmt = connection.prepareStatement("REPLACE INTO `MAP_CACHE` (`ID`, `WIDTH`, `HEIGHT`, `BACKGROUND_NUM`, `AMBIANCE_ID`, `MUSIC_ID`, `OUTDOOR`, `CAPABILITIES`, `MAP_DATA`, `VERSION`) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.setInt(1, structure.id());
            stmt.setInt(2, structure.width());
            stmt.setInt(3, structure.height());
            stmt.setInt(4, structure.backgroundNum());
            stmt.setInt(5, structure.ambianceId());
            stmt.setInt(6, structure.musicId());
            stmt.setInt(7, structure.isOutdoor() ? 1 : 0);
            stmt.setInt(8, structure.capabilities());
            stmt.setString(9, structure.mapData());
            stmt.setString(10, structure.version());

            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Cannot store map " + structure.id() + " in cache", e);
        }
    }

    @Override
    public void clear() {
        try {
            connection.createStatement().execute("DELETE  FROM `MAP_CACHE`");
        } catch (SQLException e) {
            throw new RuntimeException("Cannot clear map cache", e);
        }
    }

    /**
     * Initialize the SQL database
     *
     * @throws SQLException
     */
    private void initDatabase() throws SQLException {
        boolean hasTable = hasTable("MAP_CACHE");
        boolean checkVersion = checkSchemaVersion();

        if (hasTable && !checkVersion) {
            connection.createStatement().execute("DROP TABLE `MAP_CACHE`");
            hasTable = false;
        }

        if (!hasTable) {
            createCacheTable();
        }

        if (!checkVersion) {
            updateSchemaVersion();
        }
    }

    /**
     * Check if the schema version match
     */
    private boolean checkSchemaVersion() throws SQLException {
        if (!hasTable("SCHEMA_VERSION")) {
            connection.createStatement().execute("CREATE TABLE `SCHEMA_VERSION` (`VERSION` INTEGER PRIMARY KEY)");

            return false;
        }

        try (PreparedStatement stmt = connection.prepareStatement("SELECT `VERSION` FROM `SCHEMA_VERSION` WHERE `VERSION` = ?")) {
            stmt.setInt(1, SCHEMA_VERSION);

            return stmt.executeQuery().next();
        }
    }

    /**
     * Check if a table exists
     *
     * @param tableName The table name
     *
     * @return true if exists
     */
    private boolean hasTable(String tableName) throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("SELECT `name` FROM  `sqlite_master` WHERE `type` = 'table' AND `name` = ?")) {
            stmt.setString(1, tableName);

            if (!stmt.executeQuery().next()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Set the schema version
     */
    private void updateSchemaVersion() throws SQLException {
        try (PreparedStatement stmt = connection.prepareStatement("INSERT INTO `SCHEMA_VERSION` VALUES (?)")) {
            stmt.setInt(1, SCHEMA_VERSION);
            stmt.executeUpdate();
        }
    }

    /**
     * Create the table
     */
    private void createCacheTable() throws SQLException {
        connection.createStatement().execute(
            "CREATE TABLE `MAP_CACHE` (" +
                "`ID` INTEGER PRIMARY KEY, " +
                "`VERSION` VARCHAR(32), " +
                "`WIDTH` INTEGER, " +
                "`HEIGHT` INTEGER, " +
                "`BACKGROUND_NUM` INTEGER, " +
                "`AMBIANCE_ID` INTEGER, " +
                "`MUSIC_ID` INTEGER, " +
                "`OUTDOOR` INTEGER, " +
                "`CAPABILITIES` INTEGER, " +
                "`MAP_DATA` BLOB" +
            ")"
        );
    }

    /**
     * Create a new cache instance for a SQLite file database
     *
     * @param filename The sqlite filename
     *
     * @return The cache instance
     */
    static public SqlMapStructureCache createBySqliteFile(String filename) throws SQLException {
        return new SqlMapStructureCache(DriverManager.getConnection("jdbc:sqlite:" + filename));
    }
}
