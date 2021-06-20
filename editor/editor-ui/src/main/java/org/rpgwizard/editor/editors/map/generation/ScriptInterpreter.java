/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.map.generation;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.rpgwizard.common.assets.Script;
import org.rpgwizard.editor.MainWindow;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class ScriptInterpreter {

    private static final Logger LOGGER = LoggerFactory.getLogger(ScriptInterpreter.class);

    private static final String TYPE_REGEX = "SCRIPT_TYPE\\((?<type>.*)\\)";
    private static final Pattern TYPE_PATTERN = Pattern.compile(TYPE_REGEX);
    private static final String GROUP_NAME = "type";

    // Script Patterns
    private static final Pattern MAP_LINK_PATTERN = Pattern
            .compile("rpgcode\\.sendToBoard\\((?<boardName>.*),\\s*(?<tileX>.*),\\s*(?<tileY>.*),\\s*(?<layer>.*)\\);");

    public static Pair<ScriptType, Map<String, Object>> interpret(String child) {
        Map<String, Object> defaultParameters = new HashMap<>();
        defaultParameters.put("script", child);

        if (StringUtils.isNotBlank(child)) {
            try {
                File parent = EditorFileManager.getFullPath(Script.class);
                File file = new File(parent, child);

                Script script = MainWindow.getInstance().openScript(file);
                String code = script.getStringBuffer().toString();
                Matcher matcher = TYPE_PATTERN.matcher(code);
                if (matcher.find()) {
                    ScriptType scriptType = ScriptType.valueOf(matcher.group(GROUP_NAME));
                    switch (scriptType) {
                    case MAP_LINK:
                        return new ImmutablePair<>(scriptType, interpretBoardLink(code));
                    }
                }
            } catch (Exception ex) {
                LOGGER.error("Failed to interpret child=[{}], ex=[{}]", child, ex);
            }
        }

        return new ImmutablePair<>(ScriptType.CUSTOM, defaultParameters);
    }

    private static Map<String, Object> interpretBoardLink(String code) {
        Map<String, Object> parameters = new HashMap<>();
        Matcher matcher = MAP_LINK_PATTERN.matcher(code);
        if (matcher.find()) {
            parameters.put("boardName", matcher.group("boardName").replaceAll("^\"|\"$", ""));
            parameters.put("tileX", matcher.group("tileX"));
            parameters.put("tileY", matcher.group("tileY"));
            parameters.put("layer", matcher.group("layer"));
        }
        return parameters;
    }

    public static void main(String[] args) {
        Pair<ScriptType, Map<String, Object>> result = interpret(null);
        System.out.println(result);
    }

}
