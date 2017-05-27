/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Status Effect that can be applied to an entity, and may affect gameplay
 * inside or outside of battle.
 *
 * @author Chris Hutchinson
 */
public class StatusEffect extends AbstractAsset {

	private String name;
	private StatusEffectTarget target;
	private List<StatusEffectAttribute> attributes;
	private boolean isProgramEnabled;
	private AssetDescriptor program;

	public StatusEffect(AssetDescriptor descriptor) {
    super(descriptor);
    this.attributes = new ArrayList<>();
    this.target = StatusEffectTarget.ANY;
    this.isProgramEnabled = false;
  }
	public String getName() {
		return this.name;
	}

	public void setName(String value) {
		this.name = value;
	}

	public AssetDescriptor getProgram() {
		return this.program;
	}

	public void setProgram(AssetDescriptor value) {
		this.program = value;
	}

	public boolean isProgramEnabled() {
		return this.isProgramEnabled;
	}

	public void setProgramEnabled(boolean value) {
		this.isProgramEnabled = value;
	}

	/**
	 * Gets the kind of target(s) the status effect can be applied to.
	 *
	 * @return {@see StatusEffectTarget}
	 */
	public StatusEffectTarget getTarget() {
		return this.target;
	}

	public void setTarget(StatusEffectTarget value) {
		this.target = value;
	}

	/**
	 * Gets the first status effect attribute of the specified kind.
	 *
	 * @param kind
	 *            attribute kind
	 * @return attribute if kind is present, otherwise null
	 */
	public StatusEffectAttribute getAttributeByKind(
			StatusEffectAttributeKind kind) {
		for (final StatusEffectAttribute attr : attributes) {
			if (attr.getKind().equals(kind)) {
				return attr;
			}
		}
		return null;
	}

	/**
	 * Collects all status effect attributes of the specified kind.
	 *
	 * @param kind
	 *            attribute kind
	 * @return list of attributes.
	 */
	public List<StatusEffectAttribute> getAttributesByKind(StatusEffectAttributeKind kind) {
    List<StatusEffectAttribute> attributes = new ArrayList<>();
    for (final StatusEffectAttribute attr : attributes) {
      if (attr.getKind().equals(kind)) {
        attributes.add(attr);
      }
    }
    return attributes;
  }
	/**
	 * Returns a collection of attributes applied to a target when the status
	 * effect is given to a target.
	 *
	 * @return collection of attributes
	 */
	public List<StatusEffectAttribute> getAttributes() {
		return this.attributes;
	}

	public void setAttributes(Collection<StatusEffectAttribute> value) {
		this.attributes.clear();
		this.attributes.addAll(value);
	}

}
