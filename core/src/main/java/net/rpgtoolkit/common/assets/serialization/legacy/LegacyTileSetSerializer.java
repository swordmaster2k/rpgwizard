/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
///**
// * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
// *
// * This Source Code Form is subject to the terms of the Mozilla Public
// * License, v. 2.0. If a copy of the MPL was not distributed with this
// * file, You can obtain one at http://mozilla.org/MPL/2.0/.
// */
//package net.rpgtoolkit.common.assets.serialization.legacy;
//
//import java.awt.Color;
//import java.io.IOException;
//import java.nio.ByteBuffer;
//import java.nio.ByteOrder;
//import java.nio.channels.ReadableByteChannel;
//import java.nio.channels.WritableByteChannel;
//import net.rpgtoolkit.common.assets.AbstractAssetSerializer;
//import net.rpgtoolkit.common.assets.AssetDescriptor;
//import net.rpgtoolkit.common.assets.AssetException;
//import net.rpgtoolkit.common.assets.AssetHandle;
//import net.rpgtoolkit.common.assets.Tile;
//import net.rpgtoolkit.common.assets.TilePixelOutOfRangeException;
//import net.rpgtoolkit.common.assets.TileSet;
//import net.rpgtoolkit.common.io.ByteBufferHelper;
//import net.rpgtoolkit.common.io.Paths;
//import net.rpgtoolkit.common.utilities.CoreProperties;
//import org.apache.commons.io.FilenameUtils;
//
///**
// *
// * @author Joshua Michael Daly
// */
//public class LegacyTileSetSerializer extends AbstractAssetSerializer {
//
//  @Override
//  public boolean serializable(AssetDescriptor descriptor) {
//    final String ext = Paths.extension(descriptor.getURI().getPath());
//    return (ext.endsWith(CoreProperties.getFullExtension("toolkit.tileset.extension.legacy")));
//  }
//
//  @Override
//  public boolean deserializable(AssetDescriptor descriptor) {
//    return serializable(descriptor);
//  }
//
//  @Override
//  public void serialize(AssetHandle handle) throws IOException, AssetException {
//    System.out.println("Saving TileSet " + handle.getDescriptor());
//
//    try (final WritableByteChannel channel = handle.write()) {
//      final TileSet tileSet = (TileSet) handle.getAsset();
//
//      int bytesPerTile = (tileSet.getTileWidth() * tileSet.getTileHeight()) * 4;
//      int size = (tileSet.getNumberOfTiles() * bytesPerTile) + 6;
//      
//      final ByteBuffer buffer = ByteBuffer.allocate(size);
//      buffer.order(ByteOrder.LITTLE_ENDIAN);
//
//      buffer.put((byte) tileSet.getTilesetVersion());
//      buffer.put((byte) 0);
//      buffer.put((byte) tileSet.getNumberOfTiles());
//      buffer.put((byte) 0);
//      buffer.put((byte) 10);
//      buffer.put((byte) 0);
//
//      // Save the tiles to the tiles
//      for (Tile tile : tileSet.getTiles()) {
//        for (int x = 0; x < 32; x++) // Go through each row
//        {
//          for (int y = 0; y < 32; y++) // Go through each column
//          {
//            Color pixelColor = tile.getPixel(x, y);
//            buffer.put((byte) pixelColor.getRed());
//            buffer.put((byte) pixelColor.getGreen());
//            buffer.put((byte) pixelColor.getBlue());
//            buffer.put((byte) pixelColor.getAlpha());
//          }
//        }
//      }
//      
//      buffer.flip();
//      channel.write(buffer);
//    } catch (TilePixelOutOfRangeException e) {
//      System.out.println(e.toString());
//    }
//  }
//
//  @Override
//  public void deserialize(AssetHandle handle) throws IOException, AssetException {
//    System.out.println("Loading TileSet " + handle.getDescriptor());
//
//    try (final ReadableByteChannel channel = handle.read()) {
//      final TileSet tileSet = new TileSet(handle.getDescriptor());
//      String name = FilenameUtils.getName(handle.getDescriptor().getURI().toURL().getPath());
//      tileSet.setName(name);
//
//      final ByteBuffer buffer = ByteBuffer.allocate((int) handle.size());
//      buffer.order(ByteOrder.LITTLE_ENDIAN);
//
//      channel.read(buffer);
//
//      buffer.rewind();
//
//      tileSet.setTilesetVersion(ByteBufferHelper.getUnsignedInt(buffer));
//      int skipByte = ByteBufferHelper.getUnsignedInt(buffer);
//
//      int numberOfTiles = ByteBufferHelper.getUnsignedInt(buffer);
//      numberOfTiles += (ByteBufferHelper.getUnsignedInt(buffer) * 256);
//      tileSet.setNumberOfTiles(numberOfTiles);
//
//      tileSet.setTilesetType(ByteBufferHelper.getUnsignedInt(buffer));
//      skipByte = ByteBufferHelper.getUnsignedInt(buffer);
//
//      // Lets make sure we are reading the correct number of bytes for the tile type.
//      switch (tileSet.getTilesetType()) {
//        case 1:
//          tileSet.setTileWidth(32);
//          tileSet.setTileHeight(32);
//          tileSet.setRgbColor(true);
//          break;
//        case 2:
//          tileSet.setTileWidth(16);
//          tileSet.setTileHeight(16);
//          tileSet.setRgbColor(true);
//          break;
//        case 3:
//        case 5:
//          tileSet.setTileWidth(32);
//          tileSet.setTileHeight(32);
//          break;
//        case 4:
//        case 6:
//          tileSet.setTileWidth(16);
//          tileSet.setTileHeight(16);
//          break;
//        case 10:
//          tileSet.setTileWidth(32);
//          tileSet.setTileHeight(32);
//          tileSet.setRgbColor(true);
//          tileSet.setHasAlpha(true);
//        case 150: // Isometric Tile Set : [
//          tileSet.setTileWidth(32);
//          tileSet.setTileHeight(32);
//          tileSet.setRgbColor(true);
//      }
//
//      /*
//       * Read the actual RGB data for each tile
//       */
//      int tileWidth = tileSet.getTileWidth();
//      int tileHeight = tileSet.getTileHeight();
//      boolean rgbColor = tileSet.isRgbColor();
//      boolean hasAlpha = tileSet.isHasAlpha();
//      for (int i = 0; i < numberOfTiles; i++) {
//        // Read the next tile into memory
//        Tile newTile = new Tile(tileSet, i);
//        for (int x = 0; x < tileWidth; x++) // Go through each row
//        {
//          for (int y = 0; y < tileHeight; y++) // Go through each column
//          {
//            if (rgbColor) // is this tile using RGB color space or DOS pallet
//            {
//              int red = ByteBufferHelper.getUnsignedInt(buffer);
//              int green = ByteBufferHelper.getUnsignedInt(buffer);
//              int blue = ByteBufferHelper.getUnsignedInt(buffer);
//              int alpha;
//              if (hasAlpha) {
//                alpha = ByteBufferHelper.getUnsignedInt(buffer);
//              } else {
//                alpha = 255;
//                if ((red == 0 && green == 1 && blue == 2)
//                        || (red == 255 && green == 0 && blue == 255)) {
//                  red = 255;
//                  green = 0;
//                  blue = 255;
//                  alpha = 0;
//                }
//              }
//
//              newTile.setPixel(x, y, new Color(red, green,
//                      blue, alpha));
//
//            } else {
//              int colorIndex = ByteBufferHelper.getUnsignedInt(buffer);
//              newTile.setPixel(x, y, tileSet.getDosColors().getColor(colorIndex));
//            }
//
//          }
//        }
//
//        tileSet.getTiles().add(newTile);
//      }
//
//      handle.setAsset(tileSet);
//    } catch (IOException | TilePixelOutOfRangeException e) {
//      System.out.println(e.toString());
//    }
//  }
//
//}
