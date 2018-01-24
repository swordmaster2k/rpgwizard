/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.ui.actions;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.JOptionPane;
import org.apache.commons.io.FilenameUtils;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.editors.BoardEditor;
import org.rpgwizard.editor.editors.board.BoardView2D;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class BoardToImageAction extends AbstractAction {

    private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(BoardToImageAction.class);

    @Override
    public void actionPerformed(ActionEvent e) {
        BoardEditor editor = MainWindow.getInstance().getCurrentBoardEditor();
        if (editor != null) {
            BoardView2D view = editor.getBoardView();
            Dimension dimension = view.getBoard().getBoardPixelDimensions();
            BufferedImage bufferedImage = new BufferedImage(dimension.width, dimension.height,
                    BufferedImage.TYPE_INT_ARGB);
            int result = JOptionPane.showConfirmDialog(null, "Would you like to export tile information only?",
                    "Question", JOptionPane.YES_NO_CANCEL_OPTION);
            switch (result) {
            case JOptionPane.CANCEL_OPTION:
                return;
            case JOptionPane.YES_OPTION:
                view.paintForExport(bufferedImage.getGraphics(), true);
                break;
            default:
                view.paintForExport(bufferedImage.getGraphics(), false);
                break;
            }

            File file = EditorFileManager.saveImage();
            if (file != null) {
                try {
                    ImageIO.write(bufferedImage, FilenameUtils.getExtension(file.getName()), file);
                } catch (IOException ex) {
                    LOGGER.error("Failed to invoke save for image file=[{}]", file, ex);
                    JOptionPane.showMessageDialog(MainWindow.getInstance(), "Error saving file!", "Error on Save",
                            JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

}
