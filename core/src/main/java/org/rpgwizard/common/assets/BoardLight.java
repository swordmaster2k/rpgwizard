/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;

/**
 * A board light.
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class BoardLight implements Cloneable {

	private long layer;
	private long type;
	private Color color;
	private ArrayList<Color> colors;
	private ArrayList<Point> points;

	/**
	 * Creates a blank board light.
	 */
	public BoardLight() {

	}

	/**
	 * Gets the associated layer.
	 *
	 * @return layer index
	 */
	public long getLayer() {
		return layer;
	}

	/**
	 * Sets the light layer index.
	 *
	 * @param layer
	 *            layer index
	 */
	public void setLayer(long layer) {
		this.layer = layer;
	}

	/**
	 * Gets the type of light.
	 *
	 * @return type
	 */
	public long getType() {
		return type;
	}

	/**
	 * Sets the light type.
	 *
	 * @param eType
	 *            type
	 */
	public void setType(long eType) {
		type = eType;
	}

	/**
	 * Gets the ambient colour for this light.
	 *
	 * @return colour from light
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * Sets the light colour.
	 *
	 * @param color
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * Gets the associated colours with this light.
	 *
	 * @return colours
	 */
	public ArrayList<Color> getColors() {
		return colors;
	}

	/**
	 * Sets the lights colours.
	 *
	 * @param colors
	 */
	public void setColors(ArrayList<Color> colors) {
		this.colors = colors;
	}

	/**
	 * Gets the points.
	 *
	 * @return points
	 */
	public ArrayList<Point> getPoints() {
		return points;
	}

	/**
	 * Sets the lights points.
	 *
	 * @param points
	 */
	public void setPoints(ArrayList<Point> points) {
		this.points = points;
	}

	/**
	 * Add a new point to this light.
	 *
	 * @param point
	 *            new point
	 */
	public void addPoint(Point point) {
		// TODO: Take (x, y) as parameters instead of java.awt.Point.
		// Consistency with BoardVector#addPoint
		points.add(point);
	}

	/**
	 * Add a new colour effect to this light.
	 *
	 * @param color
	 */
	public void addColor(Color color) {
		colors.add(color);
	}

	/**
	 * Directly clones this light.
	 *
	 * @return a clone
	 * @throws CloneNotSupportedException
	 */
	@Override
	public Object clone() throws CloneNotSupportedException {
		super.clone();

		BoardLight clone = new BoardLight();
		clone.setLayer(getLayer());
		clone.setType(type);
		clone.setColor(color);
		clone.setColors((ArrayList<Color>) colors.clone());
		clone.setPoints((ArrayList<Point>) points.clone());

		return clone;
	}
}
