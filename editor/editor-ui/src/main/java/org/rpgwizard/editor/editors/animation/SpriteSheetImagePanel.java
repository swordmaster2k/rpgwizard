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
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import javax.swing.JButton;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import org.apache.commons.lang3.StringUtils;
import org.rpgwizard.common.assets.SpriteSheet;
import org.rpgwizard.editor.ui.AbstractImagePanel;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.rpgwizard.editor.utilities.TransparentDrawer;

/**
 *
 * @author Joshua Michael Daly
 */
public final class SpriteSheetImagePanel extends AbstractImagePanel implements MouseMotionListener {

    private final SpriteSheet spriteSheet;
    private String fileName;
    private Image spriteSheetImage;
    private Rectangle cursor;
    private Rectangle selection;

    private int tileWidth;
    private int tileHeight;

    private final JButton okButton;

    public SpriteSheetImagePanel(SpriteSheet spriteSheet, int tileWidth, int tileHeight, Dimension dimension,
            JButton okButton) {
        super(dimension);
        setToolTipText("Double click to select an image.");
        addMouseMotionListener(this);
        setAutoscrolls(true);

        this.spriteSheet = spriteSheet;
        this.fileName = spriteSheet.getFileName();
        this.cursor = new Rectangle();
        this.selection = new Rectangle(spriteSheet.getX() / tileWidth, spriteSheet.getY() / tileHeight,
                spriteSheet.getWidth() / tileWidth, spriteSheet.getHeight() / tileHeight);
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
        this.okButton = okButton;
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

    public String getFileName() {
        return fileName;
    }

    public Image getSpriteSheetImage() {
        return spriteSheetImage;
    }

    public Rectangle getSelection() {
        return selection;
    }

    public int getTileWidth() {
        return tileWidth;
    }

    public void setTileWidth(int tileWidth) {
        this.tileWidth = tileWidth;
    }

    public int getTileHeight() {
        return tileHeight;
    }

    public void setTileHeight(int tileHeight) {
        this.tileHeight = tileHeight;
    }

    public void init() throws IOException {
        if (StringUtils.isNotBlank(fileName)) {
            spriteSheetImage = spriteSheet.loadImage();
            updateDimension();
        }
    }

    public void reset() {
        cursor = new Rectangle();
        selection = new Rectangle();
        repaint();
    }

    public void updateDimension() {
        dimension = new Dimension(spriteSheetImage.getWidth(this), spriteSheetImage.getHeight(this));
        revalidate();
        repaint();
    }

    /**
     * Based on: http://www.java2s.com/Code/Java/Swing-JFC/ScrollingaCelltotheCenterofaJTableComponent.htm
     */
    public void ensureVisible() {
        JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
        if (viewPort != null) {
            Rectangle rect = new Rectangle(selection.x * tileWidth, selection.y * tileHeight,
                    selection.width * tileWidth, selection.height * tileHeight);
            Rectangle viewRect = viewPort.getViewRect();
            rect.setLocation(rect.x - viewRect.x, rect.y - viewRect.y);
            int centerX = (viewRect.width - rect.width) / 2;
            int centerY = (viewRect.height - rect.height) / 2;
            if (rect.x < centerX) {
                centerX = -centerX;
            }
            if (rect.y < centerY) {
                centerY = -centerY;
            }
            rect.translate(centerX, centerY);
            viewPort.scrollRectToVisible(rect);
        }
    }

    @Override
    public void paint(Graphics g) {
        TransparentDrawer.drawTransparentBackground(g, getWidth(), getHeight());

        int x, y;
        Image image;
        if (spriteSheetImage == null) {
            image = Icons.getIcon("image", Icons.Size.LARGE).getImage();
            x = (getWidth() - image.getWidth(this)) / 2;
            y = (getHeight() - image.getHeight(this)) / 2;
        } else {
            image = spriteSheetImage;
            x = y = 0;
        }

        g.drawImage(image, x, y, this);
        g.setColor(Color.LIGHT_GRAY);
        g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

        if (spriteSheetImage != null) {
            GuiHelper.drawGrid((Graphics2D) g, tileWidth, tileHeight, new Rectangle(0, 0, getWidth(), getHeight()));
            GuiHelper.drawSelection((Graphics2D) g, tileWidth, tileHeight, selection, 0);
            GuiHelper.drawSelection((Graphics2D) g, tileWidth, tileHeight, cursor, 1);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (e.getClickCount() == 2) {
                File imageFile = EditorFileManager.browseLocationBySubdir(EditorFileManager.getGraphicsSubdirectory(),
                        EditorFileManager.getImageFilterDescription(), EditorFileManager.getImageExtensions());

                if (imageFile != null) {
                    String remove = EditorFileManager.getGraphicsPath();
                    fileName = imageFile.getAbsolutePath().replace(remove, "");

                    if (bufferedImages.size() > 0) {
                        bufferedImages.remove();
                    }

                    addImage(imageFile);

                    spriteSheetImage = bufferedImages.getFirst();
                    updateDimension();
                }
            }
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        cursor.x = e.getX() / tileWidth;
        cursor.y = e.getY() / tileHeight;
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && spriteSheetImage != null) {
            selection = new Rectangle(e.getX() / tileWidth, e.getY() / tileHeight, 1, 1);
            if (!okButton.isEnabled()) {
                okButton.setEnabled(true);
            }
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && spriteSheetImage != null) {
            JViewport viewPort = (JViewport) SwingUtilities.getAncestorOfClass(JViewport.class, this);
            if (viewPort != null) {
                scrollRectToVisible(new Rectangle(e.getX(), e.getY(), 1, 1));
            }

            if (!GuiHelper.checkSelectionBounds(spriteSheetImage.getWidth(this), spriteSheetImage.getHeight(this),
                    e.getX(), e.getY())) {
                return;
            }
            selection = new Rectangle(selection.x, selection.y, 1, 1);
            selection.add(e.getX() / tileWidth, e.getY() / tileHeight);
            repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (SwingUtilities.isLeftMouseButton(e) && spriteSheetImage != null) {

        }
    }

}
