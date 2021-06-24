/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.image;

import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.swing.JScrollPane;
import lombok.Getter;
import lombok.Setter;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.Image;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.ImagePanel;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Joshua Michael Daly
 */
@Getter
@Setter
public class ImageEditor extends AbstractAssetEditorWindow {

    private final Image image;
    private final ImagePanel imagePanel;

    public ImageEditor(Image image) {
        super("", true, true, true, true, Icons.getIcon("image"));
        this.image = image;

        BufferedImage bufferedImage = this.image.getBufferedImage();
        int width = bufferedImage.getWidth(null);
        if (width < 200) {
            width = 200;
        }
        int height = bufferedImage.getHeight(null);
        if (height < 200) {
            height = 200;
        }

        imagePanel = new ImagePanel(new Dimension(width, height), bufferedImage);
        imagePanel.removeMouseListener(imagePanel);
        imagePanel.setToolTipText(null);

        init(new File(image.getDescriptor().getURI()).getName());
    }

    @Override
    public AbstractAsset getAsset() {
        return image;
    }

    @Override
    public void save() throws Exception {
        // Do nothing
    }

    @Override
    public void saveAs(File file) throws Exception {
        // Do nothing
    }

    private void init(String fileName) {
        JScrollPane scrollPane = new JScrollPane(imagePanel);
        setContentPane(scrollPane);
        setTitle(fileName);
        pack();
    }

}
