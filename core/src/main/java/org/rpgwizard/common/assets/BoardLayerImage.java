/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rpgwizard.common.assets;

import org.rpgwizard.common.utilities.CoreUtil;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;
import org.rpgwizard.common.Selectable;

/**
 *
 * @author Joshua Michael Daly
 */
public class BoardLayerImage implements Selectable {

	private String src;
	private int x;
	private int y;

	// Non-IO.
	private BufferedImage image;
	private int layer;
	private boolean selected;

	public BoardLayerImage() {
		this.src = "";
		this.x = 0;
		this.y = 0;
		this.image = null;
		this.layer = 0;
		selected = false;
	}

	public BoardLayerImage(String src, int x, int y) {
		this.src = src;
		this.x = x;
		this.y = y;
		this.loadImage();
		selected = false;
	}

	public BoardLayerImage(String src, int x, int y, int layer) {
		this(src, x, y);
		this.layer = layer;
	}

	public String getSrc() {
		return src;
	}

	public void setSrc(String src) {
		this.src = src;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public BufferedImage getImage() {
		return image;
	}

	public final void loadImage() {
		if (src.isEmpty()) {
			return;
		}
		try {
			image = CoreUtil.loadBufferedImage(src);
		} catch (IOException ex) {
			image = null;
		}
	}

	public final void loadImage(String src) {
		this.src = src;
		try {
			image = CoreUtil.loadBufferedImage(src);
		} catch (IOException ex) {
			image = null;
		}
	}

	public int getLayer() {
		return layer;
	}

	public void setLayer(int layer) {
		this.layer = layer;
	}

	@Override
	public String toString() {
		return "BoardLayerImage{" + "src=" + src + ", x=" + x + ", y=" + y
				+ '}';
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
		final BoardLayerImage other = (BoardLayerImage) obj;
		if (this.x != other.x) {
			return false;
		}
		if (this.y != other.y) {
			return false;
		}
		if (this.layer != other.layer) {
			return false;
		}
		if (!Objects.equals(this.src, other.src)) {
			return false;
		}
		if (this.selected != other.selected) {
			return false;
		}
		return true;
	}

	@Override
	public boolean isSelected() {
		return selected;
	}

	@Override
	public void setSelectedState(boolean state) {
		selected = state;
	}

}
