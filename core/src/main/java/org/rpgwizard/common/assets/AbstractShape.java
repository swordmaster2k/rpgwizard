/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.List;
import lombok.Data;
import org.rpgwizard.common.Selectable;

/**
 *
 * @author Joshua Michael Daly
 */
@Data
public abstract class AbstractShape implements Selectable {

    protected boolean enabled;
    protected int x;
    protected int y;
    protected List<Point> points;
    protected int radius;

    @JsonIgnore
    protected boolean selected;

    @JsonIgnore
    public int getPointCount() {
        return points.size();
    }

    @JsonIgnore
    public int getPointX(int index) {
        return (int) points.get(index).getX();
    }

    @JsonIgnore
    public int getPointY(int index) {
        return (int) points.get(index).getY();
    }

    public boolean addPoint(int x, int y) {
        return points.add(new Point(x, y));
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public void setSelectedState(boolean state) {
        selected = state;
    }

}
