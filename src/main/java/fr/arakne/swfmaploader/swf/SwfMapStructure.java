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

package fr.arakne.swfmaploader.swf;

/**
 * Data structure of the swf map file
 */
final public class SwfMapStructure {
    private int id;
    private int width;
    private int height;
    private int backgroundNum;
    private int ambianceId;
    private int musicId;
    private boolean outdoor;
    private int capabilities;
    private String mapData;
    private String version;

    /**
     * @return The map id. This is the primary key
     */
    public int id() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return The map width. positive integer
     */
    public int width() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    /**
     * @return The map height. positive integer
     */
    public int height() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    /**
     * @return The background image number. Can be zero for no background
     */
    public int backgroundNum() {
        return backgroundNum;
    }

    public void setBackgroundNum(int backgroundNum) {
        this.backgroundNum = backgroundNum;
    }

    /**
     * @return The ambiance sound id
     */
    public int ambianceId() {
        return ambianceId;
    }

    public void setAmbianceId(int ambianceId) {
        this.ambianceId = ambianceId;
    }

    /**
     * @return The music id
     */
    public int musicId() {
        return musicId;
    }

    public void setMusicId(int musicId) {
        this.musicId = musicId;
    }

    /**
     * @return true if the map is outdoor
     */
    public boolean isOutdoor() {
        return outdoor;
    }

    public void setOutdoor(boolean outdoor) {
        this.outdoor = outdoor;
    }

    /**
     * Bitset for allowing actions
     *
     * https://github.com/Emudofus/Dofus/blob/1.29/dofus/managers/MapsServersManager.as#L141
     * @return The bitset as integer
     */
    public int capabilities() {
        return capabilities;
    }

    public void setCapabilities(int capabilities) {
        this.capabilities = capabilities;
    }

    /**
     * The map data. If the map is encrypted (ends with X.swf), the map data will be encrypted.
     *
     * @return The raw map data as string
     */
    public String mapData() {
        return mapData;
    }

    public void setMapData(String mapData) {
        this.mapData = mapData;
    }

    /**
     * @return The map version string
     */
    public String version() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Check if the structure contains valid data
     *
     * @return true if valid
     */
    public boolean valid() {
        return id > 0 && width > 0 && height > 0 && mapData != null && !mapData.isEmpty();
    }

    @Override
    public String toString() {
        return "MapStructure{" +
            "id=" + id +
            ", width=" + width +
            ", height=" + height +
            ", backgroundNum=" + backgroundNum +
            ", ambianceId=" + ambianceId +
            ", musicId=" + musicId +
            ", outdoor=" + outdoor +
            ", capabilities=" + capabilities +
            ", mapData='" + mapData + '\'' +
            '}'
        ;
    }
}
