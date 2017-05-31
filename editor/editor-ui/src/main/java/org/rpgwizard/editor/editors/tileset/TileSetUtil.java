/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.tileset;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.rpgwizard.common.assets.Tile;
import org.rpgwizard.common.assets.TileSet;
import org.rpgwizard.editor.utilities.EditorFileManager;

/**
 *
 * @author Joshua Michael Daly
 */
public class TileSetUtil {

	public static TileSet load(TileSet tileSet) throws IOException {
        int tileWidth = tileSet.getTileWidth();
        int tileHeight = tileSet.getTileHeight();
        String image = tileSet.getImage();
        String subdir = EditorFileManager.getGraphicsSubdirectory();
        File file = EditorFileManager.getPath(subdir + File.separator + image);

        try (FileInputStream fis = new FileInputStream(file)) {
            BufferedImage source = ImageIO.read(fis);
            tileSet = loadImageIntoTileSet(tileSet, source, tileWidth, tileHeight);
            tileSet.setBufferedImage(source);
        }

        return tileSet;
    }
	public static TileSet loadImageIntoTileSet(TileSet tileSet,
			BufferedImage source, int tileWidth, int tileHeight) {
		int sourceWidth = source.getWidth();
		int sourceHeight = source.getHeight();

		int rows = sourceHeight / tileHeight;
		int columns = sourceWidth / tileWidth;

		Tile tile;
		BufferedImage subImage;
		for (int x = 0; x < rows; x++) {
			for (int y = 0; y < columns; y++) {
				subImage = new BufferedImage(tileWidth, tileHeight,
						BufferedImage.TYPE_INT_ARGB);

				Graphics2D g2d = subImage.createGraphics();
				g2d.drawImage(source, 0, 0, tileWidth, tileHeight, tileWidth
						* y, tileHeight * x, tileWidth * y + tileWidth,
						tileHeight * x + tileHeight, null);
				g2d.dispose();

				tile = new Tile();
				tileSet.addTile(tile);

				tile.setRect(0, 0, subImage.getRaster());
				tile.setTileSet(tileSet);
				tile.setIndex(tileSet.getTiles().size() - 1);
			}
		}

		return tileSet;
	}

}
