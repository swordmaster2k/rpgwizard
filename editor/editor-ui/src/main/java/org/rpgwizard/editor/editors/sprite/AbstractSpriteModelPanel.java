/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite;

import java.awt.Point;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.AbstractSprite;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.editor.ui.AbstractModelPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractSpriteModelPanel extends AbstractModelPanel {

    private final JSpinner baseVectorWidthSpinner;
    private final JSpinner baseVectorHeightSpinner;
    private final JSpinner baseVectorOffsetXSpinner;
    private final JSpinner baseVectorOffsetYSpinner;
    private final JSpinner activationVectorWidthSpinner;
    private final JSpinner activationVectorHeightSpinner;
    private final JSpinner activationVectorOffsetXSpinner;
    private final JSpinner activationVectorOffsetYSpinner;

    private final AbstractSprite sprite;

    public AbstractSpriteModelPanel(AbstractSprite model) {
        // /
        // / super
        // /
        super(model);
        // /
        // / sprite
        // /
        sprite = model;
        // /
        // / baseVectorWidthSpinner
        // /
        baseVectorWidthSpinner = getJSpinner(sprite.getBaseVector().getWidth());
        baseVectorWidthSpinner.setModel(new SpinnerNumberModel(sprite.getBaseVector().getWidth(), 1, 100, 1));
        baseVectorWidthSpinner.addChangeListener((ChangeEvent e) -> {
            int width1 = ((Double) baseVectorWidthSpinner.getValue()).intValue();
            int height1 = (int) sprite.getBaseVector().getHeight();
            // Assumes sprite base vector is rectangular in shape, will be a
            // limitiation.
            BoardVector boardVector = new BoardVector();
            boardVector.addPoint(0, 0);
            boardVector.addPoint(width1, 0);
            boardVector.addPoint(width1, height1);
            boardVector.addPoint(0, height1);
            boardVector.setClosed(true);
            sprite.setBaseVector(boardVector, true);
        });
        // /
        // / baseVectorHeightSpinner
        // /
        baseVectorHeightSpinner = getJSpinner(sprite.getBaseVector().getHeight());
        baseVectorHeightSpinner.setModel(new SpinnerNumberModel(sprite.getBaseVector().getHeight(), 1, 100, 1));
        baseVectorHeightSpinner.addChangeListener((ChangeEvent e) -> {
            int width1 = (int) sprite.getBaseVector().getWidth();
            int height1 = ((Double) baseVectorHeightSpinner.getValue()).intValue();
            // Assumes sprite base vector is rectangular in shape, will be a
            // limitiation.
            BoardVector boardVector = new BoardVector();
            boardVector.addPoint(0, 0);
            boardVector.addPoint(width1, 0);
            boardVector.addPoint(width1, height1);
            boardVector.addPoint(0, height1);
            boardVector.setClosed(true);
            sprite.setBaseVector(boardVector, true);
        });
        // /
        // / baseVectorOffsetXSpinner
        // /
        baseVectorOffsetXSpinner = getJSpinner(sprite.getBaseVectorOffset().getX());
        baseVectorOffsetXSpinner.setModel(new SpinnerNumberModel(sprite.getBaseVectorOffset().getX(), -1000, 1000, 1));
        baseVectorOffsetXSpinner.addChangeListener((ChangeEvent e) -> {
            int x1 = ((Double) baseVectorOffsetXSpinner.getValue()).intValue();
            int y1 = (int) sprite.getBaseVectorOffset().getY();
            sprite.setBaseVectorOffset(new Point(x1, y1), true);
        });
        // /
        // / baseVectorOffsetYSpinner
        // /
        baseVectorOffsetYSpinner = getJSpinner(sprite.getBaseVectorOffset().getY());
        baseVectorOffsetYSpinner.setModel(new SpinnerNumberModel(sprite.getBaseVectorOffset().getY(), -1000, 1000, 1));
        baseVectorOffsetYSpinner.addChangeListener((ChangeEvent e) -> {
            int x1 = (int) sprite.getBaseVectorOffset().getX();
            int y1 = ((Double) baseVectorOffsetYSpinner.getValue()).intValue();
            sprite.setBaseVectorOffset(new Point(x1, y1), true);
        });
        // /
        // / activationVectorWidthSpinner
        // /
        activationVectorWidthSpinner = getJSpinner(sprite.getActivationVector().getWidth());
        activationVectorWidthSpinner
                .setModel(new SpinnerNumberModel(sprite.getActivationVector().getWidth(), 1, 100, 1));
        activationVectorWidthSpinner.addChangeListener((ChangeEvent e) -> {
            int width1 = ((Double) activationVectorWidthSpinner.getValue()).intValue();
            int height1 = (int) sprite.getActivationVector().getHeight();
            // Assumes sprite base vector is rectangular in shape, will be a
            // limitiation.
            BoardVector boardVector = new BoardVector();
            boardVector.addPoint(0, 0);
            boardVector.addPoint(width1, 0);
            boardVector.addPoint(width1, height1);
            boardVector.addPoint(0, height1);
            boardVector.setClosed(true);
            sprite.setActivationVector(boardVector, true);
        });
        // /
        // / baseVectorHeightSpinner
        // /
        activationVectorHeightSpinner = getJSpinner(sprite.getActivationVector().getHeight());
        activationVectorHeightSpinner
                .setModel(new SpinnerNumberModel(sprite.getActivationVector().getHeight(), 1, 100, 1));
        activationVectorHeightSpinner.addChangeListener((ChangeEvent e) -> {
            int width1 = (int) sprite.getActivationVector().getWidth();
            int height1 = ((Double) activationVectorHeightSpinner.getValue()).intValue();
            // Assumes sprite base vector is rectangular in shape, will be a
            // limitiation.
            BoardVector boardVector = new BoardVector();
            boardVector.addPoint(0, 0);
            boardVector.addPoint(width1, 0);
            boardVector.addPoint(width1, height1);
            boardVector.addPoint(0, height1);
            boardVector.setClosed(true);
            sprite.setActivationVector(boardVector, true);
        });
        // /
        // / activationVectorOffsetXSpinner
        // /
        activationVectorOffsetXSpinner = getJSpinner(sprite.getActivationVectorOffset().getX());
        activationVectorOffsetXSpinner
                .setModel(new SpinnerNumberModel(sprite.getActivationVectorOffset().getX(), -1000, 1000, 1));
        activationVectorOffsetXSpinner.addChangeListener((ChangeEvent e) -> {
            int x1 = ((Double) activationVectorOffsetXSpinner.getValue()).intValue();
            int y1 = (int) sprite.getActivationVectorOffset().getY();
            sprite.setActivationVectorOffset(new Point(x1, y1), true);
        });
        // /
        // / activationVectorOffsetYSpinner
        // /
        activationVectorOffsetYSpinner = getJSpinner(sprite.getActivationVectorOffset().getY());
        activationVectorOffsetYSpinner
                .setModel(new SpinnerNumberModel(sprite.getActivationVectorOffset().getY(), -1000, 1000, 1));
        activationVectorOffsetYSpinner.addChangeListener((ChangeEvent e) -> {
            int x1 = (int) sprite.getActivationVectorOffset().getX();
            int y1 = ((Double) activationVectorOffsetYSpinner.getValue()).intValue();
            sprite.setActivationVectorOffset(new Point(x1, y1), true);
        });
        // /
        // / this
        // /
        insert(getJLabel("Base Vector Width"), baseVectorWidthSpinner);
        insert(getJLabel("Base Vector Height"), baseVectorHeightSpinner);
        insert(getJLabel("Base Vector Offset X"), baseVectorOffsetXSpinner);
        insert(getJLabel("Base Vector Offset Y"), baseVectorOffsetYSpinner);
        insert(getJLabel("Activation Vector Width"), activationVectorWidthSpinner);
        insert(getJLabel("Activation Vector Height"), activationVectorHeightSpinner);
        insert(getJLabel("Activation Vector Offset X"), activationVectorOffsetXSpinner);
        insert(getJLabel("Activation Vector Offset Y"), activationVectorOffsetYSpinner);
    }

}
