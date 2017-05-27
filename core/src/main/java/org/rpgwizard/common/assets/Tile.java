/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.util.Objects;

/**
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class Tile extends WritableRaster {

	private TileSet tileSet;
	private int index;

	private BufferedImage tileImage;

	/**
     *
     */
	public Tile() {
		super(ColorModel.getRGBdefault().createCompatibleSampleModel(32, 32),
				new Point(0, 0));
	}

	/**
	 *
	 * @param tileSet
	 * @param index
	 */
	public Tile(TileSet tileSet, int index) {
		super(ColorModel.getRGBdefault().createCompatibleSampleModel(32, 32),
				new Point(0, 0));
		this.tileSet = tileSet;
		this.index = index;
	}

	/*
	 * *************************************************************************
	 * Public Getters and Setters
	 * *************************************************************************
	 */
	/**
	 *
	 * @return
	 */
	public int getIndex() {
		return index;
	}

	/**
	 *
	 * @param index
	 */
	public void setIndex(int index) {
		this.index = index;
	}

	/**
	 *
	 * @return
	 */
	public String getName() {
		return tileSet.getName() + getIndex();
	}

	public TileSet getTileSet() {
		return tileSet;
	}

	public void setTileSet(TileSet tileSet) {
		this.tileSet = tileSet;
	}

	/**
	 *
	 * @param x
	 * @param y
	 * @param newPixel
	 * @throws TilePixelOutOfRangeException
	 */
	public void setPixel(int x, int y, Color newPixel)
			throws TilePixelOutOfRangeException {
		if ((x > 31) || (y > 31) || (x < 0) || (y < 0)) {
			throw new TilePixelOutOfRangeException("Invalid Pixel Coordinates");
		}

		int[] colorBands = new int[]{newPixel.getRed(), newPixel.getGreen(),
				newPixel.getBlue(), newPixel.getAlpha()};
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
			tileImage = new BufferedImage(getWidth(), getHeight(),
					BufferedImage.TYPE_INT_ARGB);
			tileImage.setData(this);
		}

		return tileImage;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 31 * hash + Objects.hashCode(this.tileSet);
		hash = 31 * hash + this.index;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Tile other = (Tile) obj;
		if (this.tileSet != other.tileSet) {
			return false; // Blank tiles.
		}
		if (this.tileSet == null && other.tileSet == null) {
			return true;
		}
		if ((!this.tileSet.getName().equals(other.tileSet.getName()))
				|| this.index != other.index) {
			return false;
		}

		return true;
	}

}
