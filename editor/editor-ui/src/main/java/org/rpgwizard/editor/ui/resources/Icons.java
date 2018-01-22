/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.resources;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

import javax.swing.ImageIcon;

/**
 * Lazy-loading and caching resource provider.
 *
 * @author Chris Hutchinson <chris@cshutchinson.com>
 */
public final class Icons {

    public enum Size {
        SMALL, LARGE
    };

    private static final Map<Integer, ImageIcon> CACHE;

    static {
        CACHE = new HashMap<>();
    }

    public static final ImageIcon getIcon(String name, Size size) {
        int key = 0;
        key ^= name.hashCode();
        key ^= size.hashCode() * 31;
        if (!CACHE.containsKey(key)) {
            CACHE.put(key, getImageIcon(name, size));
        }
        return CACHE.get(key);
    }

    public static final ImageIcon getIcon(String name) {
        return getIcon(name, Size.SMALL);
    }

    public static final ImageIcon getSmallIcon(String name) {
        return getIcon(name, Size.SMALL);
    }

    public static final ImageIcon getLargeIcon(String name) {
        return getIcon(name, Size.LARGE);
    }

    public static final BufferedImage toBufferedImage(ImageIcon icon) {
        BufferedImage bufferedImage = new BufferedImage(icon.getIconWidth(), icon.getIconHeight(),
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = bufferedImage.createGraphics();
        icon.paintIcon(null, g, 0, 0);
        g.dispose();

        return bufferedImage;
    }

    private static ImageIcon getImageIcon(String key, Size size) {
        final String path = String.format("/editor/%s/%s.png", size.name().toLowerCase(), key);
        final ImageIcon icon = new ImageIcon(Icons.class.getResource(path));
        return icon;
    }
}
