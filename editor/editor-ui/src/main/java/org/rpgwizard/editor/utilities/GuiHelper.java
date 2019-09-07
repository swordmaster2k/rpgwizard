/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.utilities;

import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;
import javax.swing.GroupLayout;
import javax.swing.ImageIcon;
import javax.swing.JComboBox;
import javax.swing.JFormattedTextField;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.ListModel;
import javax.swing.ListSelectionModel;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatter;
import org.rpgwizard.common.assets.Board;
import org.rpgwizard.editor.ui.listeners.PopupListFilesListener;

/**
 * Contains useful shortcuts and constants for building editors via Swing.
 *
 * @author Joel Moore
 */
public abstract class GuiHelper {

    public static final int JTF_HEIGHT = 24;

    /**
     * Creates a GroupLayout for the specified panel and assigns it to that panel. Common settings are applied: auto
     * create gaps and auto create container gaps are true.
     *
     * @param forPanel
     *            the JPanel to use as the host for the layout; this panel's layout will also be set to the created
     *            layout
     * @return the created GroupLayout
     */
    public static GroupLayout createGroupLayout(JPanel forPanel) {
        GroupLayout layout = new GroupLayout(forPanel);
        forPanel.setLayout(layout);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);
        return layout;
    }

    /**
     * Creates a JList with the specified data. Common settings are applied: selection mode is single selection, layout
     * orientation is vertical, visible row count is given a negative number.
     *
     * @param dataModel
     *            the list of data to assign to the JList
     * @return a new, configured JList
     */
    public static JList createVerticalJList(ListModel dataModel) {
        JList list = new JList(dataModel);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setLayoutOrientation(JList.VERTICAL);
        list.setVisibleRowCount(-1);
        return list;
    }

    /**
     * Creates and returns a new ActionListener that removes the currently selected item from a JList and from its
     * DefaultListModel.
     *
     * @param backingList
     * @param listComponent
     * @return a new ActionListener that removes the currently selected item when triggered
     */
    public static ActionListener simpleRemoveListener(final DefaultListModel backingList, final JList listComponent) {
        return new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                int index = listComponent.getSelectedIndex();
                if (index >= 0) {
                    backingList.remove(index);
                    if (index == backingList.size()) {
                        index--;
                    }
                    listComponent.setSelectedIndex(index);
                    listComponent.ensureIndexIsVisible(index);
                }
            }
        };
    }

    /**
     * Given an image, create an icon, scaled to fit in a box of the given width and height. Preserves aspect ratio.
     * Will scale up if the box is bigger than the image's dimensions in both directions. Will scale down if the box is
     * smaller than image's dimensions in either direction.
     *
     * Hooray for Stack Overflow (David Kroukamp's answer): http://stackoverflow.
     * com/questions/14548808/scale-the-imageicon-automatically-to-label-size
     *
     * @param image
     * @param width
     * @param height
     * @return a new ImageIcon from the scaled image, or null if image was null
     */
    public static ImageIcon ImageToIcon(BufferedImage image, int width, int height) {
        if (image == null) {
            return null;
        }
        double scale;
        int originalW = image.getWidth();
        int originalH = image.getHeight();
        if (originalW >= originalH) {
            scale = (double) width / originalW;
        } else {
            scale = (double) height / originalH;
        }
        int resizedW = (int) (originalW * scale);
        int resizedH = (int) (originalH * scale);
        BufferedImage resized = new BufferedImage(resizedW, resizedH, BufferedImage.TRANSLUCENT);
        Graphics2D g = resized.createGraphics();
        g.addRenderingHints(new RenderingHints(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY));
        g.drawImage(image, 0, 0, resizedW, resizedH, null);
        g.dispose();
        return new ImageIcon(resized);
    }

    /**
     * Loads an image given a filename relative to the Bitmap directory.
     *
     * @param fileName
     *            the fileName of the image, relative to the Bitmap directory
     * @return the loaded image, or null if given a blank fileName or if an exception occurred
     */
    public static BufferedImage loadImage(String fileName) {
        try {
            if (!fileName.equals("")) {
                String subdir = EditorFileManager.getGraphicsSubdirectory();
                FileInputStream fis = new FileInputStream(EditorFileManager.getPath(subdir + "/" + fileName));
                return ImageIO.read(fis);
            }
        } catch (IOException e) {
            System.err.println(e);
        }
        return null;
    }

    /**
     * Gets a list of all the documents under the given directories with the required extensions. For use with
     * JComboBoxes.
     *
     * @param rootDirectories
     * @param extensions
     * @param recursive
     * @return
     */
    public static JComboBox getFileListJComboBox(File[] rootDirectories, String[] extensions, boolean recursive) {
        JComboBox comboBox = new JComboBox();

        comboBox.addPopupMenuListener(new PopupListFilesListener(rootDirectories, extensions, recursive, comboBox));

        comboBox.setPrototypeDisplayValue("*****************");
        comboBox.insertItemAt("", 0);
        return comboBox;
    }

    /**
     * Gets a JSpinner for editing Integer values.
     * 
     * @param value
     * @return spinner
     */
    public static JSpinner getJSpinner(Integer value) {
        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(value, new Integer(0), new Integer(Integer.MAX_VALUE), new Integer(1)));
        spinner.setFont(new JLabel().getFont());
        JFormattedTextField field = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        return spinner;
    }

    /**
     * Gets a JSpinner for editing Integer values.
     *
     * @param value
     * @param min
     * @param max
     * @param increment
     * @return spinner
     */
    public static JSpinner getJSpinner(Integer value, Integer min, Integer max, Integer increment) {
        JSpinner spinner = new JSpinner(new SpinnerNumberModel(value, min, max, increment));
        spinner.setFont(new JLabel().getFont());
        JFormattedTextField field = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        return spinner;
    }

    /**
     * Gets a JSpinner for editing Double values.
     *
     * @param value
     * @return spinner
     */
    public static JSpinner getJSpinner(Double value) {
        JSpinner spinner = new JSpinner(
                new SpinnerNumberModel(value, new Double(0), new Double(Integer.MAX_VALUE), new Double(1)));
        spinner.setFont(new JLabel().getFont());
        JFormattedTextField field = ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField();
        DefaultFormatter formatter = (DefaultFormatter) field.getFormatter();
        formatter.setCommitsOnValidEdit(true);
        return spinner;
    }

    /**
     * Draws a grid on the target Graphics2D context.
     *
     * @param g2d
     * @param tileWidth
     * @param tileHeight
     * @param clipRectangle
     */
    public static void drawGrid(Graphics2D g2d, int tileWidth, int tileHeight, Rectangle clipRectangle) {
        int startX = (clipRectangle.x / tileWidth * tileWidth);
        int startY = (clipRectangle.y / tileHeight * tileHeight);
        int endX = (clipRectangle.x + clipRectangle.width);
        int endY = (clipRectangle.y + clipRectangle.height);

        g2d.setColor(Color.BLACK);

        for (int x = startX; x < endX; x += tileWidth) {
            g2d.drawLine(x, clipRectangle.y, x, clipRectangle.y + clipRectangle.height - 1);
        }
        for (int y = startY; y < endY; y += tileHeight) {
            g2d.drawLine(clipRectangle.x, y, clipRectangle.x + clipRectangle.width - 1, y);
        }
    }

    /**
     * Draws the standard selection box around the requested area.
     * 
     * @param g
     * @param tileWidth
     * @param tileHeight
     * @param selection
     * @param modifier
     */
    public static void drawSelection(Graphics2D g, int tileWidth, int tileHeight, Rectangle selection, int modifier) {
        g.setColor(new Color(100, 100, 255));
        g.drawRect(selection.x * tileWidth, selection.y * tileHeight, (selection.width + modifier) * tileWidth,
                (selection.height + modifier) * tileHeight);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_ATOP, 0.2f));
        g.fillRect(selection.x * tileWidth + 1, selection.y * tileHeight + 1,
                (selection.width + modifier) * tileWidth - 1, (selection.height + modifier) * tileHeight - 1);
    }

    /**
     * Check that the selection is within the bounds of either x or y.
     * 
     * @param max
     * @param actual
     * @return
     */
    public static boolean checkSelectionBounds(int max, int actual) {
        if (actual > max) {
            return false;
        }
        return !(actual < 0);
    }

    /**
     * Check that the selection is within the bounds of both x and y.
     * 
     * @param maxX
     * @param maxY
     * @param x
     * @param y
     * @return
     */
    public static boolean checkSelectionBounds(int maxX, int maxY, int x, int y) {
        if (x > maxX || y > maxY) {
            return false;
        }
        return !(x < 0 || y < 0);
    }

    public static int[] ensureVectorVisible(Board board, int x1, int y1, int x2, int y2) {
        int width = board.getWidth() * board.getTileWidth();
        int height = board.getHeight() * board.getTileHeight();

        if (x1 == width && x2 == width) {
            x1 -= 1;
            x2 -= 1;
        }
        if (y1 == height && y2 == height) {
            y1 -= 1;
            y2 -= 1;
        }

        return new int[] { x1, y1, x2, y2 };
    }

    public static void buildEditPanelPair(JPanel parent, JPanel child, List<Component> labels, List<Component> inputs) {
        // Create Layout for top level panel
        GroupLayout layout = GuiHelper.createGroupLayout(parent);

        // Create Layouts for second level panels
        GroupLayout generalLayout = GuiHelper.createGroupLayout(child);

        GroupLayout.ParallelGroup horizontalParallelGroup = generalLayout.createParallelGroup();
        GroupLayout.SequentialGroup sequentialGroup;
        int length = labels.size();
        for (int i = 0; i < length; i++) {
            sequentialGroup = generalLayout.createSequentialGroup();
            sequentialGroup.addComponent(labels.get(i));
            sequentialGroup.addComponent(inputs.get(i));

            generalLayout.setHorizontalGroup(horizontalParallelGroup.addGroup(sequentialGroup));
        }

        GroupLayout.SequentialGroup verticalSequentialGroup = generalLayout.createSequentialGroup();
        GroupLayout.ParallelGroup parallelGroup;
        for (int i = 0; i < length; i++) {
            parallelGroup = generalLayout.createParallelGroup();
            parallelGroup.addComponent(labels.get(i));
            parallelGroup.addComponent(inputs.get(i));
            generalLayout.setVerticalGroup(verticalSequentialGroup.addGroup(parallelGroup));
        }

        generalLayout.linkSize(SwingConstants.HORIZONTAL, labels.toArray(new Component[labels.size()]));

        generalLayout.linkSize(SwingConstants.VERTICAL, labels.toArray(new Component[inputs.size()]));

        JPanel configPanel = new JPanel(new BorderLayout());
        configPanel.add(child, BorderLayout.NORTH);

        // Configure STATS PANEL layout
        layout.setHorizontalGroup(layout.createSequentialGroup().addComponent(configPanel));

        layout.linkSize(SwingConstants.VERTICAL, configPanel);

        layout.setVerticalGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE).addComponent(configPanel)));
    }

}
