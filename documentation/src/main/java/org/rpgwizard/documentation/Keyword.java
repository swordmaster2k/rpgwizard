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

    public Keyword(Element dt, Element dd) {
        Element h4 = dt.getElementsByTag("h4").first();
        name = "rpgcode." + h4.ownText() + h4.getElementsByClass("signature").first().text() + ";";
        type = "function";

        Element details = dd.getElementsByClass("details").first();
        if (details != null) {
            details.remove();
        }

        Element returns = dd.getElementsByClass("container-returns").first();
        Element paramType = null;
        if (returns != null) {
            paramType = returns.getElementsByClass("param-type").first();
        }

        returnType = paramType != null ? paramType.ownText() : "undefined";
        definedIn = dt.getElementsByTag("a").first().ownText();

        desc = "<![CDATA[" + dd.html().replace("h5", "h3") + "]]>";
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
