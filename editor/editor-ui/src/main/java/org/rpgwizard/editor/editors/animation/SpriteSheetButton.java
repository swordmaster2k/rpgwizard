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
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.SpriteSheet;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.rpgwizard.editor.utilities.TransparentDrawer;

/**
 *
 * @author Joshua Michael Daly
 */
public class SpriteSheetButton extends JPanel implements MouseListener {

    protected Animation animation;
    private final SpriteSheet spriteSheet;

    protected Dimension dimension;

    private boolean entered;

    public SpriteSheetButton() {
        animation = null;
        spriteSheet = null;
        entered = false;
        dimension = new Dimension(150, 150);

        addMouseListener(this);
    }

    public SpriteSheetButton(Animation animation, SpriteSheet sheet) {
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

        BufferedImage image = spriteSheet.getSelection();
        if (image != null) {
            g.drawImage(image, 0, 0, null);
            GuiHelper.drawGrid((Graphics2D) g, animation.getSpriteSheet().getTileWidth(),
                    animation.getSpriteSheet().getTileHeight(), new Rectangle(dimension.width, dimension.height));
        }

        if (entered) {
            g.setColor(new Color(0, 0, 255, 64));
            g.fillRect(0, 0, getWidth() - 1, getHeight() - 1);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            try {
                SpriteSheetDialog dialog = new SpriteSheetDialog(MainWindow.getInstance(), animation.getSpriteSheet());
                dialog.display();
                if (dialog.getValue() == null) {
                    return;
                }

                File imageFile = new File(System.getProperty("project.path") + File.separator
                        + CoreProperties.getProperty("toolkit.directory.graphics") + File.separator
                        + animation.getSpriteSheet().getFileName());
                if (EditorFileManager.validatePathStartsWith(imageFile,
                        new File(EditorFileManager.getGraphicsPath()))) {
                    animation.setSpriteSheet(dialog.getValue());
                    animation.setWidth(animation.getSpriteSheet().getTileWidth());
                    animation.setHeight(animation.getSpriteSheet().getTileHeight());
                    repaint();
                }
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(MainWindow.getInstance(), "Error loading sprite sheet!", "Error on Load",
                        JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(SpriteSheetButton.class.getName()).log(Level.SEVERE, null, ex);
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
        BufferedImage image = spriteSheet.loadSelection();
        dimension = new Dimension(image.getWidth() + 1, image.getHeight() + 1);
        return image;
    }

}
