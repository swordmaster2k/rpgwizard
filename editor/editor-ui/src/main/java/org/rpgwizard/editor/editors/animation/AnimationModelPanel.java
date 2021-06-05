/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.animation;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.JComboBox;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.events.AnimationChangedEvent;
import org.rpgwizard.common.assets.listeners.AnimationChangeListener;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public final class AnimationModelPanel extends AbstractModelPanel implements AnimationChangeListener {

    private final JComboBox soundEffectComboBox;
    private final JSpinner frameRateSpinner;

    private final Animation animation;

    public AnimationModelPanel(Animation model) {
        ///
        /// super
        ///
        super(model);
        ///
        /// this
        ///
        animation = model;
        animation.addAnimationChangeListener(this);
        ///
        /// soundEffectComboBox
        ///
        File directory = new File(System.getProperty("project.path") + File.separator
                + CoreProperties.getProperty("rpgwizard.directory.sounds") + File.separator);
        String[] exts = new String[] { "wav", "mp3", "ogg" };
        soundEffectComboBox = GuiHelper.getFileListJComboBox(new File[] { directory }, exts, true);
        soundEffectComboBox.setSelectedItem(animation.getSoundEffect());
        soundEffectComboBox.addActionListener((ActionEvent e) -> {
            animation.setSoundEffect((String) soundEffectComboBox.getSelectedItem());
        });
        ///
        /// frameRateSpinner
        ///
        frameRateSpinner = getJSpinner(animation.getFrameRate());
        frameRateSpinner.setModel(new SpinnerNumberModel(animation.getFrameRate(), 1, 100, 1));
        frameRateSpinner.addChangeListener((ChangeEvent e) -> {
            int value = (int) frameRateSpinner.getValue();

            if (value > 0 && value != animation.getFrameRate()) {
                animation.setFramRate(value);
            } else {
                frameRateSpinner.setValue(animation.getFrameRate());
            }
        });
        ///
        /// this
        ///
        insert(getJLabel("Sound Effect"), soundEffectComboBox);
        insert(getJLabel("FPS"), frameRateSpinner);
    }

    @Override
    public void animationChanged(AnimationChangedEvent e) {
        Animation source = (Animation) e.getSource();
        if (!animation.equals(source)) {
            return;
        }
        if (((int) frameRateSpinner.getValue()) != source.getFrameRate()) {
            frameRateSpinner.setValue(source.getFrameCount());
        }
    }

    @Override
    public void animationFrameAdded(AnimationChangedEvent e) {

    }

    @Override
    public void animationFrameRemoved(AnimationChangedEvent e) {

    }

    @Override
    public void tearDown() {
        animation.removeAnimationChangeListener(this);
    }
}
