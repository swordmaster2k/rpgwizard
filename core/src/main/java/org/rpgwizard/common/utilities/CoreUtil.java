/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.utilities;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author Joshua Michael Daly
 */
public class CoreUtil {

    private static final Logger LOGGER = LoggerFactory.getLogger(CoreUtil.class);

    /**
     * Based on:
     * 
     * https://stackoverflow.com/a/3514297
     * 
     * @param source
     * @return
     */
    public static BufferedImage copy(BufferedImage source) {
        if (source == null) {
            return source;
        }
        final ColorModel colorModel = source.getColorModel();
        final boolean isAlphaPremultiplied = colorModel.isAlphaPremultiplied();
        final WritableRaster raster = source.copyData(source.getRaster().createCompatibleWritableRaster());
        return new BufferedImage(colorModel, raster, isAlphaPremultiplied, null);
    }

    public static BufferedImage loadBufferedImage(String fileName) throws IOException {
        LOGGER.info("Loading image fileName=[{}]", fileName);

        BufferedImage image = null;
        try {
            if (!fileName.equals("")) {
                FileInputStream fis = new FileInputStream(System.getProperty("project.path") + File.separator
                        + CoreProperties.getProperty("rpgwizard.directory.textures") + File.separator + fileName);
                image = ImageIO.read(fis);
            }
        } catch (IOException ex) {
            LOGGER.error("Failed to load image.", ex);
            throw ex;
        }

        return image;
    }

}
