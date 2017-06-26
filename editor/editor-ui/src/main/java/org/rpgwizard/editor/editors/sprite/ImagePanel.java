/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite;

import org.rpgwizard.editor.ui.AbstractImagePanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.io.File;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.TransparentDrawer;

/**
 *
 * @author Joshua Michael Daly
 */
public class ImagePanel extends AbstractImagePanel {

	private final Image defaultImage;

	private Dimension scaledDimension;

	public ImagePanel() {
		super(new Dimension(250, 500));
		setToolTipText("Double click to select an image.");

		defaultImage = Icons.getIcon("image", Icons.Size.LARGE).getImage();
		scaledDimension = null;
	}

	public ImagePanel(Dimension dimension) {
		super(dimension);
		setToolTipText("Double click to select an image.");

		defaultImage = Icons.getIcon("image", Icons.Size.LARGE).getImage();
		scaledDimension = null;
	}

	public ImagePanel(Dimension dimension, Image image) {
		super(dimension);
		setToolTipText("Double click to select an image.");

		defaultImage = image;
		scaledDimension = null;
	}

	@Override
	public Dimension getPreferredSize() {
		return dimension;
	}

	@Override
	public Dimension getMaximumSize() {
		return dimension;
	}

	@Override
	public Dimension getMinimumSize() {
		return dimension;
	}

	@Override
	public void paint(Graphics g) {
		if (scaledDimension == null) {
			// First call to paint.
			calculateScaledDimension();
		}

		TransparentDrawer.drawTransparentBackground(g, getWidth(), getHeight());

		Image image;
		if (bufferedImages.size() > 0) {
			image = bufferedImages.getFirst();
		} else {
			image = defaultImage;
		}

		int x = (getWidth() - scaledDimension.width) / 2;
		int y = (getHeight() - scaledDimension.height) / 2;
		g.drawImage(image, x, y, scaledDimension.width, scaledDimension.height,
				this);

		g.setColor(Color.LIGHT_GRAY);
		g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getClickCount() == 2) {
			MainWindow mainWindow = MainWindow.getInstance();
			File imageFile = EditorFileManager.browseLocationBySubdir(
					EditorFileManager.getGraphicsSubdirectory(),
					EditorFileManager.getImageFilterDescription(),
					EditorFileManager.getImageExtensions());

			if (imageFile != null) {
				if (bufferedImages.size() > 0) {
					bufferedImages.remove();
				}

				addImage(imageFile);
				calculateScaledDimension();
				repaint();
			}
		}
	}

	private void calculateScaledDimension() {
		Image image;
		if (bufferedImages.isEmpty()) {
			image = defaultImage;
		} else {
			image = bufferedImages.getFirst();
		}

		int originalWidth = image.getWidth(this);
		int originalHeight = image.getHeight(this);
		int boundWidth = getWidth();
		int boundHeight = getHeight();
		int newWidth = originalWidth;
		int newHeight = originalHeight;

		if (originalWidth > boundWidth) {
			newWidth = boundWidth;
			newHeight = (newWidth * originalHeight) / originalWidth;
		}

		if (newHeight > boundHeight) {
			newHeight = boundHeight;
			newWidth = (newHeight * originalWidth) / originalHeight;
		}

		scaledDimension = new Dimension(newWidth, newHeight);
	}

}
