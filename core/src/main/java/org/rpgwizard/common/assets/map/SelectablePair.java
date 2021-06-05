/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.rpgwizard.common.assets.map;

import org.apache.commons.lang3.tuple.Pair;
import org.rpgwizard.common.Selectable;

/**
 * @author Joshua Michael Daly
 * @param <L>
 * @param <R>
 */
public final class SelectablePair<L extends String, R extends Selectable> extends Pair<L, R> implements Selectable {

    public final L left;
    public final R right;

    public SelectablePair(L left, R right) {
        super();
        this.left = left;
        this.right = right;
    }

    @Override
    public boolean isSelected() {
        return right.isSelected();
    }

    @Override
    public void setSelectedState(boolean state) {
        right.setSelectedState(state);
    }

    @Override
    public L getLeft() {
        return left;
    }

    @Override
    public R getRight() {
        return right;
    }

    @Override
    public R getValue() {
        return right;
    }

    @Override
    public R setValue(R value) {
        throw new UnsupportedOperationException();
    }

}