/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.utilities;

import java.awt.Color;
import java.util.ArrayList;

/**
 * 
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public class DOSColors {
	private ArrayList<Color> colors;

	public DOSColors()
    {
        colors = new ArrayList<>();
        // 00: black
        colors.add(new Color(0, 0, 0));
        // 01: blue
        colors.add(new Color(0, 0, 170));
        // 02: green
        colors.add(new Color(0, 170, 0));
        // 03: cyan
        colors.add(new Color(0, 170, 170));
        // 04: red
        colors.add(new Color(170, 0, 0));
        // 05: magenta
        colors.add(new Color(170, 0, 170));
        // 06: brown
        colors.add(new Color(170, 85, 0));
        // 07: white / light gray
        colors.add(new Color(170, 170, 170));
        // 08: dark gray / bright black
        colors.add(new Color(85, 85, 85));
        // 09: bright blue
        colors.add(new Color(85, 85, 255));
        // 10: bright green
        colors.add(new Color(85, 255, 85));
        // 11: bright cyan
        colors.add(new Color(85, 255, 255));
        // 12: bright red
        colors.add(new Color(255, 85, 85));
        // 13: bright magenta
        colors.add(new Color(255, 85, 255));
        // 14: bright yellow
        colors.add(new Color(255, 255, 85));
        // 15: white
        colors.add(new Color(255, 255, 255));
    }
	public Color getColor(int index) {
		return colors.get(index);
	}
}
