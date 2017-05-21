/**
 * Copyright (c) 2015, rpgtoolkit.net <help@rpgtoolkit.net>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package net.rpgtoolkit.common.assets;

import java.net.URI;

/**
 * Describes the physical location and MIME type for an asset.
 *
 * @author Chris Hutchinson
 */
public class AssetDescriptor {

	protected String type;
	protected URI uri;

	/**
	 * Attempts to parse a string representation of an asset descriptor.
	 *
	 * @param value
	 *            asset descriptor representation
	 * @return AssetDescriptor when value is not null or empty, otherwise null
	 * @throws IllegalArgumentException
	 *             when value is not a valid asset descriptor URI
	 */
	public static AssetDescriptor parse(String value)
			throws IllegalArgumentException {
		if (value == null || value.isEmpty()) {
			return null;
		}
		return new AssetDescriptor(URI.create(value));
	}

	public AssetDescriptor(URI uri) {
		if (uri == null) {
			throw new NullPointerException();
		}
		this.uri = uri;
		this.type = "application/octet-stream";
	}

	public String getType() {
		return this.type;
	}

	public URI getURI() {
		return this.uri;
	}

	@Override
	public boolean equals(Object rhs) {
		if (rhs == this)
			return true;
		if (rhs == null)
			return false;
		if (rhs.getClass() != this.getClass())
			return false;
		return this.uri.equals(((AssetDescriptor) rhs).uri);
	}

	@Override
	public int hashCode() {
		return uri.hashCode();
	}

	@Override
	public String toString() {
		return uri.toString();
	}

}
