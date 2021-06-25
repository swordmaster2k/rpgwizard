/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.documentation;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 *
 * @author Joshua Michael Daly
 */
public class AutoCompleteGenerator {

    private static final String BASE = "src/main/resources/";
    private static final String INPUT = BASE + "autocomplete/input/";
    private static final String OUTPUT = BASE + "autocomplete/output/";

    public static String toXml(String keywords) {
        return String.format(
                "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n"
                + "<!DOCTYPE api SYSTEM \"CompletionXml.dtd\">\n"
                + "<api language=\"RPGCode\">\n"
                + "    <environment paramStartChar=\"(\" paramEndChar=\")\" paramSeparator=\", \" terminal=\";\"/>\n"
                + "    <keywords>\n"
                + "%s"
                + "    </keywords>\n"
                + "</api>",
                keywords
        );
    }

    public static void generate() {
        try {
            String keywords = "";
            Collection<File> files = FileUtils.listFiles(new File(INPUT), new String[]{"html"}, true);
            for (File htmlFile : files) {
                Document document = Jsoup.parse(htmlFile, null);
                Element members = document.getElementsByClass("members").first();
                if (members == null) {
                    continue;
                }
                
                for (Element member : members.getElementsByClass("member")) {
                    try {
                        keywords += new Keyword("rpg", member).toXml();
                    } catch (Exception ex) {
                        Logger.getLogger(AutoCompleteGenerator.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }

            FileUtils.writeStringToFile(new File(OUTPUT + "rpgcode.xml"), toXml(keywords), "UTF-8");
            FileUtils.copyFile(new File(OUTPUT + "rpgcode.xml"), new File("../editor/editor-ui/src/main/resources/autocomplete/rpgcode.xml"));
        } catch (IOException ex) {
            Logger.getLogger(AutoCompleteGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        generate();
    }

}
