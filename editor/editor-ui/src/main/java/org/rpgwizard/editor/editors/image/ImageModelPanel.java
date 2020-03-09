/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.image;

import javax.swing.JTextField;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.editor.ui.AbstractModelPanel;

/**
 *
 * @author Joshua Micahel Daly
 */
public final class ImageModelPanel extends AbstractModelPanel {

    private final JTextField widthField;
    private final JTextField heightField;

    public ImageModelPanel(Image image) {
        ///
        /// super
        ///
        super(image);
        ///
        /// widthField
        ///
        widthField = getJTextField(String.valueOf(image.getBufferedImage().getWidth()));
        widthField.setEnabled(false);
        ///
        /// heightField
        ///
        heightField = getJTextField(String.valueOf(image.getBufferedImage().getHeight()));
        heightField.setEnabled(false);
        ///
        /// this
        ///
        insert(getJLabel("Width"), widthField);
        insert(getJLabel("Height"), heightField);
    }

}
