/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.rpgwizard.common.utilities.CoreUtil;

/**
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true, includeFieldNames = true)
public class Tile extends WritableRaster {

    private Tileset tileSet;
    private int index;

    @JsonIgnore
    private BufferedImage tileImage;

    /**
     *
     */
    public Tile() {
        super(ColorModel.getRGBdefault().createCompatibleSampleModel(32, 32), new Point(0, 0));
    }

    /**
     * Copy constructor.
     * 
     * @param tile
     */
    public Tile(Tile tile) {
        super(tile.sampleModel, new Point(0, 0));
        tileSet = tile.tileSet;
        index = tile.index;
        tileImage = CoreUtil.copy(tile.tileImage);
    }

    public Tile(int width, int height) {
        super(ColorModel.getRGBdefault().createCompatibleSampleModel(width, height), new Point(0, 0));
    }

    /**
     *
     * @param tileSet
     * @param index
     */
    public Tile(Tileset tileSet, int index) {
        super(ColorModel.getRGBdefault().createCompatibleSampleModel(32, 32), new Point(0, 0));
        this.tileSet = tileSet;
        this.index = index;
    }

    /**
     *
     * @param x
     * @param y
     * @param newPixel
     * @throws TilePixelOutOfRangeException
     */
    public void setPixel(int x, int y, Color newPixel) throws TilePixelOutOfRangeException {
        if ((x > 31) || (y > 31) || (x < 0) || (y < 0)) {
            throw new TilePixelOutOfRangeException("Invalid Pixel Coordinates");
        }

        int[] colorBands = new int[] { newPixel.getRed(), newPixel.getGreen(), newPixel.getBlue(),
                newPixel.getAlpha() };
        setPixel(x, y, colorBands);
    }

    /**
     *
     * @param x
     * @param y
     * @return
     * @throws TilePixelOutOfRangeException
     */
    public Color getPixel(int x, int y) throws TilePixelOutOfRangeException {
        if ((x > 31) || (y > 31) || (x < 0) || (y < 0)) {
            throw new TilePixelOutOfRangeException("Invalid Pixel Coordinates");
        }

        int[] pixel = getPixel(x, y, new int[getNumBands()]);

        return new Color(pixel[0], pixel[1], pixel[2], pixel[3]);
    }

    /**
     *
     * @return
     */
    public BufferedImage getTileAsImage() {
        if (tileImage == null) {
            tileImage = new BufferedImage(getWidth(), getHeight(), BufferedImage.TYPE_INT_ARGB);
            tileImage.setData(this);
        }

        return tileImage;
    }

}
