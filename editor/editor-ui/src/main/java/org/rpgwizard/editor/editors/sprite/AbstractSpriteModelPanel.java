/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.sprite;

import java.awt.Polygon;
import java.awt.event.ItemEvent;
import java.util.List;
import javax.swing.JCheckBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.Point;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.AbstractModelPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractSpriteModelPanel extends AbstractModelPanel {

    private final JCheckBox baseVectorDisableCheckBox;
    private final JSpinner baseVectorWidthSpinner;
    private final JSpinner baseVectorHeightSpinner;
    private final JSpinner baseVectorOffsetXSpinner;
    private final JSpinner baseVectorOffsetYSpinner;

    private final JCheckBox activationVectorDisableCheckBox;
    private final JSpinner activationVectorWidthSpinner;
    private final JSpinner activationVectorHeightSpinner;
    private final JSpinner activationVectorOffsetXSpinner;
    private final JSpinner activationVectorOffsetYSpinner;

    private final Sprite sprite;

    public AbstractSpriteModelPanel(Sprite model) {
        ///
        /// super
        ///
        super(model);
        ///
        /// sprite
        ///
        sprite = model;
        ///
        /// Local Polygons
        ///
        Polygon colliderPolygon = pointsToPolygon(sprite.getCollider().getPoints());
        Polygon triggerPolygon = pointsToPolygon(sprite.getTrigger().getPoints());
        ///
        /// baseVectorWidthSpinner
        ///
        baseVectorWidthSpinner = getJSpinner(colliderPolygon.getBounds().getWidth());
        baseVectorWidthSpinner.setModel(new SpinnerNumberModel(colliderPolygon.getBounds().getWidth(), 1, 100, 1));
        baseVectorWidthSpinner.setEnabled(sprite.getCollider().isEnabled());
        baseVectorWidthSpinner.addChangeListener((ChangeEvent e) -> {
            Polygon localPolygon = pointsToPolygon(sprite.getCollider().getPoints());
            int width1 = ((Double) baseVectorWidthSpinner.getValue()).intValue();
            int height1 = (int) localPolygon.getBounds().getHeight();

            Collider collider = new Collider();
            collider.addPoint(0, 0);
            collider.addPoint(width1, 0);
            collider.addPoint(width1, height1);
            collider.addPoint(0, height1);
            collider.setX(sprite.getCollider().getX());
            collider.setY(sprite.getCollider().getY());
            collider.setEnabled(sprite.getCollider().isEnabled());

            sprite.setCollider(collider);

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// baseVectorHeightSpinner
        ///
        baseVectorHeightSpinner = getJSpinner(colliderPolygon.getBounds().getHeight());
        baseVectorHeightSpinner.setModel(new SpinnerNumberModel(colliderPolygon.getBounds().getHeight(), 1, 100, 1));
        baseVectorHeightSpinner.setEnabled(sprite.getCollider().isEnabled());
        baseVectorHeightSpinner.addChangeListener((ChangeEvent e) -> {
            Polygon localPolygon = pointsToPolygon(sprite.getCollider().getPoints());
            int width1 = (int) localPolygon.getBounds().getWidth();
            int height1 = ((Double) baseVectorHeightSpinner.getValue()).intValue();

            Collider collider = new Collider();
            collider.addPoint(0, 0);
            collider.addPoint(width1, 0);
            collider.addPoint(width1, height1);
            collider.addPoint(0, height1);
            collider.setX(sprite.getCollider().getX());
            collider.setY(sprite.getCollider().getY());
            collider.setEnabled(sprite.getCollider().isEnabled());

            sprite.setCollider(collider);

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// baseVectorOffsetXSpinner
        ///
        baseVectorOffsetXSpinner = getJSpinner(sprite.getCollider().getX());
        baseVectorOffsetXSpinner.setModel(new SpinnerNumberModel(sprite.getCollider().getX(), -1000, 1000, 1));
        baseVectorOffsetXSpinner.setEnabled(sprite.getCollider().isEnabled());
        baseVectorOffsetXSpinner.addChangeListener((ChangeEvent e) -> {
            int x1 = ((Double) baseVectorOffsetXSpinner.getValue()).intValue();
            sprite.getCollider().setX(x1);
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// baseVectorOffsetYSpinner
        ///
        baseVectorOffsetYSpinner = getJSpinner(sprite.getCollider().getY());
        baseVectorOffsetYSpinner.setModel(new SpinnerNumberModel(sprite.getCollider().getY(), -1000, 1000, 1));
        baseVectorOffsetYSpinner.setEnabled(sprite.getCollider().isEnabled());
        baseVectorOffsetYSpinner.addChangeListener((ChangeEvent e) -> {
            int y1 = ((Double) baseVectorOffsetYSpinner.getValue()).intValue();
            sprite.getCollider().setY(y1);
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// baseVectorDisableCheckBox
        ///
        baseVectorDisableCheckBox = getJCheckBox(!sprite.getCollider().isEnabled());
        baseVectorDisableCheckBox.addItemListener((ItemEvent e) -> {
            boolean disabled = e.getStateChange() == 1;
            baseVectorWidthSpinner.setEnabled(!disabled);
            baseVectorHeightSpinner.setEnabled(!disabled);
            baseVectorOffsetXSpinner.setEnabled(!disabled);
            baseVectorOffsetYSpinner.setEnabled(!disabled);
            sprite.getCollider().setEnabled(disabled);
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// activationVectorWidthSpinner
        ///
        activationVectorWidthSpinner = getJSpinner(triggerPolygon.getBounds().getWidth());
        activationVectorWidthSpinner.setModel(new SpinnerNumberModel(triggerPolygon.getBounds().getWidth(), 1, 100, 1));
        activationVectorWidthSpinner.setEnabled(sprite.getTrigger().isEnabled());
        activationVectorWidthSpinner.addChangeListener((ChangeEvent e) -> {
            Polygon localPolygon = pointsToPolygon(sprite.getTrigger().getPoints());
            int width1 = ((Double) activationVectorWidthSpinner.getValue()).intValue();
            int height1 = (int) localPolygon.getBounds().getHeight();

            Trigger trigger = new Trigger();
            trigger.addPoint(0, 0);
            trigger.addPoint(width1, 0);
            trigger.addPoint(width1, height1);
            trigger.addPoint(0, height1);
            trigger.setX(sprite.getCollider().getX());
            trigger.setY(sprite.getCollider().getY());
            trigger.setEnabled(sprite.getCollider().isEnabled());
            trigger.setEvents(sprite.getTrigger().getEvents());

            sprite.setTrigger(trigger);

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// baseVectorHeightSpinner
        ///
        activationVectorHeightSpinner = getJSpinner(triggerPolygon.getBounds().getHeight());
        activationVectorHeightSpinner
                .setModel(new SpinnerNumberModel(triggerPolygon.getBounds().getHeight(), 1, 100, 1));
        activationVectorHeightSpinner.setEnabled(sprite.getTrigger().isEnabled());
        activationVectorHeightSpinner.addChangeListener((ChangeEvent e) -> {
            Polygon localPolygon = pointsToPolygon(sprite.getTrigger().getPoints());
            int width1 = (int) localPolygon.getBounds().getWidth();
            int height1 = ((Double) activationVectorHeightSpinner.getValue()).intValue();

            Trigger trigger = new Trigger();
            trigger.addPoint(0, 0);
            trigger.addPoint(width1, 0);
            trigger.addPoint(width1, height1);
            trigger.addPoint(0, height1);
            trigger.setX(sprite.getCollider().getX());
            trigger.setY(sprite.getCollider().getY());
            trigger.setEnabled(sprite.getCollider().isEnabled());
            trigger.setEvents(sprite.getTrigger().getEvents());

            sprite.setTrigger(trigger);

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// activationVectorOffsetXSpinner
        ///
        activationVectorOffsetXSpinner = getJSpinner(sprite.getTrigger().getX());
        activationVectorOffsetXSpinner.setModel(new SpinnerNumberModel(sprite.getTrigger().getX(), -1000, 1000, 1));
        activationVectorOffsetXSpinner.setEnabled(sprite.getTrigger().isEnabled());
        activationVectorOffsetXSpinner.addChangeListener((ChangeEvent e) -> {
            int x1 = ((Double) activationVectorOffsetXSpinner.getValue()).intValue();
            sprite.getTrigger().setX(x1);
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// activationVectorOffsetYSpinner
        ///
        activationVectorOffsetYSpinner = getJSpinner(sprite.getTrigger().getY());
        activationVectorOffsetYSpinner.setModel(new SpinnerNumberModel(sprite.getTrigger().getY(), -1000, 1000, 1));
        activationVectorOffsetYSpinner.setEnabled(sprite.getTrigger().isEnabled());
        activationVectorOffsetYSpinner.addChangeListener((ChangeEvent e) -> {
            int y1 = ((Double) activationVectorOffsetYSpinner.getValue()).intValue();
            sprite.getTrigger().setX(y1);
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// baseVectorDisableCheckBox
        ///
        activationVectorDisableCheckBox = getJCheckBox(!sprite.getTrigger().isEnabled());
        activationVectorDisableCheckBox.addItemListener((ItemEvent e) -> {
            boolean disabled = e.getStateChange() == 1;
            activationVectorWidthSpinner.setEnabled(!disabled);
            activationVectorHeightSpinner.setEnabled(!disabled);
            activationVectorOffsetXSpinner.setEnabled(!disabled);
            activationVectorOffsetYSpinner.setEnabled(!disabled);
            sprite.getTrigger().setEnabled(disabled);
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// this
        ///
        insert(getJLabel("Disable Base Vector"), baseVectorDisableCheckBox);
        insert(getJLabel("Base Vector Width"), baseVectorWidthSpinner);
        insert(getJLabel("Base Vector Height"), baseVectorHeightSpinner);
        insert(getJLabel("Base Vector Offset X"), baseVectorOffsetXSpinner);
        insert(getJLabel("Base Vector Offset Y"), baseVectorOffsetYSpinner);
        insert(getJLabel("Disable Activation Vector"), activationVectorDisableCheckBox);
        insert(getJLabel("Activation Vector Width"), activationVectorWidthSpinner);
        insert(getJLabel("Activation Vector Height"), activationVectorHeightSpinner);
        insert(getJLabel("Activation Vector Offset X"), activationVectorOffsetXSpinner);
        insert(getJLabel("Activation Vector Offset Y"), activationVectorOffsetYSpinner);
    }

    private Polygon pointsToPolygon(List<Point> points) {
        Polygon polygon = new Polygon();
        points.forEach(p -> {
            polygon.addPoint(p.getX(), p.getY());
        });
        return polygon;
    }

}
