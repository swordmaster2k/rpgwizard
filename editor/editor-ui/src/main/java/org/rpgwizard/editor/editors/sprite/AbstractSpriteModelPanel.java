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

    private final JCheckBox colliderDisableCheckBox;
    private final JSpinner colliderWidthSpinner;
    private final JSpinner colliderHeightSpinner;
    private final JSpinner colliderOffsetXSpinner;
    private final JSpinner colliderOffsetYSpinner;

    private final JCheckBox triggerDisableCheckBox;
    private final JSpinner triggerWidthSpinner;
    private final JSpinner triggerHeightSpinner;
    private final JSpinner triggerOffsetXSpinner;
    private final JSpinner triggerOffsetYSpinner;

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
        /// colliderWidthSpinner
        ///
        colliderWidthSpinner = getJSpinner((int) colliderPolygon.getBounds().getWidth());
        colliderWidthSpinner.setModel(new SpinnerNumberModel((int) colliderPolygon.getBounds().getWidth(), 1, 100, 1));
        colliderWidthSpinner.setEnabled(sprite.getCollider().isEnabled());
        colliderWidthSpinner.addChangeListener((ChangeEvent e) -> {
            Polygon localPolygon = pointsToPolygon(sprite.getCollider().getPoints());
            int width1 = (Integer) colliderWidthSpinner.getValue();
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
            sprite.fireSpriteChanged();

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// colliderHeightSpinner
        ///
        colliderHeightSpinner = getJSpinner((int) colliderPolygon.getBounds().getHeight());
        colliderHeightSpinner.setModel(new SpinnerNumberModel((int) colliderPolygon.getBounds().getHeight(), 1, 100, 1));
        colliderHeightSpinner.setEnabled(sprite.getCollider().isEnabled());
        colliderHeightSpinner.addChangeListener((ChangeEvent e) -> {
            Polygon localPolygon = pointsToPolygon(sprite.getCollider().getPoints());
            int width1 = (int) localPolygon.getBounds().getWidth();
            int height1 = (Integer) colliderHeightSpinner.getValue();

            Collider collider = new Collider();
            collider.addPoint(0, 0);
            collider.addPoint(width1, 0);
            collider.addPoint(width1, height1);
            collider.addPoint(0, height1);
            collider.setX(sprite.getCollider().getX());
            collider.setY(sprite.getCollider().getY());
            collider.setEnabled(sprite.getCollider().isEnabled());

            sprite.setCollider(collider);
            sprite.fireSpriteChanged();

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// colliderOffsetXSpinner
        ///
        colliderOffsetXSpinner = getJSpinner((int) sprite.getCollider().getX(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        colliderOffsetXSpinner.setModel(new SpinnerNumberModel((int) sprite.getCollider().getX(), -1000, 1000, 1));
        colliderOffsetXSpinner.setEnabled(sprite.getCollider().isEnabled());
        colliderOffsetXSpinner.addChangeListener((ChangeEvent e) -> {
            int x1 = (Integer) colliderOffsetXSpinner.getValue();
            sprite.getCollider().setX(x1);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// colliderOffsetYSpinner
        ///
        colliderOffsetYSpinner = getJSpinner((int) sprite.getCollider().getY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        colliderOffsetYSpinner.setModel(new SpinnerNumberModel((int) sprite.getCollider().getY(), -1000, 1000, 1));
        colliderOffsetYSpinner.setEnabled(sprite.getCollider().isEnabled());
        colliderOffsetYSpinner.addChangeListener((ChangeEvent e) -> {
            int y1 = (Integer) colliderOffsetYSpinner.getValue();
            sprite.getCollider().setY(y1);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// colliderDisableCheckBox
        ///
        colliderDisableCheckBox = getJCheckBox(!sprite.getCollider().isEnabled());
        colliderDisableCheckBox.addItemListener((ItemEvent e) -> {
            boolean disabled = e.getStateChange() == 1;
            colliderWidthSpinner.setEnabled(!disabled);
            colliderHeightSpinner.setEnabled(!disabled);
            colliderOffsetXSpinner.setEnabled(!disabled);
            colliderOffsetYSpinner.setEnabled(!disabled);
            sprite.getCollider().setEnabled(disabled);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// triggerWidthSpinner
        ///
        triggerWidthSpinner = getJSpinner((int) triggerPolygon.getBounds().getWidth());
        triggerWidthSpinner.setModel(new SpinnerNumberModel((int) triggerPolygon.getBounds().getWidth(), 1, 100, 1));
        triggerWidthSpinner.setEnabled(sprite.getTrigger().isEnabled());
        triggerWidthSpinner.addChangeListener((ChangeEvent e) -> {
            Polygon localPolygon = pointsToPolygon(sprite.getTrigger().getPoints());
            int width1 = (Integer) triggerWidthSpinner.getValue();
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
            sprite.fireSpriteChanged();

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// triggerHeightSpinner
        ///
        triggerHeightSpinner = getJSpinner((int) triggerPolygon.getBounds().getHeight());
        triggerHeightSpinner
                .setModel(new SpinnerNumberModel((int) triggerPolygon.getBounds().getHeight(), 1, 100, 1));
        triggerHeightSpinner.setEnabled(sprite.getTrigger().isEnabled());
        triggerHeightSpinner.addChangeListener((ChangeEvent e) -> {
            Polygon localPolygon = pointsToPolygon(sprite.getTrigger().getPoints());
            int width1 = (int) localPolygon.getBounds().getWidth();
            int height1 = (Integer) triggerHeightSpinner.getValue();

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
            sprite.fireSpriteChanged();

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// triggerOffsetXSpinner
        ///
        triggerOffsetXSpinner = getJSpinner((int) sprite.getTrigger().getX(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        triggerOffsetXSpinner.setModel(new SpinnerNumberModel((int) sprite.getTrigger().getX(), -1000, 1000, 1));
        triggerOffsetXSpinner.setEnabled(sprite.getTrigger().isEnabled());
        triggerOffsetXSpinner.addChangeListener((ChangeEvent e) -> {
            int x1 = (Integer) triggerOffsetXSpinner.getValue();
            sprite.getTrigger().setX(x1);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// triggerOffsetYSpinner
        ///
        triggerOffsetYSpinner = getJSpinner((int) sprite.getTrigger().getY(), Integer.MIN_VALUE, Integer.MAX_VALUE);
        triggerOffsetYSpinner.setModel(new SpinnerNumberModel((int) sprite.getTrigger().getY(), -1000, 1000, 1));
        triggerOffsetYSpinner.setEnabled(sprite.getTrigger().isEnabled());
        triggerOffsetYSpinner.addChangeListener((ChangeEvent e) -> {
            int y1 = (Integer) triggerOffsetYSpinner.getValue();
            sprite.getTrigger().setY(y1);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// triggerDisableCheckBox
        ///
        triggerDisableCheckBox = getJCheckBox(!sprite.getTrigger().isEnabled());
        triggerDisableCheckBox.addItemListener((ItemEvent e) -> {
            boolean disabled = e.getStateChange() == 1;
            triggerWidthSpinner.setEnabled(!disabled);
            triggerHeightSpinner.setEnabled(!disabled);
            triggerOffsetXSpinner.setEnabled(!disabled);
            triggerOffsetYSpinner.setEnabled(!disabled);
            sprite.getTrigger().setEnabled(disabled);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// this
        ///
        insert(getJLabel("Disable Trigger"), colliderDisableCheckBox);
        insert(getJLabel("Trigger Width"), colliderWidthSpinner);
        insert(getJLabel("Trigger Height"), colliderHeightSpinner);
        insert(getJLabel("Trigger Offset X"), colliderOffsetXSpinner);
        insert(getJLabel("Trigger Offset Y"), colliderOffsetYSpinner);
        insert(getJLabel("Disable Collider"), triggerDisableCheckBox);
        insert(getJLabel("Collider Width"), triggerWidthSpinner);
        insert(getJLabel("Collider Height"), triggerHeightSpinner);
        insert(getJLabel("Collider Offset X"), triggerOffsetXSpinner);
        insert(getJLabel("Collider Offset Y"), triggerOffsetYSpinner);
    }

    private Polygon pointsToPolygon(List<Point> points) {
        Polygon polygon = new Polygon();
        points.forEach(p -> {
            polygon.addPoint(p.getX(), p.getY());
        });
        return polygon;
    }

}
