/*
 * Copyright (c) 2017, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */

export class ScriptVM {

    private _cache: object;

    constructor() {
        this._cache = {};
    }

    public async open(file: string): Promise<any> {
        console.debug("opening game script file=[%s]", file);

        let script: any;
        if (this._cache[file]) {
            script = this._cache[file];
        } else {
            script = await import(file);
            this._cache[file] = script;
        }

        console.debug("returning game script file=[%s]", file);

        return script;
    }

    public async run(file: string, origin: object) {
        console.debug("running game script file=[%s], origin=[%s]", file, origin);

        const script: any = await this.open(file);
        await script.default(origin);

        console.debug("finished game script file=[%s], origin=[%s]", file, origin);
    }

}
