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
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.ui.AbstractModelPanel;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public class AnimationModelPanel extends AbstractModelPanel {

	private final JComboBox soundEffectComboBox;
	private final JLabel soundEffectLabel;

	private final JSpinner widthSpinner;
	private final JLabel widthLabel;

	private final JSpinner heightSpinner;
	private final JLabel heightLabel;

	private final JSpinner frameRateSpinner;
	private final JLabel frameRateLabel;

	private final Animation animation;

	public AnimationModelPanel(Animation model) {
    ///
    /// super
    ///
    super(model);
    ///
    /// animation
    ///
    this.animation = model;
    ///
    /// soundEffectComboBox
    ///
    File directory = new File(
            System.getProperty("project.path") 
            + File.separator
            + CoreProperties.getProperty("toolkit.directory.sounds") 
            + File.separator);
    String[] exts = new String[] {"wav", "mp3"};
    soundEffectComboBox = GuiHelper.getFileListJComboBox(new File[]{directory}, exts, true);
    soundEffectComboBox.setSelectedItem(animation.getSoundEffect());
    soundEffectComboBox.addActionListener((ActionEvent e) -> {
        animation.setSoundEffect((String)soundEffectComboBox.getSelectedItem());
    });
    ///
    /// widthSpinner
    ///
    widthSpinner = getJSpinner(animation.getAnimationWidth());
    widthSpinner.setModel(new SpinnerNumberModel(animation.getAnimationWidth(), 10, 1000, 1));
    widthSpinner.addChangeListener((ChangeEvent e) -> {
        int value = (int)widthSpinner.getValue();
        
        if (value > 0 && value != animation.getAnimationWidth()) {
            animation.setAnimationWidth(value);
        } else {
            widthSpinner.setValue(animation.getAnimationWidth());
        }
    });
    ///
    /// heightSpinner
    ///
    heightSpinner = getJSpinner(animation.getAnimationHeight());
    heightSpinner.setModel(new SpinnerNumberModel(animation.getAnimationHeight(), 10, 1000, 1));
    heightSpinner.addChangeListener((ChangeEvent e) -> {
        int value = (int) heightSpinner.getValue();
        
        if (value > 0 && value != animation.getAnimationHeight()) {
            animation.setAnimationHeight(value);
        } else {
            heightSpinner.setValue(animation.getAnimationHeight());
        }
    });
    ///
    /// frameRateSpinner
    ///
    frameRateSpinner = getJSpinner(animation.getFrameRate());
    frameRateSpinner.setModel(new SpinnerNumberModel(animation.getFrameRate(), 0, 100, 1));
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
    horizontalGroup.addGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(soundEffectLabel = getJLabel("Sound Effect"))
                    .addComponent(widthLabel = getJLabel("Width"))
                    .addComponent(heightLabel = getJLabel("Height"))
                    .addComponent(frameRateLabel = getJLabel("FPS")));
    
    horizontalGroup.addGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(soundEffectComboBox)
                    .addComponent(widthSpinner)
                    .addComponent(heightSpinner)
                    .addComponent(frameRateSpinner));
    
    layout.setHorizontalGroup(horizontalGroup);
    
    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(soundEffectLabel).addComponent(soundEffectComboBox));
    
    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(widthLabel).addComponent(widthSpinner));
    
    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(heightLabel).addComponent(heightSpinner));
    
    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(frameRateLabel).addComponent(frameRateSpinner));
  
    layout.setVerticalGroup(verticalGroup);
  }
}
