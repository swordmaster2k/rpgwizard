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
import javax.swing.SwingConstants;
import org.apache.commons.lang3.StringUtils;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.editor.editors.board.generation.ProgramType;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public final class CustomPanel extends AbstractProgramPanel {

    private JComboBox programCombo;

    public CustomPanel() {
        super(ProgramType.CUSTOM);
        init("");
    }

    public CustomPanel(Map<String, Object> parameters) {
        super(ProgramType.CUSTOM);
        init(parameters.get("program").toString());
    }

    private void init(String program) {
        GridLayout gridLayout = new GridLayout(0, 2);
        gridLayout.setVgap(5);
        setLayout(gridLayout);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JLabel("Program", SwingConstants.LEFT));

        String[] exts = EditorFileManager.getTypeExtensions(Program.class);
        programCombo = GuiHelper.getFileListJComboBox(new File[] { EditorFileManager.getFullPath(Program.class) }, exts,
                true);
        if (StringUtils.isNotBlank(program)) {
            programCombo.setSelectedItem(program);
        }
        add(programCombo);
    }

    @Override
    public Map<String, Object> collect() {
        Map<String, Object> values = new HashMap<>();
        values.put("program", String.valueOf(programCombo.getSelectedItem()));
        return values;
    }

}
