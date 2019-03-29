/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.animation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.text.DefaultFormatter;
import org.rpgwizard.common.assets.SpriteSheet;

/**
 *
 * @author Joshua Michael Daly
 */
public final class SpriteSheetDialog extends JDialog {

    private final JSpinner tileWidthSpinner;
    private final JSpinner tileHeightSpinner;
    private final JButton okButton;
    private final JButton cancelButton;

    private final SpriteSheetImagePanel spriteSheetImagePanel;

    private SpriteSheet value;

    public SpriteSheetDialog(Window owner, SpriteSheet sheet) {
        super(owner, "Select Sprites", JDialog.ModalityType.APPLICATION_MODAL);

        okButton = new JButton("OK");
        okButton.addActionListener((e) -> {
            value = collect();
            dispose();
        });

        cancelButton = new JButton("Cancel");
        cancelButton.addActionListener((e) -> {
            value = null;
            dispose();
        });

        spriteSheetImagePanel = new SpriteSheetImagePanel(sheet, sheet.getTileWidth(), sheet.getTileHeight(),
                new Dimension(640, 480), okButton);
        spriteSheetImagePanel.addImageListener(() -> {
            okButton.setEnabled(true);
        });

        tileWidthSpinner = new JSpinner(new SpinnerNumberModel(sheet.getTileWidth(), 16, 512, 1));
        ((JSpinner.DefaultEditor) tileWidthSpinner.getEditor()).getTextField().setColumns(7);
        JFormattedTextField tileWidthField = (JFormattedTextField) tileWidthSpinner.getEditor().getComponent(0);
        ((DefaultFormatter) tileWidthField.getFormatter()).setCommitsOnValidEdit(true);
        tileWidthSpinner.addChangeListener((e) -> {
            spriteSheetImagePanel.setTileWidth((Integer) tileWidthSpinner.getValue());
            spriteSheetImagePanel.reset();
            okButton.setEnabled(false);
        });

        tileHeightSpinner = new JSpinner(new SpinnerNumberModel(sheet.getTileHeight(), 16, 512, 1));
        ((JSpinner.DefaultEditor) tileHeightSpinner.getEditor()).getTextField().setColumns(7);
        JFormattedTextField tileHeightField = (JFormattedTextField) tileHeightSpinner.getEditor().getComponent(0);
        ((DefaultFormatter) tileHeightField.getFormatter()).setCommitsOnValidEdit(true);
        tileHeightSpinner.addChangeListener((e) -> {
            spriteSheetImagePanel.setTileHeight((Integer) tileHeightSpinner.getValue());
            spriteSheetImagePanel.reset();
            okButton.setEnabled(false);
        });

        JScrollPane spriteSheetPane = new JScrollPane(spriteSheetImagePanel);

        GridLayout gridLayout = new GridLayout(0, 2);
        gridLayout.setVgap(5);

        JPanel dimensionPanel = new JPanel(gridLayout);
        dimensionPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        dimensionPanel.add(new JLabel("Tile Width", SwingConstants.LEFT));
        dimensionPanel.add(tileWidthSpinner);
        dimensionPanel.add(new JLabel("Tile Height", SwingConstants.LEFT));
        dimensionPanel.add(tileHeightSpinner);

        JPanel buttonPanel = new JPanel(new GridLayout(0, 2));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        buttonPanel.add(okButton);
        buttonPanel.add(cancelButton);

        JPanel northPanel = new JPanel(new GridLayout(1, 2));
        northPanel.add(dimensionPanel);
        northPanel.add(buttonPanel);

        add(northPanel, BorderLayout.NORTH);
        add(spriteSheetPane, BorderLayout.CENTER);

        setResizable(false);
        pack();
    }

    public SpriteSheet getValue() {
        return value;
    }

    public void display() throws IOException {
        spriteSheetImagePanel.init();
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowActivated(WindowEvent e) {
                spriteSheetImagePanel.ensureVisible();
            }
        });
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private SpriteSheet collect() {
        final String fileName = spriteSheetImagePanel.getFileName();
        final Rectangle selection = spriteSheetImagePanel.getSelection();
        final int tileWidth = (Integer) tileWidthSpinner.getValue();
        final int tileHeight = (Integer) tileHeightSpinner.getValue();
        final int x = selection.x * tileWidth;
        final int y = selection.y * tileHeight;
        final int width = selection.width * tileWidth;
        final int height = selection.height * tileHeight;
        return new SpriteSheet(fileName, x, y, width, height, tileWidth, tileHeight);
    }

    public static void main(String[] args) throws IOException {
        System.setProperty("project.path",
                "D:\\OneDrive\\Desktop\\rpgwizard-1.5.1-windows\\rpgwizard-1.5.1\\projects\\BlackVoid");

        SpriteSheet sheet = new SpriteSheet(null, 0, 0, 4 * 24, 1 * 24, 24, 24);
        SpriteSheetDialog dialog = new SpriteSheetDialog(null, sheet);
        dialog.display();

        final SpriteSheet result = dialog.getValue();
        System.out.println(result);
        final BufferedImage subImage = result.loadSelection();
        ImageIO.write(subImage, "png", new File("C:/Users/jdaly/Desktop/image.png"));
    }

}
