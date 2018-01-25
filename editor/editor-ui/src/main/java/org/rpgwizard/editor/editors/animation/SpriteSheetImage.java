/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.animation;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.JPanel;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.SpriteSheet;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.rpgwizard.editor.utilities.TransparentDrawer;

/**
 *
 * @author Joshua Michael Daly
 */
public class SpriteSheetImage extends JPanel implements MouseListener {

    protected Animation animation;
    private SpriteSheet spriteSheet;

    protected Dimension dimension;

    private boolean entered;

    public SpriteSheetImage() {
        animation = null;
        spriteSheet = null;
        entered = false;
        dimension = new Dimension(150, 150);

        addMouseListener(this);
    }

    public SpriteSheetImage(Animation animation, SpriteSheet sheet) {
        this.animation = animation;
        spriteSheet = sheet;
        entered = false;
        dimension = new Dimension(150, 150);

        addMouseListener(this);
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
        TransparentDrawer.drawTransparentBackground(g, dimension.width, dimension.height);

        BufferedImage image = spriteSheet.getImage();
        if (image != null) {
            g.drawImage(image, 0, 0, null);
            GuiHelper.drawGrid((Graphics2D) g, animation.getAnimationWidth(), animation.getAnimationHeight(),
                    new Rectangle(dimension.width, dimension.height));
        }

        if (entered) {
            g.setColor(new Color(0, 0, 255, 64));
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            File imageFile = EditorFileManager.browseLocationBySubdir(EditorFileManager.getGraphicsSubdirectory(),
                    EditorFileManager.getImageFilterDescription(), EditorFileManager.getImageExtensions());

            if (imageFile != null) {
                if (EditorFileManager.validatePathStartsWith(imageFile,
                        new File(EditorFileManager.getGraphicsPath()))) {
                    String remove = EditorFileManager.getGraphicsPath();
                    String path = imageFile.getAbsolutePath().replace(remove, "");

                    spriteSheet = new SpriteSheet(path, 0, 0, dimension.width, dimension.height);
                    animation.setSpriteSheet(spriteSheet);
                    repaint();
                }
            }
        } else if (e.getButton() == MouseEvent.BUTTON2) {
            animation.removeSpriteSheet();
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        entered = !entered;
        repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        entered = !entered;
        repaint();
    }

    public BufferedImage loadImage() throws IOException {
        BufferedImage image = spriteSheet.loadImage();
        dimension = new Dimension(image.getWidth() + 1, image.getHeight() + 1);
        return image;
    }

}
