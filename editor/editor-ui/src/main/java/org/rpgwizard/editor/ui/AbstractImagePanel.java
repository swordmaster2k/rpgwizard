/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public License,
 * v. 2.0. If a copy of the MPL was not distributed with this file, You can
 * obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import java.awt.Dimension;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import org.rpgwizard.editor.ui.listeners.ImagePanelChangeListener;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractImagePanel extends JPanel
        implements
        MouseListener {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractImagePanel.class);

    protected final List<ImagePanelChangeListener> listeners;

    protected File file;
    protected Dimension dimension;
    protected LinkedList<BufferedImage> bufferedImages;

    public AbstractImagePanel() {
        listeners = new ArrayList<>();
        bufferedImages = new LinkedList<>();
        addMouseListener(this);
    }

    public AbstractImagePanel(Dimension dimension) {
        this();
        this.dimension = dimension;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public void setDimension(Dimension dimension) {
        this.dimension = dimension;
    }

    public LinkedList<BufferedImage> getBufferedImages() {
        return bufferedImages;
    }

    public void setBufferedImages(LinkedList<BufferedImage> images) {
        this.bufferedImages = images;
    }

    public File getFile() {
        return file;
    }

    public String getImagePath() {
        return file.getAbsolutePath().replace(EditorFileManager.getGraphicsPath(), "");
    }

    public void addImageListener(ImagePanelChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeImageListener(ImagePanelChangeListener listener) {
        if (listeners.contains(listener)) {
            listeners.remove(listener);
        }
    }

    public void addImage(File file) {
        if (file != null) {
            try {
                this.file = file;
                bufferedImages.add(ImageIO.read(file));
                fireAddedImage();
            } catch (IOException ex) {
                LOGGER.error("Failed to add image file=[{}]", file, ex);
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    private void fireAddedImage() {
        listeners.forEach((listener) -> {
            listener.addedImage();
        });
    }

}
