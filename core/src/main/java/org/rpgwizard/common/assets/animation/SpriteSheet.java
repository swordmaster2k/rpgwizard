/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.animation;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.rpgwizard.common.utilities.CoreProperties;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
@NoArgsConstructor
@EqualsAndHashCode
@ToString(callSuper = true, includeFieldNames = true)
public class SpriteSheet {

    private String image;
    private int x;
    private int y;
    private int width;
    private int height;

    // Non-IO.
    @JsonIgnore
    private BufferedImage bufferedImage;
    @JsonIgnore
    private BufferedImage selection;
    @JsonIgnore
    private int tileWidth;
    @JsonIgnore
    private int tileHeight;

    public SpriteSheet(String image, int x, int y, int width, int height, int tileWidth, int tileHeight) {
        this.image = image;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.tileWidth = tileWidth;
        this.tileHeight = tileHeight;
    }

    public BufferedImage loadImage() throws IOException {
        File file = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("rpgwizard.directory.textures") + File.separator + image);
        bufferedImage = ImageIO.read(file);
        return bufferedImage;
    }

    public BufferedImage loadSelection() throws IOException {
        File file = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("rpgwizard.directory.textures") + File.separator + image);
        BufferedImage temp = ImageIO.read(file);

        // Ensure it does not go out of bounds, and throw a raster exception
        int requestedWidth = width;
        int requestedHeight = height;
        if (temp.getWidth() < requestedWidth) {
            requestedWidth = temp.getWidth();
        }
        if (temp.getHeight() < requestedHeight) {
            requestedHeight = temp.getHeight();
        }
        int requestedX = x;
        int requestedY = y;
        if (temp.getWidth() < requestedWidth + x) {
            requestedX = 0;
        }
        if (temp.getHeight() < requestedHeight + y) {
            requestedY = 0;
        }

        selection = temp.getSubimage(requestedX, requestedY, requestedWidth, requestedHeight);
        return selection;
    }

    public BufferedImage getFrame(int index, int width, int height) {
        if (width > selection.getWidth()) {
            width = selection.getWidth();
        }
        if (height > selection.getHeight()) {
            height = selection.getHeight();
        }

        int columns = Math.round(selection.getWidth() / width);
        int x1 = (index % columns) * width;
        int y1 = (Math.round(index / columns)) * height;

        return selection.getSubimage(x1, y1, width, height);
    }

    public int getFrameCount(int width, int height) {
        int columns = Math.round(selection.getWidth() / width);
        int rows = Math.round(selection.getHeight() / height);

        if (columns == 0) {
            columns = 1;
        }
        if (rows == 0) {
            rows = 1;
        }

        return columns * rows;
    }

}
