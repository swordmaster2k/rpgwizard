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
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.swing.Timer;
import lombok.Getter;
import lombok.Setter;
import org.rpgwizard.common.assets.Collider;
import org.rpgwizard.common.assets.ShapeEnum;
import org.rpgwizard.common.assets.Trigger;
import org.rpgwizard.common.assets.animation.Animation;
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

    @Getter
    @Setter
    private ShapeEnum shape;
    @Getter
    @Setter
    private Collider collider;
    @Getter
    @Setter
    private Trigger trigger;

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

    @Override
    public Dimension getPreferredSize() {
        if (dimension == null) {
            return super.getPreferredSize();
        }
        return dimension;
    }

    @Override
    public Dimension getMaximumSize() {
        if (dimension == null) {
            return super.getMaximumSize();
        }
        return dimension;
    }

    @Override
    public Dimension getMinimumSize() {
        if (dimension == null) {
            return super.getMinimumSize();
        }
        return dimension;
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
            x = (getWidth() / 2) - (animation.getWidth() / 2);
            y = (getHeight() / 2) - (animation.getHeight() / 2);

            if (frameImage != null) {
                int width = (int) animation.getWidth();
                int height = (int) animation.getHeight();

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
            if (collider != null) {
                g.setColor(Color.RED);
                if (ShapeEnum.RECTANGLE.equals(shape)) {
                    drawCollider(collider, g, x, y, (int) collider.getX(), (int) collider.getY());
                } else {
                    drawCollider(collider, g, x, y, (int) collider.getX(), (int) collider.getY(), collider.getRadius());
                }

            }

            if (trigger != null) {
                g.setColor(Color.YELLOW);
                if (ShapeEnum.RECTANGLE.equals(shape)) {
                    drawTrigger(trigger, g, x, y, (int) trigger.getX(), (int) trigger.getY());
                } else {
                    drawTrigger(trigger, g, x, y, (int) trigger.getX(), (int) trigger.getY(), trigger.getRadius());
                }
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
                    + CoreProperties.getProperty("rpgwizard.directory.sounds") + File.separator
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
        int width = (int) animation.getWidth();
        int height = (int) animation.getHeight();
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

    private void drawCollider(Collider collider, Graphics g, int x, int y, int xOffset, int yOffset) {
        int count = collider.getPointCount();
        for (int i = 0; i < count - 1; i++) {
            g.drawLine(x + collider.getPointX(i) + xOffset, y + collider.getPointY(i) + yOffset,
                    x + collider.getPointX(i + 1) + xOffset, y + collider.getPointY(i + 1) + yOffset);
        }
        // Draw the final lines
        g.drawLine(x + collider.getPointX(count - 1) + xOffset, y + collider.getPointY(count - 1) + yOffset,
                x + collider.getPointX(0) + xOffset, y + collider.getPointY(0) + yOffset);
    }

    private void drawCollider(Collider collider, Graphics g, int x, int y, int xOffset, int yOffset, int radius) {
        g.drawOval((x - radius) + xOffset, (y - radius) + yOffset, 2 * radius, 2 * radius);
    }

    private void drawTrigger(Trigger trigger, Graphics g, int x, int y, int xOffset, int yOffset) {
        int count = trigger.getPointCount();
        for (int i = 0; i < count - 1; i++) {
            g.drawLine(x + trigger.getPointX(i) + xOffset, y + trigger.getPointY(i) + yOffset,
                    x + trigger.getPointX(i + 1) + xOffset, y + trigger.getPointY(i + 1) + yOffset);
        }
        // Draw the final lines
        g.drawLine(x + trigger.getPointX(count - 1) + xOffset, y + trigger.getPointY(count - 1) + yOffset,
                x + trigger.getPointX(0) + xOffset, y + trigger.getPointY(0) + yOffset);
    }

    private void drawTrigger(Trigger trigger, Graphics g, int x, int y, int xOffset, int yOffset, int radius) {
        g.drawOval((x - radius) + xOffset, (y - radius) + yOffset, 2 * radius, 2 * radius);
    }

}
