/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite;

import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.rpgwizard.editor.ui.AnimatedPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public class AnimationsTablePanel extends JPanel {

	private final ImagePanel profilePanel;

	public AnimationsTablePanel(ImagePanel profilePanel) {
		super(new BorderLayout());
		this.profilePanel = profilePanel;
	}

	@Override
	public Dimension getPreferredSize() {
		super.getPreferredSize();
		return calculateDimensions(super.getPreferredSize().width);
	}

	@Override
	public Dimension getMaximumSize() {
		return calculateDimensions(super.getMaximumSize().width);
	}

	@Override
	public Dimension getMinimumSize() {
		return calculateDimensions(super.getMinimumSize().width);
	}

	private Dimension calculateDimensions(int width) {
		int height = profilePanel.getHeight() - AnimatedPanel.DEFAULT_HEIGHT;

		return new Dimension(width, height);
	}

}
