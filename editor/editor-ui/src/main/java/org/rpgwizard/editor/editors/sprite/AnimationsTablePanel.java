/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite;

import org.rpgwizard.editor.ui.ImagePanel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import javax.swing.JPanel;
import org.rpgwizard.editor.ui.AnimatedPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public class AnimationsTablePanel extends JPanel {

    private final int referenceHeight;

    public AnimationsTablePanel(int height) {
        super(new BorderLayout());
        this.referenceHeight = height;
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
        int height = referenceHeight - AnimatedPanel.DEFAULT_HEIGHT;

        return new Dimension(width, height);
    }

}
