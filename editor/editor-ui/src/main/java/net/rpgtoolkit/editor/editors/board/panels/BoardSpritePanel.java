/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.editor.editors.board.panels;

import java.awt.event.ActionEvent;
import java.io.File;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.event.ChangeEvent;
import net.rpgtoolkit.common.assets.BoardSprite;
import net.rpgtoolkit.common.assets.EventType;
import net.rpgtoolkit.common.assets.Item;
import net.rpgtoolkit.common.assets.Program;
import net.rpgtoolkit.editor.editors.board.BoardLayerView;
import net.rpgtoolkit.common.utilities.CoreProperties;
import net.rpgtoolkit.editor.utilities.EditorFileManager;
import net.rpgtoolkit.editor.utilities.GuiHelper;

/**
 *
 *
 * @author Joshua Michael Daly
 */
public class BoardSpritePanel extends BoardModelPanel {

  private final JComboBox fileComboBox;
  private final JLabel fileLabel;

  private final JComboBox eventProgramComboBox;
  private final JLabel eventProgramLabel;

  private final JComboBox threadComboBox;
  private final JLabel threadLabel;

  private final JSpinner xSpinner;
  private final JLabel xLabel;

  private final JSpinner ySpinner;
  private final JLabel yLabel;

  private final JSpinner layerSpinner;
  private final JLabel layerLabel;

  private int lastSpinnerLayer; // Used to ensure that the selection is valid.

  private final JComboBox eventComboBox;
  private final JLabel eventLabel;

  private static final String[] EVENT_TYPES = {
    "OVERLAP"
  };

  public BoardSpritePanel(final BoardSprite boardSprite) {
    ///
    /// super
    ///
    super(boardSprite);
    ///
    /// fileComboBox
    ///
    File directory = new File(
            System.getProperty("project.path")
            + File.separator
            + CoreProperties.getProperty("toolkit.directory.item") 
            + File.separator);
    String[] exts = EditorFileManager.getTypeExtensions(Item.class);
    fileComboBox = GuiHelper.getFileListJComboBox(directory, exts, true);
    fileComboBox.setSelectedItem(boardSprite.getFileName());
    fileComboBox.addActionListener((ActionEvent e) -> {
        String fileName = (String) fileComboBox.getSelectedItem();
        
        if (fileName == null) {
            return;
        }
        
        boardSprite.setFileName((String) fileComboBox.getSelectedItem());
        updateCurrentBoardView();
    });
    ///
    /// activationComboBox
    ///
    directory = new File(
            System.getProperty("project.path") 
            + File.separator
            + CoreProperties.getProperty("toolkit.directory.program") 
            + File.separator);
    exts = EditorFileManager.getTypeExtensions(Program.class);
    eventProgramComboBox = GuiHelper.getFileListJComboBox(directory, exts, true);
    eventProgramComboBox.setSelectedItem(boardSprite.getEventProgram());
    eventProgramComboBox.addActionListener((ActionEvent e) -> {
        if (eventProgramComboBox.getSelectedItem() != null) {
            boardSprite.setEventProgram((String) eventProgramComboBox.getSelectedItem());
        }
    });
    ///
    /// multiTaskingTextField
    ///
    threadComboBox = GuiHelper.getFileListJComboBox(directory, exts, true);
    threadComboBox.setSelectedItem(boardSprite.getThread());
    threadComboBox.addActionListener((ActionEvent e) -> {
        if (threadComboBox.getSelectedItem() != null) {
            boardSprite.setThread((String) threadComboBox.getSelectedItem());
        }
    });
    ///
    /// xSpinner
    ///
    xSpinner = new JSpinner();
    xSpinner.setValue(((BoardSprite) model).getX());
    xSpinner.addChangeListener((ChangeEvent e) -> {
        BoardSprite sprite = (BoardSprite) model;

        if (sprite.getX() != (int) xSpinner.getValue()) {
            sprite.setX((int) xSpinner.getValue());
            updateCurrentBoardView();
        }
    });
    ///
    /// ySpinner
    ///
    ySpinner = new JSpinner();
    ySpinner.setValue(((BoardSprite) model).getY());
    ySpinner.addChangeListener((ChangeEvent e) -> {
        BoardSprite sprite = (BoardSprite) model;

        if (sprite.getY() != (int) ySpinner.getValue()) {
            sprite.setY((int) ySpinner.getValue());
            updateCurrentBoardView();
        }
    });
    ///
    /// layerSpinner
    ///
    layerSpinner = getJSpinner(((BoardSprite) model).getLayer());
    layerSpinner.addChangeListener((ChangeEvent e) -> {
        BoardSprite sprite = (BoardSprite) model;

        BoardLayerView lastLayerView = getBoardEditor().getBoardView().
                getLayer((int) sprite.getLayer());

        BoardLayerView newLayerView = getBoardEditor().getBoardView().
                getLayer((int) layerSpinner.getValue());

        // Make sure this is a valid move.
        if (lastLayerView != null && newLayerView != null) {
            // Do the swap.
            sprite.setLayer((int) layerSpinner.getValue());
            newLayerView.getLayer().getSprites().add(sprite);
            lastLayerView.getLayer().getSprites().remove(sprite);
            updateCurrentBoardView();
            
            // Store new layer selection index.
            lastSpinnerLayer = (int) layerSpinner.getValue();
        } else {
            // Not a valid layer revert selection.
            layerSpinner.setValue(lastSpinnerLayer);
        }
    });
    ///
    /// typeComboBox
    ///
    eventComboBox = new JComboBox(EVENT_TYPES);
    eventComboBox.addActionListener((ActionEvent e) -> {
        String type = (String) eventComboBox.getSelectedItem();
        if (type.equals(EVENT_TYPES[0])) {
            boardSprite.setEventType(EventType.OVERLAP);
        }
    });
    ///
    /// this
    ///
    horizontalGroup.addGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(fileLabel = getJLabel("Item File"))
            .addComponent(xLabel = getJLabel("X"))
            .addComponent(yLabel = getJLabel("Y"))
            .addComponent(layerLabel = getJLabel("Layer"))
            .addComponent(eventLabel = getJLabel("Event"))
            .addComponent(eventProgramLabel = getJLabel("Event Program"))
            .addComponent(threadLabel = getJLabel("Thread")));

    horizontalGroup.addGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(fileComboBox)
            .addComponent(xSpinner)
            .addComponent(ySpinner)
            .addComponent(layerSpinner)
            .addComponent(eventComboBox)
            .addComponent(eventProgramComboBox)
            .addComponent(threadComboBox));

    layout.setHorizontalGroup(horizontalGroup);

    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(fileLabel).addComponent(fileComboBox));

    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(xLabel).addComponent(xSpinner));

    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(yLabel).addComponent(ySpinner));

    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(yLabel).addComponent(ySpinner));

    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(layerLabel).addComponent(layerSpinner));

    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(eventLabel).addComponent(eventComboBox));
    
    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(eventProgramLabel).addComponent(eventProgramComboBox));

    verticalGroup.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
            .addComponent(threadLabel).addComponent(threadComboBox));

    layout.setVerticalGroup(verticalGroup);
  }
  
}
