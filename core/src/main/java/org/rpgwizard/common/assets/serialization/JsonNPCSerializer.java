/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.NPC;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;

import org.json.JSONObject;

/**
 * @author Joshua Michael Daly
 */
public class JsonNPCSerializer extends AbstractSpriteSerializer {

	@Override
	public boolean serializable(AssetDescriptor descriptor) {
		final String ext = Paths.extension(descriptor.getURI());
		return (ext.endsWith(CoreProperties
				.getFullExtension("toolkit.npc.extension.json")));
	}

	@Override
	public boolean deserializable(AssetDescriptor descriptor) {
		return serializable(descriptor);
	}

	@Override
	protected void load(AssetHandle handle, JSONObject json)
			throws AssetException {
		final NPC npc = super.load(new NPC(handle.getDescriptor()), json);

		npc.setDescription(json.getString("description"));

		handle.setAsset(npc);
	}

	@Override
	protected void store(AssetHandle handle, JSONObject json)
			throws AssetException {
		final NPC npc = super.store((NPC) handle.getAsset(), json);

		json.put("description", npc.getDescription());
	}

}