/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.rpgwizard.common.utilities.CoreProperties;

/**
 *
 * @author Joshua Michael Daly
 */
public class SpriteSheet {

    private final String fileName;
    private final int x;
    private final int y;
    private final int width;
    private final int height;

    // Non-IO.
    private BufferedImage image;

    public SpriteSheet() {
        fileName = "";
        x = 0;
        y = 0;
        width = 0;
        height = 0;
    }

    public SpriteSheet(String image, int x, int y, int width, int height) {
        this.fileName = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public String getFileName() {
        return fileName;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public BufferedImage getImage() {
        return image;
    }

    public BufferedImage loadImage() throws IOException {
        File file = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("toolkit.directory.graphics") + File.separator + fileName);
        image = ImageIO.read(file);

        return image;
    }

    public BufferedImage getFrame(int index, int width, int height) {
        if (width > image.getWidth()) {
            width = image.getWidth();
        }
        if (height > image.getHeight()) {
            height = image.getHeight();
        }

        int columns = Math.round(image.getWidth() / width);
        int x1 = (index % columns) * width;
        int y1 = (Math.round(index / columns)) * height;

        return image.getSubimage(x1, y1, width, height);
    }

    public int getFrameCount(int width, int height) {
        int columns = Math.round(image.getWidth() / width);
        int rows = Math.round(image.getHeight() / height);

        if (columns == 0) {
            columns = 1;
        }
        if (rows == 0) {
            rows = 1;
        }

        return columns * rows;
    }

}
