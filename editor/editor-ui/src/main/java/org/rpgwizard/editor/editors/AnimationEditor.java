/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.event.InternalFrameEvent;
import javax.swing.event.InternalFrameListener;
import org.rpgwizard.common.assets.AbstractAsset;
import org.rpgwizard.common.assets.animation.Animation;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.sprite.SpriteSheet;
import org.rpgwizard.common.assets.events.AnimationChangedEvent;
import org.rpgwizard.common.assets.listeners.AnimationChangeListener;
import org.rpgwizard.editor.editors.animation.AddSpriteSheetButton;
import org.rpgwizard.editor.editors.animation.SpriteSheetButton;
import org.rpgwizard.editor.ui.AnimatedPanel;
import org.rpgwizard.editor.ui.AbstractAssetEditorWindow;
import org.rpgwizard.editor.ui.resources.Icons;

/**
 *
 * @author Geoff Wilson
 * @author Joshua Michael Daly
 */
public final class AnimationEditor extends AbstractAssetEditorWindow
        implements InternalFrameListener, AnimationChangeListener {

    private final Animation animation;

    private AnimatedPanel animatedPanel;

    private JScrollPane timelineScrollPane;
    private JPanel timelinePanel;

    public AnimationEditor(Animation theAnimation) {
        super("Untitled", true, true, true, true, Icons.getIcon("animation"));

        animation = theAnimation;
        animation.addAnimationChangeListener(this);
        if (animation.getDescriptor() != null) {
            setTitle(new File(animation.getDescriptor().getURI()).getName());
        }

        configureInterface();
        setSize(400, 400);
        setVisible(true);
    }

    @Override
    public AbstractAsset getAsset() {
        return animation;
    }

    @Override
    public void save() throws Exception {
        save(animation);
    }

    /**
     *
     *
     * @param file
     * @throws java.lang.Exception
     */
    @Override
    public void saveAs(File file) throws Exception {
        animation.setDescriptor(new AssetDescriptor(file.toURI()));
        save();
    }

    @Override
    public void internalFrameClosed(InternalFrameEvent e) {
        animatedPanel.tearDown();
        animation.removeAnimationChangeListener(this);
    }

    public Animation getAnimation() {
        return animation;
    }

    @Override
    public void animationChanged(AnimationChangedEvent e) {
        updateInterface();
        setNeedSave(true);
    }

    @Override
    public void animationFrameAdded(AnimationChangedEvent e) {
        updateInterface();
        setNeedSave(true);
    }

    @Override
    public void animationFrameRemoved(AnimationChangedEvent e) {
        updateInterface();
        setNeedSave(true);
    }

    private void updateInterface() {
        timelinePanel.removeAll();
        configureTimeline();
        revalidate();
        repaint();
    }

    private void configureInterface() {
        animatedPanel = new AnimatedPanel();

        timelinePanel = new JPanel();
        timelinePanel.setBackground(Color.LIGHT_GRAY);
        timelinePanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        configureTimeline();

        timelineScrollPane = new JScrollPane(timelinePanel);
        timelineScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        JPanel container = new JPanel(new BorderLayout());
        container.add(animatedPanel, BorderLayout.CENTER);
        container.add(timelineScrollPane, BorderLayout.SOUTH);

        setContentPane(container);
    }

    private void configureTimeline() {
        try {
            SpriteSheet spriteSheet = animation.getSpriteSheet();
            if (!spriteSheet.getFileName().isEmpty()) {
                spriteSheet.loadSelection();
                SpriteSheetButton spriteSheetButton = new SpriteSheetButton(animation, spriteSheet);
                spriteSheetButton.loadImage();
                animatedPanel.setAnimation(animation);
                timelinePanel.add(spriteSheetButton);
            } else {
                timelinePanel.add(new AddSpriteSheetButton(animation));
            }
        } catch (IOException ex) {
            Logger.getLogger(AnimationEditor.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
