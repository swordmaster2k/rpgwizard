/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.animation;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.SpriteSheet;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.TransparentDrawer;

/**
 *
 * @author Joshua Michael Daly
 */
public class AddSpriteSheetButton extends SpriteSheetImage {

    public AddSpriteSheetButton(Animation animation) {
        this.animation = animation;
        dimension = new Dimension(50, 50);
    }

    @Override
    public void paint(Graphics g) {
        TransparentDrawer.drawTransparentBackground(g, dimension.width, dimension.height);

        Image image = Icons.getIcon("plus", Icons.Size.LARGE).getImage();

        int x = getWidth() / 2 - (image.getWidth(null) / 2);
        int y = getHeight() / 2 - (image.getHeight(null) / 2);
        g.drawImage(Icons.getIcon("plus", Icons.Size.LARGE).getImage(), x, y, this);
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        File imageFile = EditorFileManager.browseLocationBySubdir(EditorFileManager.getGraphicsSubdirectory(),
                EditorFileManager.getImageFilterDescription(), EditorFileManager.getImageExtensions());

        if (imageFile != null) {
            if (EditorFileManager.validatePathStartsWith(imageFile, new File(EditorFileManager.getGraphicsPath()))) {
                try {
                    BufferedImage image = ImageIO.read(imageFile);

                    // TODO: Work around until user can specifiy region.
                    int x = 0;
                    int y = 0;
                    int width = image.getWidth();
                    int height = image.getHeight();

                    String remove = EditorFileManager.getGraphicsPath();
                    String path = imageFile.getAbsolutePath().replace(remove, "");
                    animation.setSpriteSheet(new SpriteSheet(path, x, y, width, height));
                    animation.setAnimationHeight(height); // Save the user an extra step.
                } catch (IOException ex) {
                    Logger.getLogger(AddSpriteSheetButton.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

}
