/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets.serialization;

import net.rpgtoolkit.common.assets.AssetDescriptor;
import net.rpgtoolkit.common.assets.AssetException;
import net.rpgtoolkit.common.assets.AssetHandle;
import net.rpgtoolkit.common.assets.Item;
import net.rpgtoolkit.common.io.Paths;
import net.rpgtoolkit.common.utilities.CoreProperties;

import org.json.JSONObject;

/**
 * @author Joshua Michael Daly
 */
public class JsonItemSerializer extends AbstractSpriteSerializer {

	@Override
	public boolean serializable(AssetDescriptor descriptor) {
		final String ext = Paths.extension(descriptor.getURI());
		return (ext.endsWith(CoreProperties
				.getFullExtension("toolkit.item.extension.json")));
	}

	@Override
	public boolean deserializable(AssetDescriptor descriptor) {
		return serializable(descriptor);
	}

	@Override
	protected void load(AssetHandle handle, JSONObject json)
			throws AssetException {
		final Item item = super.load(new Item(handle.getDescriptor()), json);

		item.setDescription(json.getString("description"));

		handle.setAsset(item);
	}

	@Override
	protected void store(AssetHandle handle, JSONObject json)
			throws AssetException {
		final Item item = super.store((Item) handle.getAsset(), json);

		json.put("description", item.getDescription());
	}

}