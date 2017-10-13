/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets.serialization;

import org.rpgwizard.common.assets.AssetDescriptor;
import org.rpgwizard.common.assets.AssetException;
import org.rpgwizard.common.assets.AssetHandle;
import org.rpgwizard.common.assets.Enemy;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;

import org.json.JSONObject;

/**
 * @author Joshua Michael Daly
 */
public class JsonEnemySerializer extends AbstractSpriteSerializer {

	@Override
	public boolean serializable(AssetDescriptor descriptor) {
		final String ext = Paths.extension(descriptor.getURI());
		return (ext.endsWith(CoreProperties
				.getFullExtension("toolkit.enemy.extension.json")));
	}

	@Override
	public boolean deserializable(AssetDescriptor descriptor) {
		return serializable(descriptor);
	}

	@Override
	protected void load(AssetHandle handle, JSONObject json)
			throws AssetException {
		final Enemy enemy = super.load(new Enemy(handle.getDescriptor()), json);

		enemy.setLevel(json.getInt("level"));
		enemy.setHealth(json.getDouble("health"));
		enemy.setAttack(json.getDouble("attack"));
		enemy.setDefence(json.getDouble("defence"));
		enemy.setMagic(json.getDouble("magic"));
		enemy.setExperienceReward(json.getDouble("experienceReward"));
		enemy.setGoldReward(json.getDouble("goldReward"));

		handle.setAsset(enemy);
	}

	@Override
	protected void store(AssetHandle handle, JSONObject json)
			throws AssetException {
		final Enemy enemy = super.store((Enemy) handle.getAsset(), json);

		json.put("level", enemy.getLevel());
		json.put("health", enemy.getHealth());
		json.put("attack", enemy.getAttack());
		json.put("defence", enemy.getDefence());
		json.put("magic", enemy.getMagic());
		json.put("experienceReward", enemy.getExperienceReward());
		json.put("goldReward", enemy.getGoldReward());
	}

}
