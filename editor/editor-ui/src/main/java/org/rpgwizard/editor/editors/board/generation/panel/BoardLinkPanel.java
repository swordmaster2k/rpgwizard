/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.generation.panel;

import java.awt.GridLayout;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SwingConstants;
import org.rpgwizard.common.assets.board.Board;
import org.rpgwizard.editor.editors.board.generation.ProgramType;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public final class BoardLinkPanel extends AbstractProgramPanel {

    private static final Logger LOGGER = LoggerFactory.getLogger(BoardLinkPanel.class);

    private JComboBox boardCombo;
    private JSpinner tileXSpinner;
    private JSpinner tileYSpinner;
    private JSpinner layerSpinner;

    public BoardLinkPanel() {
        super(ProgramType.BOARD_LINK);
        init("", 10, 20, 1);
    }

    public BoardLinkPanel(Map<String, Object> parameters) {
        super(ProgramType.BOARD_LINK);
        String boardName = String.valueOf(parameters.get("boardName"));
        double x = Double.valueOf(String.valueOf(parameters.get("tileX")));
        double y = Double.valueOf(String.valueOf(parameters.get("tileY")));
        int layer = Integer.valueOf(String.valueOf(parameters.get("layer")));
        init(boardName, x, y, layer);
    }

    private void init(String board, double x, double y, int layer) {
        LOGGER.info("Setting up panel, board=[{}], x=[{}], y=[{}], layer=[{}]", board, x, y, layer);

        GridLayout gridLayout = new GridLayout(0, 2);
        gridLayout.setVgap(5);
        setLayout(gridLayout);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(new JLabel("Board", SwingConstants.LEFT));

        String[] exts = EditorFileManager.getTypeExtensions(Board.class);
        boardCombo = GuiHelper.getFileListJComboBox(new File[] { EditorFileManager.getFullPath(Board.class) }, exts,
                true);
        boardCombo.setSelectedItem(board);
        add(boardCombo);

        add(new JLabel("X", SwingConstants.LEFT));
        tileXSpinner = GuiHelper.getJSpinner(Double.valueOf(x));
        add(tileXSpinner);

        add(new JLabel("Y", SwingConstants.LEFT));
        tileYSpinner = GuiHelper.getJSpinner(Double.valueOf(y));
        add(tileYSpinner);

        add(new JLabel("Layer", SwingConstants.LEFT));
        layerSpinner = GuiHelper.getJSpinner(layer);
        add(layerSpinner);
    }

    @Override
    public Map<String, Object> collect() {
        Map<String, Object> values = new HashMap<>();
        values.put("boardName", String.valueOf(boardCombo.getSelectedItem()));
        values.put("tileX", Double.valueOf(tileXSpinner.getValue().toString()));
        values.put("tileY", Double.valueOf(tileYSpinner.getValue().toString()));
        values.put("layer", Integer.valueOf(layerSpinner.getValue().toString()));
        return values;
    }

}
