/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Rectangle;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public interface Brush {

	/**
	 *
	 *
	 * @return
	 */
	public int getAffectedLayers();

	/**
	 *
	 *
	 * @return
	 */
	public Rectangle getBounds();

	/**
	 *
	 *
	 * @param container
	 * @param layer
	 */
	public void startPaint(MultiLayerContainer container, int layer);

	/**
	 *
	 *
	 * @param x
	 * @param y
	 * @param selection
	 * @return
	 * @throws Exception
	 */
	public Rectangle doPaint(int x, int y, Rectangle selection)
			throws Exception;

	/**
   *
   *
   */
	public void endPaint();

	/**
	 *
	 *
	 * @param g2d
	 * @param view
	 */
	public void drawPreview(Graphics2D g2d, AbstractBoardView view);

	/**
	 *
	 *
	 * @param g2d
	 * @param dimension
	 * @param view
	 */
	public void drawPreview(Graphics2D g2d, Dimension dimension,
			AbstractBoardView view);

	/**
	 *
	 *
	 * @param brush
	 * @return
	 */
	public boolean equals(Brush brush);

}
