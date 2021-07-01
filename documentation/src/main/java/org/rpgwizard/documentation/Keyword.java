/**
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.documentation;

import org.jsoup.nodes.Element;

/**
 *
 * @author Joshua Michael Daly
 */
public class Keyword {

    private final String name;
    private final String type;
    private final String returnType;
    private final String definedIn;
    private final String desc;

    public Keyword(String prefix, Element member) {
        Element h4 = member.getElementsByTag("h4").first();
        name = prefix + "." + h4.getElementsByClass("code-name").first().text().split("\\)")[0] + ");";
        type = "function";
        
        Element paramType = h4.getElementsByClass("type-signature").first();
        
        if (paramType != null && -1 < paramType.ownText().indexOf("{")) {
            returnType = paramType.ownText().substring(paramType.ownText().indexOf("{"));
        } else {
            returnType = "undefined";
        }
        
        Element details = member.getElementsByClass("details").first();
        Element detailsSpan = details.getElementsByTag("span").first();
        definedIn = detailsSpan.getElementsByTag("a").get(0).ownText() + ":" + detailsSpan.getElementsByTag("a").get(1).ownText();
        
        desc = "<![CDATA[" + member.html().replace("h5", "h3") + "]]>";
    }

    public String toXml() {
        return String.format(""
                + "        <keyword name=\"%s\" type=\"%s\" returnType=\"%s\" definedIn=\"%s\">\n"
                + "            <desc>%s</desc>\n"
                + "        </keyword>\n",
                name, type, returnType, definedIn, desc
        );
    }

}
