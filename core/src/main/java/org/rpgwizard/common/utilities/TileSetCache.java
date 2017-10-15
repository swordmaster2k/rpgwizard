/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.utilities;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.TileSet;

/**
 * Stores a cache of loaded TileSets for reuse between boards.
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class TileSetCache {

  private static final HashMap<String, TileSet> TILE_SETS = new HashMap<>();

  /**
   * Gets the tile set with the specified key, if it is present in the cache
   *
   * @param key Filename of the tile set to retrieve
   * @return the Tile set with the corresponding filename
   */
  public static TileSet getTileSet(String key) {
    if (TILE_SETS.containsKey(key)) {
      return TILE_SETS.get(key);
    } else {
      return null;
    }
  }
  
  /**
   * Clears the Tileset cache.
   */
  public static void clear() {
      TILE_SETS.clear();
  }

  /**
   * Allows the calling object to check if the tile set has already been loaded, this should be
   * checked before calling loadTileSet
   *
   * @param key Tile Set file to check for
   * @return true if the tile set is already present in the cache
   */
  public static boolean contains(String key) {
    return TILE_SETS.containsKey(key);
  }

  /**
   * Adds the specified tile set into the cache, it will only load the file if it is not already
   * present in the cache, it is important to call contains(String key) before calling this method.
   *
   * @param fileName Tile set to attempt to load into the cache
   * @return The loaded tile set is returned, this is to remove the need to call getTileSet(String
   * key) straight after loading a set
   */
  public static TileSet addTileSet(String fileName) {
    TileSet set;

    if (!TILE_SETS.containsKey(fileName)) {
      try {
        File file = new File(
                System.getProperty("project.path")
                + File.separator
                + CoreProperties.getProperty("toolkit.directory.tileset")
                + File.separator + fileName);

        AssetHandle handle = AssetManager.getInstance().deserialize(
                new AssetDescriptor(file.toURI()));
        set = (TileSet) handle.getAsset();

        TILE_SETS.put(set.getName(), set);

        return set;
      } catch (IOException | AssetException ex) {
        Logger.getLogger(TileSetCache.class.getName()).log(Level.SEVERE, null, ex);
        return null;
      }
    } else {
      set = getTileSet(fileName);

      return set;
    }
  }

  /**
   * Removes the specified TileSet from the cache, it will only remove the TileSet if the number of
   * board references have reached 0.
   *
   * @param fileName TileSet to attempt to load into the cache
   * @return the remove TileSet is returned.
   */
  public static TileSet removeTileSet(String fileName) {
    if (TILE_SETS.containsKey(fileName)) {
      TileSet set = TILE_SETS.get(fileName);

      return set;
    }

    return null;
  }

  /**
   * Removes the specified TileSets from the cache, it will only remove the TileSet if the number of
   * board references have reached 0.
   *
   * @param fileNames list of TileSet file names to remove
   * @return the removed TileSets
   */
  public static LinkedList<TileSet> removeTileSets(LinkedList<String> fileNames) {
    LinkedList<TileSet> removedSets = new LinkedList<>();

    for (String fileName : fileNames) {
      removedSets.add(removeTileSet(fileName));
    }

    return removedSets;
  }

}
