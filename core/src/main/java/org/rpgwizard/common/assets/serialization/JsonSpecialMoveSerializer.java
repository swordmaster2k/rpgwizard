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
import org.rpgwizard.common.assets.SpecialMove;
import org.rpgwizard.common.io.Paths;
import org.rpgwizard.common.utilities.CoreProperties;

import org.json.JSONObject;

/**
 *
 * @author Joel Moore
 * @author Chris Hutchinson
 */
public class JsonSpecialMoveSerializer extends AbstractJsonSerializer {

	@Override
	public boolean serializable(AssetDescriptor descriptor) {
		final String ext = Paths.extension(descriptor.getURI());
		return (ext.contains(CoreProperties
				.getFullExtension("toolkit.specialmove.extension.json")));
	}

	@Override
	public boolean deserializable(AssetDescriptor descriptor) {
		return this.serializable(descriptor);
	}

	@Override
	public void load(AssetHandle handle, JSONObject json) {

		final SpecialMove smove = new SpecialMove(handle.getDescriptor());

		smove.setName(json.optString("name"));
		smove.setDescription(json.optString("description"));
		smove.setFightPower(json.optInt("fightPower"));
		smove.setMovePowerCost(json.optInt("mpCost"));
		smove.setMovePowerDrainedFromTarget(json.optInt("mpDrainedFromTarget"));
		smove.isUsableInBattle(json.optBoolean("canUseInBattle"));
		smove.isUsableInMenu(json.optBoolean("canUseInMenu"));

		smove.setProgram(AssetDescriptor.parse(json.optString("script")));
		smove.setStatusEffect(AssetDescriptor.parse(json
				.optString("statusEffect")));
		smove.setAnimation(AssetDescriptor.parse(json.optString("animation")));

		handle.setAsset(smove);

	}

	@Override
	public void store(AssetHandle handle, JSONObject json)
			throws AssetException {
		super.store(handle, json);

		final SpecialMove smove = (SpecialMove) handle.getAsset();

		json.put("name", smove.getName());
		json.put("description", smove.getDescription());
		json.put("fightPower", smove.getFightPower());
		json.put("mpCost", smove.getMovePowerCost());
		json.put("mpDrainedFromTarget", smove.getMovePowerDrainedFromTarget());
		json.put("canUseInBattle", smove.isUsableInBattle());
		json.put("canUseInMenu", smove.isUsableInMenu());

		json.put("script", smove.getProgram());
		json.put("statusEffect", smove.getStatusEffect());
		json.put("animation", smove.getAnimation());

	}

}
