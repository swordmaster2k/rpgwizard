/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.rpgwizard.common.utilities.CoreProperties;

/**
 * Represents an image as used on a <code>Board</code>.
 *
 * @author Joshua Michael Daly
 */
public class BoardImage implements Cloneable {

	private long drawType;
	private long layer;
	private long boundLeft; // should be RECT
	private long boundTop;
	private long transparentColour;
	private long canvasPointer; // Wont be needed in Java
	private double scrollX;
	private double scrollY;
	private String fileName;
	private BufferedImage image;

	private int scrollRatio;

	/**
	 * Gets the filename associated with this image.
	 * 
	 * @return filename
	 */
	public String getFileName() {
		return fileName;
	}

	/**
	 * Sets the filename of this image, opens the new file that was specified.
	 * 
	 * @param fileName
	 */
	public void setFileName(String fileName) {
		this.fileName = fileName;

		try {
			if (!fileName.equals("")) {
				FileInputStream fis = new FileInputStream(
						System.getProperty("project.path")
								+ File.separator
								+ CoreProperties
										.getProperty("toolkit.directory.bitmap")
								+ File.separator + fileName);
				image = ImageIO.read(fis);
			}
		} catch (IOException e) {
			System.out.println(e.toString());
		}
	}

	/**
	 * Gets the board images as a displayable image.
	 * 
	 * @return image
	 */
	public BufferedImage getAsImage() {
		return image;
	}

	/**
	 * Gets the draw type.
	 * 
	 * @return draw type
	 */
	public long getDrawType() {
		return drawType;
	}

	/**
	 * Sets the draw type for this image
	 * 
	 * @param drawType
	 *            draw type
	 */
	public void setDrawType(long drawType) {
		this.drawType = drawType;
	}

	/**
	 * Gets the layer this image belongs to.
	 * 
	 * @return layer it is on
	 */
	public long getLayer() {
		return layer;
	}

	/**
	 * Sets the layer this image belongs to.
	 * 
	 * @param layer
	 *            image layer
	 */
	public void setLayer(long layer) {
		this.layer = layer;
	}

	/**
	 * Gets the left bound of this image.
	 * 
	 * @return left bound
	 */
	public long getBoundLeft() {
		return boundLeft;
	}

	/**
	 * Sets the left bound of this image.
	 * 
	 * @param boundLeft
	 *            left bound of image
	 */
	public void setBoundLeft(long boundLeft) {
		this.boundLeft = boundLeft;
	}

	/**
	 * Gets the top bound of this image.
	 * 
	 * @return top bound
	 */
	public long getBoundTop() {
		return boundTop;
	}

	/**
	 * Sets the top bound of this image.
	 * 
	 * @param boundTop
	 *            top bound of image
	 */
	public void setBoundTop(long boundTop) {
		this.boundTop = boundTop;
	}

	/**
	 * Gets the transparent colour used by this image.
	 * 
	 * @return transparent colour
	 */
	public long getTransparentColour() {
		return transparentColour;
	}

	/**
	 * Sets the transparent colour used by this image.
	 * 
	 * @param transparentColour
	 *            transparent colour
	 */
	public void setTransparentColour(long transparentColour) {
		this.transparentColour = transparentColour;
	}

	/**
	 * Gets the canvas pointer.
	 * 
	 * @return pointer
	 */
	public long getCanvasPointer() {
		return canvasPointer;
	}

	/**
	 * Sets the canvas pointer.
	 * 
	 * @param canvasPointer
	 *            canvas pointer
	 */
	public void setCanvasPointer(long canvasPointer) {
		this.canvasPointer = canvasPointer;
	}

	/**
	 * Gets the scroll x as the player moves.
	 * 
	 * @return amount to shift in x
	 */
	public double getScrollX() {
		return scrollX;
	}

	/**
	 * Sets the scroll x as the player moves.
	 * 
	 * @param scrollX
	 *            amount to shift in x
	 */
	public void setScrollX(double scrollX) {
		this.scrollX = scrollX;
	}

	/**
	 * Gets the scroll y as the player moves.
	 * 
	 * @return amount to shift in y
	 */
	public double getScrollY() {
		return scrollY;
	}

	/**
	 * Sets the scroll y as the player moves.
	 * 
	 * @param scrollY
	 *            amount to shift in y
	 */
	public void setScrollY(double scrollY) {
		this.scrollY = scrollY;
	}

	/**
	 * Gets the scroll ratio.
	 * 
	 * @return scroll ratio
	 */
	public int getScrollRatio() {
		return scrollRatio;
	}

	/**
	 * Sets the scroll ratio
	 * 
	 * @param ratio
	 *            scroll ratio
	 */
	public void setScrollRatio(int ratio) {
		scrollRatio = ratio;
	}

	/**
	 * Clones the board image directly.
	 * 
	 * @return @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		super.clone();

		BoardImage clone = new BoardImage();
		clone.boundLeft = boundLeft;
		clone.boundTop = boundTop;
		clone.canvasPointer = canvasPointer;
		clone.drawType = drawType;
		clone.fileName = fileName;
		clone.image = image;
		clone.layer = layer;
		clone.scrollRatio = scrollRatio;
		clone.scrollX = scrollX;
		clone.scrollY = scrollY;
		clone.transparentColour = transparentColour;

		return clone;
	}
}
