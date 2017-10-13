/**
 * Copyright (c) 2015, rpgwizard.org, some files forked from rpgtoolkit.net <info@rpgwizard.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.rpgwizard.common.assets;

import java.io.File;
import java.util.Objects;

public abstract class AbstractAsset implements Asset {

	protected double version;

	protected AssetDescriptor descriptor;

	public AbstractAsset(AssetDescriptor descriptor) {
		this.descriptor = descriptor;
	}

	public double getVersion() {
		return version;
	}

	public void setVersion(double version) {
		this.version = version;
	}

	@Override
	public void reset() {
	}

	@Override
	public File getFile() {
		if (descriptor == null) {
			return null;
		}

		return new File(descriptor.getURI());
	}

	@Override
	public AssetDescriptor getDescriptor() {
		return this.descriptor;
	}

	@Override
	public void setDescriptor(AssetDescriptor assetDescriptor) {
		descriptor = assetDescriptor;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 43 * hash + Objects.hashCode(this.descriptor);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final AbstractAsset other = (AbstractAsset) obj;
		if (!Objects.equals(this.descriptor, other.descriptor)) {
			return false;
		}
		return true;
	}

}
