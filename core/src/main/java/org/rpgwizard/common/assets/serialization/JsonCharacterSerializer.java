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
import org.rpgwizard.common.assets.Character;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;

import org.json.JSONObject;

/**
 * @author Joshua Michael Daly
 */
public class JsonCharacterSerializer extends AbstractSpriteSerializer {

	@Override
	public boolean serializable(AssetDescriptor descriptor) {
		final String ext = Paths.extension(descriptor.getURI());
		return (ext.endsWith(CoreProperties
				.getFullExtension("toolkit.character.extension.json")));
	}

	@Override
	public boolean deserializable(AssetDescriptor descriptor) {
		return serializable(descriptor);
	}

	@Override
	protected void load(AssetHandle handle, JSONObject json)
			throws AssetException {
		final Character player = super.load(
				new Character(handle.getDescriptor()), json);

		player.setLevel(json.getInt("level"));
		player.setMaxLevel(json.getInt("maxLevel"));
		player.setExperience(json.getDouble("experience"));
		player.setMaxExperience(json.getDouble("maxExperience"));
		player.setHealth(json.getDouble("health"));
		player.setMaxHealth(json.getDouble("maxHealth"));
		player.setAttack(json.getDouble("attack"));
		player.setMaxAttack(json.getDouble("maxAttack"));
		player.setDefence(json.getDouble("defence"));
		player.setMaxDefence(json.getDouble("maxDefence"));
		player.setMagic(json.getDouble("magic"));
		player.setMaxMagic(json.getDouble("maxMagic"));

		handle.setAsset(player);
	}

	@Override
	protected void store(AssetHandle handle, JSONObject json)
			throws AssetException {
		final Character player = super.store((Character) handle.getAsset(),
				json);

		json.put("level", player.getLevel());
		json.put("maxLevel", player.getMaxLevel());
		json.put("experience", player.getExperience());
		json.put("maxExperience", player.getMaxExperience());
		json.put("health", player.getHealth());
		json.put("maxHealth", player.getMaxHealth());
		json.put("attack", player.getAttack());
		json.put("maxAttack", player.getMaxAttack());
		json.put("defence", player.getDefence());
		json.put("maxDefence", player.getMaxDefence());
		json.put("magic", player.getMagic());
		json.put("maxMagic", player.getMaxMagic());
	}

}
