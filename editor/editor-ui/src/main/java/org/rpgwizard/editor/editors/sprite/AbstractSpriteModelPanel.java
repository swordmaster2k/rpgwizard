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
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.Point;
import org.rpgwizard.common.assets.ShapeEnum;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.sprite.Sprite;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.ui.AbstractModelPanel;

/**
 *
 * @author Joshua Michael Daly
 */
public abstract class AbstractSpriteModelPanel extends AbstractModelPanel {

    private static final String[] SHAPE_TYPES = ShapeEnum.toStringArray();
    private final JComboBox<String> shapeComboBox;

    private final JCheckBox colliderDisableCheckBox;
    private final JSpinner colliderRadiusSpinner;
    private final JSpinner colliderWidthSpinner;
    private final JSpinner colliderHeightSpinner;
    private final JSpinner colliderOffsetXSpinner;
    private final JSpinner colliderOffsetYSpinner;

    private final JCheckBox triggerDisableCheckBox;
    private final JSpinner triggerRadiusSpinner;
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
        /// colliderRadiusSpinner
        ///
        colliderRadiusSpinner = getJSpinner(sprite.getCollider().getRadius());
        colliderRadiusSpinner.addChangeListener((ChangeEvent e) -> {
            int r = (Integer) colliderRadiusSpinner.getValue();
            sprite.getCollider().setRadius(r);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
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
        colliderHeightSpinner
                .setModel(new SpinnerNumberModel((int) colliderPolygon.getBounds().getHeight(), 1, 100, 1));
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
            final boolean enabled = !colliderDisableCheckBox.isSelected();
            final boolean circleShape = ShapeEnum.CIRCLE.equals(sprite.getShape());

            colliderWidthSpinner.setEnabled(enabled && !circleShape);
            colliderHeightSpinner.setEnabled(enabled && !circleShape);
            colliderRadiusSpinner.setEnabled(enabled && circleShape);

            colliderOffsetXSpinner.setEnabled(enabled);
            colliderOffsetYSpinner.setEnabled(enabled);

            sprite.getCollider().setEnabled(enabled);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// triggerRadiusSpinner
        ///
        triggerRadiusSpinner = getJSpinner(sprite.getTrigger().getRadius());
        triggerRadiusSpinner.addChangeListener((ChangeEvent e) -> {
            int r = (Integer) triggerRadiusSpinner.getValue();
            sprite.getTrigger().setRadius(r);
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
            trigger.setEvent(sprite.getTrigger().getEvent());

            sprite.setTrigger(trigger);
            sprite.fireSpriteChanged();

            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// triggerHeightSpinner
        ///
        triggerHeightSpinner = getJSpinner((int) triggerPolygon.getBounds().getHeight());
        triggerHeightSpinner.setModel(new SpinnerNumberModel((int) triggerPolygon.getBounds().getHeight(), 1, 100, 1));
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
            trigger.setEvent(sprite.getTrigger().getEvent());

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
            final boolean enabled = !triggerDisableCheckBox.isSelected();
            final boolean circleShape = ShapeEnum.CIRCLE.equals(sprite.getShape());

            triggerWidthSpinner.setEnabled(enabled && !circleShape);
            triggerHeightSpinner.setEnabled(enabled && !circleShape);
            triggerRadiusSpinner.setEnabled(enabled && circleShape);

            triggerOffsetXSpinner.setEnabled(enabled);
            triggerOffsetYSpinner.setEnabled(enabled);

            sprite.getTrigger().setEnabled(enabled);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// shapeComboBox
        ///
        shapeComboBox = new JComboBox<>(SHAPE_TYPES);
        shapeComboBox.setSelectedItem(sprite.getShape().getValue());
        changeShape(sprite.getShape());

        shapeComboBox.addActionListener(e -> {
            ShapeEnum newShape = ShapeEnum.valueOf(shapeComboBox.getSelectedItem().toString().toUpperCase());
            changeShape(newShape);
            sprite.setShape(newShape);
            sprite.fireSpriteChanged();
            MainWindow.getInstance().markWindowForSaving();
        });
        ///
        /// this
        ///
        insert(getJLabel("Shape"), shapeComboBox);

        insert(getJLabel("Disable Collider"), colliderDisableCheckBox);
        insert(getJLabel("Collider Radius"), colliderRadiusSpinner);
        insert(getJLabel("Collider Width"), colliderWidthSpinner);
        insert(getJLabel("Collider Height"), colliderHeightSpinner);
        insert(getJLabel("Collider Offset X"), colliderOffsetXSpinner);
        insert(getJLabel("Collider Offset Y"), colliderOffsetYSpinner);

        insert(getJLabel("Disable Trigger"), triggerDisableCheckBox);
        insert(getJLabel("Trigger Radius"), triggerRadiusSpinner);
        insert(getJLabel("Trigger Width"), triggerWidthSpinner);
        insert(getJLabel("Trigger Height"), triggerHeightSpinner);
        insert(getJLabel("Trigger Offset X"), triggerOffsetXSpinner);
        insert(getJLabel("Trigger Offset Y"), triggerOffsetYSpinner);
    }

    private Polygon pointsToPolygon(List<Point> points) {
        Polygon polygon = new Polygon();
        points.forEach(p -> {
            polygon.addPoint(p.getX(), p.getY());
        });
        return polygon;
    }

    private void changeShape(ShapeEnum shape) {
        final boolean triggerEnabled = sprite.getTrigger().isEnabled();
        final boolean colliderEnabled = sprite.getCollider().isEnabled();
        final boolean circleShape = ShapeEnum.CIRCLE.equals(shape);

        triggerWidthSpinner.setEnabled(triggerEnabled && !circleShape);
        triggerHeightSpinner.setEnabled(triggerEnabled && !circleShape);
        triggerRadiusSpinner.setEnabled(triggerEnabled && circleShape);

        colliderWidthSpinner.setEnabled(colliderEnabled && !circleShape);
        colliderHeightSpinner.setEnabled(colliderEnabled && !circleShape);
        colliderRadiusSpinner.setEnabled(colliderEnabled && circleShape);
    }

}
