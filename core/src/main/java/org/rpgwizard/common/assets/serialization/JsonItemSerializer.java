/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import java.util.Map;
import org.json.JSONObject;
import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.Item;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;

/**
 *
 * @author Joshua Michael Daly
 */
public class JsonItemSerializer extends AbstractJsonSerializer {

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
		final Item item = new Item(handle.getDescriptor());

		item.setVersion(json.getDouble("version"));
		item.setName(json.getString("name"));
		item.setIcon(json.getString("icon"));
		item.setDescription(json.getString("description"));
		item.setType(json.getString("type"));
		item.setPrice(json.getInt("price"));

		Map<String, Integer> effects = deserializeIntegerMap(json
				.getJSONObject("effects"));
		item.setHealthEffect(effects.get("health"));
		item.setAttackEffect(effects.get("attack"));
		item.setDefenceEffect(effects.get("defence"));
		item.setMagicEffect(effects.get("magic"));

		handle.setAsset(item);
	}

	@Override
	protected void store(AssetHandle handle, JSONObject json)
			throws AssetException {
		super.store(handle, json);

		final Item item = (Item) handle.getAsset();

		json.put("name", item.getName());
		json.put("icon", serializePath(item.getIcon()));
		json.put("description", item.getDescription());
		json.put("type", item.getType());
		json.put("price", item.getPrice());

		final JSONObject effects = new JSONObject();
		effects.put("health", item.getHealthEffect());
		effects.put("attack", item.getAttackEffect());
		effects.put("defence", item.getDefenceEffect());
		effects.put("magic", item.getMagicEffect());

		json.put("effects", effects);
	}

}
