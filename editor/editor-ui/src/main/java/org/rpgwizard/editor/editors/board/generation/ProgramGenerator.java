/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.editor.editors.board.generation;

import com.google.common.io.Resources;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.apache.commons.io.FileUtils;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetManager;
import org.rpgwizard.common.assets.Program;
import org.rpgwizard.editor.utilities.EditorFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Joshua Michael Daly
 */
public class ProgramGenerator {

    private static final Logger LOGGER = LoggerFactory.getLogger(ProgramGenerator.class);

    private static final String TEMPLATE_DIR = "program/templates";
    private static final String AUTO_GENERATED_DIR = "auto_generated";
    private static final String TEMPLATE_EXT = ".js";

    public static String generate(Map<String, Object> placeHolders, ProgramType type)
            throws IOException, AssetException, URISyntaxException {
        String id = type.toString() + "_" + UUID.randomUUID().toString();
        return generate(id, placeHolders, type);
    }

    public static String generate(String id, Map<String, Object> parameters, ProgramType type)
            throws IOException, AssetException, URISyntaxException {
        LOGGER.info("Generating program, id=[{}], parameters=[{}], type=[{}]", id, parameters, type);

        String template = readTemplate(type.toString());
        String code = applyPlaceHolders(parameters, template);
        return saveProgram(id, code);
    }

    private static String readTemplate(String template) throws IOException, URISyntaxException {
        URL url = Resources.getResource(TEMPLATE_DIR + File.separator + template + TEMPLATE_EXT);
        File file = new File(url.toURI());
        return FileUtils.readFileToString(file, StandardCharsets.UTF_8.name());
    }

    private static String applyPlaceHolders(Map<String, Object> placeHolders, String template) {
        for (Entry<String, Object> entry : placeHolders.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (value instanceof String) {
                value = '"' + ((String) value) + '"'; // Escape string parameters.
            }
            template = template.replace(key, String.valueOf(value));
        }
        return template;
    }

    private static String saveProgram(String id, String code) throws IOException, AssetException {
        String typeExt = EditorFileManager.getTypeExtensions(Program.class)[0];
        String typeDir = EditorFileManager.getTypeSubdirectory(Program.class);
        File projectPath = EditorFileManager.getPath(typeDir);
        File autoPath = new File(projectPath, AUTO_GENERATED_DIR);
        File programFile = new File(autoPath, id + "." + typeExt);

        Program program = new Program(new AssetDescriptor(programFile.toURI()));
        program.update(code);

        AssetManager.getInstance().serialize(AssetManager.getInstance().getHandle(program));

        return program.getFileName();
    }

    public static void main(String[] args) throws Exception {
        Map<String, Object> input = new HashMap<>();
        input.put("boardName", "test.board");
        input.put("tileX", 10);
        input.put("tileY", 15);
        input.put("layer", 1);

        generate(input, ProgramType.BOARD_LINK);
    }

}
