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
import net.rpgtoolkit.common.assets.Enemy;
import net.rpgtoolkit.common.io.Paths;
import net.rpgtoolkit.common.utilities.CoreProperties;

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
