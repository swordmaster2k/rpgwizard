/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
export class ScriptVM {
    constructor() {
        this.cache = {};
    }
    async run(file, origin) {
        console.info("running game script file=[%s], origin=[%s]", file, origin);
        // Try to get the game script from the cache
        let script;
        if (this.cache[file]) {
            script = this.cache[file];
        }
        else {
            script = await import(file);
            this.cache[file] = script;
        }
        // Run the game script, provide the origin
        await script.default(origin);
        console.info("finished game script file=[%s], origin=[%s]", file, origin);
    }
}
