/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;
import org.rpgwizard.common.assets.Animation;
import org.rpgwizard.common.assets.board.BoardVector;
import org.rpgwizard.common.assets.events.AnimationChangedEvent;
import org.rpgwizard.common.assets.listeners.AnimationChangeListener;
import org.rpgwizard.common.utilities.CoreProperties;
import org.rpgwizard.editor.ui.resources.Icons;
import org.rpgwizard.editor.utilities.TransparentDrawer;

/**
 *
 * @author Joshua Michael Daly
 */
public class AnimatedPanel extends AbstractImagePanel implements AnimationChangeListener {

    public static final int DEFAULT_HEIGHT = 300;

    private Image playImage;
    private Image stopImage;
    private Image currentActionImage;

    private Animation animation;
    private BufferedImage frameImage;
    private Timer timer;

    private BoardVector baseVector;
    private BoardVector activationVector;

    private Point baseVectorOffset;
    private Point activationVectorOffset;

    private final ActionListener animate = new ActionListener() {
        private int index = 0;

        @Override
        public void actionPerformed(ActionEvent e) {
            if (index < animation.getFrameCount() - 1) {
                index++;
            } else {
                index = 0;
                currentActionImage = playImage;
                timer.stop();
                timer = null;
            }

            frameImage = animation.getFrame(index);
            repaint();
        }
    };

    public AnimatedPanel() {
        init();
    }

    public AnimatedPanel(Dimension dimension) {
        super(dimension);
        init();
    }

    public Animation getAnimation() {
        return animation;
    }

    public void setAnimation(Animation animation) throws IOException {
        if (this.animation != null) {
            this.animation.removeAnimationChangeListener(this);
            this.animation.addAnimationChangeListener(this);
        }

        this.animation = animation;

        if (animation == null) {
            timer = null;
            frameImage = null;
        } else if (animation.getSpriteSheet() != null) {
            animation.getSpriteSheet().loadSelection();
            frameImage = animation.getFrame(0);
        }

        repaint();
    }

    public BoardVector getBaseVector() {
        return baseVector;
    }

    public void setBaseVector(BoardVector baseVector) {
        this.baseVector = baseVector;
    }

    public BoardVector getActivationVector() {
        return activationVector;
    }

    public void setActivationVector(BoardVector activationVector) {
        this.activationVector = activationVector;
    }

    public Point getBaseVectorOffset() {
        return baseVectorOffset;
    }

    public void setBaseVectorOffset(Point baseVectorOffset) {
        this.baseVectorOffset = baseVectorOffset;
    }

    public Point getActivationVectorOffset() {
        return activationVectorOffset;
    }

    public void setActivationVectorOffset(Point activationVectorOffset) {
        this.activationVectorOffset = activationVectorOffset;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (animation != null) {
            if (animation.getFrameCount() > 0) {
                if (timer == null) {
                    animate();
                } else {
                    stop();
                }
            }
        }
    }

    @Override
    public void animationChanged(AnimationChangedEvent e) {
        updateAnimation();
    }

    @Override
    public void animationFrameAdded(AnimationChangedEvent e) {
        updateAnimation();
    }

    @Override
    public void animationFrameRemoved(AnimationChangedEvent e) {
        updateAnimation();
    }

    @Override
    public void paint(Graphics g) {
        TransparentDrawer.drawTransparentBackground(g, getWidth(), getHeight());

        int x;
        int y;
        if (animation != null) {
            x = (getWidth() / 2) - (animation.getAnimationWidth() / 2);
            y = (getHeight() / 2) - (animation.getAnimationHeight() / 2);

            if (frameImage != null) {
                int width = (int) animation.getAnimationWidth();
                int height = (int) animation.getAnimationHeight();

                if (frameImage.getWidth() > width || frameImage.getHeight() > height) {
                    makeSubImage();
                }

                g.drawImage(frameImage, x, y, null);
            }

            g.setColor(Color.LIGHT_GRAY);
            g.drawRect(0, 0, getWidth() - 1, getHeight() - 1);

            // Define them for drawing the vectors at the animations base.
            x = getWidth() / 2;
            y = getHeight() / 2;
            if (baseVector != null) {
                g.setColor(Color.RED);
                drawVector(baseVector, g, x, y, (int) baseVectorOffset.getX(), (int) baseVectorOffset.getY());
            }

            if (activationVector != null) {
                g.setColor(Color.YELLOW);
                drawVector(activationVector, g, x, y, (int) activationVectorOffset.getX(),
                        (int) activationVectorOffset.getY());
            }
        }

        x = getWidth();
        y = getHeight();
        x -= currentActionImage.getWidth(null);
        y -= currentActionImage.getHeight(null);

        // Draw the current action button (i.e. play or stop).
        g.drawImage(currentActionImage, x, y, null);
    }

    public void animate() {
        currentActionImage = stopImage;

        int fps = animation.getFrameRate();
        double framePerMillsecond = 1.0 / fps;
        int milliseconds = (int) (framePerMillsecond * 1000);
        timer = new Timer(milliseconds, animate);
        timer.start();

        if (!animation.getSoundEffect().isEmpty()) {
            String path = System.getProperty("project.path") + File.separator
                    + CoreProperties.getProperty("toolkit.directory.sounds") + File.separator
                    + animation.getSoundEffect();
        }
    }

    public void stop() {
        if (timer != null) {
            timer.stop();
            timer = null;
        }

        frameImage = animation.getFrame(0);
        currentActionImage = playImage;
        repaint();
    }

    public void tearDown() {
        if (animation != null) {
            animation.removeAnimationChangeListener(this);
        }
    }

    private void init() {
        playImage = Icons.getLargeIcon("animation-play").getImage();
        stopImage = Icons.getLargeIcon("animation-stop").getImage();
        currentActionImage = playImage;

        baseVectorOffset = new Point(0, 0);
        activationVectorOffset = new Point(0, 0);
    }

    private void updateAnimation() {
        if (animation.getFrameCount() > 0) {
            frameImage = animation.getFrame(0);
            repaint();
        } else {
            frameImage = null;
        }
    }

    private void makeSubImage() {
        int width = (int) animation.getAnimationWidth();
        int height = (int) animation.getAnimationHeight();
        int frameWidth = frameImage.getWidth();
        int frameHeight = frameImage.getHeight();

        if (frameWidth > width || frameHeight > height) {
            if (frameWidth > width && frameHeight > height) {
                frameImage = frameImage.getSubimage(0, 0, width, height);
            } else if (frameWidth > width) {
                frameImage = frameImage.getSubimage(0, 0, width, frameHeight);
            } else {
                frameImage = frameImage.getSubimage(0, 0, frameWidth, height);
            }
        }
    }

    private void drawVector(BoardVector vector, Graphics g, int x, int y, int xOffset, int yOffset) {
        int count = vector.getPointCount();
        for (int i = 0; i < count - 1; i++) {
            g.drawLine(x + vector.getPointX(i) + xOffset, y + vector.getPointY(i) + yOffset,
                    x + vector.getPointX(i + 1) + xOffset, y + vector.getPointY(i + 1) + yOffset);
        }
        // Draw the final lines
        g.drawLine(x + vector.getPointX(count - 1) + xOffset, y + vector.getPointY(count - 1) + yOffset,
                x + vector.getPointX(0) + xOffset, y + vector.getPointY(0) + yOffset);
    }

}
