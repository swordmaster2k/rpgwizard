/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.generation.panel;

import java.awt.GridLayout;
import java.io.File;
import java.util.HashMap;
import java.util.Map;
import javax.swing.BorderFactory;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import org.apache.commons.lang3.StringUtils;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.editor.editors.map.generation.ScriptType;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.rpgwizard.editor.utilities.GuiHelper;

/**
 *
 * @author Joshua Michael Daly
 */
public final class CustomPanel extends AbstractScriptPanel {

    private JComboBox scriptCombo;

    public CustomPanel() {
        super(ScriptType.CUSTOM);
        init("");
    }

    public CustomPanel(Map<String, Object> parameters) {
        super(ScriptType.CUSTOM);
        init(parameters.get("script") != null ? parameters.get("script").toString() : "");
    }

    private void init(String script) {
        GridLayout gridLayout = new GridLayout(0, 2);
        gridLayout.setVgap(5);
        setLayout(gridLayout);

        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(new JLabel("Script", SwingConstants.LEFT));

        String[] exts = EditorFileManager.getTypeExtensions(Script.class);
        scriptCombo = GuiHelper.getFileListJComboBox(new File[] { EditorFileManager.getFullPath(Script.class) }, exts,
                true);
        if (StringUtils.isNotBlank(script)) {
            scriptCombo.setSelectedItem(script);
        }
        add(scriptCombo);
    }

    @Override
    public Map<String, Object> collect() {
        Map<String, Object> values = new HashMap<>();
        values.put("script", String.valueOf(scriptCombo.getSelectedItem()));
        return values;
    }

}
